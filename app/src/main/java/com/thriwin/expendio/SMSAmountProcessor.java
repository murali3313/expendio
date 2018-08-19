package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SMSAmountProcessor extends AmountProcessor {
    public BigDecimal extract(StringBuilder expModifiable) {
        String[] allWords = Utils.splitStatementBy(expModifiable.toString(), " ");
        List<BigDecimal> values = new ArrayList<>();
        Integer firstOccurenceOfNumber = -1;
        int index = 0;
        for (String word : allWords) {
            BigDecimal decimalFrom = getDecimalFrom(word);
            if (decimalFrom.compareTo(new BigDecimal(0)) > 0) {
                removeAllTextBetweenTwoNumerals(expModifiable, allWords, firstOccurenceOfNumber, index);
                values.add(decimalFrom);
                removeProcessedText(expModifiable, word);
                break;
            }
            index++;
        }
        return values.isEmpty() ? new BigDecimal(0) : getRupeesAndPaiseFormat(values);
    }
}
