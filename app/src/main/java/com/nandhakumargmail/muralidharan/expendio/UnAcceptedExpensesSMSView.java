package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class UnAcceptedExpensesSMSView extends UnAcceptedExpensesBaseView {
    UnAcceptedExpensesSMSView inflatedView;

    public UnAcceptedExpensesSMSView(Context context, @Nullable AttributeSet attrs, Expenses unAcceptedExpenses, int expensesHeader) {
        super(context, attrs, unAcceptedExpenses, expensesHeader);
    }

    @Override
    protected void setClickAction() {
        inflatedView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
