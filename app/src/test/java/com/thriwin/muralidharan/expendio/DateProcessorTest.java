package com.thriwin.expendio;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.thriwin.expendio.Utils.splitStatementBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DateProcessorTest {
    DateProcessor dateProcessor = new DateProcessor();

    @Test
    public void shouldExtractDateFromTheStatement1() {


        StringBuilder expenseStatement = new StringBuilder("Today Spent 200 rupees for Bakery");
        Date date = dateProcessor.extract(expenseStatement);
        Date today = new Date();
        Date yesterday = yesterday();
        assertDay(date, today.getDate(), today.getMonth(), today.getYear(), expenseStatement, "Today");

        expenseStatement = new StringBuilder(" Spent 200 rupees for Bakery Yesterday");
        date = dateProcessor.extract(expenseStatement);
        assertDay(date, yesterday.getDate(), yesterday.getMonth(), yesterday.getYear(), expenseStatement, "Yesterday");


        expenseStatement = new StringBuilder(" Spent 200 rupees for Bakery on 26 march");
        date = dateProcessor.extract(expenseStatement);
        assertDay(date, 26, 3, today.getYear(), expenseStatement, "26 march");

        expenseStatement = new StringBuilder(" Spent 200 rupees for Bakery on 26th march");
        date = dateProcessor.extract(expenseStatement);
        assertDay(date, 26, 3, today.getYear(), expenseStatement, "26th march");

        expenseStatement = new StringBuilder(" Spent 200 rupees for Bakery on  march 26th");
        date = dateProcessor.extract(expenseStatement);
        assertDay(date, 26, 3, today.getYear(), expenseStatement, "march 26th");

        expenseStatement = new StringBuilder(" Spent 200 rupees for Bakery on  26-02-2017");
        date = dateProcessor.extract(expenseStatement);
        Date expectedDate = new Date(Date.parse("2017/2/26"));

        assertDay(date, expectedDate.getDate(), expectedDate.getMonth(), expectedDate.getYear(), expenseStatement, "26-02-2017");
    }

    @Test
    public void shouldSplitExpensesOnAndWordCaseInsensitive() {
        String[] allWords = splitStatementBy("this expense_time_view and that expense_time_view AND these expense_time_view AnD some other expense_time_view", "and");
        assertThat(allWords.length, is(4));
    }

    private void assertDay(Date processedDate, int date, int month, int year, StringBuilder expenseStatement, String removedText) {
        assertThat(processedDate.getDate(), is(date));
        assertThat(processedDate.getMonth() , is(month));
        assertThat(processedDate.getYear(), is(year));
        assertFalse(expenseStatement.indexOf(removedText) > -1);
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

}