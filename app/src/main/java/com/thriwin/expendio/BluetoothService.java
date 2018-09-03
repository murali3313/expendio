package com.thriwin.expendio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.thriwin.expendio.MessageConstants.CONNECTED;
import static com.thriwin.expendio.MessageConstants.MESSAGE_CONNECT_ERROR;
import static com.thriwin.expendio.MessageConstants.MESSAGE_ERROR;
import static com.thriwin.expendio.MessageConstants.MESSAGE_READ;
import static com.thriwin.expendio.MessageConstants.MESSAGE_SUCCESS;
import static com.thriwin.expendio.MessageConstants.MESSAGE_WRITE;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_END_WITHESC;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_START_WITHESC;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_WITHESC;
import static com.thriwin.expendio.Utils.isNull;
import static com.thriwin.expendio.Utils.showToast;

public class BluetoothService {

    public static final int ENABLE_BLUE_TOOTH_SEND = 5567;
    public static final int ENABLE_BLUE_TOOTH_INITIALIZE = 687687;
    private final UUID MY_UUID = UUID.fromString("d3ea5966-8bad-44b3-9c7d-335f3297919d");
    private Activity activity;
    public static final int ENABLE_BLUE_TOOTH_ADD = 1234;
    public static final int ENABLE_FOR_PAIR = 234;
    BluetoothAdapter bluetoothAdapter;
    ConnectedThread connectedThread;
    ConnectThread connectThread;
    AcceptThread acceptThread;
    private String expenseStorageKey;
    private BluetoothSocket bluetoothSocket;
    public BluetoothDevice bluetoothDevice;
    private int connectionTime = 0;

