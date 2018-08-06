package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.String.format;

public class UnAcceptedExpensesAudioView extends UnAcceptedExpensesBaseView {

    public UnAcceptedExpensesAudioView(Context context, @Nullable AttributeSet attrs, Expenses unAcceptedExpenses, int expensesHeader) {
        super(context, attrs, unAcceptedExpenses, expensesHeader);
    }

    @Override
    protected void setClickAction() {
        inflatedView.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ExpenseAcceptance.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            ContextCompat.startActivity(getContext(), i, null);
        });
    }


}
