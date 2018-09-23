package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        EditText defaultExpenseLimit = findViewById(R.id.defaultExpenseLimit);
        defaultExpenseLimit.setText(Utils.getDefaultExpenseLimit().toString());
        findViewById(R.id.acceptDefaultLimit).setOnClickListener(v -> {
            Utils.saveDefaultExpenseLimit(defaultExpenseLimit.getText().toString());
            showToast(ExpenseDefaultLimit.this, R.string.expenseDefaultSuccessfully);
            ExpenseDefaultLimit.this.finish();
        });
        findViewById(R.id.discardExpenseLimit).setOnClickListener(v -> ExpenseDefaultLimit.this.finish());
    }
}
