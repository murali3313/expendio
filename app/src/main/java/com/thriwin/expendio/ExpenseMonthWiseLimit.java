package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.thriwin.expendio.Utils.showToast;

public class ExpenseMonthWiseLimit extends Activity {
    ObjectMapper obj = new ObjectMapper();
    public static String EXPENSE_STORAGE_KEY = "EXPENSE_STORAGE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_monthwise_limit_view);
        load();
    }

    public void load() {
        String expenseStorageKey = getIntent().getStringExtra(EXPENSE_STORAGE_KEY);
        EditText expenseLimit = findViewById(R.id.monthWiseExpenseLimit);
        expenseLimit.setText(Utils.getMonthWiseExpenseLimit(expenseStorageKey).toString());
        findViewById(R.id.acceptExpenseLimit).setOnClickListener(v -> {
            Utils.saveExpenseLimit(expenseStorageKey, expenseLimit.getText().toString());
            showToast(ExpenseMonthWiseLimit.this, R.string.expenseDefaultSuccessfully);
            ExpenseMonthWiseLimit.this.finish();
        });
        findViewById(R.id.discardExpenseLimit).setOnClickListener(v -> ExpenseMonthWiseLimit.this.finish());
    }
}
