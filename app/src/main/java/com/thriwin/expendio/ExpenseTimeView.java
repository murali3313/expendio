package com.thriwin.expendio;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

public class ExpenseTimeView extends LinearLayout {

    private Expense expense;
    TextView spentOn;
    TextView amount;
    TextView user;
    EditText reason;
    ImageButton remove;
    FlowLayout tagsContainer;
    private ExpensesTimeView parentView;
    ImageButton cashTransaction;
    ImageButton cardTransaction;


    public ExpenseTimeView(Context context, @Nullable AttributeSet attrs, Expense expense, ExpensesTimeView parentView, int colourIndex) {
        super(context, attrs);
        this.parentView = parentView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expense_time_view, this);
        this.expense = expense;
        tagsContainer = (FlowLayout) findViewById(R.id.flowContainer);
        amount = (TextView) findViewById(R.id.amount);
        user = (TextView) findViewById(R.id.userName);
        cashTransaction = (ImageButton) findViewById(R.id.cashTransaction);
        cardTransaction = (ImageButton) findViewById(R.id.cardTransaction);


        cashTransaction.setVisibility(expense.isCashTransaction() ? VISIBLE : GONE);
        cardTransaction.setVisibility(expense.isCashTransaction() ? GONE : VISIBLE);


        for (String tag : expense.getAssociatedExpenseTags()) {
            if (Utils.isEmpty(tag.trim())) {
                continue;
            }

            TextView textView = new TextView(this.getContext(), null);
            textView.setText(tag);
            textView.setPadding(15, 5, 15, 5);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(7, 7, 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.edit_outline);
            tagsContainer.addView(textView);
        }
        amount.setText(expense.getAmountSpent() + " ");
    }

    public ExpenseTimeView(Context context, @Nullable AttributeSet attrs, Expense expense, ExpensesTimeView parentView, String username, Integer colourIndex) {
        this(context, attrs, expense, parentView, colourIndex);
        user.setText(username);
        user.setVisibility(VISIBLE);
    }
}
