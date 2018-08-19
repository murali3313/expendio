package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SMSProcessor {

    private DateProcessor dateProcessor;
    private ReasonProcessor reasonProcessor;
    private AmountProcessor amountProcessor;


    SMSProcessor() {
        dateProcessor = new DateProcessor();
        amountProcessor = new SMSAmountProcessor();
        reasonProcessor = new ReasonProcessor(Utils.getLocalStorageForPreferences());
    }

    public Expense getExpense(String completMessages) {
        StringBuilder expModifiable = new StringBuilder(completMessages);
        Date spentOn = dateProcessor.extract(expModifiable);
        BigDecimal amountSpent = amountProcessor.extract(expModifiable);
        List<String> spentFor = reasonProcessor.extract(expModifiable);
        return new Expense(amountSpent, spentOn, spentFor, completMessages);
    }
}
