package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

class RecurringExpenseView extends LinearLayout {

    String selectedDayExpense = "Sun";
    LinearLayout daySelectionContainer;

    public RecurringExpenseView(Context context, @Nullable AttributeSet attrs, RecurringExpensesView activity) {
        super(context, attrs);
        inflate(context, R.layout.recurring_expense_view, this);
        RadioGroup recurringTypeSelection = findViewById(R.id.recurringTypeSelection);
        View dailyContainer = findViewById(R.id.dailyExpenseData);
        View specificDayOfWeekContainer = findViewById(R.id.dayOfWeekExpenseContainer);
        View specificDayOfMonthContainer = findViewById(R.id.dayOfMonthExpenseContainer);
        daySelectionContainer = findViewById(R.id.daySelection);
        for (int i = 0; i < daySelectionContainer.getChildCount(); i++) {
            daySelectionContainer.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    unselectAllDays();
                    TextView daySelected = (TextView) v;
                    selectedDayExpense = daySelected.getText().toString();
                    daySelected.setBackgroundResource(R.drawable.circle_selected);

                }
            });
        }
        recurringTypeSelection.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.daily:
                    dailyContainer.setVisibility(VISIBLE);
                    specificDayOfWeekContainer.setVisibility(GONE);
                    specificDayOfMonthContainer.setVisibility(GONE);
                    break;
                case R.id.specificDayOfWeek:
                    dailyContainer.setVisibility(GONE);
                    specificDayOfWeekContainer.setVisibility(VISIBLE);
                    specificDayOfMonthContainer.setVisibility(GONE);
                    break;
                case R.id.specificDayOfMonth:
                    dailyContainer.setVisibility(GONE);
                    specificDayOfWeekContainer.setVisibility(GONE);
                    specificDayOfMonthContainer.setVisibility(VISIBLE);
                    break;
            }
        });

//        CommonActivity.setupParent(this.getRootView(), activity);

    }

    private void unselectAllDays() {
        for (int i = 0; i < daySelectionContainer.getChildCount(); i++) {
            daySelectionContainer.getChildAt(i).setBackgroundResource(R.drawable.circle);
        }
    }
}
