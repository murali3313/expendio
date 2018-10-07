package com.thriwin.expendio;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.List;

class ExpenseUploader extends Thread {
    private Context context;
    private List<String> allExpensesStorageKeys;
    private Handler handler;

    public ExpenseUploader(Context context, List<String> allExpensesStorageKeys, Handler handler) {
        this.context = context;
        this.allExpensesStorageKeys = allExpensesStorageKeys;

        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = new Message();
        try {
            for (String storageKey : allExpensesStorageKeys) {
                GoogleCloudSynchActivity.silentSignInAndWriteMyExpenseToGoogleSync(context, storageKey);
            }
            msg.obj = true;
            handler.sendMessage(msg);
        } catch (Exception e) {
            msg.obj = false;
            handler.sendMessage(msg);
        }




    }
}
