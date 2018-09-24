package com.thriwin.expendio;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Set;

class BluetoothUserView extends LinearLayout implements PopupMenu.OnMenuItemClickListener {
    private final User bluetoothUser;
    private final ExpenseShareActivity expenseShareActivity;
    TextView userName;
    TextView pairDetails;
    View pairNow;
    private BluetoothService bluetoothService;
    private String expenseStorageKey;

    public BluetoothUserView(Context context, LinearLayout bluetoothContainer, User bluetoothUser,
                             ExpenseShareActivity expenseShareActivity, BluetoothService bluetoothService, String expenseStorageKey) {
        super(expenseShareActivity, null);
        this.bluetoothService = bluetoothService;
        this.expenseStorageKey = expenseStorageKey;
        inflate(expenseShareActivity, R.layout.bluetooth_user_view, this);
        this.bluetoothUser = bluetoothUser;
        this.expenseShareActivity = expenseShareActivity;
        pairNow = findViewById(R.id.userPairInfo);


        findViewById(R.id.sync).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bluetoothUser.hasPairDetails()) {
                    if (!expenseShareActivity.requestBluetoothEnablement(BluetoothUserView.this, BluetoothService.ENABLE_BLUE_TOOTH_SEND)) {
                        sendExpense();
                    }
                } else {
                    Utils.showToast(expenseShareActivity, "Please select a paired device to share your expenses.");
                }

            }
        });

        pairNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expenseShareActivity.requestBluetoothEnablement(BluetoothUserView.this, BluetoothService.ENABLE_FOR_PAIR)) {
                    showPairDetails();
                }

            }
        });

        loadDetail();
    }

    public User getBluetoothUser() {
        com.thriwin.expendio.User bluetoothUser = new User();
        bluetoothUser.setName(userName.getText().toString());
        bluetoothUser.setPairInfoDetail(pairDetails.getText().toString());
        return bluetoothUser;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String[] macAddress = item.getTitle().toString().split("-");
        bluetoothUser.setPairDeviceName(macAddress[0]);
        bluetoothUser.setPairDetail(macAddress[1]);
        loadDetail();
        return false;
    }

    private void loadDetail() {
        userName = (TextView)findViewById(R.id.userName);
        pairDetails = (TextView)findViewById(R.id.userPairInfo);
        userName.setText(bluetoothUser.getName());
        pairDetails.setText(bluetoothUser.getPairInfo());
    }

    public void showPairDetails() {
        Set<BluetoothDevice> alreadyPairedDevices = bluetoothService.getAlreadyPairedDevices();
        PopupMenu popup = new PopupMenu(getContext(), findViewById(R.id.userPairInfo));
        for (BluetoothDevice bluetoothDevice : alreadyPairedDevices) {
            popup.getMenu().add(bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress());
        }
        popup.setOnMenuItemClickListener(BluetoothUserView.this);
        popup.show();
    }

    public void sendExpense() {
        bluetoothService.sendExpense(bluetoothUser, expenseStorageKey);
    }

    public void setName(String name) {
        userName.setText(name);
        bluetoothUser.setName(name);
    }
}
