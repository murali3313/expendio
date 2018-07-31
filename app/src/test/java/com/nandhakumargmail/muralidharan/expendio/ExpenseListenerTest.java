package com.nandhakumargmail.muralidharan.expendio;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ExpenseListenerTest {

    @Test
    public void displayExpenseForCorrection() {
        ExpenseListener expenseListener = new ExpenseListener();
        String s = expenseListener.serializeExpenses(asList(new Expense(new BigDecimal("200.30"), new Date(), asList(), "342432424")));

        assertTrue(s.contains("[{\"amountSpent\":200.30,\"spentOn\""));
        assertTrue(s.contains("\"spentFor\":[],\"expenseStatement\":\"342432424\"}]"));
    }

}