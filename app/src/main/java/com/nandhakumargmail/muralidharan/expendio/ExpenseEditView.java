package com.nandhakumargmail.muralidharan.expendio;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static com.nandhakumargmail.muralidharan.expendio.Utils.isEmpty;
import static java.util.Arrays.asList;

public class ExpenseEditView extends LinearLayout {

    private Expense expense;
    TextView spentOn;
    EditText amount;
    EditText reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private ExpensesEditView parentView;
    private boolean makeDatePermissibleWithinMonthLimit;


    public ExpenseEditView(Context context, @Nullable AttributeSet attrs, Expense expens, ExpensesEditView parentView, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit) {
        super(context, attrs);
        this.parentView = parentView;
        this.makeDatePermissibleWithinMonthLimit = makeDatePermissibleWithinMonthLimit;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expense_edit, this);
        this.expense = expens;
        spentOn = findViewById(R.id.spentOn);
        spentOn.setEnabled(makeDateEditable);
        amount = findViewById(R.id.amount);
        reason = findViewById(R.id.reason);
        remove = findViewById(R.id.remove);
        tagsContainer = findViewById(R.id.tags);


        populateData();
    }

    private void populateData() {
        spentOn.setText(expense.getSpentOnDisplayText());
        amount.setText(expense.getAmountSpent().toString());
        reason.setText(expense.getSpentForDisplayText());

        for (String tag : expense.getAssociatedExpenseTags()) {
            if (isEmpty(tag.trim())) {
                continue;
            }
            TextView textView = new TextView(this.getContext(), null);
            textView.setText(tag);
            textView.setPadding(15, 5, 15, 5);
            textView.setBackgroundResource(R.drawable.tag_border);
            tagsContainer.addView(textView);
        }

        spentOn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                expense.setSpentOnBy(year, month, dayOfMonth);
                spentOn.setText(expense.getSpentOnDisplayText());
            }, expense.spentYear(), expense.spentMonth(), expense.spentDay());
            if (makeDatePermissibleWithinMonthLimit) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(expense.getStartDate());
                datePicker.setMaxDate(expense.getEndDate());
            }
            datePickerDialog.show();
        });


        remove.setOnClickListener(v -> {
            parentView.removeView(this);
            parentView.removeExpenseView(this);
        });
    }


    public Expense getEditedExpense() {
        String[] words = Utils.splitStatementBy(reason.getText().toString(), " ");
        expense.setSpentFor(new ArrayList<>(asList(words)));
        String amount = this.amount.getText().toString();
        expense.setExpenseStatement(reason.getText().toString());
        expense.setAmountSpent(new BigDecimal(isEmpty(amount) ? "0" : amount));
        return expense;
    }
}
