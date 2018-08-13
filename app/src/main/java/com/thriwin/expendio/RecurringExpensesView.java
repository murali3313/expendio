package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.thriwin.expendio.Utils.getReadableMonthAndYear;
import static com.thriwin.expendio.Utils.showToast;

public class RecurringExpensesView extends Activity {
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recurring_expenses_view);
        load();
    }

    public void load() {
        Utils.getAllRecurringExpenses();
        ImageButton addRecurringExpenseButton = findViewById(R.id.addRecurringExpenses);
        addRecurringExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout recurringExpensesContainer = findViewById(R.id.recurringExpenses);
                RecurringExpenseView recurringExpenseView = new RecurringExpenseView(getApplicationContext(), null,RecurringExpensesView.this);
                recurringExpensesContainer.addView(recurringExpenseView);
            }
        });
    }
}
