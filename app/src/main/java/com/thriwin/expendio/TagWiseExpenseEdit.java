package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TagWiseExpenseEdit extends Activity {

    EditText editText;
    ImageButton okButton, cancelButton, notNowButton;
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
        boolean containsOtherExpenses = this.getIntent().getBooleanExtra("containsOtherExpenses", false);
        findViewById(R.id.noteIfOtherExpenseIncluded).setVisibility(containsOtherExpenses ? View.VISIBLE : View.GONE);
        this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, makeDateEditable, true, this, true, tagKey, false);

        okButton = findViewById(R.id.acceptedExpense);
        cancelButton = findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            Expenses expenses = dayWiseExpensesEdit.getExpenses();
            ExpenseTimelineView.glowFor = expenses.getDateMonth();
            Utils.saveTagWiseExpenses(this.expenses.getStorageKey(), tagKey, expenses);

            TagWiseExpenseEdit.this.finish();
        });

        cancelButton.setOnClickListener(v -> TagWiseExpenseEdit.this.finish());

        ImageButton addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(v -> dayWiseExpensesEdit.addNewExpense(true, tagKey));

        String headerText = "Expenses for " + tagKey;
        int maxAllowedLengthInHeader = 22;
        Integer tillText = headerText.length() > maxAllowedLengthInHeader ? maxAllowedLengthInHeader : headerText.length();
        String appendingText = tillText == maxAllowedLengthInHeader ? "..." : "";
        ((TextView) findViewById(R.id.dayWiseExpenseHeader)).setText(headerText.substring(0, tillText) + appendingText);
    }


}
