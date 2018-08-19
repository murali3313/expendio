package com.thriwin.expendio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        number = pruneDots(number);
        return number.isEmpty() ? new BigDecimal(0) : new BigDecimal(number);
    }

    private String pruneDots(String number) {
        List<Integer> dotPosition = new ArrayList<>();
        char[] chars = number.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                dotPosition.add(i);
            }
        }
        if (dotPosition.size() > 1) {
            number = number.replaceFirst(".", "");
            pruneDots(number);
        }
        return number;
    }

    protected void removeProcessedText(StringBuilder expenseStatement, String word) {
        int startingIndex = expenseStatement.indexOf(word);
        if (startingIndex != -1) {
            expenseStatement.replace(startingIndex, startingIndex + word.length(), "");
        }
    }
}
