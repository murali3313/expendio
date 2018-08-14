package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.thriwin.expendio.Utils.isNull;

@Setter
@Getter
public class RecurringExpense {
    RecurringExpenseType recurringType;
    private BigDecimal amount;
    private String reason;
    private List<String> dayOfWeek;
    private String dayOfMonth;

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
}
