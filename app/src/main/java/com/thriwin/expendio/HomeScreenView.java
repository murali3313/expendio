package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.util.HashMap;
import java.util.Map;

public class HomeScreenView extends LinearLayout implements IDisplayAreaView {
    ObjectMapper obj = new ObjectMapper();
    private ExpenseListener expenseListener;

    public HomeScreenView(Context context, @Nullable AttributeSet attrs, ExpenseListener expenseListener) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflate(context, R.layout.home_screeen, this);
    }

    public void load() {
        HashMap<String, MonthWiseExpenses> allExpensesMonthWise = Utils.getAllExpensesMonthWise();
        FlowLayout homeScreenContainer = findViewById(R.id.homeScreen);
        homeScreenContainer.removeAllViews();
        for (Map.Entry<String, MonthWiseExpenses> monthWise : allExpensesMonthWise.entrySet()) {
            ExpenseMonthWiseBlock expenseMonthWiseBlock = new ExpenseMonthWiseBlock(getContext(), null, monthWise, this, expenseListener);
            homeScreenContainer.addView(expenseMonthWiseBlock);

        }
    }


}
