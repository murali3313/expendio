package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.nandhakumargmail.muralidharan.expendio.ExpenseTags.loadDefaultExpenseTagsIfNotInitialized;

public class Utils {

    @NonNull
    public static String[] splitStatementBy(String expenseStatement, String delimiter) {
        return Pattern.compile(delimiter, Pattern.CASE_INSENSITIVE).split(expenseStatement);
    }

    private static final String DAILY_EXPENSER = "DAILY_EXPENSER";
    public static final String UNACCEPTED_EXPENSES = "UnAcceptedExpenses";
    public static final String TAGS = "tags";


    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date tomorrow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date today() {
        return new Date();
    }

    public static boolean isEmpty(String anyWord) {
        return anyWord == null || anyWord.length() == 0;
    }

    public static SharedPreferences globalAccessibleSharedPreferences;


    public static void loadLocalStorageForPreferences(Context c) {
        globalAccessibleSharedPreferences = c.getSharedPreferences(DAILY_EXPENSER, MODE_PRIVATE);
        loadDefaultExpenseTagsIfNotInitialized();
    }

    public static SharedPreferences getLocalStorageForPreferences() {
        return globalAccessibleSharedPreferences;
    }

    public static Expenses getDeserializedExpenses(String expensesString) {
        Expenses expenses = new Expenses();
        try {
            ObjectMapper obj = new ObjectMapper();
            expenses = obj.readValue(expensesString, new TypeReference<Expenses>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static MonthWiseExpenses getDeserializedMonthWiseExpenses(String expensesString) {
        MonthWiseExpenses expenses = new MonthWiseExpenses();
        try {
            ObjectMapper obj = new ObjectMapper();
            expenses = obj.readValue(expensesString, new TypeReference<MonthWiseExpenses>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static String getSerializedExpenses(List<Expense> expenses) {
        String expensesString = null;
        try {
            ObjectMapper obj = new ObjectMapper();
            expensesString = obj.writeValueAsString(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expensesString;
    }

    public static String getSerializedExpenses(MonthWiseExpenses monthWiseExpenses) {
        String expensesString = null;
        try {
            ObjectMapper obj = new ObjectMapper();
            expensesString = obj.writeValueAsString(monthWiseExpenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expensesString;
    }

    public static Map<String, List<Expense>> getExpenseGroupedByDate(List<Expense> expenses) {
        HashMap<String, List<Expense>> expenseGroupedByDate = new HashMap<>();
        for (Expense expens : expenses) {
            String dayMonth = expens.getDateMonth();
            if (isNull(expenseGroupedByDate.get(dayMonth))) {
                expenseGroupedByDate.put(dayMonth, new ArrayList<Expense>() {{
                    add(expens);
                }});
            } else {
                expenseGroupedByDate.get(dayMonth).add(expens);
            }
        }

        return expenseGroupedByDate;

    }

    public static void saveExpenses(Expenses expenses) {
        SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        Map<String, MonthWiseExpenses> allMonthWiseExpenses = new HashMap<>();
        expenses.sanitizeData();
        for (Expense expens : expenses) {
            MonthWiseExpenses storedExpenses;
            if (isNull(allMonthWiseExpenses.get(expens.getStorageKey()))) {
                storedExpenses = getDeserializedMonthWiseExpenses(localStorageForPreferences.getString(expens.getStorageKey(), "[]"));
                allMonthWiseExpenses.put(expens.getStorageKey(), storedExpenses);
            } else {
                storedExpenses = allMonthWiseExpenses.get(expens.getStorageKey());
            }
            storedExpenses.addExpense(expens);

        }
        SharedPreferences.Editor edit = localStorageForPreferences.edit();

        for (Map.Entry<String, MonthWiseExpenses> allEditedExpense : allMonthWiseExpenses.entrySet()) {
            edit.putString(allEditedExpense.getKey(), getSerializedExpenses(allEditedExpense.getValue()));
        }
        edit.apply();
    }

    public static void deleteAMonthExpense(String storageKey) {
        SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        SharedPreferences.Editor edit = localStorageForPreferences.edit();
        edit.remove(storageKey);
        edit.apply();
    }

    public static void saveDayWiseExpenses(String storageKey, String dateMonth, Expenses expenses) {
        expenses.sanitizeData();
        SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        MonthWiseExpenses storedExpenses = getDeserializedMonthWiseExpenses(localStorageForPreferences.getString(storageKey, "[]"));
        storedExpenses.updateExpenses(dateMonth, expenses);

        SharedPreferences.Editor edit = localStorageForPreferences.edit();

        edit.putString(storageKey, getSerializedExpenses(storedExpenses));
        edit.apply();
    }


    public static String getReadableMonth(String month) {
        int monthIndex = Integer.parseInt(month);
        String monthName = DateProcessor.allMonths.get(monthIndex - 1);


        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static Expenses getUnAcceptedExpenses() {
        String unAcceptedExpenses = Utils.getLocalStorageForPreferences()
                .getString(UNACCEPTED_EXPENSES, "[]");
        return getDeserializedExpenses(unAcceptedExpenses);
    }

    public static Expenses getUnAcceptedExpensesViaSMS() {
        return new Expenses();
    }

    public static void clearUnAcceptedExpense() {
        getLocalStorageForPreferences().edit().putString(UNACCEPTED_EXPENSES, "[]").apply();
    }

}
