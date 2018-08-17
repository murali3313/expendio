package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.math.BigDecimal;

public class RecurringExpenseEditView extends LinearLayout {

    private RecurringExpensesView parentView;
    private RecurringExpenseView intermediateView;
    AutoCompleteTextView reason;
    EditText amount;

    public RecurringExpenseEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.recurrence_expense_edit, this);
        amount = findViewById(R.id.amount);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ExpenseTags.getSavedExpenseTags().getWords());
        reason = findViewById(R.id.reason);
        reason.setThreshold(1);
        reason.setAdapter(adapter);

        findViewById(R.id.remove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.removeChild(intermediateView);
            }
        });


    }

    public void setParent(RecurringExpensesView recurringExpensesView, RecurringExpenseView intermediateParent) {
        this.parentView = recurringExpensesView;
        this.intermediateView = intermediateParent;
    }

    public RecurringExpense getRecurringExpense() {
        RecurringExpense recurringExpense = null;
        boolean isAmountNotEmpty = !Utils.isEmpty(amount.getText().toString().trim());
        boolean isReasonNotEmpty = !Utils.isEmpty(reason.getText().toString().trim());
        if (isAmountNotEmpty && isReasonNotEmpty) {
            recurringExpense = new RecurringExpense(new BigDecimal(amount.getText().toString()), reason.getText().toString());
        }
        return recurringExpense;
    }

    public void populate(RecurringExpense recurringExpense) {
        amount.setText(recurringExpense.getAmount().toString());
        reason.setText(recurringExpense.getReason());
    }


}
