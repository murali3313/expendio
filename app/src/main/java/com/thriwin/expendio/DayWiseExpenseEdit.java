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
        this.findViewById(R.id.container).setBackgroundResource(GeneralActivity.getBackGround(null));

        String dayWiseExpenses = this.getIntent().getStringExtra("DayWiseExpenses");
        boolean makeDateEditable = this.getIntent().getBooleanExtra("MakeDateEditable", false);
        boolean containsOtherExpenses = this.getIntent().getBooleanExtra("containsOtherExpenses", false);
        findViewById(R.id.noteIfOtherExpenseIncluded).setVisibility(containsOtherExpenses ? View.VISIBLE : View.GONE);
            this.expenses = Utils.getDeserializedExpenses(dayWiseExpenses);

        ExpensesEditView dayWiseExpensesEdit =(ExpensesEditView) findViewById(R.id.dayWiseExpensesEdit);
        dayWiseExpensesEdit.populate(expenses, makeDateEditable, true, this, false, null, false);

        okButton =(ImageButton) findViewById(R.id.acceptedExpense);
        cancelButton = (ImageButton) findViewById(R.id.discardExpenses);

        okButton.setOnClickListener(v -> {
            ExpenseTimelineView.glowFor = expenses.getDateMonth();
            Handler handler=new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    DayWiseExpenseEdit.this.finish();
                    return true;
                }
            });
            SaveExpenseDayWiseLoader saveExpenseDayWiseLoader=new SaveExpenseDayWiseLoader(handler,this.expenses.getStorageKey(), this.expenses.getDateMonth(), dayWiseExpensesEdit.getExpenses());
            saveExpenseDayWiseLoader.start();

        });

        cancelButton.setOnClickListener(v -> DayWiseExpenseEdit.this.finish());

        ImageButton addExpense = (ImageButton) findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayWiseExpensesEdit.addNewExpense();
            }
        });

        ((TextView) findViewById(R.id.dayWiseExpenseHeader)).setText("Expenses on " + this.expenses.getDateMonthHumanReadable());


    }


}
