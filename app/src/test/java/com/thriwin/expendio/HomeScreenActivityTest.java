package com.thriwin.expendio;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HomeScreenActivityTest {

    @Test
    public void displayExpenseForCorrection() {
        String s = Utils.serializeExpenses(new Expenses(new Expense(new BigDecimal("200.30"), new Date(),  "342432424")));

        assertTrue(s.contains("[{\"amountSpent\":\"200.30\""));
        assertTrue(s.contains("\"spentFor\":[],\"expenseStatement\":\"342432424\""));
    }

}