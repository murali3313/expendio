package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class NotificationView extends LinearLayout implements IDisplayAreaView {

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.notification, this);
    }

    @Override
    public void load(CommonActivity expenseListener, Intent intent) {
        Expenses allUnAcceptedExpenses = new Expenses();
        LinearLayout container = findViewById(R.id.unAcceptedExpensesContainer);
        container.removeAllViews();
        List<Expenses> unAcceptedExpenses = Utils.getUnAcceptedExpenses();
        for (Expenses unAcceptedExpens : unAcceptedExpenses) {
            allUnAcceptedExpenses.addAll(unAcceptedExpens);
        }
        if (!unAcceptedExpenses.isEmpty()) {
            for (Expenses expenses : unAcceptedExpenses) {
                container.addView(new UnAcceptedExpensesBaseView(getContext(), null, expenses, "UnApproved expenses via Audio", Utils.UNACCEPTED_EXPENSES));
            }
        }

        ArrayList<Expenses> notificationExpenses = Utils.getNotificationExpenses();
        for (Expenses notificationExpens : notificationExpenses) {
            allUnAcceptedExpenses.addAll(notificationExpens);
        }
        if (!notificationExpenses.isEmpty()) {
            for (Expenses notificationExpens : notificationExpenses) {
                container.addView(new UnAcceptedExpensesBaseView(getContext(), null, notificationExpens, "Recurring expense on: " + notificationExpens.getDateMonthHumanReadable(), notificationExpens.getKeyForUnApprovedDailyExpense()));
            }
        }

        View viewById = findViewById(R.id.noUnAcceptedExpensePresent);
        viewById.setVisibility(allUnAcceptedExpenses.size() == 0 ? VISIBLE : GONE);


    }
}
