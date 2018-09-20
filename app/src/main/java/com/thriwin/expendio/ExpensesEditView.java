package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
    ArrayList<Long> date = new ArrayList<>();
    ArrayList<Long> dateForOthers = new ArrayList<>();

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
        for (Expense expens : this.expenses.sortByYou()) {
            addExpense(makeDateEditable, makeDatePermissibleWithinMonthLimit, expens, isTagEditDisabled, tagText, fromSharedExpenses);
        }
        setupParent(this.getRootView(), activity);
    }

    private void addExpense(boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, Expense expens, boolean isTagEditDisabled, String tagText, boolean fromSharedExpenses) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                ExpenseEditView expenseEditView = (ExpenseEditView) msg.obj;
                expenseEditViews.add(expenseEditView);
                expenseEditView.setReasonAdapter();

                addView(expenseEditView, getIndex(expenseEditView.spentDate(), expens));
                ExpensesEditView.this.getChildAt(0).requestFocus();
                return true;
            }
        });

        ExpenseEditViewLoader expenseEditViewLoader = new ExpenseEditViewLoader(context, this, expens, makeDateEditable, makeDatePermissibleWithinMonthLimit, isTagEditDisabled, tagText, fromSharedExpenses, handler);
        expenseEditViewLoader.start();

    }

    private int getIndex(long l, Expense expens) {
        int size = date.size();
        if (expens.spentbyOthers()) {
            dateForOthers.add(l);
            return date.size() + dateForOthers.size() - 1;
        }
        for (int i = 0; i < size; i++) {
            Long time = date.get(i);
            if (l > time) {
                date.add(i, l);
                return i;
            }
            if (l == time) {
                date.add(i + 1, l);
                return i + 1;
            }

        }
        date.add(l);
        return date.size() - 1;
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
