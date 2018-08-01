package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.nandhakumargmail.muralidharan.expendio.Utils.getDeserializedExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getSerializedExpenses;
import static com.nandhakumargmail.muralidharan.expendio.Utils.saveDayWiseExpenses;

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
        this.expenses = getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, false, true);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = (Button) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            saveDayWiseExpenses(this.expenses.getStorageKey(),this.expenses.getDateMonth(), dayWiseExpensesEdit.getExpenses());

            DayWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> DayWiseExpenseEdit.this.finish());


    }


}
