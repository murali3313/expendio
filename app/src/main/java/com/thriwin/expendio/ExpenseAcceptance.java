package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.thriwin.expendio.Utils.UNACCEPTED_EXPENSES;
import static com.thriwin.expendio.Utils.isNull;

public class ExpenseAcceptance extends Activity {

    EditText editText;
    Button okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();
    String keyToRemove;

    public ExpenseAcceptance() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_acceptance);

        String unacceptedExpenses = getIntent().getStringExtra("UNACCEPTED_EXPENSES");
        keyToRemove = getIntent().getStringExtra("EXPENSE_KEY_TO_REMOVE");
        keyToRemove = isNull(keyToRemove) ? UNACCEPTED_EXPENSES : keyToRemove;
        try {
            this.expenses = obj.readValue(unacceptedExpenses, new TypeReference<Expenses>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExpensesEditView unapprovedExpenses = findViewById(R.id.unApprovedExpensesViaVoice);
        unapprovedExpenses.populate(expenses, true, false, this, false, null);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);
        notNowButton = findViewById(R.id.notNowExpense);

        okButton.setOnClickListener(v -> {
            ExpenseListener.glowFor = unapprovedExpenses.getExpenses().getStorageKey();
            Utils.saveExpenses(unapprovedExpenses.getExpenses());
            Utils.clearUnAcceptedExpense(this.keyToRemove);
            ExpenseAcceptance.this.finish();
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.clearUnAcceptedExpense(ExpenseAcceptance.this.keyToRemove);
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
