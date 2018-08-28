package com.thriwin.expendio;

import android.content.Context;

import static com.thriwin.expendio.Utils.isNull;

public class SMSExpenseParser extends Thread {

    private ShareSettings shareSettings;
    private User authenticatedUser;
    private Context context;
    private SMSUserMessages smsFromUsers;

    public SMSExpenseParser(ShareSettings shareSettings, User authenticatedUser, Context context, SMSUserMessages smsUserMessages) {

        this.shareSettings = shareSettings;
        this.authenticatedUser = authenticatedUser;
        this.context = context;
        this.smsFromUsers = smsUserMessages;
    }

    @Override
    public void run() {
        Expenses parsedExpenses = shareSettings.getParsedExpenses(smsFromUsers.getCollatedMessages(authenticatedUser.getName()));
        if (!isNull(parsedExpenses) && !parsedExpenses.isEmpty()) {
            Utils.saveSMSParsedExpenses(authenticatedUser, parsedExpenses);
            NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                    "Expense shared from trusted user", "Pending for your approval from: " + authenticatedUser.getName() + " :" + parsedExpenses.size(), RecurringExpensesAlarmReceiver.genaralTips.get(Utils.getTipsIndex()), "NOTIFICATION");
        }
    }
}
