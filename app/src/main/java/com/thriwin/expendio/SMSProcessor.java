package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.Date;

public class SMSProcessor {

    private AmountProcessor amountProcessor;


    SMSProcessor() {
        amountProcessor = new SMSAmountProcessor();
    }

    public Expense getExpense(String completMessages) {
        StringBuilder expModifiable = new StringBuilder(completMessages);
        Date spentOn = Utils.today();
        BigDecimal amountSpent = amountProcessor.extract(expModifiable);
        Expense expense = new Expense(amountSpent, spentOn, completMessages);
        expense.setTransactionType(TransactionType.DIGITAL);
        return expense;
    }
}
