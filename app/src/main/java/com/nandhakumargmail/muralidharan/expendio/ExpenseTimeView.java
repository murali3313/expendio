package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nandhakumargmail.muralidharan.expendio.Utils.isEmpty;

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
            if (isEmpty(tag.trim())) {
                continue;
            }
            TextView textView = new TextView(this.getContext(), null);
            textView.setText(tag);
            textView.setPadding(15, 5, 15, 5);
            textView.setTextColor(getResources().getColor(R.color.primaryLight));
            textView.setBackgroundResource(R.drawable.tag_border);
            tagsContainer.addView(textView);
        }
        amount.setText(expense.getAmountSpent() + " For ");
    }

}
