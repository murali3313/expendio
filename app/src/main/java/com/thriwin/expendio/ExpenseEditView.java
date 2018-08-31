package com.thriwin.expendio;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseEditView extends LinearLayout implements PopupMenu.OnMenuItemClickListener {

    private Expense expense;
    TextView spentOn;
    EditText amount;
    AutoCompleteTextView reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private ExpensesEditView parentView;
    private boolean makeDatePermissibleWithinMonthLimit;
    private boolean fromSharedExpenses;
    TextView selectedTextViewTag;


    public ExpenseEditView(Context context, @Nullable AttributeSet attrs, Expense expens, ExpensesEditView parentView, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, boolean isTagEditDisabled, String tagText, boolean fromSharedExpenses) {
        super(context, attrs);
        this.parentView = parentView;
        this.makeDatePermissibleWithinMonthLimit = makeDatePermissibleWithinMonthLimit;
        this.fromSharedExpenses = fromSharedExpenses;
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

        spentOn.setText(expense.getSpentOnDisplayText());
        amount.setText(expense.getAmountSpent().toString());
        reason.setText(expense.getSpentForDisplayText());

        if (isTagEditDisabled) {
            reason.setText(tagText);
            reason.setEnabled(false);
            reason.setBackgroundResource(R.drawable.disabled);
        }
        if (fromSharedExpenses) {
            reason.setEnabled(false);
            amount.setEnabled(false);
            spentOn.setEnabled(false);
            tagsContainer.setClickable(false);
            remove.setVisibility(GONE);
        }
        populateData();
    }

    private void populateData() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, ExpenseTags.getSavedExpenseTags().getWords());

        reason.setThreshold(1);
        //Set the adapter
        reason.setAdapter(adapter);
        ExpenseTags expenseTags = ExpenseTags.getSavedExpenseTags();
        List<String> tags = expenseTags.getTagsOnly();

        for (String tag : expense.getAssociatedExpenseTags()) {
            if (Utils.isEmpty(tag.trim())) {
                continue;
            }
            TextView textView = new TextView(this.getContext(), null);
            textView.setText(tag);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 3;
            textView.setLayoutParams(params);
            textView.setPadding(15, 5, 15, 5);
            textView.setBackgroundResource(R.drawable.edit_outline);
            textView.setTextColor(getResources().getColor(R.color.primaryText));
            tagsContainer.addView(textView);

            if (!fromSharedExpenses) {
                textView.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    for (String tagEntry : tags) {
                        popup.getMenu().add(tagEntry);
                    }
                    selectedTextViewTag = (TextView) v;
                    popup.setOnMenuItemClickListener(ExpenseEditView.this);
                    popup.show();
                });
            }

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
        String amount = this.amount.getText().toString();
        expense.setExpenseStatement(reason.getText().toString());
        expense.setAmountSpent(new BigDecimal(Utils.isEmpty(amount) ? "0" : amount));
        return expense.isValid() ? expense : null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String reasonString = reason.getText().toString();
        String previousString = selectedTextViewTag.getText().toString();
        reasonString = reasonString.replace((item.getTitle().equals("UnCategorized") ? ExpenseTags.MISCELLANEOUS_TAG : previousString), "");
        reasonString = reasonString.trim() + " " + item.getTitle();
        reason.setText(reasonString.trim());
        selectedTextViewTag.setText(item.getTitle());
        return false;
    }
}
