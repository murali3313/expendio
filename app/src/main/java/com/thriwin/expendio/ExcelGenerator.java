package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.NonNull;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import static java.lang.String.format;

public class ExcelGenerator {


    public File genarateExcelForMonthExpenses(Context context, MonthWiseExpense monthWiseExpense, String fileName) {
        Workbook wb = new HSSFWorkbook();
        SortedMap<String, MonthWiseExpense> allSharedExpensesFor = Utils.getAllSharedExpensesFor(monthWiseExpense.getStorageKey());
        for (Map.Entry<String, MonthWiseExpense> otherUserExpenses : allSharedExpensesFor.entrySet()) {
            monthWiseExpense.merge(otherUserExpenses.getValue());
        }
        createMonthWiseExpenseSheet(monthWiseExpense, wb);
        File file = saveFile(context, fileName, wb);
        return file;
    }

    public File genarateExcelForAllMonths(Context context, SortedMap<String, MonthWiseExpense> allmonthWiseExpenses, String fileName) {
        Workbook wb = new HSSFWorkbook();
        for (Map.Entry<String, MonthWiseExpense> monthWiseExpenses : allmonthWiseExpenses.entrySet()) {
            MonthWiseExpense currentUserMonthWiseExpenses = monthWiseExpenses.getValue();
            SortedMap<String, MonthWiseExpense> allSharedExpensesFor = Utils.getAllSharedExpensesFor(monthWiseExpenses.getKey());
            for (Map.Entry<String, MonthWiseExpense> otherUserExpenses : allSharedExpensesFor.entrySet()) {
                currentUserMonthWiseExpenses.merge(otherUserExpenses.getValue());
            }

            createMonthWiseExpenseSheet(currentUserMonthWiseExpenses, wb);
        }

        File file = saveFile(context, fileName, wb);
        return file;
    }

    private void createMonthWiseExpenseSheet(MonthWiseExpense monthWiseExpense, Workbook wb) {
        Sheet sheet = wb.createSheet(monthWiseExpense.getMonthYearHumanReadable());
        createHeaderRow(wb, sheet, monthWiseExpense.getTotalExpenditure());
        createDataRows(wb, sheet, monthWiseExpense);
    }

    @NonNull
    private File saveFile(Context context, String fileName, Workbook wb) {
        File file = new File(context.getExternalFilesDir(null), fileName + ".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
        } catch (IOException e) {
        } catch (Exception e) {
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return file;
    }

    private void createDataRows(Workbook wb, Sheet sheet, MonthWiseExpense monthWiseExpense) {
        Integer initialRows = 1;
        for (Expenses dayWiseExpenses : monthWiseExpense.getSortedDayWiseExpenses()) {
            for (int i = 0; i < dayWiseExpenses.size(); i++) {
                Row row = sheet.createRow(i + initialRows);
                Expense expense = dayWiseExpenses.get(i);
                for (int j = 0; j < Expense.headerColumns.size(); j++) {
                    createCellForValue(wb, row, expense, i, j, dayWiseExpenses.size());
                }
            }
            if (dayWiseExpenses.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(initialRows, initialRows + dayWiseExpenses.size() - 1,
                        Expense.headerColumns.size() - 1, Expense.headerColumns.size() - 1));
            }
            Cell dayWiseTotalCell = sheet.getRow(initialRows).getCell(Expense.headerColumns.size() - 1);
            dayWiseTotalCell.setCellFormula(format("SUMIF(B:B,\"%s\",C:C)", dayWiseExpenses.getDateMonthHumanReadable()));
            initialRows += dayWiseExpenses.size();
        }
    }

    private void createCellForValue(Workbook wb, Row row, Expense expense, int rowIndex, int cellIndex, int totalRowCount) {
        Cell c = row.createCell(cellIndex);
        CellStyle oCellStyle = wb.createCellStyle();
        oCellStyle.setAlignment(HorizontalAlignment.CENTER);
        oCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        oCellStyle.setWrapText(true);
        if (Expense.headerColumns.get(cellIndex).equals("Amount")) {
            c.setCellValue(Double.valueOf(expense.getValue(Expense.headerColumns.get(cellIndex))));
        } else {
            c.setCellValue(expense.getValue(Expense.headerColumns.get(cellIndex)));
        }

        if (cellIndex == 0) {
            oCellStyle.setBorderLeft(BorderStyle.THIN);
        }
        if (cellIndex == Expense.headerColumns.size() - 1) {
            oCellStyle.setBorderRight(BorderStyle.THIN);
        }
        if (rowIndex == totalRowCount - 1) {
            oCellStyle.setBorderBottom(BorderStyle.THIN);
        }
        c.setCellStyle(oCellStyle);
    }

    private void createHeaderRow(Workbook wb, Sheet sheet, String totalExpenditure) {
        CellStyle oCellStyle = getCellStyle(wb, IndexedColors.LIME);
        Row row = sheet.createRow(0);

        for (int i = 0; i < Expense.headerColumns.size(); i++) {
            Cell c = row.createCell(i);
            c.setCellStyle(oCellStyle);
            if (i == Expense.headerColumns.size() - 1) {
                c.setCellValue(String.format("%s:", Expense.headerColumns.get(i)));
                c = row.createCell(i + 1);
                CellStyle cellStyle = getCellStyle(wb, IndexedColors.ORANGE);
                c.setCellFormula("SUM(G:G)");
                c.setCellStyle(cellStyle);
            } else {
                c.setCellValue(Expense.headerColumns.get(i));
            }
        }
    }

    @NonNull
    private CellStyle getCellStyle(Workbook wb, IndexedColors color) {
        CellStyle oCellStyle = wb.createCellStyle();
        oCellStyle.setBorderBottom(BorderStyle.THIN);
        oCellStyle.setBorderTop(BorderStyle.THIN);
        oCellStyle.setBorderLeft(BorderStyle.THIN);
        oCellStyle.setBorderRight(BorderStyle.THIN);
        oCellStyle.setAlignment(HorizontalAlignment.CENTER);
        oCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        oCellStyle.setFillForegroundColor(color.getIndex());
        oCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        oCellStyle.setWrapText(true);
        return oCellStyle;
    }
}
