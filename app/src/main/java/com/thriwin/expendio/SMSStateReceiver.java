package com.thriwin.expendio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import static com.thriwin.expendio.Utils.isNull;

public class SMSStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            if(isNull(Utils.getLocalStorageForPreferences())){
                Utils.loadLocalStorageForPreferences(context);
            }
            SMSInferenceSettings smsInfererSettings = Utils.getSMSInfererSettings();

            if (!smsInfererSettings.isEnabled())
                return;
            StringBuilder completMessages = new StringBuilder();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    completMessages.append(messages[i].getDisplayMessageBody());
                }

                if (messages.length > -1) {
                    Expense probableExpenses = smsInfererSettings.getProbableExpenses(completMessages.toString());
                    if (!isNull(probableExpenses)) {
                        Utils.saveSMSInferredExpense(probableExpenses);
                        NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                                "Expense suggestion based on your sms", "Pending for your approval:" + 1 , RecurringExpensesAlarmReceiver.genaralTips.get(Utils.getTipsIndex()), "NOTIFICATION");
                    }
                }
            }
        }


    }
}
