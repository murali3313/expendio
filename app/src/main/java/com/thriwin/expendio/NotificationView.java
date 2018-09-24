package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

public class NotificationView extends LinearLayout implements IDisplayAreaView {

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.notification, this);
    }

    @Override
    public void load(CommonActivity expenseListener, Intent intent) {
        LinearLayout container = (LinearLayout)findViewById(R.id.unAcceptedExpensesContainer);
        container.removeAllViews();
        HashMap<String, Expenses> unAcceptedExpenses = Utils.getAllUnAcceptedExpenses();

        if (!unAcceptedExpenses.isEmpty()) {
            for (Map.Entry<String, Expenses> expenses : unAcceptedExpenses.entrySet()) {
                String expenseHeader = getExpenseHeader(expenses);
                container.addView(new UnAcceptedExpensesBaseView(expenseListener, getContext(), null, expenses.getValue(), expenseHeader, expenses.getKey(), this));
            }
        }

        View viewById = findViewById(R.id.noUnAcceptedExpensePresent);
        viewById.setVisibility(unAcceptedExpenses.size() == 0 ? VISIBLE : GONE);


    }

    @NonNull
    private String getExpenseHeader(Map.Entry<String, Expenses> expenses) {
        String header = "";
        if (expenses.getKey().startsWith(Utils.UNACCEPTED_EXPENSES)) {
            header = "UnApproved expenses via Audio";
        } else if (expenses.getKey().startsWith(Utils.UNACCEPTED_SMS_EXPENSES)) {
            header = "SMS Expense suggestion";
        } else if (expenses.getKey().startsWith(Utils.DAILY_EXPENSES)) {
            header = "Recurring expense on: " + expenses.getValue().getDateMonthHumanReadable();
        } else if (expenses.getKey().startsWith(Utils.UNACCEPTED_SHARED_SMS_EXPENSES)) {
            header = "Expenses from " + expenses.getKey().split("-")[1] + " for " + expenses.getValue().getMonthYearHumanReadable();
        }
        return header;
    }
}
