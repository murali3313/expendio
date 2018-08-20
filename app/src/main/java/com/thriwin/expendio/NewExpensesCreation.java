package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thriwin.expendio.Utils.isNull;
import static com.thriwin.expendio.Utils.saveExpenses;
import static com.thriwin.expendio.Utils.today;

public class NewExpensesCreation extends Activity {

    ImageButton okButton, cancelButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expenses);

        String selectedMonthKey = getIntent().getStringExtra("SELECTED_STORAGE_KEY");
        Date selectedDate = today();
        if (!isNull(selectedMonthKey)) {
            selectedDate= new Date(Utils.getDeserializedMonthWiseExpenses(selectedMonthKey).getLatestDate(selectedMonthKey));
        }
        this.expenses = new Expenses(new Expense(new BigDecimal(0), selectedDate, new ArrayList<>(), ""));
        ExpensesEditView expenses = findViewById(R.id.newExpenses);
        expenses.populate(this.expenses, true, false, this, false, null);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            HomeScreenActivity.glowFor = this.expenses.getStorageKey();
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

        ((TextView)findViewById(R.id.newExpensesHeader)).setText("New Expenses");
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
