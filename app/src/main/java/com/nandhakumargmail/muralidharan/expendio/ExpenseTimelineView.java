package com.nandhakumargmail.muralidharan.expendio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.nandhakumargmail.muralidharan.expendio.Utils.getDeserializedMonthWiseExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;

public class ExpenseTimelineView extends SpeechActivity {

    Button okButton, cancelButton;
    MonthWiseExpenses monthWiseExpenses;
    ObjectMapper obj = new ObjectMapper();
    String expenseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.expense_visualization_view);
        ImageButton addExpenseInCurrentMonth = findViewById(R.id.addExpenseInCurrentMonth);
        expenseKey = this.getIntent().getStringExtra("ExpenseKey");
        addExpenseInCurrentMonth.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MonthWiseExpenseEdit.class);
            i.putExtra("LatestDate", getDeserializedMonthWiseExpenseslocal(expenseKey).getLatestDate(expenseKey));
            ContextCompat.startActivity(getApplicationContext(), i, null);
        });

        loadTimeLineView(expenseKey);
        super.onCreate(savedInstanceState);


    }

    public void loadTimeLineView(String expenseKey) {
        this.monthWiseExpenses = getDeserializedMonthWiseExpenseslocal(expenseKey);

        TextView monthWiseTotalExpenditure = findViewById(R.id.monthWiseTotalExpenditure);
        monthWiseTotalExpenditure.setText("Total expense : " + monthWiseExpenses.getTotalExpenditure());

        LinearLayout timeMarker = findViewById(R.id.timeMarker);
        timeMarker.removeAllViews();
        int i = 0;
        for (String key : monthWiseExpenses.getSortedKeys()) {
            Map.Entry<String, Expenses> dayWiseExpense = monthWiseExpenses.getDayWiseExpenses(key);
            ExpensesTimeView expensesTimeView = new ExpensesTimeView(getApplicationContext(), null, dayWiseExpense, this, i % 2 == 0);
            timeMarker.addView(expensesTimeView);
            i++;
        }
    }

    private MonthWiseExpenses getDeserializedMonthWiseExpenseslocal(String expenseKey) {
        return getDeserializedMonthWiseExpenses(getLocalStorageForPreferences().getString(expenseKey, "[]"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadTimeLineView(expenseKey);
    }
}
