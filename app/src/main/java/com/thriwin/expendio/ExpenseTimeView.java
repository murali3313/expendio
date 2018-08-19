package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpenseTimeView extends LinearLayout {

    private Expense expense;
    TextView spentOn;
    TextView amount;
    EditText reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private ExpensesTimeView parentView;


    public ExpenseTimeView(Context context, @Nullable AttributeSet attrs, Expense expense, ExpensesTimeView parentView) {
        super(context, attrs);
        this.parentView = parentView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expense_time_view, this);
        this.expense = expense;
        tagsContainer = findViewById(R.id.tags);
        amount = findViewById(R.id.amount);
        for (String tag : expense.getAssociatedExpenseTags()) {
            if (Utils.isEmpty(tag.trim())) {
                continue;
            }
            TextView textView = new TextView(this.getContext(), null);
            textView.setText(tag);
            textView.setPadding(15, 5, 15, 5);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 0, 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(getResources().getColor(R.color.primaryText));
            textView.setBackgroundResource(R.drawable.edit_outline);
            tagsContainer.addView(textView);
        }
        amount.setText(expense.getAmountSpent() + " For ");
    }

}
