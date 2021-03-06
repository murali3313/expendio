package com.thriwin.expendio;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RecurringExpensesView extends GeneralActivity {
    ObjectMapper obj = new ObjectMapper();
    LinearLayout recurringExpensesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.recurring_expenses_view);
        super.onCreate(savedInstanceState);
        load();
    }

    public void load() {
        ImageButton addRecurringExpenseButton = (ImageButton) findViewById(R.id.addRecurringExpenses);
        recurringExpensesContainer = (LinearLayout) findViewById(R.id.recurringExpenses);
        recurringExpensesContainer.removeAllViews();
        addRecurringExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurringExpenseView recurringExpenseView = new RecurringExpenseView(getApplicationContext(), null, RecurringExpensesView.this, new RecurringExpense());
                recurringExpensesContainer.addView(recurringExpenseView, 0);
                recurringExpenseView.requestFocus();
            }
        });
        ImageButton saveRecurringExpenseButton = (ImageButton) findViewById(R.id.saveRecurringExpenses);
        saveRecurringExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurringExpenses recurringExpenses = new RecurringExpenses();
                for (int i = 0; i < recurringExpensesContainer.getChildCount(); i++) {
                    RecurringExpenseView recurringExpenseView = (RecurringExpenseView) recurringExpensesContainer.getChildAt(i);
                    RecurringExpense recurringExpense = recurringExpenseView.getRecurringExpense();
                    if (!Utils.isNull(recurringExpense)) {
                        recurringExpenses.add(recurringExpense);
                    }
                }
                Utils.saveRecurrigExpenses(recurringExpenses);
                Utils.showToast(getApplicationContext(), R.string.recurringExpensesSavedSuccessfully);
                Utils.markSettingsForSyncing(true);

                loadRecurringExpenses();

            }
        });

        loadRecurringExpenses();
    }

    private void loadRecurringExpenses() {
        RecurringExpenses allRecurringExpenses = Utils.getAllRecurringExpenses();
        recurringExpensesContainer.removeAllViews();
        for (RecurringExpense recurringExpense : allRecurringExpenses) {
            RecurringExpenseView recurringExpenseView = new RecurringExpenseView(getApplicationContext(), null, RecurringExpensesView.this, recurringExpense);
            recurringExpensesContainer.addView(recurringExpenseView);
        }
    }

    public void removeChild(View view) {
        recurringExpensesContainer.removeView(view);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
