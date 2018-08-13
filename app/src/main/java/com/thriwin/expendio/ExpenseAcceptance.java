package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExpenseAcceptance extends Activity {

    EditText editText;
    Button okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public ExpenseAcceptance() {
        this.expenses = Utils.getUnAcceptedExpenses();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_acceptance);

        ExpensesEditView unapprovedExpenses = findViewById(R.id.unApprovedExpensesViaVoice);
        unapprovedExpenses.populate(expenses, true, false, this, false, null);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);
        notNowButton = findViewById(R.id.notNowExpense);

        okButton.setOnClickListener(v -> {
            ExpenseListener.glowFor = unapprovedExpenses.getExpenses().getStorageKey();
            Utils.saveExpenses(unapprovedExpenses.getExpenses());
            Utils.clearUnAcceptedExpense();
            ExpenseAcceptance.this.finish();
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseAcceptance.this.finish();
            }
        });

        notNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseAcceptance.this.finish();
            }
        });

        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unapprovedExpenses.addNewExpense();
            }
        });

    }


}
