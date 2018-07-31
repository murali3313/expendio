package com.nandhakumargmail.muralidharan.expendio;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AmountProcessorTest {

    AmountProcessor amountProcessor = new AmountProcessor();

    @Test
    public void shouldCollectAllNumerics() {
        StringBuilder expenseStatement = new StringBuilder("Spent 200 rupees for Bakery");
        BigDecimal amount = amountProcessor.extract(expenseStatement);
        assertThat(amount, is(new BigDecimal(200)));
        assertFalse(expenseStatement.indexOf("200") > -1);

        expenseStatement = new StringBuilder("Spent 200.20  for Bakery");
        amount = amountProcessor.extract(expenseStatement);
        assertThat(amount, is(new BigDecimal("200.20")));
        assertFalse(expenseStatement.indexOf("200.20") > -1);

        expenseStatement = new StringBuilder("Spent 200 rupees 20 paise  for Bakery");
        amount = amountProcessor.extract(expenseStatement);
        assertThat(amount, is(new BigDecimal("200.20")));
        assertFalse(expenseStatement.indexOf("200 rupees 20 paise") > -1);


        expenseStatement = new StringBuilder("200 rupees 20 paise  for Bakery");
        amount = amountProcessor.extract(expenseStatement);
        assertThat(amount, is(new BigDecimal("200.20")));
        assertFalse(expenseStatement.indexOf("200 rupees 20 paise") > -1);
    }

}