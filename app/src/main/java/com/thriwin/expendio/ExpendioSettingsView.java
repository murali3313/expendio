package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExpendioSettingsView extends Activity {
    ObjectMapper obj = new ObjectMapper();
    RecurringExpenseEditView dailyRecurringExpenseEditView;
    RecurringExpenseEditView dayOfWeekRecurringExpenseEditView;
    RecurringExpenseEditView dayOfMonthRecurringExpenseEditView;
    LinearLayout recurringExpensesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expendio_settings_view);
    }


}
