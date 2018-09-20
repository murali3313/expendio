package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.Utils.getReadableMonthAndYear;
import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class ExpenseAnalyticsView extends LinearLayout implements IDisplayAreaView, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {
    List<String> allExpensesMonths;
    boolean isDisplayingPieChart = true;
    public String selectedMonthStorageKey;
    private String comparingMonthStorageKey;
    private long selectedMonthIndex = 0l;
    private long comparingMonthIndex = 0l;
    private Integer tagBatchSize = 3;
    private Integer selectedTagBatch = 1;
    Map<String, Expenses> tagBasedExpenses;
    PieChart pieChart;
    BarChart barChart;
    boolean shouldIncludeOtherExpenses = false;
    Map<String, Expenses> viewableTagExpenses = null;

    public ExpenseAnalyticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.expense_analytics, this);
        pieChart = findViewById(R.id.chart);
        barChart = findViewById(R.id.groupedBarChart);

        pieChart.setNoDataTextColor(getResources().getColor(R.color.colorPrimaryDark));
        barChart.setNoDataTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void load(CommonActivity expenseListener, Intent intent) {
        selectedTagBatch = 1;
        Handler handler = new Handler(msg -> {

            Map<String, Object> dataFromThread = (Map<String, Object>) msg.obj;

            allExpensesMonths = (List<String>) dataFromThread.get("allExpensesMonths");
            String storageKeyForCurrentMonth = !isNull(intent) && !isEmpty(intent.getStringExtra("ANALYTICS_MONTH")) ?
                    intent.getStringExtra("ANALYTICS_MONTH") : isNull(selectedMonthStorageKey) ? allExpensesMonths.get(allExpensesMonths.size() - 1) : selectedMonthStorageKey;
            intent.putExtra("ANALYTICS_MONTH", "");
            if (allExpensesMonths.size() == 0) {
                return true;
            }


            loadPieChart(storageKeyForCurrentMonth);
            loadMonthSelector(storageKeyForCurrentMonth);

            ExpenseAnalyticsView.this.selectedMonthIndex = allExpensesMonths.indexOf(storageKeyForCurrentMonth);
            View barChartContainer = findViewById(R.id.barChartContainer);
            View pieChartContainer = findViewById(R.id.pieChartContainer);

            ImageButton barChartIcon = expenseListener.findViewById(R.id.bar_chart);
            barChartIcon.setBackgroundResource(R.drawable.ic_bar_chart);

            barChartIcon.setOnClickListener(v -> {
                LinearLayout userSelectorContainer;
                if (isDisplayingPieChart) {
                    barChartIcon.setBackgroundResource(R.drawable.ic_pie_chart);
                    isDisplayingPieChart = false;
                    barChartContainer.setVisibility(VISIBLE);
                    pieChartContainer.setVisibility(GONE);
                    loadCurrentMonthBarChart(selectedMonthStorageKey, comparingMonthStorageKey);
                    userSelectorContainer = findViewById(R.id.userSelectorInBarChart);
                } else {
                    barChartIcon.setBackgroundResource(R.drawable.ic_bar_chart);
                    isDisplayingPieChart = true;
                    loadMonthSelector(selectedMonthStorageKey);
                    loadPieChart(selectedMonthStorageKey);
                    barChartContainer.setVisibility(GONE);
                    pieChartContainer.setVisibility(VISIBLE);
                    userSelectorContainer = findViewById(R.id.userSelectorInPieChart);

                }

                Button onlyYourExpense = (Button) userSelectorContainer.getChildAt(0);
                Button includeOtherExpenses = (Button) userSelectorContainer.getChildAt(1);
                if (shouldIncludeOtherExpenses) {
                    buttonHighlightForUserButton(includeOtherExpenses, onlyYourExpense);
                } else {
                    buttonHighlightForUserButton(onlyYourExpense, includeOtherExpenses);
                }
            });

            loadCurrentMonthBarChart(selectedMonthStorageKey, comparingMonthStorageKey);

            return false;
        });
        AnalyticsViewLoader analyticsViewLoader = new AnalyticsViewLoader(handler);
        analyticsViewLoader.start();
    }

    private void loadUserSelector(LinearLayout userSelectorContainer) {
        Integer userCountOfSharedExpensesFor = Utils.getUserCountOfSharedExpensesFor(selectedMonthStorageKey);

        if (!isDisplayingPieChart) {
            userCountOfSharedExpensesFor += Utils.getUserCountOfSharedExpensesFor(comparingMonthStorageKey);
        }

        if (userCountOfSharedExpensesFor > 0) {
            Button onlyYourExpense = (Button) userSelectorContainer.getChildAt(0);
            Button includeOtherExpenses = (Button) userSelectorContainer.getChildAt(1);
            onlyYourExpense.setOnClickListener(v -> {
                shouldIncludeOtherExpenses = false;
                userButtonSelection(onlyYourExpense, includeOtherExpenses);
            });

            includeOtherExpenses.setOnClickListener(v -> {
                shouldIncludeOtherExpenses = true;
                userButtonSelection(includeOtherExpenses, onlyYourExpense);
            });
            userSelectorContainer.setVisibility(VISIBLE);

        } else {
            userSelectorContainer.setVisibility(GONE);
        }
    }

    private void userButtonSelection(Button selectedUser, Button unselectedUserOption) {
        buttonHighlightForUserButton(selectedUser, unselectedUserOption);
        if (isDisplayingPieChart) {
            loadPieChart(ExpenseAnalyticsView.this.selectedMonthStorageKey);
        } else {
            loadCurrentMonthBarChart(selectedMonthStorageKey, comparingMonthStorageKey);
        }
    }

    private void buttonHighlightForUserButton(Button selectedUser, Button unselectedUserOption) {
        selectedUser.setBackgroundResource(R.drawable.circle_selected);
        unselectedUserOption.setBackgroundResource(R.drawable.circle);
        selectedUser.setTextColor(getResources().getColor(R.color.white));
        unselectedUserOption.setTextColor(getResources().getColor(R.color.primaryText));
    }

    private void loadCurrentMonthBarChart(String primaryMonth, String comparingMonth) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Map<String, Object> dataFromThread = (Map<String, Object>) msg.obj;

                setMonthSelectorSpinners(primaryMonth, comparingMonth);
                Map<String, Expenses> primaryTagBasedExpenses = (Map<String, Expenses>) dataFromThread.get("primaryTagBasedExpenses");
                Map<String, Expenses> comparingTagBasedExpenses = new HashMap<>();
                if (!isNull(comparingMonth)) {
                    comparingTagBasedExpenses = (Map<String, Expenses>) dataFromThread.get("comparingTagBasedExpenses");
                }
                List<String> selectedTags = loadTagSelector(primaryTagBasedExpenses, comparingTagBasedExpenses);

                loadBarChart(selectedTags, primaryTagBasedExpenses, comparingTagBasedExpenses);

                return false;
            }
        });
        BarChartViewLoader barChartViewLoader = new BarChartViewLoader(primaryMonth, comparingMonth, shouldIncludeOtherExpenses, handler);
        barChartViewLoader.start();


    }


    private List<String> loadTagSelector(Map<String, Expenses> primaryMonthTagBased, Map<String, Expenses> comparingMonthTagBased) {
        LinearLayout tagContainer = findViewById(R.id.tagSelector);
        tagContainer.removeAllViews();
        Set<String> allTagsAsSet = new HashSet<>(primaryMonthTagBased.keySet());
        allTagsAsSet.addAll(comparingMonthTagBased.keySet());
        ArrayList<String> allTags = new ArrayList(allTagsAsSet);


        ImageButton leftArrow = new ImageButton(getContext(), null);
        LayoutParams buttonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.width = 80;
        buttonParams.height = 80;
        leftArrow.setLayoutParams(buttonParams);
        leftArrow.setBackground(getResources().getDrawable(R.drawable.ic_previous));
        ImageButton rightArrow = new ImageButton(getContext(), null);
        rightArrow.setBackground(getResources().getDrawable(R.drawable.ic_next));
        rightArrow.setLayoutParams(buttonParams);
        int startFrom = tagBatchSize * selectedTagBatch - tagBatchSize;
        int endAt = startFrom + tagBatchSize > allTags.size() - 1 ? allTags.size() - 1 : startFrom + tagBatchSize - 1;
        if (startFrom > 0) {
            tagContainer.addView(leftArrow);
            leftArrow.setOnClickListener(v -> {
                this.selectedTagBatch--;
                loadCurrentMonthBarChart(this.selectedMonthStorageKey, this.comparingMonthStorageKey);
            });
        }

        List<String> displayedTags = new ArrayList<>();
        for (int i = startFrom; i <= endAt; i++) {
            TextView textView = new TextView(this.getContext(), null);
            String tag = allTags.get(i);
            displayedTags.add(tag);
            textView.setText(format("%s", tag));
            textView.setPadding(15, 5, 15, 5);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.circle);
            tagContainer.addView(textView);
        }

        if (endAt != allTags.size() - 1) {
            tagContainer.addView(rightArrow);
            rightArrow.setOnClickListener(v -> {
                this.selectedTagBatch++;
                loadCurrentMonthBarChart(ExpenseAnalyticsView.this.selectedMonthStorageKey, ExpenseAnalyticsView.this.comparingMonthStorageKey);
            });
        }
        return displayedTags;

    }

    private void loadBarChart(List<String> selectedTags, Map<String, Expenses> primaryTagBasedExpenses, Map<String, Expenses> comparingTagBasedExpenses) {

        List<BarEntry> primaryMonthEntries = new ArrayList<>();
        List<BarEntry> comparingMonthEntries = new ArrayList<>();
        ArrayList<String> groupTitles = new ArrayList<String>();


        for (int i = 0; i < selectedTags.size(); i++) {
            Expenses primaryExpenses = primaryTagBasedExpenses.get(selectedTags.get(i));
            Expenses comparingExpenses = comparingTagBasedExpenses.get(selectedTags.get(i));
            primaryMonthEntries.add(new BarEntry(i, isNull(primaryExpenses) ? 0f : Float.parseFloat(primaryExpenses.getTotalExpenditure())));
            comparingMonthEntries.add(new BarEntry(i, isNull(comparingExpenses) ? 0f : Float.parseFloat(comparingExpenses.getTotalExpenditure())));
            groupTitles.add(selectedTags.get(i));
        }
        barChart.getXAxis().setValueFormatter(new ValueFormatter(groupTitles));
        barChart.getXAxis().setTextSize(17);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setLabelRotationAngle(2f);
        barChart.getXAxis().setGranularityEnabled(true);
        Description desc = new Description();
        desc.setText("Thriwin Solutions.");
        barChart.setDescription(desc);
        barChart.getLegend().setEnabled(false);

        float groupSpace = getSpace(selectedTags.size(), "G");
        float barSpace = getSpace(selectedTags.size(), "B"); // x2 dataset
        float barWidth = getSpace(selectedTags.size(), "W");
        String[] primaryReadableMonthAndYear = Utils.getReadableMonthAndYear(this.selectedMonthStorageKey);
        String[] comparingReadableMonthAndYear = Utils.getReadableMonthAndYear(this.comparingMonthStorageKey);
        BarDataSet set1 = new BarDataSet(primaryMonthEntries, format("%s - %s", primaryReadableMonthAndYear[0], primaryReadableMonthAndYear[1]));
        BarDataSet set2 = new BarDataSet(comparingMonthEntries, format("%s - %s", comparingReadableMonthAndYear[0], comparingReadableMonthAndYear[1]));
        set1.setValueTextSize(10f);
        set2.setValueTextSize(10f);


        BarData barData = new BarData(asList(set1, set2));
        barData.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(barData);


        set1.setColors(Color.parseColor("#C39EBA"));
        set2.setColors(Color.parseColor("#FF83A3"));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);


        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.invalidate();
        loadUserSelector(findViewById(R.id.userSelectorInBarChart));
    }

    private float getSpace(int size, String g) {
        if (g.equalsIgnoreCase("G")) {
            switch (size) {
                case 1:
                    return 0.09f;
                case 2:
                    return 0.09f;
                case 3:
                    return 0.09f;
                default:
                    return 0.09f;
            }
        }
        if (g.equalsIgnoreCase("B")) {
            switch (size) {
                case 1:
                    return 0.03f;
                case 2:
                    return 0.03f;
                case 3:
                    return 0.03f;
                default:
                    return 0.03f;
            }
        }
        if (g.equalsIgnoreCase("W")) {
            switch (size) {
                case 1:
                    return 0.08f;
                case 2:
                    return 0.22f;
                case 3:
                    return 0.35f;
                default:
                    return 0.35f;
            }
        }
        return 0;
    }

    private void setMonthSelectorSpinners(String primaryMonth, String comparingMonth) {
        Spinner primaryMonthSelector = findViewById(R.id.primaryMonth);
        Spinner comparingMonthSelector = findViewById(R.id.comparingMonth);

        ArrayAdapter<String> primaryAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_primary, getAllMonthHumanReadable(allExpensesMonths));
        primaryMonthSelector.setAdapter(primaryAdapter);

        primaryMonthSelector.setSelection(allExpensesMonths.indexOf(primaryMonth));

        primaryMonthSelector.setOnItemSelectedListener(this);

        List<String> allMonthHumanReadableForComparingSpinner = getAllMonthHumanReadable(allExpensesMonths);
        allMonthHumanReadableForComparingSpinner.add(0, "None");
        ArrayAdapter<String> comparingAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_comparing, allMonthHumanReadableForComparingSpinner);
        comparingMonthSelector.setAdapter(comparingAdapter);

        comparingMonthSelector.setSelection(isNull(comparingMonth) ? 0 : allExpensesMonths.indexOf(comparingMonth) + 1);
        comparingMonthSelector.setOnItemSelectedListener(this);
    }

    private List<String> getAllMonthHumanReadable(List<String> storageKeys) {
        ArrayList<String> humanReadableKeys = new ArrayList<>();
        for (String storageKey : storageKeys) {
            String[] readableMonthAndYear = Utils.getReadableMonthAndYear(storageKey);
            humanReadableKeys.add(readableMonthAndYear[0] + " - " + readableMonthAndYear[1]);
        }
        return humanReadableKeys;
    }

    private void loadMonthSelector(String currentMonth) {
        this.selectedMonthStorageKey = currentMonth;
        LinearLayout monthContainer = findViewById(R.id.monthSelector);
        monthContainer.removeAllViews();
        int indexOfSelectedMonth = allExpensesMonths.indexOf(currentMonth);
        ImageButton leftArrow = new ImageButton(getContext(), null);
        ViewGroup.LayoutParams buttonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.width = 100;
        buttonParams.height = 100;
        leftArrow.setLayoutParams(buttonParams);
        leftArrow.setBackground(getResources().getDrawable(R.drawable.ic_previous));
        ImageButton rightArrow = new ImageButton(getContext(), null);
        rightArrow.setBackground(getResources().getDrawable(R.drawable.ic_next));
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
            textView.setText(format("%s - %s", readableMonthAndYear[0], readableMonthAndYear[1]));
            textView.setPadding(15, 5, 15, 5);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.circle);
            if (expenseMonth.equals(currentMonth)) {
                textView.setBackgroundResource(R.drawable.circle_selected);
                textView.setTextColor(getResources().getColor(R.color.white));
            }
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPieChart(expenseMonth);
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
        loadUserSelector(findViewById(R.id.userSelectorInPieChart));
    }

    private void loadPieChart(String storageKeyForCurrentMonth) {
        Handler handler = new Handler(msg -> {

            Map<String, Object> dataFromThread = (Map<String, Object>) msg.obj;

            MonthWiseExpense monthExpenses = (MonthWiseExpense) dataFromThread.get("monthExpenses");

            tagBasedExpenses = (Map<String, Expenses>) dataFromThread.get("tagBasedExpenses");
            viewableTagExpenses =  (Map<String, Expenses>) dataFromThread.get("viewableTagExpenses");;

            PieDataSet set = new PieDataSet((List<PieEntry>) dataFromThread.get("entries"), "Expense analysis");
            List<Integer> colors = getColorsForPieChart();

            set.setColors(colors);
            set.setValueTextColors(asList(getResources().getColor(R.color.primaryText)));
            set.setValueTextSize(19);
            Description desc = new Description();
            desc.setText("Thriwin solutions.");
            pieChart.setDescription(desc);
            pieChart.getLegend().setEnabled(false);
            pieChart.setCenterTextColor(getResources().getColor(R.color.colorAccent));
            if (shouldIncludeOtherExpenses) {

                pieChart.setCenterText(format("Expendio\n%s\n$$ %s", monthExpenses.getMonthYearHumanReadable(storageKeyForCurrentMonth), dataFromThread.get("totalExpenditureOfAllUsers").toString()));
            } else {
                pieChart.setCenterText(format("Expendio\n%s\n$$ %s", monthExpenses.getMonthYearHumanReadable(storageKeyForCurrentMonth), dataFromThread.get("yourExpenditure")));
            }
            pieChart.setCenterTextSize(25);
            pieChart.setOnChartValueSelectedListener(this);
            PieData data = new PieData(set);
            pieChart.setData(data);
            pieChart.invalidate();

            pieChart.setEntryLabelColor(getResources().getColor(R.color.primaryText));

            return true;
        });

        PieChartViewLoader pieChartViewLoader = new PieChartViewLoader(storageKeyForCurrentMonth, shouldIncludeOtherExpenses, handler);
        pieChartViewLoader.start();


    }

    @NonNull
    private List<Integer> getColorsForPieChart() {
        return Utils.getTimeLineColors();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String label = ((PieEntry) e).getLabel();
        Intent i = new Intent(getContext(), TagWiseExpenseEdit.class);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);

        Handler handler = new Handler(msg -> {
            Map<String, Object> dataFromThread = (Map<String, Object>) msg.obj;
            i.putExtra("TagWiseExpenses", (String) dataFromThread.get("TagWiseExpenses"));
            i.putExtra("TagKey", label);
            i.putExtra("MakeDateEditable", true);
            i.putExtra("containsOtherExpenses", shouldIncludeOtherExpenses);
            ContextCompat.startActivity(getContext(), i, null);

            return false;
        });

        PieChartEntryClickLoader pieChartEntryClickLoader = new PieChartEntryClickLoader(label, this.viewableTagExpenses, handler);
        pieChartEntryClickLoader.start();


    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.primaryMonth) {
            if (parent.getSelectedItemId() != this.selectedMonthIndex) {
                this.selectedMonthStorageKey = Utils.getStorageKeyFromText(parent.getSelectedItem().toString());
                this.selectedMonthIndex = id;
                loadCurrentMonthBarChart(this.selectedMonthStorageKey, this.comparingMonthStorageKey);
            }
        } else {
            if (parent.getSelectedItemId() != this.comparingMonthIndex) {
                this.comparingMonthStorageKey = Utils.getStorageKeyFromText(parent.getSelectedItem().toString());
                this.comparingMonthIndex = id;
                loadCurrentMonthBarChart(this.selectedMonthStorageKey, this.comparingMonthStorageKey);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
