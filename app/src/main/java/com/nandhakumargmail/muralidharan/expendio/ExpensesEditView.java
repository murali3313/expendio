package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ExpensesEditView extends LinearLayout {

    private Context context;
    private List<ExpenseEditView> expenseEditViews = new ArrayList<>();

    public ExpensesEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void populate(List<Expense> expenses) {
        inflate(context, R.layout.expenses_edit, null);
        expenseEditViews = new ArrayList<>();
        for (Expense expens : expenses) {
            ExpenseEditView expenseEditView = new ExpenseEditView(this.context, null, expens, this);
            expenseEditViews.add(expenseEditView);
            addView(expenseEditView);
        }
    }

    public List<Expense> getExpenses() {
        List<Expense> expenses = new ArrayList<>();
        for (ExpenseEditView expenseEditView : expenseEditViews) {
            expenses.add(expenseEditView.getEditedExpense());
        }
        return expenses;
    }

    public void removeExpenseView(View view) {
        this.expenseEditViews.remove(view);
    }
}
