package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nandhakumargmail.muralidharan.expendio.Utils.UNACCEPTED_EXPENSES;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getDeserializedExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getSerializedExpenses;

public class ExpenseAcceptance extends Activity {

    EditText editText;
    Button okButton, cancelButton, notNowButton;
    List<Expense> expenses;
    ObjectMapper obj = new ObjectMapper();

    public ExpenseAcceptance() {

        String unAcceptedExpenses = Utils.getLocalStorageForPreferences()
                .getString(UNACCEPTED_EXPENSES, "[]");
        this.expenses = getDeserializedExpenses(unAcceptedExpenses);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_acceptance);

        ExpensesEditView unapprovedExpenses = findViewById(R.id.unApprovedExpenses);
        unapprovedExpenses.populate(expenses);

        okButton = (Button) findViewById(R.id.acceptedExpense);
        cancelButton = (Button) findViewById(R.id.discardExpenses);
        notNowButton = (Button) findViewById(R.id.notNowExpense);

        okButton.setOnClickListener(v -> {
            SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
            SharedPreferences.Editor edit = localStorageForPreferences.edit();
            for (Expense expens : unapprovedExpenses.getExpenses()) {
                List<Expense> storedExpenses = getDeserializedExpenses(localStorageForPreferences.getString(expens.getStorageKey(), "[]"));
                storedExpenses.add(expens);
                edit.putString(expens.getStorageKey(), getSerializedExpenses(storedExpenses));
            }
            edit.apply();
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

    }


}
