package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static android.content.Context.MODE_PRIVATE;
import static java.util.Objects.isNull;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthWiseExpenses {

    private HashMap<String, Expenses> dayWiseExpenses = new HashMap<>();

    public Map.Entry<String, Expenses> getDayWiseExpenses(String key) {
        for (Map.Entry<String, Expenses> expensesEntry : dayWiseExpenses.entrySet()) {
            if (expensesEntry.getKey().equals(key))
                return expensesEntry;
        }
        return null;
    }

    public void addExpense(Expense expens) {
        if (isNull(this.dayWiseExpenses.get(expens.getDateMonth()))) {
            Expenses expenses = new Expenses();
            expenses.add(expens);
            this.dayWiseExpenses.put(expens.getDateMonth(), expenses);
        } else {
            this.dayWiseExpenses.get(expens.getDateMonth()).add(expens);
        }

    }

    public void updateExpenses(String dateMonth, Expenses expensesList) {
        if (expensesList.isEmpty()) {
            this.dayWiseExpenses.remove(dateMonth);
            return;
        }
        for (Expense expense : expensesList) {
            Expenses expenses = this.dayWiseExpenses.get(expense.getDateMonth());
            if (!isNull(expenses))
                expenses.clear();
        }
        for (Expense expens : expensesList) {
            addExpense(expens);
        }
    }

    public SortedSet<String> getSortedKeys() {
        return new TreeSet<>(this.getDayWiseExpenses().keySet()).descendingSet();
    }

    @JsonIgnore
    public String getTotalExpenditure() {
        BigDecimal totalExpenditure = new BigDecimal("0");
        for (Map.Entry<String, Expenses> expensesEntry : dayWiseExpenses.entrySet()) {
            totalExpenditure = totalExpenditure.add(new BigDecimal(expensesEntry.getValue().getTotalExpenditure()));
        }
        return totalExpenditure.toString();
    }

    public String getStorageKey() {
        if (!dayWiseExpenses.isEmpty())
            return this.dayWiseExpenses.entrySet().iterator().next().getValue().getStorageKey();
        return "NA";
    }

    public long getLatestDate() {
        return getDayWiseExpenses(getSortedKeys().first()).getValue().getSpentOnDate();
    }
}
