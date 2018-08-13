package com.thriwin.expendio;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.util.Map;
import java.util.SortedMap;

public class HomeScreenView extends LinearLayout implements IDisplayAreaView {
    ObjectMapper obj = new ObjectMapper();
    private ExpenseListener expenseListener;
    SortedMap<String, MonthWiseExpense> allExpensesMonthWise;

    public HomeScreenView(Context context, @Nullable AttributeSet attrs, ExpenseListener expenseListener) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflate(context, R.layout.home_screeen, this);
    }

    public void load(CommonActivity expenseListener, Intent intent) {
        allExpensesMonthWise = Utils.getAllExpensesMonthWise();
        FlowLayout homeScreenContainer = findViewById(R.id.homeScreen);
        homeScreenContainer.removeAllViews();
        for (Map.Entry<String, MonthWiseExpense> monthWise : allExpensesMonthWise.entrySet()) {
            ExpenseMonthWiseBlock expenseMonthWiseBlock = new ExpenseMonthWiseBlock(getContext(), null, monthWise, this, this.expenseListener);
            homeScreenContainer.addView(expenseMonthWiseBlock);

        }
    }


    public void glow(String glowFor) {
        FlowLayout homeScreenContainer = findViewById(R.id.homeScreen);
        Integer blockHeight = 180;
        for (int i = 0; i < homeScreenContainer.getChildCount(); i++) {
            ExpenseMonthWiseBlock childAt = (ExpenseMonthWiseBlock) homeScreenContainer.getChildAt(i);
            if (childAt.expensesBlock.getKey().equals(glowFor)) {
                ScrollView scrollView = this.getRootView().findViewById(R.id.scrollParent);
                ObjectAnimator.ofInt(scrollView, "scrollY", i / 2 * blockHeight).setDuration(2000).start();
                AppCompatResources.getDrawable(getContext(), R.drawable.expense_border);
                Drawable[] color = {AppCompatResources.getDrawable(getContext(), R.drawable.expenses_block_border_transition), AppCompatResources.getDrawable(getContext(), R.drawable.expenses_block_border)};
                TransitionDrawable trans = new TransitionDrawable(color);
                View viewById = childAt.findViewById(R.id.expenseBlockName);
                viewById.setBackground(trans);
                trans.startTransition(3500);
                break;
            }
        }
    }
}
