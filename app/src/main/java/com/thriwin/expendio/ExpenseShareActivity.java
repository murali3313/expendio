package com.thriwin.expendio;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import static com.thriwin.expendio.BluetoothService.ENABLE_BLUE_TOOTH_ADD;
import static com.thriwin.expendio.BluetoothService.ENABLE_BLUE_TOOTH_INITIALIZE;
import static com.thriwin.expendio.BluetoothService.ENABLE_BLUE_TOOTH_SEND;
import static com.thriwin.expendio.BluetoothService.ENABLE_FOR_PAIR;
import static com.thriwin.expendio.Utils.isNull;
import static com.thriwin.expendio.Utils.showToast;

public class ExpenseShareActivity extends Activity {
    BluetoothService bluetoothService;
    LinearLayout smsDetails;
    View smsDetailsNote;
    LinearLayout bluetoothDetails;
    View bluetoothDetailsNote;
    RadioButton smsRadioButton;
    RadioButton bluetoothRadioButton;
    ShareSettings shareSettings;
    RadioGroup sharingMethodSelector;
    LinearLayout usersContainer;

    public static final int SEND_SMS_CODE = 345;
    String expenseStorageKey;
    public static final int SMS_SAVE = 678;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_share);

        expenseStorageKey = getIntent().getStringExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expenseStorageKey);
        ((TextView) findViewById(R.id.expenseShareForMonthHeader)).setText("Share " + readableMonthAndYear[0] + "-" + readableMonthAndYear[1]);
        smsRadioButton = findViewById(R.id.smsSharing);
        bluetoothRadioButton = findViewById(R.id.bluetoothSharing);

        smsDetailsNote = findViewById(R.id.smsNote);
        smsDetails = findViewById(R.id.sms_sharing_details);
        bluetoothDetails = findViewById(R.id.bluetooth_sharing_details);
        bluetoothDetailsNote = findViewById(R.id.bluetoothNote);
        shareSettings = Utils.getShareSettings();
        bluetoothService = new BluetoothService(this);

        usersContainer = findViewById(R.id.onlyUsers);
        findViewById(R.id.addUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsers(new User());
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        onSharingMethodSelectionHandlers();
        loadShareSettingPreferences();

    }

    private void startBlueToothServerListening() {
        if (!requestBluetoothEnablement(ENABLE_BLUE_TOOTH_INITIALIZE)) {
            bluetoothService.initializeBluetoothServer();
            showToast(ExpenseShareActivity.this, "Ready to accept expenses, via bluetooth!!!");
        }
    }

    private void stopBlueToothServerListening() {
        bluetoothService.stopAllBluetooth();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stopAllBluetooth();
        bluetoothService.disableBluetooth();
    }

    private void saveSettings() {
        ShareSettings shareSettings = new ShareSettings();
        shareSettings.setShareType(sharingMethodSelector.getCheckedRadioButtonId() == R.id.smsSharing ? SHARE_TYPE.SMS : SHARE_TYPE.BLUETOOTH);
        for (int i = 0; i < smsDetails.getChildCount(); i++) {
            SMSUserView smsUserView = (SMSUserView) smsDetails.getChildAt(i);
            shareSettings.addSMSUser(smsUserView.getSmsUser());
        }
        for (int i = 0; i < bluetoothDetails.getChildCount(); i++) {
            BluetoothUserView bluetoothUserView = (BluetoothUserView) bluetoothDetails.getChildAt(i);
            shareSettings.addBluetoothUser(bluetoothUserView.getBluetoothUser());
        }

        ExpenseShareActivity.this.shareSettings = shareSettings;
        if (shareSettings.getAllSMSUsers().size() > 0) {
            requestSMSPermission(null, SMS_SAVE);
        }
        Utils.saveShareSettings(shareSettings);
        showToast(ExpenseShareActivity.this, R.string.settingsSavedSuccessfully);
    }

    private void loadUsers(User user) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(ExpenseShareActivity.this, null);
        linearLayout.setLayoutParams(layoutParams);

        EditText child = new EditText(ExpenseShareActivity.this, null);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextParams.weight=4;
        child.setLayoutParams(editTextParams);

        child.setText(user.getName());
        ImageButton removeButton = new ImageButton(ExpenseShareActivity.this, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 90);
        params.topMargin = 30;
        removeButton.setLayoutParams(params);
        removeButton.setBackgroundResource(R.drawable.ic_remove);

        child.setHint("User Name");
        linearLayout.addView(child);
        linearLayout.addView(removeButton);
        usersContainer.addView(linearLayout, 0);
        SMSUserView smsUserView = new SMSUserView(getApplicationContext(), smsDetails, user, ExpenseShareActivity.this, expenseStorageKey);
        smsDetails.addView(smsUserView);
        BluetoothUserView bluetoothUserView = new BluetoothUserView(ExpenseShareActivity.this, bluetoothDetails, user, ExpenseShareActivity.this, bluetoothService, expenseStorageKey);
        bluetoothDetails.addView(bluetoothUserView);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersContainer.removeView(linearLayout);
                smsDetails.removeView(smsUserView);
                bluetoothDetails.removeView(bluetoothUserView);
            }
        });

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    String name = child.getText().toString();
                    smsUserView.setName(name);
                    bluetoothUserView.setName(name);
                }
            }

            private boolean filterLongEnough() {
                return child.getText().toString().trim().length() >= 0;
            }
        };
        child.addTextChangedListener(fieldValidatorTextWatcher);

    }

    private void onSharingMethodSelectionHandlers() {
        sharingMethodSelector = findViewById(R.id.sharingMethodSelection);

        sharingMethodSelector.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.smsSharing:
                    shareSettings.setShareType(SHARE_TYPE.SMS);
                    setVisibilityForSMS(true);
                    break;
                case R.id.bluetoothSharing:
                    shareSettings.setShareType(SHARE_TYPE.BLUETOOTH);
                    setVisibilityForSMS(false);
                    break;
            }
        });
    }


    private void loadShareSettingPreferences() {

        if (!isNull(shareSettings.getShareType())) {
            if (shareSettings.isSMS()) {
                setVisibilityForSMS(true);
            } else {
                setVisibilityForSMS(false);
            }

            List<User> smsUsers = shareSettings.getAllUsers();
            smsDetails.removeAllViews();
            bluetoothDetails.removeAllViews();
            for (User smsUser : smsUsers) {
                loadUsers(smsUser);
            }
        }
    }

    private void setVisibilityForSMS(boolean isVisible) {
        smsDetails.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        smsDetailsNote.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        bluetoothDetails.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        bluetoothDetailsNote.setVisibility(isVisible ? View.GONE : View.VISIBLE);

        smsRadioButton.setChecked(isVisible);
        bluetoothRadioButton.setChecked(!isVisible);
        if (isVisible) {
            stopBlueToothServerListening();
        } else {
            startBlueToothServerListening();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        afterBluetoothEnablement(requestCode, resultCode);
    }

    private void afterBluetoothEnablement(int requestCode, int resultCode) {
        switch (requestCode) {
            case ENABLE_FOR_PAIR: {
                if (resultCode == Activity.RESULT_OK) {
                    selectedBluetoothUserView.showPairDetails();
                } else {
                    showToast(this, R.string.bluetoothPermissionIsRequiredToShareData);
                }
            }
            case ENABLE_BLUE_TOOTH_SEND: {
                if (resultCode == Activity.RESULT_OK) {
                    selectedBluetoothUserView.sendExpense();
                } else {
                    showToast(this, R.string.bluetoothPermissionIsRequiredToShareData);
                }
            }
            case ENABLE_BLUE_TOOTH_INITIALIZE: {
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothService.initializeBluetoothServer();
                    showToast(ExpenseShareActivity.this, "Ready to accept expenses, via bluetooth!!!");
                } else {
                    showToast(this, R.string.bluetoothPermissionIsRequiredToShareData);
                }
            }
        }
    }

    SMSUserView currentlySMSRequestingView;

    public void requestSMSPermission(SMSUserView smsUserView, int code) {
        currentlySMSRequestingView = smsUserView;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
        }, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SEND_SMS_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentlySMSRequestingView.prepareToSendMessage();

                } else {

                    showToast(this, R.string.smsPermissionDenied);
                }
                return;
            }
            case SMS_SAVE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    showToast(this, R.string.smsPermissionDenied);
                }
                return;
            }
        }
    }

    public boolean requestBluetoothEnablement(int reasonCode) {
        if (bluetoothService.shouldRequestBluetoothPermission()) {
            bluetoothService.enableBluetooth();
            if (bluetoothService.bluetoothAdapter.isEnabled()) {
                afterBluetoothEnablement(reasonCode, RESULT_OK);
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, reasonCode);
            }
            return true;
        } else {
            return false;
        }
    }


    BluetoothUserView selectedBluetoothUserView;

    public boolean requestBluetoothEnablement(BluetoothUserView bluetoothUserView, int enableForPair) {
        this.selectedBluetoothUserView = bluetoothUserView;
        return requestBluetoothEnablement(enableForPair);
    }
}
