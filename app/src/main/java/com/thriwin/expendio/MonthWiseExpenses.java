package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        if (Utils.isNull(this.dayWiseExpenses.get(expens.getDateMonth()))) {
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
            if (!Utils.isNull(expenses))
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

    public long getLatestDate(String expenseKey) {
        if (getSortedKeys().isEmpty()) {
            String monthAndYear = expenseKey.replace("Expense-", "");
            int startDayOfMonth = Expense.getStartDayOfMonth();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                return simpleDateFormat.parse(String.format("%d-%s", startDayOfMonth, monthAndYear)).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0l;
        }
        return getDayWiseExpenses(getSortedKeys().first()).getValue().getSpentOnDate();
    }

    public String getMonthYearHumanReadable() {
        if (!this.dayWiseExpenses.isEmpty()) {
            return this.dayWiseExpenses.get(getSortedKeys().first()).getMonthYearHumanReadable();
        }
        return "NA";
    }

    public List<Expenses> getSortedDayWiseExpenses() {
        List<Expenses> expenses = new ArrayList<>();
        for (String dateMonth : getSortedKeys()) {
            expenses.add(this.dayWiseExpenses.get(dateMonth));
        }
        return expenses;
    }
}
