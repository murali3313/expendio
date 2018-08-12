package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.thriwin.expendio.Utils.getSerializedExpenses;

public class DayWiseExpenseEdit extends Activity {

    EditText editText;
    Button okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public DayWiseExpenseEdit() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_wise_expense_edit);

        String dayWiseExpenses = this.getIntent().getStringExtra("DayWiseExpenses");
        this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, false, true,this);

        okButton = findViewById(R.id.acceptedExpense);
        ImageButton addExpense = findViewById(R.id.addExpense);
        cancelButton = (Button) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            Utils.saveDayWiseExpenses(this.expenses.getStorageKey(),this.expenses.getDateMonth(), dayWiseExpensesEdit.getExpenses());

            DayWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> DayWiseExpenseEdit.this.finish());

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayWiseExpensesEdit.addNewExpense();
            }
        });


    }


}
