package com.thriwin.expendio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ExpenseEditViewLoader extends Thread {

    private Context context;
    private ExpensesEditView parent;
    private Expense expens;
    private boolean makeDateEditable;
    private boolean makeDatePermissibleWithinMonthLimit;
    private boolean isTagEditDisabled;
    private String tagText;
    private boolean fromSharedExpenses;
    private Handler handler;

    public ExpenseEditViewLoader(Context context, ExpensesEditView parent, Expense expens, boolean makeDateEditable, boolean makeDatePermissibleWithinMonthLimit, boolean isTagEditDisabled, String tagText, boolean fromSharedExpenses, Handler handler) {
        this.context = context;
        this.parent = parent;
        this.expens = expens;
        this.makeDateEditable = makeDateEditable;
        this.makeDatePermissibleWithinMonthLimit = makeDatePermissibleWithinMonthLimit;
        this.isTagEditDisabled = isTagEditDisabled;
        this.tagText = tagText;
        this.fromSharedExpenses = fromSharedExpenses;

        this.handler = handler;
    }

    @Override
    public void run() {
        ExpenseEditView expenseEditView = new ExpenseEditView(context, null, expens, parent, makeDateEditable, makeDatePermissibleWithinMonthLimit, isTagEditDisabled, tagText, fromSharedExpenses);

        Message msg = new Message();
        msg.obj = expenseEditView;
        this.handler.sendMessage(msg);
    }
}
