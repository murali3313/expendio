package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
        this.findViewById(R.id.container).setBackgroundResource(GeneralActivity.getBackGround(null));
        String dayWiseExpenses = this.getIntent().getStringExtra("TagWiseExpenses");
        String tagKey = this.getIntent().getStringExtra("TagKey");
        boolean makeDateEditable = this.getIntent().getBooleanExtra("MakeDateEditable", false);
        boolean containsOtherExpenses = this.getIntent().getBooleanExtra("containsOtherExpenses", false);
        findViewById(R.id.noteIfOtherExpenseIncluded).setVisibility(containsOtherExpenses ? View.VISIBLE : View.GONE);
        this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit = (ExpensesEditView) findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, makeDateEditable, true, this, true, tagKey, false);

        okButton = (ImageButton) findViewById(R.id.acceptedExpense);
        cancelButton = (ImageButton) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            Expenses expenses = dayWiseExpensesEdit.getExpenses();
            ExpenseTimelineView.glowFor = expenses.getDateMonth();

            Handler handler=new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    TagWiseExpenseEdit.this.finish();
                    return true;
                }
            });

            TagWiseExpenseSaver tagWiseExpenseSaver=new TagWiseExpenseSaver( handler,this.expenses.getStorageKey(), tagKey, expenses);
            tagWiseExpenseSaver.start();
        });

        cancelButton.setOnClickListener(v -> TagWiseExpenseEdit.this.finish());

        ImageButton addExpense = (ImageButton) findViewById(R.id.addExpense);
        addExpense.setOnClickListener(v -> dayWiseExpensesEdit.addNewExpense(true, tagKey));

        String headerText = "Expenses for " + tagKey;
        int maxAllowedLengthInHeader = 22;
        Integer tillText = headerText.length() > maxAllowedLengthInHeader ? maxAllowedLengthInHeader : headerText.length();
        String appendingText = tillText == maxAllowedLengthInHeader ? "..." : "";
        ((TextView) findViewById(R.id.dayWiseExpenseHeader)).setText(headerText.substring(0, tillText) + appendingText);
    }


}
