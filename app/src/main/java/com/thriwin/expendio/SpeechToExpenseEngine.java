package com.thriwin.expendio;

import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpeechToExpenseEngine {

    DateProcessor dateProcessor;
    ReasonProcessor reasonProcessor;
    AmountProcessor amountProcessor;

    public SpeechToExpenseEngine(SharedPreferences localStorageForPreferences) {
        dateProcessor = new DateProcessor();
        amountProcessor = new AmountProcessor();
        reasonProcessor = new ReasonProcessor(localStorageForPreferences);
    }


    public List<Expense> processAudio(String userStatements) {
        String[] multipleExpenseStatements = Utils.splitStatementBy(userStatements, " and ");
        List<Expense> expenses = new ArrayList<>();
        for (String expenseStatement : multipleExpenseStatements) {
            if (expenseStatement.trim().equals(""))
                continue;
            StringBuilder expModifiable = new StringBuilder(expenseStatement);
            Date spentOn = dateProcessor.extract(expModifiable);
            BigDecimal amountSpent = amountProcessor.extract(expModifiable);
            List<String> spentFor = reasonProcessor.extract(expModifiable);
            expenses.add(new Expense(amountSpent, spentOn, spentFor, expenseStatement));
        }
        return expenses;
    }


}
