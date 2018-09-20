package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

import static com.thriwin.expendio.Utils.isNull;

public class BarChartViewLoader extends Thread {

    private String primaryMonth;
    private String comparingMonth;
    private boolean shouldIncludeOtherExpenses;
    private Handler handler;

    Map<String, Object> dataMap = new HashMap<>();

    public BarChartViewLoader(String primaryMonth, String comparingMonth, boolean shouldIncludeOtherExpenses, Handler handler) {
        this.primaryMonth = primaryMonth;
        this.comparingMonth = comparingMonth;
        this.shouldIncludeOtherExpenses = shouldIncludeOtherExpenses;

        this.handler = handler;
    }

    @Override
    public void run() {
        Map<String, Expenses> primaryTagBasedExpenses = getTagBasedExpenseFor(primaryMonth);
        Map<String, Expenses> comparingTagBasedExpenses = new HashMap<>();
        if (!isNull(comparingMonth)) {
            comparingTagBasedExpenses = getTagBasedExpenseFor(comparingMonth);
        }
        this.dataMap.put("primaryTagBasedExpenses", primaryTagBasedExpenses);
        this.dataMap.put("comparingTagBasedExpenses", comparingTagBasedExpenses);
        Message msg = new Message();
        msg.obj = this.dataMap;
        this.handler.sendMessage(msg);
    }

    private Map<String, Expenses> getTagBasedExpenseFor(String primaryMonth) {
        MonthWiseExpense primaryMonthExpenses = Utils.getDeserializedMonthWiseExpenses(primaryMonth);
        Map<String, Expenses> tagBasedExpenses = primaryMonthExpenses.getTagBasedExpenses();
        if (shouldIncludeOtherExpenses) {
            tagBasedExpenses = Utils.mergeTagBasedExpenses(primaryMonthExpenses, Utils.getAllSharedExpensesFor(primaryMonth));
        }
        return tagBasedExpenses;
    }
}
