package com.nandhakumargmail.muralidharan.expendio;

import android.support.annotation.NonNull;
import android.util.ArraySet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import static com.nandhakumargmail.muralidharan.expendio.Utils.isEmpty;
import static com.nandhakumargmail.muralidharan.expendio.Utils.isNull;
import static java.lang.String.format;
import static java.lang.String.join;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expense {
    private BigDecimal amountSpent;
    private Date spentOn;
    private List<String> spentFor = new ArrayList<>();
    private String expenseStatement;
    private Set<String> associatedExpenseTags = new ArraySet<>();

    public Expense() {
        this.spentOn = new Date();
        this.amountSpent = new BigDecimal("0");
        this.expenseStatement = "";
    }

    public Expense(BigDecimal amountSpent, Date spentOn, List<String> spentFor, String expenseStatement) {
        this.amountSpent = amountSpent;
        this.spentOn = spentOn;
        this.spentFor = spentFor;
        this.expenseStatement = expenseStatement;
        addTags();
    }

    public Expense(Date spentDate) {
        this();
        this.spentOn = spentDate;
    }

    @JsonIgnore
    public Integer spentYear() {
        return this.spentOn.getYear() + 1900;
    }

    @JsonIgnore
    public int spentMonth() {
        return this.spentOn.getMonth();
    }

    @JsonIgnore
    public int spentDay() {
        return this.spentOn.getDate();
    }

    @JsonIgnore
    public String getSpentOnDisplayText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd - MMM");
        return simpleDateFormat.format(this.spentOn);
    }

    @JsonIgnore
    public void setSpentOnBy(int year, int month, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = getDateFormat();
        try {
            this.spentOn = simpleDateFormat.parse(format("%s/%s/%s", dayOfMonth, month + 1, year));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @JsonIgnore
    private SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    @JsonIgnore
    public String getSpentForDisplayText() {
        String spentForDisplayableText = "";
        for (String s : spentFor) {
            spentForDisplayableText += s + " ";
        }
        return spentForDisplayableText;
    }

    @JsonIgnore
    public void addTags() {
        if (!isNull(spentFor) && !spentFor.isEmpty())

            this.associatedExpenseTags = ExpenseTags.getAssociatedExpenseTags(spentFor);
    }

    public void setSpentFor(List<String> words) {
        this.spentFor = words;
        addTags();
    }

    @JsonIgnore
    public String getStorageKey() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedString = simpleDateFormat.format(this.getStartDate());
        String month = formattedString.substring(formattedString.indexOf("/") + 1, formattedString.lastIndexOf("/")).toUpperCase();
        return format("Expense-%s-%d", month, this.spentYear());
    }


    public String getAmountSpent() {

        return isNull(this.amountSpent) || this.amountSpent.equals(new BigDecimal(0)) ? "" : this.amountSpent.toString();
    }

    public String getDateMonth() {
        return new SimpleDateFormat("MM-dd").format(this.spentOn);
    }

    @JsonIgnore
    public String getDateMonthHumanReadable() {
        return new SimpleDateFormat("dd-MMM").format(this.spentOn);
    }

    public Set<String> getAssociatedExpenseTags() {
        if (isNull(this.associatedExpenseTags) || this.associatedExpenseTags.isEmpty()) {
            ArraySet<String> spenFor = new ArraySet<>();
            spenFor.add(this.getSpentForDisplayText());
            return spenFor;
        } else {
            return this.associatedExpenseTags;
        }
    }

    @JsonIgnore
    public long getStartDate() {
        Date currentDate = this.getSpentOn();
        int startDayOfMonth = getStartDayOfMonth();
        if (this.spentDay() < startDayOfMonth) {
            int month = this.spentMonth() - 1;
            if (month < 0) {
                currentDate.setYear(spentYear() - 1);
                currentDate.setMonth(11);
            } else {
                currentDate.setMonth(month);
            }
        }
        currentDate.setDate(startDayOfMonth);

        return currentDate.getTime();
    }

    @JsonIgnore
    public long getEndDate() {
        Integer month = this.spentMonth();
        Integer year = this.spentYear();
        int startDayOfMonth = getStartDayOfMonth();
        if (this.spentDay() < startDayOfMonth) {
            int month1 = this.spentMonth() - 1;
            if (month1 < 0) {
                year = this.spentYear() - 1;
                month = 11;
            } else {
                month = month1;
            }
        }
        GregorianCalendar g = new GregorianCalendar(year, month, startDayOfMonth);
        g.add(Calendar.MONTH, 1);
        g.add(Calendar.DATE, -1);


        return g.getTimeInMillis();
    }

    public Date getSpentOn() {
        return new Date(this.spentOn.getTime());
    }

    @JsonIgnore
    public static int getStartDayOfMonth() {
        return Utils.getLocalStorageForPreferences().getInt("startDayOfMonth", 1);
    }

    public void santiseData() {
        if (spentFor.isEmpty() || isEmpty(spentFor.get(0))) {
            spentFor.clear();
            spentFor.add("Miscellaneous");
        }
        if (isEmpty(expenseStatement.trim()))
            this.expenseStatement = "Miscellaneous";

    }
}
