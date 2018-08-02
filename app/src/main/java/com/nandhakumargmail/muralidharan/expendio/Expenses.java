package com.nandhakumargmail.muralidharan.expendio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Expenses extends ArrayList<Expense> {
    public String getDateMonthHumanReadable() {
        if (!this.isEmpty())
            return this.get(0).getDateMonthHumanReadable();
        return "NA";
    }

    public String getDateMonth() {
        if (!this.isEmpty())
            return this.get(0).getDateMonth();
        return "NA";
    }

    public String getTotalExpenditure() {
        BigDecimal totalExpenditure = new BigDecimal("0");
        for (Expense expense : this) {
            totalExpenditure = totalExpenditure.add(new BigDecimal(expense.getAmountSpent()));
        }
        return totalExpenditure.toString();
    }

    public String getStorageKey() {
        if (!this.isEmpty())
            return this.get(0).getStorageKey();
        return "NA";
    }

    public long getSpentOnDate() {
        if (!this.isEmpty())
            return this.get(0).getSpentOn().getTime();
        return new Date().getTime();
    }

    public void sanitizeData() {
        for (Expense expense : this) {
            expense.santiseData();
        }
    }
}
