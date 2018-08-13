package com.thriwin.expendio;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class RecurringExpenseEditView extends LinearLayout {

    public RecurringExpenseEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.recurrence_expense_edit, this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, ExpenseTags.getSavedExpenseTags().getWords());
        AutoCompleteTextView reason = findViewById(R.id.reason);
        reason.setThreshold(1);
        reason.setAdapter(adapter);


    }
}
