package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static com.nandhakumargmail.muralidharan.expendio.Utils.getDeserializedExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getExpenseGroupedByDate;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;

public class ExpenseTimelineView extends Activity {

    Button okButton, cancelButton;
    List<Expense> expenses;
    ObjectMapper obj = new ObjectMapper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_visualization_view);

        String expenseKey = this.getIntent().getStringExtra("ExpenseKey");

        this.expenses = getDeserializedExpenses(getLocalStorageForPreferences().getString(expenseKey, "[]"));
        LinearLayout timeMarker = findViewById(R.id.timeMarker);

        Map<String, List<Expense>> expenseGroupedByDate = getExpenseGroupedByDate(this.expenses);
        for (Map.Entry<String, List<Expense>> dayWiseExpense : expenseGroupedByDate.entrySet()) {
            ExpensesTimeView expensesTimeView = new ExpensesTimeView(getApplicationContext(), null, dayWiseExpense, this);
            timeMarker.addView(expensesTimeView);

        }

    }


}
