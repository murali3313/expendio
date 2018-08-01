package com.nandhakumargmail.muralidharan.expendio;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ExpenseTest {

    @Test
    public void getTheStartDayOfTheMonth() {
        Expense expense = new Expense();
        assertDate(expense, 2018, 2, 4, 3, 3, 2, 2018);
        assertDate(expense, 2018, 2, 25, 3, 3, 2, 2018);
        assertDate(expense, 2018, 2, 2, 3, 3, 1, 2018);
        assertDate(expense, 2018, 0, 2, 3, 3, 11, 2017);

    }


 @Test
    public void getTheEndDayOfTheMonth() {
        Expense expense = new Expense();
        assertEndDate(expense, 2018, 2, 4, 3, 2, 3, 2018);
        assertEndDate(expense, 2018, 2, 25, 3, 2, 3, 2018);
        assertEndDate(expense, 2018, 2, 2, 3, 2, 2, 2018);
        assertEndDate(expense, 2018, 0, 2, 3, 2, 0, 2018);
        assertEndDate(expense, 2018, 1, 2, 1, 28, 1, 2018);
        assertEndDate(expense, 2017, 11, 5, 2, 1, 0, 2018);

    }

    private void assertDate(Expense expense, int year, int spentMonth, int spentDay, int startDayOfMonth, int expectedDay, int expectedMonth, int expectedYear) {
        expense.setSpentOn(new Date(year - 1900, spentMonth, spentDay));

        long startDate = expense.getStartDate();
        Date date = new Date(startDate);
        assertThat(date.getDate(), is(expectedDay));
        assertThat(date.getMonth(), is(expectedMonth));
        assertThat(date.getYear(), is(expectedYear - 1900));
    }

    private void assertEndDate(Expense expense, int year, int spentMonth, int spentDay, int startDayOfMonth, int expectedDay, int expectedMonth, int expectedYear) {
        expense.setSpentOn(new Date(year - 1900, spentMonth, spentDay));
        long startDate = expense.getEndDate();
        Date date = new Date(startDate);
        assertThat(date.getDate(), is(expectedDay));
        assertThat(date.getMonth(), is(expectedMonth));
        assertThat(date.getYear(), is(expectedYear - 1900));
    }

}