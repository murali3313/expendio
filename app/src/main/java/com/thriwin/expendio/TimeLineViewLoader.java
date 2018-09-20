package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

public class TimeLineViewLoader extends Thread {

    private String expenseKey;
    private Handler handler;

    Map<String, Object> dataMap = new HashMap<>();

    public TimeLineViewLoader(String expenseKey, Handler handler) {

        this.expenseKey = expenseKey;
        this.handler = handler;
    }

    @Override
    public void run() {
        MonthWiseExpense monthWiseExpense = Utils.getDeserializedMonthWiseExpenses(expenseKey);
        this.dataMap.put("monthWiseExpense", monthWiseExpense);
        this.dataMap.put("monthWiseExpenseTotalExpenditure", monthWiseExpense.getTotalExpenditure());

        SortedMap<String, MonthWiseExpense> allSharedExpenses = Utils.getAllSharedExpensesFor(expenseKey);
        this.dataMap.put("allSharedExpenses", allSharedExpenses);

        BigDecimal totalExpenditureOfOtherUsers = new BigDecimal("0");

        for (Map.Entry<String, MonthWiseExpense> sharedUserExpenses : allSharedExpenses.entrySet()) {
            totalExpenditureOfOtherUsers = totalExpenditureOfOtherUsers.add(new BigDecimal(sharedUserExpenses.getValue().getTotalExpenditure()));
        }

        this.dataMap.put("totalExpenditureOfOtherUsers", totalExpenditureOfOtherUsers);
        this.dataMap.put("allTotalExpenditure", totalExpenditureOfOtherUsers.add(new BigDecimal(monthWiseExpense.getTotalExpenditure())));

        SortedSet<String> allSortedKeys = monthWiseExpense.getSortedKeys();
        for (MonthWiseExpense wiseExpense : allSharedExpenses.values()) {
            allSortedKeys = MonthWiseExpense.mergeKeys(allSortedKeys, wiseExpense.getSortedKeys());
        }

        this.dataMap.put("allSortedKeys", allSortedKeys);
        HashMap<String, Map.Entry<String, Expenses>> expensePerDayOfYou = new HashMap<>();
        Map<String, Map<String, Map.Entry<String, Expenses>>> expensePerDayOfOthers = new HashMap<>();


        for (String dayKey : allSortedKeys) {
            expensePerDayOfYou.put(dayKey, monthWiseExpense.getDayWiseExpenses(dayKey));

            Map<String, Map.Entry<String, Expenses>> allDayWiseExpenseFromSharer = Utils.getAllDayWiseExpenseFromSharer(dayKey, allSharedExpenses);
            expensePerDayOfOthers.put(dayKey, allDayWiseExpenseFromSharer);
        }
        this.dataMap.put("dayWiseExpenseOfYou", expensePerDayOfYou);
        this.dataMap.put("dayWiseExpenseOfOthers", expensePerDayOfOthers);

        Message msg = new Message();
        msg.obj = this.dataMap;
        this.handler.sendMessage(msg);
    }
}
