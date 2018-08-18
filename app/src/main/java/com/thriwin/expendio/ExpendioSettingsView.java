package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExpendioSettingsView extends Activity {
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expendio_settings_view);
        EditText notificationHour = findViewById(R.id.notificationHour);
        EditText startDayOfMonth = findViewById(R.id.startDayOfMonth);
        loadSettings(notificationHour, startDayOfMonth);
        findViewById(R.id.saveSettings).setOnClickListener(v -> {
            ExpendioSettings.saveExpendioSettings(new ExpendioSettings(startDayOfMonth.getText().toString(), notificationHour.getText().toString()));
            Utils.showToast(getBaseContext(), R.string.settingsSavedSuccessfully);
            loadSettings(notificationHour, startDayOfMonth);
        });
    }

    private void loadSettings(EditText notificationHour, EditText startDayOfMonth) {
        ExpendioSettings expendioSettings = ExpendioSettings.loadExpendioSettings();
        notificationHour.setText(expendioSettings.getNotificationHour().toString());
        startDayOfMonth.setText(expendioSettings.getStartDayOfMonth().toString());
    }


}
