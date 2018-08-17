package com.thriwin.expendio;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecurringExpenses extends ArrayList<RecurringExpense> {

    public Expenses getTodaysExpenses() {
        Expenses unApprovedRecurringExpenses = new Expenses();
        for (RecurringExpense recurringExpense : this) {
            if (recurringExpense.isValidForToday()) {
                unApprovedRecurringExpenses.add(recurringExpense.getExpense());
            }
        }
        return unApprovedRecurringExpenses;
    }
}
