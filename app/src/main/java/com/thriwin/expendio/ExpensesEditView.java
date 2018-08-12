package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thriwin.expendio.CommonActivity.setupParent;

public class ExpensesEditView extends LinearLayout {

    private Context context;
    private List<ExpenseEditView> expenseEditViews = new ArrayList<>();
    private Expenses expenses;
    private boolean makeDateEditable;
    private boolean makeDatePermissibleWithinMonthLimit;

    public ExpensesEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void populate(Expenses expenses, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, Activity activity) {
        this.expenses = expenses;
        this.makeDateEditable = makeDateEditable;
        this.makeDatePermissibleWithinMonthLimit = makeDatePermissibleWithinMonthLimit;
        inflate(context, R.layout.expenses_edit, this);
        expenseEditViews = new ArrayList<>();
        for (Expense expens : expenses) {
            addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, expens);
        }

        setupParent(this.getRootView(), activity);


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    private void addExpense(boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, Expense expens) {
        ExpenseEditView expenseEditView = new ExpenseEditView(this.context, null, expens, this, makeDateEditable, makeDatePermissibleWithinMonthLimit);
        expenseEditViews.add(expenseEditView);
        addView(expenseEditView);
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

    public void addNewExpense() {
        addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, new Expense(new Date(expenses.getSpentOnDate())));
    }
}
