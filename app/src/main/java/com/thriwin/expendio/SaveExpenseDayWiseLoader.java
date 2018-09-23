package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

class SaveExpenseDayWiseLoader extends Thread {

    private Handler handler;
    private String storageKey;
    private String dateMonth;
    private Expenses expenses;

    public SaveExpenseDayWiseLoader(Handler handler, String storageKey, String dateMonth, Expenses expenses) {
        this.handler = handler;

        this.storageKey = storageKey;
        this.dateMonth = dateMonth;
        this.expenses = expenses;
    }

    @Override
    public void run() {
        Utils.saveDayWiseExpenses(storageKey, dateMonth, expenses);
        handler.sendMessage(new Message());
    }
}
