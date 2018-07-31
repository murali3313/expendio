package com.nandhakumargmail.muralidharan.expendio;

import android.support.annotation.NonNull;
import android.util.ArraySet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.isNull;

@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expense {
    @JsonIgnore
    private ExpenseTags expenseTags;
    private BigDecimal amountSpent;
    private Date spentOn;
    private List<String> spentFor = new ArrayList<>();
    private String expenseStatement;
    private Set<String> associatedExpenseTags = new ArraySet<>();

    public Expense(BigDecimal amountSpent, Date spentOn, List<String> spentFor, String expenseStatement) {
        this.amountSpent = amountSpent;
        this.spentOn = spentOn;
        this.spentFor = spentFor;
        this.expenseStatement = expenseStatement;
        this.expenseTags = new ExpenseTags(Utils.getLocalStorageForPreferences());
        addTags();
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
        if (!isNull(expenseTags) && !isNull(spentFor) && !spentFor.isEmpty())

            this.associatedExpenseTags = expenseTags.getAssociatedExpenseTags(spentFor);
    }

    public void setSpentFor(List<String> words) {
        this.spentFor = words;
        addTags();
    }

    @JsonIgnore
    public String getStorageKey() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMMM/yyyy");
        String formattedString = simpleDateFormat.format(this.spentOn);
        String month = formattedString.substring(formattedString.indexOf("/") + 1, formattedString.lastIndexOf("/")).toUpperCase();
        return format("Expense-%s-%d", month, this.spentYear());
    }


    public String getAmountSpent() {
        return this.amountSpent.equals(new BigDecimal(0)) ? "" : this.amountSpent.toString();
    }

    public String getDateMonth() {
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
}
