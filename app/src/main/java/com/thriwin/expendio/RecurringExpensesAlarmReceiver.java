package com.thriwin.expendio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;
import java.util.List;

import static com.thriwin.expendio.Utils.isNull;
import static java.util.Arrays.asList;


public class RecurringExpensesAlarmReceiver extends BroadcastReceiver {

    public static List<String> genaralTips = asList("Do you know!! \nYou can download your expenses and upload your expenses into your storage like google drive for future refereneces.",
            "Do you know!! \nYou can record your recurring expenses like daily, week days or specific day of a month. Expendio will take care of reminding you to review and add the expenses",
            "Do you know!! \nYou can add custom tags of your expenses. You can add words to specific tags. This will help in visualizing your expenses in analytics section ",
            "Do you know!! \nYou can customize when to receive notification of your recurring expenses. Like end of day 8 P.M (completely configurable!!",
            "Do you know!! \nYou can configure Expendio to remind you to fill your expenses. Also you can mark it to Do not Disturb mode."
    );

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                NotificationScheduler.setReminder(context, RecurringExpensesAlarmReceiver.class);
                context.startService(new Intent(context, SMSReceiverService.class));
                return;
            }
        }
        if (isNull(Utils.getLocalStorageForPreferences())) {
            Utils.loadLocalStorageForPreferences(context);
        }
        remindExpenseFilling(context);
        recurrenceExpenseNotifier(context);
        syncSettings(context);
        syncExpenses(context);
    }

    private void syncExpenses(Context context) {
        if (Utils.isExpenseForSyncing()) {
            String expenseForSyncing = Utils.getExpenseForSyncing();
            String[] expenseKeys = expenseForSyncing.split(",");
            for (String expenseKey : expenseKeys) {
                GoogleCloudSynchActivity.silentSignInAndWriteMyExpenseToGoogleSync(context, expenseKey);
            }

            Utils.clearExpenseForSyncing();
        }
    }

    private void syncSettings(Context context) {
        if (Utils.isSettingsForSyncing()) {
            GoogleCloudSynchActivity.silentSignInAndWriteSettingsToGoogleSync(context);
            Utils.markSettingsForSyncing(false);
        }
    }


    private void remindExpenseFilling(Context context) {
        ExpendioSettings expendioSettings = ExpendioSettings.loadExpendioSettings();
        if (expendioSettings.canRemindUser()) {
            NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                    "Expendio", "Gentle reminder for filling your expenses.", genaralTips.get(Utils.getTipsIndex()), "HOME");
            Utils.lastNotifiedOn(new Date());
        }
    }

    private void recurrenceExpenseNotifier(Context context) {
        if (!Utils.didRecurrenceChekerRanToday() && ExpendioSettings.loadExpendioSettings().isWithinNotificationHour()) {

            try {
                RecurringExpenses allRecurringExpenses = Utils.getAllRecurringExpenses();

                Expenses todaysExpenses = allRecurringExpenses.getTodaysExpenses();


                if (!todaysExpenses.isEmpty()) {
                    Utils.saveNotificationExpenses(todaysExpenses);

                    NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                            "Expenses pending for approval", "Pending for your approval:" + todaysExpenses.size(), genaralTips.get(Utils.getTipsIndex()), "NOTIFICATION");
                }
                Utils.markRecurrenceCheckerRanToday();

            } catch (Exception ex) {

            }
        }
    }
}