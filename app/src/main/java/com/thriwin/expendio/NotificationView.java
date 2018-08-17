package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
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
        LinearLayout container = findViewById(R.id.unAcceptedExpensesContainer);
        container.removeAllViews();
        HashMap<String, Expenses> unAcceptedExpenses = Utils.getAllUnAcceptedExpenses();

        if (!unAcceptedExpenses.isEmpty()) {
            for (Map.Entry<String, Expenses> expenses : unAcceptedExpenses.entrySet()) {
                String expenseHeader = expenses.getKey().startsWith(Utils.UNACCEPTED_EXPENSES) ? "UnApproved expenses via Audio" : "Recurring expense on: "+ expenses.getValue().getDateMonthHumanReadable();
                container.addView(new UnAcceptedExpensesBaseView(getContext(), null, expenses.getValue(), expenseHeader, expenses.getKey()));
            }
        }

        View viewById = findViewById(R.id.noUnAcceptedExpensePresent);
        viewById.setVisibility(unAcceptedExpenses.size() == 0 ? VISIBLE : GONE);


    }
}
