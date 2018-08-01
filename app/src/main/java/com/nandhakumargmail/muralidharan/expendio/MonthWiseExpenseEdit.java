package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

import static com.nandhakumargmail.muralidharan.expendio.Utils.getDeserializedExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.saveExpenses;

public class MonthWiseExpenseEdit extends Activity {

    Button okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public MonthWiseExpenseEdit() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_wise_expense_edit);
        Date latestDate = new Date(getIntent().getLongExtra("LatestDate",new Date().getTime()));
        this.expenses = new Expenses();
        this.expenses.add(new Expense(latestDate));

        ExpensesEditView monthWiseExpensesEdit = findViewById(R.id.monthWiseExpensesEdit);
        monthWiseExpensesEdit.populate(expenses, true, true);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = (Button) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            saveExpenses(monthWiseExpensesEdit.getExpenses());
            MonthWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> MonthWiseExpenseEdit.this.finish());


    }


}
