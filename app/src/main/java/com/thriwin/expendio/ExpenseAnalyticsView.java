package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.thriwin.expendio.Utils.getAllExpensesMonths;
import static com.thriwin.expendio.Utils.getReadableMonthAndYear;
import static java.util.Arrays.asList;

public class ExpenseAnalyticsView extends LinearLayout implements IDisplayAreaView, OnChartValueSelectedListener {
    List<String> allExpensesMonths;
    boolean isDisplayingPieChart = true;
    private String selectedMonth;

    public ExpenseAnalyticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.expense_analytics, this);

    }

    @Override
    public void load(ExpenseListener expenseListener) {
        allExpensesMonths = getAllExpensesMonths();
        Expense expense = new Expense();
        expense.setSpentOn(new Date());
        String storageKeyForCurrentMonth = expense.getStorageKey();
        String currentMonth = loadCurrentMonthPieChart(storageKeyForCurrentMonth);
        loadMonthSelector(currentMonth);
        View barChartContainer = findViewById(R.id.barChartContainer);
        View pieChartContainer = findViewById(R.id.pieChartContainer);

        ImageButton barChartIcon = expenseListener.findViewById(R.id.bar_chart);
        barChartIcon.setBackgroundResource(R.mipmap.ic_bar_chart);
        barChartIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisplayingPieChart) {
                    barChartIcon.setBackgroundResource(R.mipmap.ic_pie_chart);
                    isDisplayingPieChart = false;
                    barChartContainer.setVisibility(VISIBLE);
                    pieChartContainer.setVisibility(GONE);
                    loadCurrentMonthBarChart(selectedMonth);
                } else {
                    barChartIcon.setBackgroundResource(R.mipmap.ic_bar_chart);
                    isDisplayingPieChart = true;
                    loadMonthSelector(selectedMonth);
                    loadCurrentMonthPieChart(selectedMonth);
                    barChartContainer.setVisibility(GONE);
                    pieChartContainer.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void loadCurrentMonthBarChart(String selectedMonth) {

    }

    private void loadMonthSelector(String currentMonth) {
        this.selectedMonth = currentMonth;
        LinearLayout monthContainer = findViewById(R.id.monthSelector);
        monthContainer.removeAllViews();
        int indexOfSelectedMonth = allExpensesMonths.indexOf(currentMonth);
        ImageButton leftArrow = new ImageButton(getContext(), null);
        ViewGroup.LayoutParams buttonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.width = 100;
        buttonParams.height = 100;
        leftArrow.setLayoutParams(buttonParams);
        leftArrow.setBackground(getResources().getDrawable(R.mipmap.ic_previous));
        ImageButton rightArrow = new ImageButton(getContext(), null);
        rightArrow.setBackground(getResources().getDrawable(R.mipmap.ic_next));
        rightArrow.setLayoutParams(buttonParams);
        int startFrom = indexOfSelectedMonth == 0 ? 0
                : indexOfSelectedMonth < allExpensesMonths.size() - 1 ? indexOfSelectedMonth - 1
                : allExpensesMonths.size() > 2 ? indexOfSelectedMonth - 2 : indexOfSelectedMonth - 1;
        int endAt = startFrom + 2 >= allExpensesMonths.size() ? allExpensesMonths.size() - 1 : startFrom + 2;
        if (startFrom > 0) {
            monthContainer.addView(leftArrow);
            leftArrow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMonthSelector(allExpensesMonths.get(startFrom));
                }
            });
        }
        for (int i = startFrom; i <= endAt; i++) {
            TextView textView = new TextView(this.getContext(), null);
            String expenseMonth = allExpensesMonths.get(i);
            String[] readableMonthAndYear = getReadableMonthAndYear(expenseMonth);
            textView.setText(String.format("%s - %s", readableMonthAndYear[0], readableMonthAndYear[1]));
            textView.setPadding(15, 5, 15, 5);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.item_border);
            if (expenseMonth.equals(currentMonth)) {
                textView.setBackgroundResource(R.drawable.item_border_selected);
            }
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCurrentMonthPieChart(expenseMonth);
                    loadMonthSelector(expenseMonth);
                }
            });
            monthContainer.addView(textView);
        }

        if (endAt < allExpensesMonths.size() - 1) {
            monthContainer.addView(rightArrow);
            rightArrow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMonthSelector(allExpensesMonths.get(endAt));
                }
            });
        }

    }

    private String loadCurrentMonthPieChart(String storageKeyForCurrentMonth) {

        MonthWiseExpenses monthExpenses = Utils.getDeserializedMonthWiseExpenses(storageKeyForCurrentMonth);

        Map<String, Expenses> tagBasedExpenses = monthExpenses.getTagBasedExpenses();
        PieChart pieChart = findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Expenses> tagBasedExpense : tagBasedExpenses.entrySet()) {
            entries.add(new PieEntry(Float.parseFloat(tagBasedExpense.getValue().getTotalExpenditure()), tagBasedExpense.getKey()));
        }

        PieDataSet set = new PieDataSet(entries, "Expense analysis");
        List<Integer> colors = getColorsForPieChart();

        set.setColors(colors);
        set.setValueTextColors(asList(getResources().getColor(R.color.white)));
        set.setValueTextSize(19);
        Description desc = new Description();
        desc.setText("Thriwin solutions.");
        pieChart.setDescription(desc);
        pieChart.setCenterTextColor(getResources().getColor(R.color.colorAccent));
        pieChart.setCenterText(String.format("Expendio\n%s\n$$ %s", monthExpenses.getMonthYearHumanReadable(), monthExpenses.getTotalExpenditure()));
        pieChart.setCenterTextSize(25);
        pieChart.setOnChartValueSelectedListener(this);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();
        return storageKeyForCurrentMonth;
    }

    @NonNull
    private List<Integer> getColorsForPieChart() {
        return new ArrayList<Integer>() {{
            add(getResources().getColor(R.color.colorAccent));
            add(getResources().getColor(R.color.colorAlternateDark1));
            add(getResources().getColor(R.color.colorPrimary));
            add(getResources().getColor(R.color.colorAlternateDark2));
            add(getResources().getColor(R.color.colorPrimaryDark));
        }};
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
