package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RecurringExpensesView extends Activity {
    ObjectMapper obj = new ObjectMapper();
    RecurringExpenseEditView dailyRecurringExpenseEditView;
    RecurringExpenseEditView dayOfWeekRecurringExpenseEditView;
    RecurringExpenseEditView dayOfMonthRecurringExpenseEditView;
    LinearLayout recurringExpensesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recurring_expenses_view);
        load();
    }

    public void load() {


        ImageButton addRecurringExpenseButton = findViewById(R.id.addRecurringExpenses);
        recurringExpensesContainer = findViewById(R.id.recurringExpenses);
        recurringExpensesContainer.removeAllViews();
        addRecurringExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurringExpenseView recurringExpenseView = new RecurringExpenseView(getApplicationContext(), null, RecurringExpensesView.this, new RecurringExpense());
                recurringExpensesContainer.addView(recurringExpenseView, 0);
            }
        });
        ImageButton saveRecurringExpenseButton = findViewById(R.id.saveRecurringExpenses);
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
