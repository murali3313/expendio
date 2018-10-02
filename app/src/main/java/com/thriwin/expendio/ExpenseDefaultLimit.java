package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.thriwin.expendio.Utils.showToast;

public class ExpenseDefaultLimit extends Activity {
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_expense_limit_view);
        this.findViewById(R.id.container).setBackgroundResource(GeneralActivity.getBackGround(null));
        load();
    }

    public void load() {
        EditText defaultExpenseLimit = (EditText) findViewById(R.id.defaultExpenseLimit);
        defaultExpenseLimit.setText(Utils.getDefaultExpenseLimit().toString());
        findViewById(R.id.acceptDefaultLimit).setOnClickListener(v -> {
            Utils.saveDefaultExpenseLimit(defaultExpenseLimit.getText().toString());
            showToast(ExpenseDefaultLimit.this, R.string.expenseDefaultSuccessfully);
            Utils.markSettingsForSyncing(true);

            ExpenseDefaultLimit.this.finish();
        });
        findViewById(R.id.discardExpenseLimit).setOnClickListener(v -> ExpenseDefaultLimit.this.finish());
    }
}
