package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static java.lang.String.format;

public class PieChartViewLoader extends Thread {

    private String storageKeyForCurrentMonth;
    private boolean shouldIncludeOtherExpenses;
    private Handler handler;

    Map<String, Object> dataMap = new HashMap<>();

    public PieChartViewLoader(String storageKeyForCurrentMonth, boolean shouldIncludeOtherExpenses, Handler handler) {
        this.storageKeyForCurrentMonth = storageKeyForCurrentMonth;
        this.shouldIncludeOtherExpenses = shouldIncludeOtherExpenses;

        this.handler = handler;
    }

    @Override
    public void run() {
        MonthWiseExpense monthExpenses = Utils.getDeserializedMonthWiseExpenses(storageKeyForCurrentMonth);

        this.dataMap.put("monthExpenses", monthExpenses);
        Map<String, Expenses> tagBasedExpenses = monthExpenses.getTagBasedExpenses();
        this.dataMap.put("tagBasedExpenses", tagBasedExpenses);

        BigDecimal totalExpenditureOfAllUsers = new BigDecimal("0");

        Map<String, Expenses> viewableTagExpenses = tagBasedExpenses;
        if (shouldIncludeOtherExpenses) {
            SortedMap<String, MonthWiseExpense> allSharedExpensesFor = Utils.getAllSharedExpensesFor(storageKeyForCurrentMonth);
            viewableTagExpenses = Utils.mergeTagBasedExpenses(monthExpenses, allSharedExpensesFor);
            totalExpenditureOfAllUsers = new BigDecimal(monthExpenses.getTotalExpenditure());
            for (Map.Entry<String, MonthWiseExpense> sharedUserExpenses : allSharedExpensesFor.entrySet()) {
                totalExpenditureOfAllUsers = totalExpenditureOfAllUsers.add(new BigDecimal(sharedUserExpenses.getValue().getTotalExpenditure()));
            }
        }

        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Expenses> tagBasedExpense : viewableTagExpenses.entrySet()) {
            entries.add(new PieEntry(Float.parseFloat(tagBasedExpense.getValue().getTotalExpenditure()), tagBasedExpense.getKey()));
        }

        this.dataMap.put("totalExpenditureOfAllUsers", totalExpenditureOfAllUsers);
        this.dataMap.put("yourExpenditure", monthExpenses.getTotalExpenditure());
        this.dataMap.put("viewableTagExpenses", viewableTagExpenses);
        this.dataMap.put("entries", entries);

        Message msg = new Message();
        msg.obj = this.dataMap;
        this.handler.sendMessage(msg);
    }
}
