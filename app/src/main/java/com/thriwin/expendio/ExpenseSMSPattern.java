package com.thriwin.expendio;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseSMSPattern extends Activity {

    SMSInferenceSettings smsInferenceSettingsToSave = new SMSInferenceSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_pattern_setup);
        load();


    }

    private void load() {
        SMSInferenceSettings smsInfererSettings = Utils.getSMSInfererSettings();
        SwitchCompat smsTrackkingEnabler = findViewById(R.id.enableSmsTracking);
        smsTrackkingEnabler.setChecked(smsInfererSettings.isEnabled());
        ArrayList<String> smsPhrases = smsInfererSettings.getSmsPhrases();
        LinearLayout smsPhraseContainer = findViewById(R.id.smsPhraseContainer);
        for (String smsPhrase : smsPhrases) {
            EditText smsPhraseView = getSMSTextView(smsPhrase);
            smsPhraseContainer.addView(smsPhraseView);
        }

        findViewById(R.id.addSMSPhrase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsPhraseContainer.addView(getSMSTextView(""), 0);
            }
        });

        findViewById(R.id.saveSMSPhrase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsInferenceSettingsToSave = new SMSInferenceSettings();
                boolean enabled = smsTrackkingEnabler.isEnabled();
                if (enabled) {

                }
                String[] permissions = new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS
                };

                smsInferenceSettingsToSave.setEnabled(enabled);
                for (int i = 0; i < smsPhraseContainer.getChildCount(); i++) {
                    String smsPhrase = ((TextView) smsPhraseContainer.getChildAt(i)).getText().toString();
                    smsInferenceSettingsToSave.setSmsPhrase(smsPhrase);
                }

                ActivityCompat.requestPermissions(ExpenseSMSPattern.this, permissions, 23);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23) {
            Utils.saveSMSInfererSettings(smsInferenceSettingsToSave);
            if (resultCode == Activity.RESULT_OK) {
                getBaseContext().startService(new Intent(getBaseContext(), SMSReceiverService.class));

                Utils.showToast(getBaseContext(), R.string.savedSmsPhrases);
            } else {
                Utils.showToast(getBaseContext(), R.string.smsPermissionDenied);
            }
        }
    }

    @NonNull
    private EditText getSMSTextView(String smsPhrase) {
        EditText smsPhraseView = new EditText(getBaseContext(), null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = 180;
        smsPhraseView.setText(smsPhrase);
        smsPhraseView.setLayoutParams(layoutParams);
        return smsPhraseView;
    }
}
