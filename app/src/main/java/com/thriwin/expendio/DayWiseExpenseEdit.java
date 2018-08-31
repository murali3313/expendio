package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DayWiseExpenseEdit extends Activity {

    EditText editText;
    ImageButton okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public DayWiseExpenseEdit() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_wise_expense_edit);

        String dayWiseExpenses = this.getIntent().getStringExtra("DayWiseExpenses");
        boolean makeDateEditable = this.getIntent().getBooleanExtra("MakeDateEditable", false);
        boolean containsOtherExpenses = this.getIntent().getBooleanExtra("containsOtherExpenses", false);
        findViewById(R.id.noteIfOtherExpenseIncluded).setVisibility(containsOtherExpenses ? View.VISIBLE : View.GONE);
        this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, makeDateEditable, true, this, false, null, false);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            ExpenseTimelineView.glowFor = expenses.getDateMonth();
            Utils.saveDayWiseExpenses(this.expenses.getStorageKey(), this.expenses.getDateMonth(), dayWiseExpensesEdit.getExpenses());

            DayWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> DayWiseExpenseEdit.this.finish());

        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayWiseExpensesEdit.addNewExpense();
            }
        });

        ((TextView) findViewById(R.id.dayWiseExpenseHeader)).setText("Expenses on " + this.expenses.getDateMonthHumanReadable());


    }


}
