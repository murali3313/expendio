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
import static java.util.Objects.isNull;

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
    }

    public static SharedPreferences getLocalStorageForPreferences() {
        return globalAccessibleSharedPreferences;
    }

    public static List<Expense> getDeserializedExpenses(String expensesString) {
        List<Expense> expenses = new ArrayList<>();
        try {
            ObjectMapper obj = new ObjectMapper();
            expenses = obj.readValue(expensesString, new TypeReference<List<Expense>>() {
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
}
