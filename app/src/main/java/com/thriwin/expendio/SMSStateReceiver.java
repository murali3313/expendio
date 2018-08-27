package com.thriwin.expendio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import static com.thriwin.expendio.Utils.isNull;

public class SMSStateReceiver extends BroadcastReceiver {

    static SMSUserMessages smsFromUsers = new SMSUserMessages();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            if (isNull(Utils.getLocalStorageForPreferences())) {
                Utils.loadLocalStorageForPreferences(context);
            }
            SMSInferenceSettings smsInfererSettings = Utils.getSMSInfererSettings();
            ShareSettings shareSettings = Utils.getShareSettings();


            StringBuilder completMessages = new StringBuilder();
            String from = null;
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    completMessages.append(messages[i].getDisplayMessageBody());
                    from = messages[i].getOriginatingAddress();
                }

                if (messages.length > -1) {

                    if (smsInfererSettings.isEnabled()) {
                        Expense probableExpenses = smsInfererSettings.getProbableExpenses(completMessages.toString());
                        if (!isNull(probableExpenses)) {
                            Utils.saveSMSInferredExpense(probableExpenses);
                            NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                                    "Expense suggestion based on your sms", "Pending for your approval:" + 1, RecurringExpensesAlarmReceiver.genaralTips.get(Utils.getTipsIndex()), "NOTIFICATION");
                        }
                    }

                    User authenticatedUser = shareSettings.getAuthenticatedSMSUser(from, completMessages.toString());
                    if (!isNull(authenticatedUser)) {
                        smsFromUsers.add(authenticatedUser.getName(), completMessages.toString());
                        if (smsFromUsers.isAllMessagesComplete(authenticatedUser.getName())) {
                            SMSExpenseParser smsExpenseParser = new SMSExpenseParser(shareSettings, authenticatedUser, context);
                            if (true != false) {
                                smsExpenseParser.start();
                            }
                            smsExpenseParser.start();
                        }
                    }
                }
            }
        }


    }
}
