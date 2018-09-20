package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import java.util.Set;

class ExpenseRestoreSaveLoader extends Thread {
    private Expenses processPastedExpenses;
    private Handler handler;

    public ExpenseRestoreSaveLoader(Expenses processPastedExpenses, Handler handler) {
        this.processPastedExpenses = processPastedExpenses;
        this.handler = handler;
    }

    @Override
    public void run() {
        Set<String> uniqueStorageExpenseKeys = processPastedExpenses.getUniqueStorageExpenseKeys();
        for (String uniqueStorageExpenseKey : uniqueStorageExpenseKeys) {
            Utils.deleteAMonthExpense(uniqueStorageExpenseKey);
        }

        Utils.saveExpenses(processPastedExpenses);
        handler.sendMessage(new Message());
    }
}
