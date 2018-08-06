package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ExpensesEditView extends LinearLayout {

    private Context context;
    private List<ExpenseEditView> expenseEditViews = new ArrayList<>();

    public ExpensesEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void populate(List<Expense> expenses, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit) {
        inflate(context, R.layout.expenses_edit, null);
        expenseEditViews = new ArrayList<>();
        for (Expense expens : expenses) {
            ExpenseEditView expenseEditView = new ExpenseEditView(this.context, null, expens, this,makeDateEditable, makeDatePermissibleWithinMonthLimit);
            expenseEditViews.add(expenseEditView);
            addView(expenseEditView);
        }
    }

    public Expenses getExpenses() {
        Expenses expenses = new Expenses();
        for (ExpenseEditView expenseEditView : expenseEditViews) {
            expenses.add(expenseEditView.getEditedExpense());
        }
        return expenses;
    }

    public void removeExpenseView(View view) {
        this.expenseEditViews.remove(view);
    }
}
