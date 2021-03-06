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
import android.widget.TextView;

import java.math.BigDecimal;

public class RecurringExpenseEditView extends LinearLayout {

    private RecurringExpensesView parentView;
    private RecurringExpenseView intermediateView;
    AutoCompleteTextView reason;
    EditText amount;
    LinearLayout cashTransaction;
    LinearLayout cardTransaction;
    TextView transactionTypeSelected;
    TransactionType selectedTransaction = TransactionType.CASH;

    public RecurringExpenseEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.recurrence_expense_edit, this);
        amount = (EditText) findViewById(R.id.amount);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ExpenseTags.getSavedExpenseTags().getWords());
        reason = (AutoCompleteTextView) findViewById(R.id.reason);
        reason.setThreshold(1);
        reason.setAdapter(adapter);

        findViewById(R.id.remove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.removeChild(intermediateView);
            }
        });

        cashTransaction = (LinearLayout) findViewById(R.id.cashTransaction);
        cardTransaction = (LinearLayout) findViewById(R.id.cardTransaction);
        transactionTypeSelected = (TextView) findViewById(R.id.transactionType);

        cashTransaction.getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTransaction = TransactionType.CASH;
                loadTransactionType();
            }
        });
        cardTransaction.getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTransaction = TransactionType.DIGITAL;
                loadTransactionType();
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
            recurringExpense.setTransactionType(selectedTransaction);
        }
        return recurringExpense;
    }

    public void populate(RecurringExpense recurringExpense) {
        String amount = recurringExpense.getAmount().toString();
        amount = amount.equals("0") ? "" : amount;
        this.amount.setText(amount);
        reason.setText(recurringExpense.getReason());
        selectedTransaction = recurringExpense.getTransactionType();
        loadTransactionType();
    }

    private void loadTransactionType() {
        if (selectedTransaction.equals(TransactionType.CASH)) {
            cashTransaction.setBackgroundResource(R.drawable.transaction_border_selected);
            cardTransaction.setBackgroundResource(R.drawable.transaction_border);
        } else {
            cashTransaction.setBackgroundResource(R.drawable.transaction_border);
            cardTransaction.setBackgroundResource(R.drawable.transaction_border_selected);
        }

        transactionTypeSelected.setText(selectedTransaction.toString());
    }


}
