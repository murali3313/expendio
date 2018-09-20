package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import static com.thriwin.expendio.Utils.getAllExpensesMonths;
import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;

public class AnalyticsViewLoader extends Thread {

    private Handler handler;

    Map<String, Object> dataMap = new HashMap<>();

    public AnalyticsViewLoader(Handler handler) {

        this.handler = handler;
    }

    @Override
    public void run() {
        this.dataMap.put("allExpensesMonths", getAllExpensesMonths());

        Message msg = new Message();
        msg.obj = this.dataMap;
        this.handler.sendMessage(msg);
    }
}
