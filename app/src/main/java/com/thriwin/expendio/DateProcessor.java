package com.thriwin.expendio;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class DateProcessor extends Processor {

    public static List<String> allMonths = asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");
    private List<String> colloquialDays = asList("today", "yesterday", "tomorrow");

    protected Date extract(StringBuilder expenseStatement) {
        String[] allWords = Utils.splitStatementBy(expenseStatement.toString(), " ");
        Date resultantDate = null;
        Integer index = 0;
        for (String word : allWords) {
            resultantDate = getDateFromColloquialFormat(expenseStatement, resultantDate, word);
            if (Utils.isNull(resultantDate)) {
                resultantDate = getDateFromMonthText(expenseStatement, allWords, resultantDate, index, word);
            }
            if (Utils.isNull(resultantDate)) {
                resultantDate = getDateFromFormattedDate(expenseStatement, allWords, resultantDate, index, word);
            }
            if (!Utils.isNull(resultantDate)) {
                return resultantDate;
            }
            index++;
        }
        return Utils.today();
    }

    private Date getDateFromFormattedDate(StringBuilder expenseStatement, String[] allWords, Date resultantDate, Integer index, String word) {
        Date date = null;
        List<SimpleDateFormat> simpleDateFormats = asList(new SimpleDateFormat("dd-MM-yyyy"),
                new SimpleDateFormat("MM-dd-yyyy"), new SimpleDateFormat("yyyy-MM-dd"));

        for (SimpleDateFormat dateFormat : simpleDateFormats) {
            try {
                date = dateFormat.parse(word);
                removeProcessedText(expenseStatement, word);
                break;
            } catch (Exception e) {

            }
        }

        return date;
    }

    private Date getDateFromMonthText(StringBuilder expenseStatement, String[] allWords, Date
            resultantDate, Integer index, String word) {
        String monthSpecified = getMonthSpecified(word);
        if (Utils.isNull(resultantDate) && !Utils.isEmpty(monthSpecified)) {
            if (index > 0) {
                int previousNumber = getNumberFrom(allWords[index - 1]);
                resultantDate = inferDate(resultantDate, monthSpecified, previousNumber);
                if (!Utils.isNull(resultantDate)) {
                    removeMonthAndDateText(expenseStatement, allWords, resultantDate, word, index - 1);

                }
                if (Utils.isNull(resultantDate)) {
                    int afterNumber = getNumberFrom(allWords[index + 1]);
                    resultantDate = inferDate(resultantDate, monthSpecified, afterNumber);
                    if (!Utils.isNull(resultantDate)) {
                        removeMonthAndDateText(expenseStatement, allWords, resultantDate, word, index + 1);

                    }
                }
            }
        }
        return resultantDate;
    }

    private void removeMonthAndDateText(StringBuilder expenseStatement, String[] allWords, Date
            resultantDate, String word, int beforeOrAfter) {
        if (!Utils.isNull(resultantDate)) {
            removeProcessedText(expenseStatement, allWords[beforeOrAfter]);
            removeProcessedText(expenseStatement, word);
        }
    }

    private Date inferDate(Date resultantDate, String monthSpecified, int dateNumber) {
        if (dateNumber > 0 && dateNumber <= 31) {
            resultantDate = new Date(Utils.today().getYear(), allMonths.indexOf(monthSpecified), dateNumber);
        }
        return resultantDate;
    }

    private Date getDateFromColloquialFormat(StringBuilder expenseStatement, Date
            resultantDate, String word) {
        String colloquialDay = getColloquialDay(word);
        if (!Utils.isEmpty(colloquialDay)) {
            switch (colloquialDay) {
                case "today":
                    resultantDate = new Date();
                    break;
                case "tomorrow":
                    resultantDate = Utils.tomorrow();
                    break;
                case "yesterday":
                    resultantDate = Utils.yesterday();
                    break;
            }
            removeProcessedText(expenseStatement, word);
        }
        return resultantDate;
    }

    private String getMonthSpecified(String word) {
        return containsSpecifiWord(word, allMonths);
    }

    private String getColloquialDay(String word) {
        return containsSpecifiWord(word, colloquialDays);
    }

    @NonNull
    private String containsSpecifiWord(String word, List<String> searchWords) {
        for (String month : searchWords) {
            if (month.equalsIgnoreCase(word))
                return word.toLowerCase();
        }
        return "";
    }
}
