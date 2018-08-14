package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.thriwin.expendio.Utils.isNull;

class RecurringExpenseView extends LinearLayout implements AdapterView.OnItemSelectedListener {

    List<String> selectedDays = new ArrayList<String>() {{
        add("Sun");
    }};
    LinearLayout daySelectionContainer;
    RecurringExpenseEditView dailyRecurringExpenseEditView;
    RecurringExpenseEditView dayOfWeekRecurringExpenseEditView;
    RecurringExpenseEditView dayOfMonthRecurringExpenseEditView;

    RecurringExpenseType selectedRecurringExpensetype = RecurringExpenseType.DAILY;
    private String selectedDayOfMonthExpense = "1";


    public RecurringExpenseView(Context context, @Nullable AttributeSet attrs, RecurringExpensesView activity, RecurringExpense recurringExpense) {
        super(context, attrs);
        inflate(context, R.layout.recurring_expense_view, this);
        RadioGroup recurringTypeSelection = findViewById(R.id.recurringTypeSelection);
        View dailyContainer = findViewById(R.id.dailyExpenseContainer);
        View specificDayOfWeekContainer = findViewById(R.id.dayOfWeekExpenseContainer);
        View specificDayOfMonthContainer = findViewById(R.id.dayOfMonthExpenseContainer);
        daySelectionContainer = findViewById(R.id.daySelection);
        for (int i = 0; i < daySelectionContainer.getChildCount(); i++) {
            daySelectionContainer.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView daySelected = (TextView) v;
                    if (selectedDays.contains(daySelected.getText().toString())) {
                        v.setBackgroundResource(R.drawable.circle);
                        selectedDays.remove(daySelected.getText().toString());
                    } else {
                        selectedDays.add(daySelected.getText().toString());
                        v.setBackgroundResource(R.drawable.circle_selected);
                    }
                }
            });
        }
        recurringTypeSelection.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.daily:
                    selectedRecurringExpensetype = RecurringExpenseType.DAILY;
                    dailyContainer.setVisibility(VISIBLE);
                    specificDayOfWeekContainer.setVisibility(GONE);
                    specificDayOfMonthContainer.setVisibility(GONE);
                    break;
                case R.id.specificDayOfWeek:
                    selectedRecurringExpensetype = RecurringExpenseType.SPECIFIC_DAY_OF_WEEK;
                    dailyContainer.setVisibility(GONE);
                    specificDayOfWeekContainer.setVisibility(VISIBLE);
                    specificDayOfMonthContainer.setVisibility(GONE);
                    break;
                case R.id.specificDayOfMonth:
                    selectedRecurringExpensetype = RecurringExpenseType.SPECIFIC_DAY_OF_MONTH;
                    dailyContainer.setVisibility(GONE);
                    specificDayOfWeekContainer.setVisibility(GONE);
                    specificDayOfMonthContainer.setVisibility(VISIBLE);
                    break;
            }
        });

        Spinner dayOfMonthSelector = findViewById(R.id.dayOfMonthSelection);
        ArrayList<String> days = new ArrayList<>();
        for (Integer i = 1; i <= 31; i++) {
            days.add(i.toString());
        }
        ArrayAdapter<String> daySelectorAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_primary, days);
        dayOfMonthSelector.setAdapter(daySelectorAdapter);

        dayOfMonthSelector.setSelection(0);

        dayOfMonthSelector.setOnItemSelectedListener(this);


        dailyRecurringExpenseEditView = findViewById(R.id.dailyExpenseData);
        dayOfWeekRecurringExpenseEditView = findViewById(R.id.dayOfWeekExpenseData);
        dayOfMonthRecurringExpenseEditView = findViewById(R.id.dayOfMonthData);

        dailyRecurringExpenseEditView.setParent(activity, this);
        dayOfWeekRecurringExpenseEditView.setParent(activity, this);
        dayOfMonthRecurringExpenseEditView.setParent(activity, this);

        selectedRecurringExpensetype = recurringExpense.getRecurringType();

        switch (selectedRecurringExpensetype) {
            case DAILY:
                recurringTypeSelection.check(R.id.daily);
                dailyRecurringExpenseEditView.populate(recurringExpense);
                break;
            case SPECIFIC_DAY_OF_WEEK:
                selectedDays = recurringExpense.getDayOfWeek();
                selectDay(selectedDays);
                recurringTypeSelection.check(R.id.specificDayOfWeek);
                dayOfWeekRecurringExpenseEditView.populate(recurringExpense);

                break;
            case SPECIFIC_DAY_OF_MONTH:
                recurringTypeSelection.check(R.id.specificDayOfMonth);
                selectedDayOfMonthExpense = recurringExpense.getDayOfMonth();
                dayOfMonthSelector.setSelection(Integer.parseInt(selectedDayOfMonthExpense) - 1);
                dayOfMonthRecurringExpenseEditView.populate(recurringExpense);
                break;
        }
    }

    private void selectDay(List<String> days) {
        for (int i = 0; i < daySelectionContainer.getChildCount(); i++) {
            TextView dayText = (TextView) daySelectionContainer.getChildAt(i);
            if (days.contains(dayText.getText().toString())) {
                dayText.setBackgroundResource(R.drawable.circle_selected);
            } else {
                dayText.setBackgroundResource(R.drawable.circle);
            }
        }
    }

    public RecurringExpense getRecurringExpense() {
        RecurringExpense recurringExpense = null;
        switch (selectedRecurringExpensetype) {
            case DAILY:
                recurringExpense = dailyRecurringExpenseEditView.getRecurringExpense();
                recurringExpense.setRecurringType(RecurringExpenseType.DAILY);
                break;
            case SPECIFIC_DAY_OF_WEEK:
                recurringExpense = dayOfWeekRecurringExpenseEditView.getRecurringExpense();
                if (!isNull(recurringExpense)) {
                    recurringExpense.setDayOfWeek(selectedDays);
                    recurringExpense.setRecurringType(RecurringExpenseType.SPECIFIC_DAY_OF_WEEK);
                }
                break;
            case SPECIFIC_DAY_OF_MONTH:
                recurringExpense = dayOfMonthRecurringExpenseEditView.getRecurringExpense();
                if (!isNull(recurringExpense)) {
                    recurringExpense.setDayOfMonth(selectedDayOfMonthExpense);
                    recurringExpense.setRecurringType(RecurringExpenseType.SPECIFIC_DAY_OF_MONTH);
                }
                break;
        }

        return recurringExpense;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int daySelected = position + 1;
        selectedDayOfMonthExpense = String.valueOf(daySelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
