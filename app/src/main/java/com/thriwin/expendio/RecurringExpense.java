package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.thriwin.expendio.Utils.isNull;
import static com.thriwin.expendio.Utils.today;

@Setter
@Getter
public class RecurringExpense {
    RecurringExpenseType recurringType;
    private BigDecimal amount;
    private String reason;
    private List<String> dayOfWeek;
    private String dayOfMonth;
    private TransactionType transactionType = TransactionType.CASH;

    public BigDecimal getAmount() {
        return isNull(amount) ? new BigDecimal("0") : amount;
    }

    public RecurringExpense() {
        this.recurringType = RecurringExpenseType.DAILY;
    }

    public RecurringExpense(BigDecimal amount, String reason) {
        this.amount = amount;
        this.reason = reason;
    }

    @JsonIgnore
    public boolean isValidForToday() {
        Date today = Utils.today();
        String todayName = new SimpleDateFormat("EEE").format(today);
        boolean isValid = false;
        switch (recurringType) {
            case DAILY:
                isValid = true;
                break;
            case SPECIFIC_DAY_OF_WEEK:
                isValid = this.dayOfWeek.contains(todayName);
                break;
            case SPECIFIC_DAY_OF_MONTH:
                isValid = today.getDate() == Integer.parseInt(dayOfMonth);
                break;
        }
        return isValid;
    }

    @JsonIgnore
    public Expense getExpense() {
        Expense expense = new Expense(amount, today(), reason);
        expense.setTransactionType(transactionType);
        return expense;
    }

    @JsonIgnore
    public boolean isCashTransaction() {
        return this.transactionType.equals(TransactionType.CASH);
    }

}
