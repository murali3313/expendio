package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmountProcessor extends Processor {
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
                firstOccurenceOfNumber = index;
            }
            index++;
        }
        return values.isEmpty() ? new BigDecimal(0) : getRupeesAndPaiseFormat(values);
    }

    protected void removeAllTextBetweenTwoNumerals(StringBuilder expModifiable, String[] allWords, Integer firstOccurenceOfNumber, int index) {
        if (firstOccurenceOfNumber >= 0) {
                for (int i = firstOccurenceOfNumber + 1; i <= index + 1; i++) {
                removeProcessedText(expModifiable, allWords[i]);
            }
        }
    }

    protected BigDecimal getRupeesAndPaiseFormat(List<BigDecimal> values) {
        String s = "";
        int index = 0;
        for (BigDecimal value : values) {
            if(value.toString().length()>=7)
                continue;
            s += index > 0 ? "." + value.toString() : value.toString();
            if (index == 1)
                break;
            index++;
        }
        return new BigDecimal(s);
    }
}
