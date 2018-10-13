package com.thriwin.expendio;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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

import static android.content.Context.MODE_PRIVATE;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Utils {

    public static final String UNACCEPTED_SHARED_SMS_EXPENSES = "UNACCEPTED_SHARED_SMS_EXPENSES";
    public static final String EXPENDIO_SMS_START = "#ExpendioSTART#";
    public static final String EXPENDIO_SMS = "#ES#";
    public static final String EXPENDIO_SMS_END = "#ExpendioEND#";
    public static final String EXPENDIO_SMS_START_WITHESC = "\\#ExpendioSTART\\#";
    public static final String EXPENDIO_SMS_WITHESC = "\\#ES\\#";
    public static final String EXPENDIO_SMS_END_WITHESC = "\\#ExpendioEND\\#";
    public static final String SHARED = "SHARED";
    private static String defaultExpense = "DEFAULT_EXPENSE";
    public static List<String> timeLineColors = asList("#F2C39EBA", "#F2FFCECE", "#F2FF83A3",
            "#F2F0DEFF", "#F2BAC39E", "#F2BF97AB", "#F2FFE6F9",
            "#F2E1FF83", "#F2FFB5B5", "#F2E0DEFF", "#F2BF97AB", "#F2D8FFE1", "#F2f8f6bd");

    public static List<String> complimentaryTimeLineColors = asList("#9ec3a7", "#ceffff", "#83ffdf",
            "#edffde", "#a79ec3", "#97bfab", "#e6ffec",
            "#a183ff", "#b5ffff", "#fdffde", "#97bfab", "#ffd8f6", "#a2a5f6");

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
    public static final String DAILY_EXPENSES = "DAILY_EXPENSES";
    public static final String UNACCEPTED_SMS_EXPENSES = "UnAcceptedSMSExpenses";
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

    public static CachedSharedPreferences globalAccessibleSharedPreferences;


    public static void loadLocalStorageForPreferences(Context c) {
        globalAccessibleSharedPreferences = new CachedSharedPreferences(c.getSharedPreferences(DAILY_EXPENSER, MODE_PRIVATE));
        ExpenseTags.loadDefaultExpenseTagsIfNotInitialized();
        ExpendioSettings.loadExpendioSettings();
    }

    public static CachedSharedPreferences getLocalStorageForPreferences() {
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
        return isNull(expenses) ? new Expenses() : expenses;
    }

    public static MonthWiseExpense getDeserializedExpensesForAMonth(String monthWiseExpensesString) {
        MonthWiseExpense monthWiseExpense = new MonthWiseExpense();
        try {
            ObjectMapper obj = new ObjectMapper();
            monthWiseExpense = obj.readValue(monthWiseExpensesString, new TypeReference<MonthWiseExpense>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isNull(monthWiseExpense) ? new MonthWiseExpense() : monthWiseExpense;
    }

    public static MonthWiseExpense getDeserializedMonthWiseExpenses(String expenseKey) {
        String deserializedMonthWiseExpenses = Utils.getLocalStorageForPreferences().getString(expenseKey, "{}");
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
        String expensesString = convertObjectToString(expenses);
        return expensesString;
    }

    private static String convertObjectToString(Object o) {
        String objectString = "";

        try {
            ObjectMapper obj = new ObjectMapper();
            objectString = obj.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objectString;
    }

    private static HashMap<String, String> convertStringToMap(String o) {
        HashMap<String, String> objectString = new HashMap<>();

        try {
            ObjectMapper obj = new ObjectMapper();
            objectString = obj.readValue(o, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectString;
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
        CachedSharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        Map<String, MonthWiseExpense> allMonthWiseExpenses = new HashMap<>();
        expenses.sanitizeData();
        for (Expense expens : expenses) {
            MonthWiseExpense storedExpenses;
            String storageKeyForSelfAndOtherDistinguished = expens.getStorageKeyForSelfAndOtherDistinguished();
            if (isNull(allMonthWiseExpenses.get(storageKeyForSelfAndOtherDistinguished))) {
                storedExpenses = getDeserializedMonthWiseExpenses(expens.getStorageKey());
                allMonthWiseExpenses.put(storageKeyForSelfAndOtherDistinguished, storedExpenses);
            } else {
                storedExpenses = allMonthWiseExpenses.get(storageKeyForSelfAndOtherDistinguished);
            }
            storedExpenses.addExpense(expens);

        }
        CachedSharedPreferences.Editor edit = localStorageForPreferences.edit();

        for (Map.Entry<String, MonthWiseExpense> allEditedExpense : allMonthWiseExpenses.entrySet()) {
            edit.putString(allEditedExpense.getKey(), getSerializedExpenses(allEditedExpense.getValue()));
            markExpenseForSyncing(allEditedExpense.getKey());
        }
        edit.commit();
    }

    public static void deleteAMonthExpense(String storageKey) {
        CachedSharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        CachedSharedPreferences.Editor edit = localStorageForPreferences.edit();
        edit.remove(storageKey);
        edit.commit();
        Utils.clearLastBackgroundSyncDone(storageKey);
    }


    public static void saveDayWiseExpenses(String storageKey, String dateMonth, Expenses expenses) {
        expenses.sanitizeData();
        CachedSharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        MonthWiseExpense storedExpenses = getDeserializedMonthWiseExpenses(storageKey);
        storedExpenses.updateExpenses(dateMonth, expenses);

        CachedSharedPreferences.Editor edit = localStorageForPreferences.edit();

        edit.putString(storageKey, getSerializedExpenses(storedExpenses));
        edit.commit();
        markExpenseForSyncing(storageKey);
    }


    public static String getReadableMonth(String month) {
        int monthIndex = Integer.parseInt(month);
        String monthName = DateProcessor.allMonths.get(monthIndex - 1);


        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
    }

    public static boolean isNull(Object o) {
        return o == null;
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

    public static List<String> getAllExpensesStorageKeys() {
        List<String> storageKeys = new ArrayList<>();
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("Expense-")) {
                storageKeys.add(entry.getKey());
            }
        }
        return storageKeys;
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
        return format("Expense-%s-%02d", monthAndYear[1].trim(), monthIndex + 1);
    }

    public static BigDecimal getDefaultExpenseLimit() {
        String defaultExpense = getLocalStorageForPreferences().getString(Utils.defaultExpense, "5000");
        return new BigDecimal(defaultExpense);
    }

    public static void saveDefaultExpenseLimit(String defaultExpenseLimit) {
        BigDecimal expenseLimit = new BigDecimal(isEmpty(defaultExpenseLimit.trim()) ? "0" : defaultExpenseLimit);
        CachedSharedPreferences.Editor edit = getLocalStorageForPreferences().edit();
        edit.putString(defaultExpense, expenseLimit.toString());
        edit.commit();

    }

    protected static void showToast(Context cx, int resourceId) {
        Toast toast = Toast.makeText(cx, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }

    public static void showLongToast(Context cx, int resourceId) {
        Toast toast = Toast.makeText(cx, resourceId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }

    public static void showToast(Context cx, String resourceId) {
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
        CachedSharedPreferences.Editor edit = getLocalStorageForPreferences().edit();

        edit.putString(expenseStorageKey, getSerializedExpenses(deserializedMonthWiseExpense));
        edit.commit();
    }

    public static void saveTagWiseExpenses(String storageKey, String tag, Expenses expenses) {
        expenses.sanitizeData();
        CachedSharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
        MonthWiseExpense storedExpenses = getDeserializedMonthWiseExpenses(storageKey);
        storedExpenses.updateTagWiseExpenses(tag, expenses);

        CachedSharedPreferences.Editor edit = localStorageForPreferences.edit();

        edit.putString(storageKey, getSerializedExpenses(storedExpenses));
        edit.commit();
        markExpenseForSyncing(storageKey);
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
            getLocalStorageForPreferences().edit().putString(DAILY_EXPENSES + "-" + todaysExpenses.getDateMonth(), dailyExpenses).commit();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Expenses> getNotificationExpenses() {
        ArrayList<Expenses> unApprovedDayWiseExpenses = new ArrayList<>();

        ObjectMapper obj = new ObjectMapper();
        Map<String, ?> all = getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith(DAILY_EXPENSES + "-")) {
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
        Utils.getLocalStorageForPreferences().edit().putString("CHECKER_RAN_ON", dateMonth).commit();
    }


    public static HashMap<String, Expenses> getAllUnAcceptedExpenses() {
        HashMap<String, Expenses> unAcceptedExpenses = new HashMap<>();

        ObjectMapper obj = new ObjectMapper();
        Map<String, ?> all = getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (isWaitingForAcceptance(entry.getKey())) {
                try {
                    Expenses expenses = obj.readValue(all.get(entry.getKey()).toString(), new TypeReference<Expenses>() {
                    });
                    unAcceptedExpenses.put(entry.getKey(), expenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return unAcceptedExpenses;
    }


    public static void clearUnAcceptedExpense(String key) {
        getLocalStorageForPreferences().edit().remove(key).commit();
    }

    public static String getUnApprovedExpensesCount() {
        Integer i = 0;
        Map<String, ?> all = getLocalStorageForPreferences().getAll();
        for (String key : all.keySet()) {
            if (isWaitingForAcceptance(key)) {
                i++;
            }
        }

        return i.toString().equals("0") ? "" : i.toString();
    }

    private static boolean isWaitingForAcceptance(String key) {
        return key.startsWith(DAILY_EXPENSES + "-") ||
                key.startsWith(UNACCEPTED_EXPENSES + "-") ||
                key.startsWith(UNACCEPTED_SMS_EXPENSES + "-") ||
                key.startsWith(UNACCEPTED_SHARED_SMS_EXPENSES + "-");
    }

    public static String saveUnacceptedExpenses(Expenses processedExpenses) {
        CachedSharedPreferences.Editor edit = Utils.getLocalStorageForPreferences().edit();
        String key = Utils.UNACCEPTED_EXPENSES + "-" + Math.random();
        edit.putString(key, serializeExpenses(processedExpenses));
        edit.commit();
        return key;
    }

    @Nullable
    protected static String serializeExpenses(Expenses processedExpenses) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return objectMapper.writeValueAsString(processedExpenses);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearAllData() {
        getLocalStorageForPreferences().edit().clear().commit();
        ExpenseTags.loadDefaultExpenseTagsIfNotInitialized();
    }

    public static Date lastNotiferDisplayTime() {
        String checkerRanOn = Utils.getLocalStorageForPreferences().getString("NOTIFIER", null);
        try {
            return isNull(checkerRanOn) ? null : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(checkerRanOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void lastNotifiedOn(Date date) {
        String format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        Utils.getLocalStorageForPreferences().edit().putString("NOTIFIER", format).commit();
    }

    public static boolean isReminderAlreadySet() {
        return false;
//        return Utils.getLocalStorageForPreferences().getBoolean("REMINDER", false);
    }

    public static void setReminder() {
        Utils.getLocalStorageForPreferences().edit().putBoolean("REMINDER", true);
    }

    public static int getTipsIndex() {
        int tipIndex = getLocalStorageForPreferences().getInt("TipIndex", 0);
        int newTipIndex = tipIndex + 1 >= RecurringExpensesAlarmReceiver.genaralTips.size() ? 0 : tipIndex + 1;
        getLocalStorageForPreferences().edit().putInt("TipIndex", newTipIndex).commit();
        return tipIndex;
    }

    public static SMSInferenceSettings getSMSInfererSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        SMSInferenceSettings smsInferenceSettings = new SMSInferenceSettings();

        String smsInfererSettings = getLocalStorageForPreferences().getString("SMS_INFERER_SETTINGS", "{}");
        try {
            smsInferenceSettings = objectMapper.readValue(smsInfererSettings, SMSInferenceSettings.class);
        } catch (IOException e) {

        }
        return smsInferenceSettings;
    }

    public static void saveSMSInfererSettings(SMSInferenceSettings settings) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            getLocalStorageForPreferences().edit().putString("SMS_INFERER_SETTINGS", objectMapper.writeValueAsString(settings)).commit();
        } catch (IOException e) {

        }
    }

    public static void saveSMSInferredExpense(Expense probableExpenses) {
        CachedSharedPreferences.Editor edit = Utils.getLocalStorageForPreferences().edit();
        edit.putString(Utils.UNACCEPTED_SMS_EXPENSES + "-" + Math.random(), serializeExpenses(new Expenses(probableExpenses)));
        edit.commit();
    }

    public static void saveSMSParsedExpenses(User authenticatedUser, Expenses parsedExpenses) {
        CachedSharedPreferences.Editor edit = Utils.getLocalStorageForPreferences().edit();
        String key = Utils.UNACCEPTED_SHARED_SMS_EXPENSES + "-" + authenticatedUser.getName() + "-" +
                parsedExpenses.getMonthYearHumanReadable() + "-" + SHARED;
        edit.putString(key, serializeExpenses(parsedExpenses));
        edit.commit();
    }

    public static ShareSettings getShareSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        ShareSettings shareSettings = new ShareSettings();

        String shareSettings1 = getLocalStorageForPreferences().getString("SHARE_SETTINGS", "{}");
        try {
            shareSettings = objectMapper.readValue(shareSettings1, ShareSettings.class);
        } catch (IOException e) {

        }
        return shareSettings;
    }

    public static void saveShareSettings(ShareSettings shareSettings) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            getLocalStorageForPreferences().edit().putString("SHARE_SETTINGS", objectMapper.writeValueAsString(shareSettings)).commit();
        } catch (IOException e) {

        }

    }


    public static String getFormattedShareExpense(String expenseStorageKey) {
        StringBuilder expenses = new StringBuilder();
        MonthWiseExpense deserializedMonthWiseExpenses = Utils.getDeserializedMonthWiseExpenses(expenseStorageKey);

        expenses.append(EXPENDIO_SMS_START);
        for (Map.Entry<String, Expenses> dayWiseExpenses : deserializedMonthWiseExpenses.getDayWiseExpenses().entrySet()) {
            for (Expense expense : dayWiseExpenses.getValue()) {
                expenses.append(EXPENDIO_SMS + expense.getStringFormatForSharing() + "&");
            }

        }
        expenses.append(EXPENDIO_SMS_END);

        return expenses.toString();
    }

    public static void saveSharedExpenses(String userName, Expenses expenses) {
        if (isNull(getLocalStorageForPreferences().getString(expenses.getStorageKey(), null))) {
            getLocalStorageForPreferences().edit().putString(expenses.getStorageKey(), getSerializedExpenses(new MonthWiseExpense()));
        }
        HashMap<String, MonthWiseExpense> sharedExpenses = new HashMap<>();
        for (Expense expens : expenses) {
            String storageKeyForUser = expens.getStorageKeyForUser(userName);
            if (sharedExpenses.keySet().contains(storageKeyForUser)) {
                sharedExpenses.get(storageKeyForUser).addExpense(expens);
            } else {
                MonthWiseExpense monthWiseExpense = new MonthWiseExpense();
                monthWiseExpense.addExpense(expens);
                sharedExpenses.put(storageKeyForUser, monthWiseExpense);
            }
        }

        for (Map.Entry<String, MonthWiseExpense> stringExpensesEntry : sharedExpenses.entrySet()) {
            getLocalStorageForPreferences().edit().putString(stringExpensesEntry.getKey(), getSerializedExpenses(stringExpensesEntry.getValue())).commit();
        }
    }

    public static SortedMap<String, MonthWiseExpense> getAllSharedExpensesFor(String key) {

        SortedMap<String, MonthWiseExpense> allExpenses = new TreeMap<>(Collections.reverseOrder());
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().endsWith(key) && !entry.getKey().equals(key) && !entry.getKey().contains("GOOGLE")) {
                try {
                    String userName = entry.getKey().split("-")[0];
                    MonthWiseExpense expenses = ExpenseTags.objectMapper.readValue(all.get(entry.getKey()).toString(), new TypeReference<MonthWiseExpense>() {
                    });
                    allExpenses.put(userName, expenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return allExpenses;
    }

    public static Integer getUserCountOfSharedExpensesFor(String key) {
        key = isNull(key) ? "UNKNOWN" : key;
        Integer count = 0;

        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey.endsWith(key) && !entryKey.equals(key)) {
                count++;
            }
        }
        return count;
    }


    public static void removeAllSharerInfo(List<String> userNames) {

        for (String userName : userNames) {
            Map<String, Object> all = getLocalStorageForPreferences().getAll();
            for (String key : all.keySet()) {
                if (key.contains(userName + "-")) {
                    getLocalStorageForPreferences().edit().remove(key).commit();
                }
            }
        }
    }

    public static Map<String, Map.Entry<String, Expenses>> getAllDayWiseExpenseFromSharer(String key, SortedMap<String, MonthWiseExpense> allSharedExpenses) {
        Map<String, Map.Entry<String, Expenses>> sharerDayWiseExpenses = new HashMap<>();
        for (Map.Entry<String, MonthWiseExpense> monthWiseExpense : allSharedExpenses.entrySet()) {
            Map.Entry<String, Expenses> dayWiseExpenses = monthWiseExpense.getValue().getDayWiseExpenses(key);
            if (!isNull(dayWiseExpenses)) {
                sharerDayWiseExpenses.put(monthWiseExpense.getKey(), dayWiseExpenses);
            }
        }
        return sharerDayWiseExpenses;
    }

    public static Map<String, Expenses> mergeTagBasedExpenses(MonthWiseExpense yourMonthWiseExpenses, SortedMap<String, MonthWiseExpense> sharedExpenses) {
        SortedMap<String, Expenses> tagBasedExpenses = new TreeMap<>();
        tagBasedExpenses.putAll(yourMonthWiseExpenses.getTagBasedExpenses());
        for (Map.Entry<String, MonthWiseExpense> tagBasedExpensesFromShared : sharedExpenses.entrySet()) {
            Map<String, Expenses> expenses = tagBasedExpensesFromShared.getValue().getTagBasedExpenses();
            for (Map.Entry<String, Expenses> expensesEntry : expenses.entrySet()) {
                if (tagBasedExpenses.containsKey(expensesEntry.getKey())) {
                    tagBasedExpenses.get(expensesEntry.getKey()).addAll(expensesEntry.getValue());
                } else {
                    tagBasedExpenses.put(expensesEntry.getKey(), expensesEntry.getValue());
                }
            }
        }
        return tagBasedExpenses;
    }

    public static String getAllSettingsInfo() {
        Map<String, Object> settingsData = new HashMap<>();
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (!entry.getKey().contains("Expense-") && !entry.getKey().startsWith("GOOGLE_") && !entry.getKey().startsWith("REMINDER")) {
                settingsData.put(entry.getKey(), entry.getValue());
            }
        }
        return convertObjectToString(settingsData);
    }

    public static void readAndSaveSettings(String settingsJson) {
        ExpenseTags.clearExpensetags();
        ExpendioThemeSettings.clear();
        HashMap<String, String> settings = convertStringToMap(settingsJson);
        for (Map.Entry<String, String> settingsEntry : settings.entrySet()) {
            Utils.saveGeneric(settingsEntry.getKey(), settingsEntry.getValue());
        }
    }

    private static void saveGeneric(String key, String value) {
        if (!key.equalsIgnoreCase("REMINDER") && !key.equalsIgnoreCase("TipIndex")) {
            Utils.getLocalStorageForPreferences().edit().putString(key, value).commit();
        }
    }

    public static String getExpenseInfo(String expenseKey) {
        return Utils.getLocalStorageForPreferences().getString(expenseKey, "");
    }

    public static void markSettingsForSyncing(Boolean forSyncing) {
        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_MARK_SETTINGS_SYNC", forSyncing.toString()).commit();
        if (!forSyncing)
            Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_SETTINGS_BACKED_UP_LAST", String.valueOf(today().getTime())).commit();
    }

    public static boolean isSettingsForSyncing() {
        String string = Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_SETTINGS_SYNC", "false");
        return Boolean.valueOf(string);
    }

    public static void markExpenseForSyncing(String expenseKey) {
        String expenseKeys = Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_EXPENSE_SYNC", "");
        if (expenseKeys.contains(expenseKey)) {
            return;
        }
        String expenseKeysForSyncing = isEmpty(expenseKeys) ? expenseKey : expenseKeys + "," + expenseKey;
        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_MARK_EXPENSE_SYNC", expenseKeysForSyncing).commit();
    }

    public static void expenseSyncingDone(String storageKey) {

        String expenseKeys = Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_EXPENSE_SYNC", "");
        String[] split = expenseKeys.split(",");
        String remaining = "";
        for (String s : split) {
            if (!s.equalsIgnoreCase(storageKey)) {
                remaining += s + ",";
            }
        }
        if (remaining.lastIndexOf(",") > -1) {
            remaining = remaining.substring(0, remaining.length() - 1);
        }


        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_MARK_EXPENSE_SYNC", remaining).commit();


        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_EXPENSE_BACKED_UP_LAST" + storageKey, String.valueOf(today().getTime())).commit();

    }


    public static String getExpenseForSyncing() {
        return Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_EXPENSE_SYNC", "");

    }

    public static boolean isExpenseForSyncing() {
        String string = Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_EXPENSE_SYNC", "");
        return !isEmpty(string);
    }

    private static boolean isExpenseForSyncing(String storageKey) {
        return Utils.getLocalStorageForPreferences().getString("GOOGLE_MARK_EXPENSE_SYNC", "")
                .toLowerCase().contains(storageKey.toLowerCase());
    }


    public static String getLastSettingBackedOn() {
        String lastBackedOn = Utils.getLocalStorageForPreferences().getString("GOOGLE_SETTINGS_BACKED_UP_LAST", "0");
        Date date = new Date(Long.valueOf(lastBackedOn));
        if (lastBackedOn.equalsIgnoreCase("0")) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM hh:mm a");
        return format("Settings - Last backedup on : %s", simpleDateFormat.format(date));
    }

    public static void saveExpense(String expenseKey, String expenseSerialzedString, String expenseFor) {

        MonthWiseExpense backedUpExpenses = Utils.getDeserializedExpensesForAMonth(expenseSerialzedString);
        backedUpExpenses.setExpenseFor(expenseFor);
        Utils.getLocalStorageForPreferences().edit().putString(expenseKey, getSerializedExpenses(backedUpExpenses)).commit();
    }

    public static String getStoredNameForGoogle() {
        return Utils.getLocalStorageForPreferences().getString("GOOGLE_GoogleName", "Yours") + "-";
    }


    public static String getStoredNameForPrimaryGoogle() {
        return Utils.getLocalStorageForPreferences().getString("GOOGLE_GoogleName_primary", "PrimaryUser") + "-";
    }

    public static void setStoredNameForGoogle(String name) {
        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_GoogleName", name).commit();
    }


    public static void setStoredNameForPrimaryGoogle(String name) {
        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_GoogleName_primary", name).commit();
    }

    public static void enableSyncing(boolean enabled) {
        Utils.getLocalStorageForPreferences().edit().putBoolean("GOOGLE_Sync_enabled", enabled).commit();

    }

    public static boolean isSyncingEnabled() {
        return Utils.getLocalStorageForPreferences().getBoolean("GOOGLE_Sync_enabled", false);

    }

    public static void lastBackgroundSyncDone(String storedInGoogleFileName, String modifiedTime) {
        Utils.getLocalStorageForPreferences().edit().putString("GOOGLE_LASTSYNC_TIME_" + storedInGoogleFileName, modifiedTime).commit();
    }

    public static String lastBackgroundExpenseSyncDone(String storageKey) {

        String lastBackedOn = Utils.getLocalStorageForPreferences().getString("GOOGLE_EXPENSE_BACKED_UP_LAST" + storageKey, "0");
        Date date = new Date(Long.valueOf(lastBackedOn));
        if (lastBackedOn.equalsIgnoreCase("0")) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM hh:mm a");
        String lastBackup = format("Last Backup on : %s", simpleDateFormat.format(date));
        if (isExpenseForSyncing()) {
            String nextBackgroundTimer = nextBackgroundTimer();
            if (isExpenseForSyncing(storageKey) && !isEmpty(nextBackgroundTimer)) {
                lastBackup += format("\nLocal changes present.\n Next Backup at : %s", nextBackgroundTimer);
            }
        }
        return lastBackup;

    }


    private static void clearLastBackgroundSyncDone(String storageKey) {

        Map<String, Object> all = Utils.getLocalStorageForPreferences().getAll();
        for (String key : all.keySet()) {
            if (key.toLowerCase().contains(storageKey.toLowerCase()) && key.startsWith("GOOGLE_LASTSYNC_TIME_")) {
                Utils.getLocalStorageForPreferences().edit().remove(key).commit();
            }
        }


    }

    public static String lastBackgroundSyncDoneFor(String storedInGoogleFileName) {
        return Utils.getLocalStorageForPreferences().getString("GOOGLE_LASTSYNC_TIME_" + storedInGoogleFileName, "");
    }

    public static void recordBackgroundTimer() {
        Utils.getLocalStorageForPreferences().edit().putString("BACKUP_TIME", String.valueOf(today().getTime()));
    }

    public static String nextBackgroundTimer() {
        String lastBGRan = Utils.getLocalStorageForPreferences().getString("BACKUP_TIME", "0");
        Date date = new Date(Long.valueOf(lastBGRan));
        if (lastBGRan.equalsIgnoreCase("0")) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 15);
        date = cal.getTime();
        return simpleDateFormat.format(date);

    }
}
