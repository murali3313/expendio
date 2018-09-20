package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.thriwin.expendio.Utils.isNull;

public class PieChartEntryClickLoader extends Thread {


    private String label;
    private Map<String, Expenses> viewableTagExpenses;
    private Handler handler;

    Map<String, Object> dataMap = new HashMap<>();

    public PieChartEntryClickLoader(String label, Map<String, Expenses> viewableTagExpenses, Handler handler) {
        this.label = label;
        this.viewableTagExpenses = viewableTagExpenses;


        this.handler = handler;
    }

    @Override
    public void run() {
        Expenses tagBasedExpenses = this.viewableTagExpenses.get(label);

        if (isNull(tagBasedExpenses)) {
            tagBasedExpenses = new Expenses(new Expense(new Date(viewableTagExpenses.get(label).getSpentOnDate())));
        }

        dataMap.put("TagWiseExpenses", Utils.getSerializedExpenses(tagBasedExpenses));


        Message msg = new Message();
        msg.obj = this.dataMap;
        this.handler.sendMessage(msg);
    }
}
