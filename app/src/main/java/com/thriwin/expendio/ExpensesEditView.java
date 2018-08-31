package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thriwin.expendio.CommonActivity.setupParent;
import static com.thriwin.expendio.Utils.isNull;

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

    public void populate(Expenses expenses, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, Activity activity, boolean isTagEditDisabled, String tagText, boolean fromSharedExpenses) {
        this.expenses = isNull(expenses) ? new Expenses() : expenses;
        this.makeDateEditable = makeDateEditable;
        this.makeDatePermissibleWithinMonthLimit = makeDatePermissibleWithinMonthLimit;
        inflate(context, R.layout.expenses_edit, this);
        expenseEditViews = new ArrayList<>();
        for (Expense expens : this.expenses) {
            addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, expens, isTagEditDisabled, tagText,fromSharedExpenses);
        }
        setupParent(this.getRootView(), activity);
    }

    private void addExpense(boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, Expense expens, boolean isTagEditDisabled, String tagText, boolean fromSharedExpenses) {
        ExpenseEditView expenseEditView = new ExpenseEditView(this.context, null, expens, this, makeDateEditable, makeDatePermissibleWithinMonthLimit, isTagEditDisabled, tagText,fromSharedExpenses);
        expenseEditViews.add(expenseEditView);
        addView(expenseEditView, 0);
    }

    public Expenses getExpenses() {
        Expenses expenses = new Expenses();
        for (ExpenseEditView expenseEditView : expenseEditViews) {
            Expense editedExpense = expenseEditView.getEditedExpense();
            if (!isNull(editedExpense))
                expenses.add(editedExpense);
        }
        return expenses;
    }

    public void removeExpenseView(View view) {
        this.expenseEditViews.remove(view);
    }

    public void addNewExpense() {
        addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, new Expense(new Date(expenses.getSpentOnDate())), false, null, false);
    }

    public void addNewExpense(boolean isTagEditDisabled, String tagText) {
        addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, new Expense(new Date(expenses.getSpentOnDate())), isTagEditDisabled, tagText, false);
    }
}
