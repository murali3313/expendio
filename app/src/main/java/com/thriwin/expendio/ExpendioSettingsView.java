package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExpendioSettingsView extends Activity {
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expendio_settings_view);
        EditText notificationHour = findViewById(R.id.notificationHour);
        EditText startDayOfMonth = findViewById(R.id.startDayOfMonth);
        Spinner reminderOption = findViewById(R.id.reminderOption);
        loadSettings(notificationHour, startDayOfMonth, reminderOption);

        findViewById(R.id.eraseAllData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sheetView = View.inflate(ExpendioSettingsView.this, R.layout.bottom_reset_all_data_confirmation, null);
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ExpendioSettingsView.this);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.clearAllData();
                        mBottomSheetDialog.cancel();
                        ExpendioSettingsView.this.finish();
                    }
                });

                mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.cancel();
                    }
                });
            }
        });

        findViewById(R.id.saveSettings).setOnClickListener(v -> {
            ExpendioSettings.saveExpendioSettings(new ExpendioSettings(startDayOfMonth.getText().toString(),
                    notificationHour.getText().toString(), ReminderOption.values()[reminderOption.getSelectedItemPosition()]));
            Utils.showToast(getBaseContext(), R.string.settingsSavedSuccessfully);
            loadSettings(notificationHour, startDayOfMonth, reminderOption);
        });
    }

    private void loadSettings(EditText notificationHour, EditText startDayOfMonth, Spinner reminderOption) {
        ExpendioSettings expendioSettings = ExpendioSettings.loadExpendioSettings();
        notificationHour.setText(expendioSettings.getNotificationHour().toString());
        startDayOfMonth.setText(expendioSettings.getStartDayOfMonth().toString());
        reminderOption.setSelection(expendioSettings.getReminderOptionIndex());
    }
}