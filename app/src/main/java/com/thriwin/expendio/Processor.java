package com.thriwin.expendio;

import java.math.BigDecimal;

public abstract class Processor {
    protected int getNumberFrom(String word) {
        String number = "";
        for (Character character : word.toCharArray()) {
            if (Character.isDigit(character)) {
                number += character;
            }
        }
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }

    protected BigDecimal getDecimalFrom(String word) {
        String number = "";
        for (Character character : word.toCharArray()) {
            if (Character.isDigit(character) || character.equals('.')) {
                number += character;
            }
        }
        return number.isEmpty() ? new BigDecimal(0) : new BigDecimal(number);
    }

    protected void removeProcessedText(StringBuilder expenseStatement, String word) {
        int startingIndex = expenseStatement.indexOf(word);
        if (startingIndex != -1) {
            expenseStatement.replace(startingIndex, startingIndex + word.length(), "");
        }
    }
}
