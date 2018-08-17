package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Expenses extends ArrayList<Expense> {

    public Expenses(Expense e) {
        this.add(e);
    }

    public String getDateMonthHumanReadable() {
        if (!this.isEmpty())
            return this.get(0).getDateMonthHumanReadable();
        return "NA";
    }

    public String getMonthYearHumanReadable() {
        if (!this.isEmpty())
            return this.get(0).getMonthYearHumanReadable();
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
            String amountSpent = expense.getAmountSpent();
            totalExpenditure = totalExpenditure.add(new BigDecimal(Utils.isEmpty(amountSpent) ? "0" : amountSpent));
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

    public void removeExpenseBasedOnTag(String tag) {
        Expenses markForRemoval = new Expenses();
        for (Expense expense : this) {
            if (expense.getFirstAssociatedExpenseTag().equalsIgnoreCase(tag)) {
                markForRemoval.add(expense);
            }
        }

        for (Expense expense : markForRemoval) {
            this.remove(expense);
        }
    }

    public String getKeyForUnApprovedDailyExpense() {
        return "DAILY_EXPENSES-" + this.getDateMonth();
    }

    public String getFirstAssociatedTag() {
        if (!this.isEmpty())
            return this.get(0).getFirstAssociatedExpenseTag();
        return "NA";
    }
}
