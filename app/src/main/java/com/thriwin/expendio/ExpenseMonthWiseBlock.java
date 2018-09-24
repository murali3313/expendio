package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.GeneralActivity.getBackGround;
import static com.thriwin.expendio.Utils.timeLineColors;
import static java.lang.String.format;

public class ExpenseMonthWiseBlock extends LinearLayout {
    ObjectMapper obj = new ObjectMapper();
    public Map.Entry<String, MonthWiseExpense> expensesBlock;

    public ExpenseMonthWiseBlock(Context context, @Nullable AttributeSet attrs, Map.Entry<String, MonthWiseExpense> expensesBlock, HomeScreenView homeScreenView, HomeScreenActivity homeScreenActivity, int index) {
        super(context, attrs);
        this.expensesBlock = expensesBlock;
        inflate(context, R.layout.expense_month_block, this);

        int colourIndex = index % timeLineColors.size();
        View monthBlockContainer = findViewById(R.id.monthBlockContainer);
        GradientDrawable drawable = (GradientDrawable) monthBlockContainer.getBackground();
        drawable.setColor(Color.parseColor(timeLineColors.get(colourIndex)));

        TextView blockName = (TextView) findViewById(R.id.expenseBlockName);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expensesBlock.getKey());
        String expenseLimit = format("\n %s", expensesBlock.getValue().monthlyLimitExceededDetails());
        blockName.setText(readableMonthAndYear[0] + "\n" + readableMonthAndYear[1]);
        TextView spentValue = (TextView) findViewById(R.id.expenseSpentValue);
        TextView spentBy = (TextView) findViewById(R.id.expenseSpentBy);
        String totalExpenditure = expensesBlock.getValue().getTotalExpenditure();

        spentValue.setText(format("\n: %s", totalExpenditure));
        spentBy.setText(format("\nYou spent "));

        SortedMap<String, MonthWiseExpense> allSharedExpensesFor = Utils.getAllSharedExpensesFor(expensesBlock.getKey());

        BigDecimal totalExpenditureOfAllUsers = new BigDecimal(expensesBlock.getValue().getTotalExpenditure());
        for (Map.Entry<String, MonthWiseExpense> sharedUserExpenses : allSharedExpensesFor.entrySet()) {
            spentBy.append(format("\n%s", sharedUserExpenses.getKey()));
            spentValue.append(format("\n: %s", sharedUserExpenses.getValue().getTotalExpenditure()));
            totalExpenditureOfAllUsers = totalExpenditureOfAllUsers.add(new BigDecimal(sharedUserExpenses.getValue().getTotalExpenditure()));
        }

        if (allSharedExpensesFor.size() == 0) {
            ((TextView) findViewById(R.id.expenseLimitValue)).setText(expenseLimit);
        } else {
            spentBy.append(format("\n\nTotal"));
            spentValue.append(format("\n\n: %s", totalExpenditureOfAllUsers));
        }

        blockName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        monthBlockContainer.setOnClickListener(v -> {
            Intent i = new Intent(context, ExpenseTimelineView.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("ExpenseKey", expensesBlock.getKey());
            ContextCompat.startActivity(context, i, null);
        });


        monthBlockContainer.setLongClickable(true);
        monthBlockContainer.setOnLongClickListener(v -> {
            View sheetView = View.inflate(context, R.layout.bottom_delete_month_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(homeScreenActivity);
            mBottomSheetDialog.setContentView(sheetView);
            ((View)sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.deleteAMonthExpense(expensesBlock.getKey());
                    homeScreenActivity.loadDisplayArea(DashboardView.HOME, null);
                    mBottomSheetDialog.cancel();
                }
            });

            mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.cancel();
                }
            });
            return false;
        });
    }

}
