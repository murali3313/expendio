package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.thriwin.expendio.Utils.saveExpenses;
import static com.thriwin.expendio.Utils.today;

public class NewExpensesCreation extends Activity {

    Button okButton, cancelButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public NewExpensesCreation() {
        this.expenses = new Expenses(new Expense(new BigDecimal(0), today(), new ArrayList<>(), ""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expenses);

        ExpensesEditView expenses = findViewById(R.id.newExpenses);
        expenses.populate(this.expenses, true, false, this);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            ExpenseListener.glowFor = this.expenses.getStorageKey();
            saveExpenses(expenses.getExpenses());
            NewExpensesCreation.this.finish();

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpensesCreation.this.finish();
            }
        });
        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenses.addNewExpense();
            }
        });


    }

    private List<Expense> getDesrializedExpenses(String expensesString) {
        List<Expense> expenses = new ArrayList<>();
        try {
            expenses = obj.readValue(expensesString, new TypeReference<List<Expense>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    private String getSerializedExpenses(List<Expense> expenses) {
        String expensesString = null;
        try {
            expensesString = obj.writeValueAsString(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expensesString;
    }
}
