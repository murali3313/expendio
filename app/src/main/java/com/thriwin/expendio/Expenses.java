package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Expenses extends ArrayList<Expense> {

    public Expenses(Expense e) {
        this.add(e);
    }

    @JsonIgnore
    public String getDateMonthHumanReadable() {
        if (!this.isEmpty())
            return this.get(0).getDateMonthHumanReadable();
        return "NA";
    }
    @JsonIgnore
    public String getMonthYearHumanReadable() {
        if (!this.isEmpty())
            return this.get(0).getMonthYearHumanReadable();
        return "NA";
    }

    @JsonIgnore
    public String getDateMonth() {
        if (!this.isEmpty())
            return this.get(0).getDateMonth();
        return "NA";
    }

    @JsonIgnore
    public String getTotalExpenditure() {
        BigDecimal totalExpenditure = new BigDecimal("0");
        for (Expense expense : this) {
            String amountSpent = expense.getAmountSpent();
            totalExpenditure = totalExpenditure.add(new BigDecimal(Utils.isEmpty(amountSpent) ? "0" : amountSpent));
        }
        return totalExpenditure.toString();
    }

    @JsonIgnore
    public String getStorageKey() {
        if (!this.isEmpty())
            return this.get(0).getStorageKey();
        return "NA";
    }

    @JsonIgnore
    public long getSpentOnDate() {
        if (!this.isEmpty())
            return this.get(0).getSpentOn().getTime();
        return new Date().getTime();
    }

    @JsonIgnore
    public void sanitizeData() {
        for (Expense expense : this) {
            expense.santiseData();
        }
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getFirstAssociatedTag() {
        if (!this.isEmpty())
            return this.get(0).getFirstAssociatedExpenseTag();
        return "NA";
    }

    @JsonIgnore
    public String getNameOfSharer(String key) {
        String[] split = key.split("-");
        String name = split[1];
        return name;
    }


}
