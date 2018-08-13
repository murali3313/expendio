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
import android.widget.TableLayout;
import android.widget.TableRow;

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
        TableLayout homeScreenContainer = findViewById(R.id.homeScreen);
        homeScreenContainer.removeAllViews();
        int i = 0;
        TableRow tableRow = null;

        for (Map.Entry<String, MonthWiseExpense> monthWise : allExpensesMonthWise.entrySet()) {
            boolean isNewLayoutStarted = i % 2 == 0;
            if (isNewLayoutStarted) {
                tableRow = new TableRow(getContext(), null);
                homeScreenContainer.addView(tableRow);
            }
            ExpenseMonthWiseBlock expenseMonthWiseBlock = new ExpenseMonthWiseBlock(getContext(), null, monthWise, this, this.expenseListener, i);
            tableRow.addView(expenseMonthWiseBlock, isNewLayoutStarted ? 0 : 1);
            i++;
        }
    }


    public void glow(String glowFor) {
        TableLayout homeScreenContainer = findViewById(R.id.homeScreen);
        Integer blockHeight = 180;
        for (int i = 0; i < homeScreenContainer.getChildCount(); i++) {
            TableRow tableRow = (TableRow) homeScreenContainer.getChildAt(i);
            for (int j = 0; j < tableRow.getChildCount(); j++) {
                ExpenseMonthWiseBlock childAt = (ExpenseMonthWiseBlock) tableRow.getChildAt(j);
                if (childAt.expensesBlock.getKey().equals(glowFor)) {
                    ScrollView scrollView = this.getRootView().findViewById(R.id.scrollParent);
                    ObjectAnimator.ofInt(scrollView, "scrollY", i * blockHeight).setDuration(2000).start();
                    AppCompatResources.getDrawable(getContext(), R.drawable.expense_border);
                    View viewById = childAt.findViewById(R.id.monthBlockContainer);
                    Drawable[] color = {AppCompatResources.getDrawable(getContext(), R.drawable.expenses_day_block_border_transition),
                            viewById.getBackground()};
                    TransitionDrawable trans = new TransitionDrawable(color);
                    viewById.setBackground(trans);
                    trans.startTransition(4000);
                    break;
                }
            }
        }
    }
}
