package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import static java.lang.String.format;

public abstract class UnAcceptedExpensesBaseView extends LinearLayout {
    UnAcceptedExpensesBaseView inflatedView;

    public UnAcceptedExpensesBaseView(Context context, @Nullable AttributeSet attrs, Expenses unAcceptedExpenses, int expensesHeader) {
        super(context, attrs);
        inflatedView = (UnAcceptedExpensesBaseView) inflate(context, R.layout.un_accepted_expenses_view, this);
        TextView header = inflatedView.findViewById(R.id.unApprovedExpense);
        header.setText(expensesHeader);

        TextView count = inflatedView.findViewById(R.id.totalExpensesUnApproved);
        count.setText(format("Total unapproved expenses: %d.",unAcceptedExpenses.size()));

        setClickAction();

    }

    protected abstract void setClickAction() ;
}
