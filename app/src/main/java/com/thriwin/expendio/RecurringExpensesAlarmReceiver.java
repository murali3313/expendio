package com.thriwin.expendio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.thriwin.expendio.Utils.isNull;


public class RecurringExpensesAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                NotificationScheduler.setReminder(context, RecurringExpensesAlarmReceiver.class, 8, 0);
                return;
            }
        }

        if (!Utils.didRecurrenceChekerRanToday()) {

            try {

                if (isNull(Utils.getLocalStorageForPreferences())) {
                    Utils.loadLocalStorageForPreferences(context);
                }

                RecurringExpenses allRecurringExpenses = Utils.getAllRecurringExpenses();

                Expenses todaysExpenses = allRecurringExpenses.getTodaysExpenses();

                Utils.saveNotificationExpenses(todaysExpenses);

                if (!todaysExpenses.isEmpty()) {
                    NotificationScheduler.showNotification(context, ExpenseListener.class,
                            "Expenses pending for approval", "Pending for your approval:" + todaysExpenses.size());
                }
                Utils.markRecurrenceCheckerRanToday();

            } catch (Exception ex) {

            }
        }


    }
}