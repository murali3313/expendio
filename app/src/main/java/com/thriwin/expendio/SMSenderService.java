package com.thriwin.expendio;

import android.telephony.SmsManager;

import java.util.ArrayList;

import static com.thriwin.expendio.Utils.getFormattedShareExpense;

public class SMSenderService implements Runnable {
    private User user;
    private String expenseStorageKey;

    public void sendExpenses(User user, String expenseStorageKey) {
        this.user = user;
        this.expenseStorageKey = expenseStorageKey;
        this.run();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String formattedShareExpense = getFormattedShareExpense(expenseStorageKey);
            ArrayList<String> parts = smsManager.divideMessage(formattedShareExpense);
            smsManager.sendMultipartTextMessage(user.getNumber(), "", parts, null, null);


        } catch (Exception e) {

        }
    }
}
