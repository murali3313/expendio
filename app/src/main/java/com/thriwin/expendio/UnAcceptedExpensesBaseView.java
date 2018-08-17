package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.String.format;

public class UnAcceptedExpensesBaseView extends LinearLayout {
    UnAcceptedExpensesBaseView inflatedView;
    private Expenses unAcceptedExpenses;
    private String key;

    public UnAcceptedExpensesBaseView(Context context, @Nullable AttributeSet attrs, Expenses unAcceptedExpenses, String expensesHeader, String key) {
        super(context, attrs);
        inflatedView = (UnAcceptedExpensesBaseView) inflate(context, R.layout.un_accepted_expenses_view, this);
        this.unAcceptedExpenses = unAcceptedExpenses;
        this.key = key;
        TextView header = inflatedView.findViewById(R.id.unApprovedExpense);
        header.setText(expensesHeader);

        TextView count = inflatedView.findViewById(R.id.totalExpensesUnApproved);
        count.setText(format("Total unapproved expenses: %d.",unAcceptedExpenses.size()));

        setClickAction();

    }

    protected void setClickAction() {
        inflatedView.setOnClickListener(v -> {
            ObjectMapper objectMapper = new ObjectMapper();
            Intent i = new Intent(getContext(), ExpenseAcceptance.class);
            try {
                i.putExtra("UNACCEPTED_EXPENSES",objectMapper.writeValueAsString(unAcceptedExpenses));
                i.putExtra("EXPENSE_KEY_TO_REMOVE",key);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            ContextCompat.startActivity(getContext(), i, null);
        });
    }
}
