package com.thriwin.expendio;

import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.poi.util.StringUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import static com.thriwin.expendio.ExpenseTags.MISCELLANEOUS_TAG;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expense {
    private BigDecimal amountSpent;
    private Date spentOn;
    private String expenseStatement;
    private Set<String> associatedExpenseTags = new ArraySet<>();
    public static List<String> headerColumns = asList("Spent by", "Spent On", "Amount", "Reason", "Tags", "Transaction Type", "Total");

    private TransactionType transactionType = TransactionType.CASH;
    private String spentBy = "You";

    public Expense() {
        this.spentOn = new Date();
        this.amountSpent = new BigDecimal("0");
        this.expenseStatement = "";
        addTags();
    }

    public Expense(BigDecimal amountSpent, Date spentOn, String expenseStatement) {
        this.amountSpent = amountSpent;
        this.spentOn = spentOn;
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
        expenseStatement = Utils.isEmpty(expenseStatement) ? "" : expenseStatement;
        return expenseStatement;
    }

    @JsonIgnore
    public void addTags() {
        this.associatedExpenseTags = ExpenseTags.getAssociatedExpenseTags(expenseStatement);
    }

    @JsonIgnore
    public String getStorageKey() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedString = simpleDateFormat.format(this.getStartDate());
        String month = formattedString.substring(formattedString.indexOf("/") + 1, formattedString.lastIndexOf("/")).toUpperCase();
        return format("Expense-%d-%s", this.spentYear(), month);
    }

    @JsonIgnore
    public String getStorageKeyForUser(String userName) {
        String storageKey = getStorageKey();
        return userName + "-" + storageKey;
    }

    public String getAmountSpent() {
        return Utils.isNull(this.amountSpent) || this.amountSpent.equals(new BigDecimal(0)) ? "" : this.amountSpent.toString();
    }

    @JsonIgnore
    public String getDateMonth() {
        return new SimpleDateFormat("MM-dd").format(this.spentOn);
    }

    @JsonIgnore
    public String getDateMonthHumanReadable() {
        return new SimpleDateFormat("dd - MMM").format(this.spentOn);
    }

    public Set<String> getAssociatedExpenseTags() {
        if (Utils.isNull(this.associatedExpenseTags) || this.associatedExpenseTags.isEmpty()) {
            ArraySet<String> spenFor = new ArraySet<>();
            spenFor.add(this.getSpentForDisplayText());
            return spenFor;
        } else {
            return this.associatedExpenseTags;
        }
    }

    @JsonIgnore
    public String getFirstAssociatedExpenseTag() {
        Set<String> associatedExpenseTags = this.getAssociatedExpenseTags();
        if (!isEmpty(associatedExpenseTags)) {
            return associatedExpenseTags.iterator().next();
        }
        return MISCELLANEOUS_TAG;
    }

    @JsonIgnore
    public String getConcatenatedTags() {
        String tags = "";
        Set<String> associatedExpenseTags = this.getAssociatedExpenseTags();
        if (Utils.isNull(associatedExpenseTags) || associatedExpenseTags.isEmpty()) {
            tags = "";
        } else {
            tags = StringUtil.join(associatedExpenseTags.toArray(), ", ");
        }
        return tags;
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
        return new ExpendioSettings().getStartDayOfMonth();
    }

    @JsonIgnore
    public void santiseData() {
        if (Utils.isEmpty(expenseStatement.trim()))
            this.expenseStatement = MISCELLANEOUS_TAG;

    }

    @JsonIgnore
    public String getMonthYearHumanReadable() {
        return new SimpleDateFormat("MMM - yyyy").format(this.spentOn);
    }

    @JsonIgnore
    public String getValue(String headerColumn) {
        String value = "";
        switch (headerColumn) {
            case "Spent by":
                value = this.spentBy;
                break;
            case "Spent On":
                value = getDateMonthHumanReadable();
                break;
            case "Reason":
                value = getExpenseStatement();
                break;
            case "Amount":
                String amountSpent = getAmountSpent();
                value = Utils.isEmpty(amountSpent) ? "0" : amountSpent;
                break;
            case "Tags":
                value = getConcatenatedTags();
                break;
            case "Transaction Type":
                value = this.transactionType.toString();
                break;
            default:
                break;
        }
        return value;
    }

    @JsonIgnore
    public boolean isValid() {
        return !this.getAmountSpent().equals("") && !this.getAmountSpent().equals("0");
    }

    @JsonIgnore
    public String getStringFormatForSharing() {
        return this.spentOn.getTime() + "|" + this.expenseStatement + "|" + this.amountSpent + "|" + (this.isCashTransaction() ? "C" : "D");
    }


    public static Expense parse(String message) {
        String[] split = message.split("\\|");
        try {

            Expense expense = new Expense();
            if (split.length == 3) {
                expenseWithoutTransactionType(split, expense);
                expense.addTags();
                return expense;
            }
            if (split.length == 4) {
                expenseWithoutTransactionType(split, expense);
                expense.transactionType = split[3].equalsIgnoreCase("D") ? TransactionType.DIGITAL : TransactionType.CASH;
                expense.addTags();
                return expense;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static void expenseWithoutTransactionType(String[] split, Expense expense) {
        expense.spentOn = new Date(Long.valueOf(split[0]));
        expense.expenseStatement = split[1];
        expense.amountSpent = new BigDecimal(split[2]);
    }

    public void setExpenseStatement(String expenseStatement) {
        this.expenseStatement = expenseStatement;
        addTags();
    }

    @JsonIgnore
    public boolean isCashTransaction() {
        return this.transactionType.equals(TransactionType.CASH);
    }

    @JsonIgnore
    public String getTransactionTypeDisplayText() {
        return this.transactionType.toString();
    }
}
