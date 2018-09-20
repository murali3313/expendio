package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.thriwin.expendio.Utils.getDefaultExpenseLimit;
import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;
import static java.lang.String.format;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthWiseExpense {

    private HashMap<String, Expenses> dayWiseExpenses = new HashMap<>();
    private BigDecimal monthWiseExpenseLimit;

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

        List<String> markForRemoval = new ArrayList<>();
        for (Map.Entry<String, Expenses> expensesEntry : dayWiseExpenses.entrySet()) {
            if (expensesEntry.getValue().size() == 0) {
                markForRemoval.add(expensesEntry.getKey());
            }
        }

        for (String s : markForRemoval) {
            this.dayWiseExpenses.remove(s);
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

    public void updateTagWiseExpenses(String tag, Expenses expensesList) {
        for (Map.Entry<String, Expenses> dayWiseExpenses : dayWiseExpenses.entrySet()) {
            dayWiseExpenses.getValue().removeExpenseBasedOnTag(tag);
        }
        for (Expense expense : expensesList) {
            addExpense(expense);
        }
    }

    @JsonIgnore
    public SortedSet<String> getSortedKeys() {
        return new TreeSet<>(this.getDayWiseExpenses().keySet()).descendingSet();
    }

    @JsonIgnore
    public static SortedSet<String> mergeKeys(SortedSet<String> keys1, SortedSet<String> keys2) {
        Set<String> dayKeys = new TreeSet<>(keys1);
        dayKeys.addAll(keys2);
        return new TreeSet<>(dayKeys).descendingSet();
    }

    @JsonIgnore
    public String getTotalExpenditure() {
        BigDecimal totalExpenditure = new BigDecimal("0");
        for (Map.Entry<String, Expenses> expensesEntry : dayWiseExpenses.entrySet()) {
            totalExpenditure = totalExpenditure.add(new BigDecimal(expensesEntry.getValue().getTotalExpenditure()));
        }
        return totalExpenditure.toString();
    }

    @JsonIgnore
    public long getLatestDate(String expenseStorageKey) {
        if (new Expense(Utils.today()).getStorageKey().equalsIgnoreCase(expenseStorageKey)) {
            return Utils.today().getTime();
        }

        if (getSortedKeys().isEmpty()) {
            String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expenseStorageKey);
            Date date;
            try {
                date = new SimpleDateFormat("dd/MMM/yyyy").parse(format("%d/%s/%s", Expense.getStartDayOfMonth(), readableMonthAndYear[0], readableMonthAndYear[1]));
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date();
            }
            return date.getTime();
        }
        return getDayWiseExpenses(getSortedKeys().first()).getValue().getSpentOnDate();
    }

    @JsonIgnore
    public String getMonthYearHumanReadable() {
        if (!this.dayWiseExpenses.isEmpty()) {
            return this.dayWiseExpenses.get(getSortedKeys().first()).getMonthYearHumanReadable();
        }
        return "NA";
    }

    @JsonIgnore
    public String getMonthYearHumanReadable(String expenseKey) {
        String monthYearHumanReadable = this.getMonthYearHumanReadable();
        if (monthYearHumanReadable.equals("NA") && !isEmpty(expenseKey)) {
            String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expenseKey);
            return readableMonthAndYear[0] + " - " + readableMonthAndYear[1];
        }
        return monthYearHumanReadable;
    }

    @JsonIgnore
    public List<Expenses> getSortedDayWiseExpenses() {
        List<Expenses> expenses = new ArrayList<>();
        for (String dateMonth : getSortedKeys()) {
            expenses.add(this.dayWiseExpenses.get(dateMonth));
        }
        return expenses;
    }

    @JsonIgnore
    public Map<String, Expenses> getTagBasedExpenses() {
        SortedMap<String, Expenses> tagBasedExpenses = new TreeMap<>();
        Expenses allExpenses = getAllExpenses();
        for (Expense expense : allExpenses) {

            String tag = expense.getFirstAssociatedExpenseTag();
            Expenses taggedExpenses = tagBasedExpenses.get(tag);
            if (isNull(taggedExpenses)) {
                tagBasedExpenses.put(tag, new Expenses(expense));
            } else {
                taggedExpenses.add(expense);
            }

        }
        return tagBasedExpenses;
    }

    @JsonIgnore
    private Expenses getAllExpenses() {
        Expenses expenses = new Expenses();
        for (Map.Entry<String, Expenses> dayWiseExpense : dayWiseExpenses.entrySet()) {
            expenses.addAll(dayWiseExpense.getValue());
        }
        return expenses;
    }

    public BigDecimal getMonthWiseExpenseLimit() {
        return isNull(this.monthWiseExpenseLimit) ? getDefaultExpenseLimit() : this.monthWiseExpenseLimit;
    }

    @JsonIgnore
    public String monthlyLimitExceededDetails() {
        String limitDetails = "NA";
        BigDecimal actualSpent = new BigDecimal(this.getTotalExpenditure());
        BigDecimal monthWiseExpenseLimit = this.getMonthWiseExpenseLimit();
        switch (actualSpent.compareTo(monthWiseExpenseLimit)) {
            case 0:
                limitDetails = "Limit reached";
                break;
            case -1:
                limitDetails = format("$$: %s to reach the limit", monthWiseExpenseLimit.subtract(actualSpent).toString());
                break;

            case 1:
                limitDetails = format("Over spent by - %s", actualSpent.subtract(monthWiseExpenseLimit).toString());
                break;

        }
        return limitDetails;
    }

    @JsonIgnore
    public void merge(MonthWiseExpense thatMonthWiseExpense) {
        for (Map.Entry<String, Expenses> otherUserExpenses : thatMonthWiseExpense.getDayWiseExpenses().entrySet()) {
            Map.Entry<String, Expenses> dayWiseExpenses = this.getDayWiseExpenses(otherUserExpenses.getKey());
            if (isNull(dayWiseExpenses)) {
                this.dayWiseExpenses.put(otherUserExpenses.getKey(), otherUserExpenses.getValue());
            } else {
                this.dayWiseExpenses.get(otherUserExpenses.getKey()).addAll(otherUserExpenses.getValue());
            }
        }
    }

    @JsonIgnore
    public String getStorageKey() {
        if (!this.dayWiseExpenses.isEmpty()) {
            return this.dayWiseExpenses.get(getSortedKeys().first()).getStorageKey();
        }
        return "NA";
    }


    public Integer getTotalEntries() {
        Integer totalEntries = 0;
        for (Expenses expenses : dayWiseExpenses.values()) {
            totalEntries += expenses.size();
        }
        return totalEntries;
    }
}