    static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg_type) {
            super.handleMessage(msg_type);

            switch (msg_type.what) {
                case MESSAGE_READ:

                    byte[] readbuf = trim((byte[]) msg_type.obj);
                    String string_recieved = null;
                    try {
                        string_recieved = new String(readbuf, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    ShareSettings shareSettings = Utils.getShareSettings();
                    User authenticatedBluetoothUser = shareSettings.getAuthenticatedBluetoothUser(bluetoothSocket.getRemoteDevice().getAddress(), string_recieved);
                    if (!isNull(authenticatedBluetoothUser)) {
                        string_recieved = string_recieved.replaceAll(EXPENDIO_SMS_START_WITHESC, "").replaceAll(EXPENDIO_SMS_WITHESC, "").replaceAll(EXPENDIO_SMS_END_WITHESC, "");

                        Expenses parsedExpenses = shareSettings.getParsedExpenses(string_recieved, authenticatedBluetoothUser.getName());
                        if (!isNull(parsedExpenses) && !parsedExpenses.isEmpty()) {
                            Utils.saveSMSParsedExpenses(authenticatedBluetoothUser, parsedExpenses);
                            NotificationScheduler.showNotification(activity, HomeScreenActivity.class,
                                    "Expense shared from trusted user", "Pending for your approval from: " + authenticatedBluetoothUser.getName() + " :" + parsedExpenses.size(), RecurringExpensesAlarmReceiver.genaralTips.get(Utils.getTipsIndex()), "NOTIFICATION");
                        }
                    }

                    break;
                case MESSAGE_WRITE:

                    if (msg_type.obj != null) {
                        connectedThread = new ConnectedThread((BluetoothSocket) msg_type.obj);
                        String formattedShareExpense = Utils.getFormattedShareExpense(expenseStorageKey);
                        connectedThread.write(formattedShareExpense.getBytes());

                    }
                    break;

                case CONNECTED:
                    showToast(activity, "Connected");
                    connectedThread = new ConnectedThread((BluetoothSocket) msg_type.obj);
                    connectedThread.start();
                    break;
                case MESSAGE_ERROR:
                    showToast(activity, msg_type.obj.toString());
                    break;
                case MESSAGE_SUCCESS:
                    showToast(activity, msg_type.obj.toString());
                    break;

                case MESSAGE_CONNECT_ERROR:
                    if (connectionTime < 3) {
                        connectThread = new ConnectThread(BluetoothService.this.bluetoothDevice);
                        connectThread.start();

                    } else {
                        showToast(activity, "Failed connecting device in attempted: " + connectionTime + " times. Please make sure other device is ready to accept expense.");
                    }
                    break;


            }
        }
    };


    public BluetoothService(Activity activity) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
    }

    public void initializeBluetoothServer() {
        if (isNull(acceptThread)) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public Set<BluetoothDevice> getAlreadyPairedDevices() {
        Set<BluetoothDevice> pairedDevices = new HashSet<>();
        if (!isNull(bluetoothAdapter) && bluetoothAdapter.isEnabled()) {
            pairedDevices = bluetoothAdapter.getBondedDevices();

        }
        return pairedDevices;
    }

    public boolean shouldRequestBluetoothPermission() {
        if (!isNull(bluetoothAdapter)) {
            return !bluetoothAdapter.isEnabled();
        }
        return false;
    }

    public void sendExpense(User bluetoothUser, String expenseStorageKey) {
        prepareToSend(bluetoothUser, expenseStorageKey);
    }

    private void prepareToSend(User bluetoothUser, String expenseStorageKey) {
        View sheetView = View.inflate(activity, R.layout.bottom_send_bluetooth_confirmation, null);
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothService.this.expenseStorageKey = expenseStorageKey;
                Set<BluetoothDevice> alreadyPairedDevices = getAlreadyPairedDevices();
                BluetoothDevice bluetoothDevice = null;
                for (BluetoothDevice alreadyPairedDevice : alreadyPairedDevices) {
                    if (alreadyPairedDevice.getAddress().equalsIgnoreCase(bluetoothUser.getPairDetail())) {
                        bluetoothDevice = alreadyPairedDevice;
                    }
                }

                if (!isNull(bluetoothDevice)) {
                    BluetoothService.this.bluetoothDevice = bluetoothDevice;
                    connectThread = new ConnectThread(bluetoothDevice);
                    connectThread.start();

                }
                mBottomSheetDialog.cancel();
            }
        });

        mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.cancel();
            }
        });
    }

    public void stopAllBluetooth() {
        try {
            if (!isNull(acceptThread))
                acceptThread.stop();
            if (!isNull(connectedThread))
                connectedThread.stop();
        } catch (Exception e) {

        }

    }

    public void disableBluetooth() {
        if (!isNull(bluetoothAdapter))
            bluetoothAdapter.disable();
    }

    public void enableBluetooth() {
        if (!isNull(bluetoothAdapter))
            bluetoothAdapter.enable();
    }

    public static BluetoothSocket socketInAccept = null;

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Expendio", MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }


        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {

                try {
                    socket = mmServerSocket.accept();
                    socketInAccept = socket;
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    mHandler.obtainMessage(CONNECTED, socket).sendToTarget();
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }


    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            BluetoothService.this.connectionTime = BluetoothService.this.connectionTime < 3 ? BluetoothService.this.connectionTime + 1 : 0;
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                mHandler.obtainMessage(MESSAGE_CONNECT_ERROR).sendToTarget();

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    mHandler.obtainMessage(MESSAGE_ERROR, "Close exception: " + closeException.getMessage()).sendToTarget();
                }
                return;
            }

            mHandler.obtainMessage(MESSAGE_WRITE, mmSocket).sendToTarget();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[10240];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {

                try {
                    bluetoothSocket = mmSocket;
                    numBytes = mmInStream.read(mmBuffer);
                    Message readMsg = mHandler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mHandler.obtainMessage(MESSAGE_SUCCESS, "Expenses send successfully").sendToTarget();
            } catch (IOException e) {
                mHandler.obtainMessage(MESSAGE_ERROR, "Couldn't transfer the expense. Please make sure other device is in the share expense page.").sendToTarget();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


};

