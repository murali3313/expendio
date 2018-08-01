package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.util.List;
import java.util.Map;

public class ExpensesTimeView extends LinearLayout {

    private List<Expense> expense;
    TextView spentOn;
    EditText amount;
    EditText reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private ExpenseTimelineView parentView;


    public ExpensesTimeView(Context context, @Nullable AttributeSet attrs, Map.Entry<String, Expenses> expenses, ExpenseTimelineView parentView) {
        super(context, attrs);
        this.parentView = parentView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expenses_time_view, this);

        TextView totalExpenseView = findViewById(R.id.totalExpenseDayWise);
        totalExpenseView.setText(expenses.getValue().getTotalExpenditure());

        this.setOnClickListener(v -> {
            Intent i = new Intent(context, DayWiseExpenseEdit.class);
            i.putExtra("DayWiseExpenses", Utils.getSerializedExpenses(expenses.getValue()));
            ContextCompat.startActivity(context, i, null);
        });

        TextView dateWiseRepresentation = findViewById(R.id.dateWiseString);
        dateWiseRepresentation.setText(expenses.getValue().getDateMonthHumanReadable());
        this.expense = expenses.getValue();
        FlowLayout expensesPerDay = findViewById(R.id.expensesPerDay);
        for (Expense expens : this.expense) {
            ExpenseTimeView expenseTimeView = new ExpenseTimeView(context, null, expens, this);
            expensesPerDay.addView(expenseTimeView);
        }
    }

}
