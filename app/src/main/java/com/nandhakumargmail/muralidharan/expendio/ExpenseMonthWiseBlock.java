package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class ExpenseMonthWiseBlock extends LinearLayout {
    ObjectMapper obj = new ObjectMapper();

    public ExpenseMonthWiseBlock(Context context, @Nullable AttributeSet attrs, Map.Entry<String,
            List<Expense>> expensesBlock, HomeScreenView homeScreenView) {
        super(context, attrs);
        inflate(context, R.layout.expense_month_block, this);
        TextView blockName = findViewById(R.id.expenseBlockName);
        blockName.setText(expensesBlock.getKey().replace("Expense-", ""));
        blockName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExpenseTimelineView.class);
                i.putExtra("ExpenseKey", expensesBlock.getKey());
                ContextCompat.startActivity(context, i, null);
                homeScreenView.load();
            }
        });
    }

}
