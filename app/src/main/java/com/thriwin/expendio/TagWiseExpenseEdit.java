package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TagWiseExpenseEdit extends Activity {

    EditText editText;
    Button okButton, cancelButton, notNowButton;
    Expenses expenses;
    ObjectMapper obj = new ObjectMapper();

    public TagWiseExpenseEdit() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_wise_expense_edit);

        String dayWiseExpenses = this.getIntent().getStringExtra("TagWiseExpenses");
        String tagKey = this.getIntent().getStringExtra("TagKey");
        boolean makeDateEditable = this.getIntent().getBooleanExtra("MakeDateEditable", false);
        this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, makeDateEditable, true, this, true, tagKey);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = (Button) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            ExpenseTimelineView.glowFor = expenses.getDateMonth();
            Utils.saveTagWiseExpenses(this.expenses.getStorageKey(), tagKey, dayWiseExpensesEdit.getExpenses());

            TagWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> TagWiseExpenseEdit.this.finish());

        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(v -> dayWiseExpensesEdit.addNewExpense(true, tagKey));

        ((TextView)findViewById(R.id.dayWiseExpenseHeader)).setText("Expenses for "+ this.expenses.getFirstAssociatedTag());
    }


}
