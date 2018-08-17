package com.thriwin.expendio;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
import static android.content.Context.MODE_PRIVATE;
import static java.util.Arrays.asList;

public class Utils {

    private static String defaultExpense = "DEFAULT_EXPENSE";
    public static List<String> timeLineColors = asList("#C39EBA", "#FFCECE", "#FF83A3",
            "#F0DEFF", "#BAC39E", "#BF97AB", "#FFE6F9",
            "#E1FF83", "#FFB5B5", "#E0DEFF", "#BF97AB", "#F6F3A2", "#D8FFE1");

    public static List<Integer> getTimeLineColors() {
        List<Integer> colorsHex = new ArrayList<>();
        for (String timeLineColor : timeLineColors) {
            colorsHex.add(Color.parseColor(timeLineColor));
        }
        return colorsHex;
    }

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
        ExpenseTags.loadDefaultExpenseTagsIfNotInitialized();
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

    public static MonthWiseExpense getDeserializedMonthWiseExpenses(String expenseKey) {
        String deserializedMonthWiseExpenses = Utils.getLocalStorageForPreferences().getString(expenseKey, "[]");
        MonthWiseExpense expenses = new MonthWiseExpense();
        try {
            ObjectMapper obj = new ObjectMapper();
            expenses = obj.readValue(deserializedMonthWiseExpenses, new TypeReference<MonthWiseExpense>() {
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

    public static String getSerializedExpenses(MonthWiseExpense monthWiseExpense) {
        String expensesString = null;
        try {
            ObjectMapper obj = new ObjectMapper();
            expensesString = obj.writeValueAsString(monthWiseExpense);
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
        Map<String, MonthWiseExpense> allMonthWiseExpenses = new HashMap<>();
        expenses.sanitizeData();
        for (Expense expens : expenses) {
            MonthWiseExpense storedExpenses;
            if (isNull(allMonthWiseExpenses.get(expens.getStorageKey()))) {
                storedExpenses = getDeserializedMonthWiseExpenses(expens.getStorageKey());
                allMonthWiseExpenses.put(expens.getStorageKey(), storedExpenses);
            } else {
                storedExpenses = allMonthWiseExpenses.get(expens.getStorageKey());
            }
            storedExpenses.addExpense(expens);

        }
        SharedPreferences.Editor edit = localStorageForPreferences.edit();

        for (Map.Entry<String, MonthWiseExpense> allEditedExpense : allMonthWiseExpenses.entrySet()) {
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
        MonthWiseExpense storedExpenses = getDeserializedMonthWiseExpenses(storageKey);
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


    public static void clearUnAcceptedExpense(String key) {
        getLocalStorageForPreferences().edit().remove(key).apply();
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static SortedMap<String, MonthWiseExpense> getAllExpensesMonthWise() {
        SortedMap<String, MonthWiseExpense> allExpenses = new TreeMap<>(Collections.reverseOrder());
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("Expense-")) {
                try {
                    MonthWiseExpense expenses = ExpenseTags.objectMapper.readValue(all.get(entry.getKey()).toString(), new TypeReference<MonthWiseExpense>() {
                    });
                    allExpenses.put(entry.getKey(), expenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return allExpenses;
    }

    public static List<String> getAllExpensesMonths() {
        List<String> allExpenseMonths = new ArrayList<>();
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("Expense-")) {
                allExpenseMonths.add(entry.getKey());
            }
        }
        return new ArrayList<>(new TreeSet<>(allExpenseMonths));
    }

    public static String[] getReadableMonthAndYear(String storageKey) {
        if (isNull(storageKey))
            return new String[]{"NA", "NA"};
        String[] monthAndYearAsArray = new String[2];
        String monthAndYear = storageKey.replace("Expense-", "");
        String year = monthAndYear.substring(0, monthAndYear.indexOf("-"));
        String month = monthAndYear.substring(monthAndYear.indexOf("-") + 1);
        monthAndYearAsArray[0] = Utils.getReadableMonth(month);
        monthAndYearAsArray[1] = year;
        return monthAndYearAsArray;
    }

    public static String getStorageKeyFromText(String humanReadableText) {
        String[] monthAndYear = humanReadableText.split("-");
        if (monthAndYear.length != 2) {
            return null;
        }
        int monthIndex = DateProcessor.allMonths.indexOf(monthAndYear[0].trim().toLowerCase());
        return String.format("Expense-%s-%02d", monthAndYear[1].trim(), monthIndex + 1);
    }

    public static BigDecimal getDefaultExpenseLimit() {
        String defaultExpense = getLocalStorageForPreferences().getString(Utils.defaultExpense, "5000");
        return new BigDecimal(defaultExpense);
    }

    public static void saveDefaultExpenseLimit(String defaultExpenseLimit) {
        BigDecimal expenseLimit = new BigDecimal(isEmpty(defaultExpenseLimit.trim()) ? "0" : defaultExpenseLimit);
        SharedPreferences.Editor edit = getLocalStorageForPreferences().edit();
        edit.putString(defaultExpense, expenseLimit.toString());
        edit.commit();

    }

    public static void showToast(Context cx, int resourceId) {
        Toast toast = Toast.makeText(cx, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }

    public static BigDecimal getMonthWiseExpenseLimit(String expenseStorageKey) {
        return getDeserializedMonthWiseExpenses(expenseStorageKey).getMonthWiseExpenseLimit();
    }

    public static void saveExpenseLimit(String expenseStorageKey, String expenseLimit) {
        MonthWiseExpense deserializedMonthWiseExpense = getDeserializedMonthWiseExpenses(expenseStorageKey);
        deserializedMonthWiseExpense.setMonthWiseExpenseLimit(new BigDecimal(expenseLimit));
        SharedPreferences.Editor edit = getLocalStorageForPreferences().edit();

        edit.putString(expenseStorageKey, getSerializedExpenses(deserializedMonthWiseExpense));
        edit.commit();
    }

    public static void saveTagWiseExpenses(String storageKey, String tag, Expenses expenses) {
        expenses.sanitizeData();
        SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        MonthWiseExpense storedExpenses = getDeserializedMonthWiseExpenses(storageKey);
        storedExpenses.updateTagWiseExpenses(tag, expenses);

        SharedPreferences.Editor edit = localStorageForPreferences.edit();

        edit.putString(storageKey, getSerializedExpenses(storedExpenses));
        edit.apply();
    }

    public static RecurringExpenses getAllRecurringExpenses() {
        RecurringExpenses recurringExpenses = new RecurringExpenses();
        try {
            String recurringExpensesString = Utils.getLocalStorageForPreferences().getString("RECURRING_EXPENSES", "[]");
            ObjectMapper obj = new ObjectMapper();
            recurringExpenses = obj.readValue(recurringExpensesString, new TypeReference<RecurringExpenses>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recurringExpenses;
    }

    public static void saveRecurrigExpenses(RecurringExpenses recurringExpenses) {

        try {
            ObjectMapper obj = new ObjectMapper();
            String recurringExpenseString = obj.writeValueAsString(recurringExpenses);
            getLocalStorageForPreferences().edit().putString("RECURRING_EXPENSES", recurringExpenseString).commit();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void saveNotificationExpenses(Expenses todaysExpenses) {
        try {
            ObjectMapper obj = new ObjectMapper();
            String dailyExpenses = obj.writeValueAsString(todaysExpenses);
            getLocalStorageForPreferences().edit().putString("DAILY_EXPENSES-" + todaysExpenses.getDateMonth(), dailyExpenses).commit();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Expenses> getNotificationExpenses() {
        ArrayList<Expenses> unApprovedDayWiseExpenses = new ArrayList<>();

        ObjectMapper obj = new ObjectMapper();
        Map<String, ?> all = getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("DAILY_EXPENSES-")) {
                try {
                    Expenses expenses = obj.readValue(all.get(entry.getKey()).toString(), new TypeReference<Expenses>() {
                    });
                    unApprovedDayWiseExpenses.add(expenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return unApprovedDayWiseExpenses;

    }

    public static boolean didRecurrenceChekerRanToday() {
//        return false;
        String checkerRanOn = Utils.getLocalStorageForPreferences().getString("CHECKER_RAN_ON", null);
        String dateMonth = new SimpleDateFormat("MM-dd").format(today());
        return !isNull(checkerRanOn) && checkerRanOn.equals(dateMonth);
    }

    public static void markRecurrenceCheckerRanToday() {
        String dateMonth = new SimpleDateFormat("MM-dd").format(today());
        Utils.getLocalStorageForPreferences().edit().putString("CHECKER_RAN_ON", dateMonth).apply();
    }


    public static String getUnApprovedExpensesCount() {
        Integer i = 0;
        Map<String, ?> all = getLocalStorageForPreferences().getAll();
        for (String key : all.keySet()) {
            if (key.startsWith("DAILY_EXPENSES-") || key.equals(UNACCEPTED_EXPENSES)) {
                i++;
            }
        }
        return i.toString();
    }
}
