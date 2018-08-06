package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
