package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

public class MonthWiseExpenseAdd extends Activity {

    ImageButton okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public MonthWiseExpenseAdd() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_wise_expense_edit);
        Date latestDate = new Date(getIntent().getLongExtra("LatestDate", new Date().getTime()));
        this.expenses = new Expenses();
        this.expenses.add(new Expense(latestDate));

        ExpensesEditView monthWiseExpensesEdit = findViewById(R.id.monthWiseExpensesEdit);
        monthWiseExpensesEdit.populate(expenses, true, true, this, false, null, false);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton =  findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            ExpenseTimelineView.glowFor = expenses.getDateMonth();
            Utils.saveExpenses(monthWiseExpensesEdit.getExpenses());
            MonthWiseExpenseAdd.this.finish();
        });

        cancelButton.setOnClickListener(v -> MonthWiseExpenseAdd.this.finish());
        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthWiseExpensesEdit.addNewExpense();
            }
        });
        ((TextView)findViewById(R.id.monthWiseExpenseHeader)).setText("Expenses for "+ this.expenses.getMonthYearHumanReadable());
        this.findViewById(R.id.container).setBackgroundResource(GeneralActivity.getBackGround(null));
    }


}
