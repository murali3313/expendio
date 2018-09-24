package com.thriwin.expendio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ExpenseSMSPattern extends GeneralActivity {

    SMSInferenceSettings smsInferenceSettingsToSave = new SMSInferenceSettings();
    LinearLayout smsPhraseContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sms_pattern_setup);
        super.onCreate(savedInstanceState);
        load();


    }

    private void load() {
        SMSInferenceSettings smsInfererSettings = Utils.getSMSInfererSettings();
        SwitchCompat smsTrackkingEnabler = (SwitchCompat) findViewById(R.id.enableSmsTracking);
        smsTrackkingEnabler.setChecked(smsInfererSettings.isEnabled());
        ArrayList<String> smsPhrases = smsInfererSettings.getSmsPhrases();
        smsPhraseContainer = (LinearLayout) findViewById(R.id.smsPhraseContainer);
        for (String smsPhrase : smsPhrases) {
            LinearLayout smsPhraseView = getSMSPhraseCoontrol(smsPhrase);
            smsPhraseContainer.addView(smsPhraseView);
        }

        findViewById(R.id.addSMSPhrase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout smsPhraseCoontrol = getSMSPhraseCoontrol("");
                smsPhraseContainer.addView(smsPhraseCoontrol, 0);
                smsPhraseCoontrol.requestFocus();

            }
        });

        findViewById(R.id.saveSMSPhrase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsInferenceSettingsToSave = new SMSInferenceSettings();
                boolean enabled = smsTrackkingEnabler.isEnabled();

                String[] permissions = new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS
                };

                smsInferenceSettingsToSave.setEnabled(enabled);
                for (int i = 0; i < smsPhraseContainer.getChildCount(); i++) {
                    String smsPhrase = ((EditText) ((LinearLayout) smsPhraseContainer.getChildAt(i)).getChildAt(0)).getText().toString();
                    smsInferenceSettingsToSave.setSmsPhrase(smsPhrase);
                }

                if (enabled) {
                    if (isPermissionDenied(ExpenseSMSPattern.this, permissions)){
                        requestPermissions(permissions, 23);
                    }else{
                        saveSMSPatterns();
                    }


                } else {
                    getBaseContext().stopService(new Intent(getBaseContext(), SMSReceiverService.class));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                saveSMSPatterns();
            } else {
                Utils.saveSMSInfererSettings(smsInferenceSettingsToSave);
                Utils.showToast(getBaseContext(), R.string.smsPermissionDenied);
            }
        }
    }

    private void saveSMSPatterns() {
        Utils.saveSMSInfererSettings(smsInferenceSettingsToSave);
        getBaseContext().startService(new Intent(getBaseContext(), SMSReceiverService.class));
        Utils.showToast(getBaseContext(), R.string.savedSmsPhrases);
    }


    @NonNull
    private LinearLayout getSMSPhraseCoontrol(String smsPhrase) {
        LinearLayout linearLayout = new LinearLayout(getBaseContext(), null);
        LinearLayout.LayoutParams linearLayoutLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayoutLayoutParams.topMargin = 5;
        linearLayout.setLayoutParams(linearLayoutLayoutParams);

        EditText smsPhraseView = new EditText(getBaseContext(), null);
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.weight = 2;

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonLayoutParams.height = 80;
        buttonLayoutParams.width = 80;
        buttonLayoutParams.bottomMargin = 10;
        buttonLayoutParams.gravity = Gravity.BOTTOM;
        smsPhraseView.setText(smsPhrase);
        smsPhraseView.setTextColor(getResources().getColor(R.color.white));
        smsPhraseView.setLayoutParams(textViewLayoutParams);


        ImageButton removeButton = new ImageButton(getBaseContext(), null);
        removeButton.setBackgroundResource(R.drawable.ic_remove);
        removeButton.setLayoutParams(buttonLayoutParams);
        linearLayout.addView(smsPhraseView);
        linearLayout.addView(removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsPhraseContainer.removeView(linearLayout);
            }
        });
        linearLayout.setBackgroundResource(R.drawable.edit_outline);
        return linearLayout;
    }
}
