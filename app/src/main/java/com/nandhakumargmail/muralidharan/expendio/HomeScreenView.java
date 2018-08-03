package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.inflate;

public class HomeScreenView extends LinearLayout implements IDisplayAreaView {
    ObjectMapper obj = new ObjectMapper();
    private ExpenseListener expenseListener;

    public HomeScreenView(Context context, @Nullable AttributeSet attrs, ExpenseListener expenseListener) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflate(context, R.layout.home_screeen, this);
    }

    public void load() {
        HashMap<String, MonthWiseExpenses> allExpensesMonthWise = getAllExpensesMonthWise();
        FlowLayout homeScreenContainer = findViewById(R.id.homeScreen);
        homeScreenContainer.removeAllViews();
        for (Map.Entry<String, MonthWiseExpenses> monthWise : allExpensesMonthWise.entrySet()) {
            ExpenseMonthWiseBlock expenseMonthWiseBlock = new ExpenseMonthWiseBlock(getContext(), null, monthWise, this, expenseListener);
            homeScreenContainer.addView(expenseMonthWiseBlock);

        }
    }

    private HashMap<String, MonthWiseExpenses> getAllExpensesMonthWise() {
        HashMap<String, MonthWiseExpenses> allExpenses = new HashMap<>();
        Map<String, ?> all = Utils.getLocalStorageForPreferences().getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("Expense-")) {
                try {
                    MonthWiseExpenses expenses = obj.readValue(all.get(entry.getKey()).toString(), new TypeReference<MonthWiseExpenses>() {
                    });
                    allExpenses.put(entry.getKey(), expenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return allExpenses;
    }
}
