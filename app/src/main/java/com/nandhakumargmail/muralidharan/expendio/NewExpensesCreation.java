package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.nandhakumargmail.muralidharan.expendio.Utils.saveExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.today;
import static java.util.Arrays.asList;

public class NewExpensesCreation extends Activity {

    Button okButton, cancelButton;
    List<Expense> expenses;
    ObjectMapper obj = new ObjectMapper();

    public NewExpensesCreation() {
        this.expenses = asList(new Expense(new BigDecimal(0), today(), new ArrayList<>(), ""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expenses);

        ExpensesEditView expenses = findViewById(R.id.newExpenses);
        expenses.populate(this.expenses, true, false);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            saveExpenses(expenses.getExpenses());
            NewExpensesCreation.this.finish();
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpensesCreation.this.finish();
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
