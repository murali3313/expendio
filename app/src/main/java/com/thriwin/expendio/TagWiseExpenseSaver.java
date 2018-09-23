package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

class TagWiseExpenseSaver extends Thread {
    private Handler handler;
    private String storageKey;
    private String tagKey;
    private Expenses expenses;

    public TagWiseExpenseSaver(Handler handler, String storageKey, String tagKey, Expenses expenses) {
        this.handler = handler;

        this.storageKey = storageKey;
        this.tagKey = tagKey;
        this.expenses = expenses;
    }

    @Override
    public void run() {
        Utils.saveTagWiseExpenses(storageKey, tagKey, expenses);
        handler.sendMessage(new Message());

    }
}
