package com.thriwin.expendio;

import android.os.Handler;
import android.os.Message;

import java.util.List;

import lombok.Getter;

import static com.thriwin.expendio.Utils.isNull;
import static java.util.Arrays.asList;

public class ProcessPastedExpenses extends Thread {
    private String restorableExpenses;
    private Handler handler;
    @Getter
    Expenses expenses = new Expenses();

    public ProcessPastedExpenses(String restorableExpenses, Handler handler) {
        this.restorableExpenses = restorableExpenses;
        this.handler = handler;
    }


    private Expense processExpense(String eachLine, List<String> headers) {
        Expense expense = new Expense();
        String[] expenseSplit = eachLine.split("\t");
        for (int i = 0; i < headers.size(); i++) {
            try {
                expense.parse(headers.get(i), expenseSplit[i]);
            } catch (Exception e) {

            }
        }

        return expense.isParseValid() ? expense : null;
    }

    private List<String> isHeaderIncluded(String firstLine) {
        List<String> headers = asList(firstLine.split("\t"));
        for (String headerColumn : Expense.headerColumns) {
            if (!headers.contains(headerColumn) && !headerColumn.equalsIgnoreCase("Total"))
                return asList();
        }

        return headers;
    }

    @Override
    public void run() {
        String[] allLines = new String[0];

        if (!Utils.isEmpty(restorableExpenses)) {
            try {
                allLines = restorableExpenses.split("\n");
                List<String> headers = isHeaderIncluded(allLines[0]);
                if (headers.size() == 0) {
                    throw new RuntimeException("Paste the expense with the headers.");
                }
                for (int i = 1; i < allLines.length; i++) {
                    Expense e = processExpense(allLines[i], headers);
                    if (!isNull(e))
                        expenses.add(e);
                }

            } catch (Exception e) {

            }
        }

        Message msg = new Message();
        msg.obj = expenses;
        handler.sendMessage(msg);

    }
}
