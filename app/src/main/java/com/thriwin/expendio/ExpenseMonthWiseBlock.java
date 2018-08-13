package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.Utils.timeLineColors;

public class ExpenseMonthWiseBlock extends LinearLayout {
    ObjectMapper obj = new ObjectMapper();
    public Map.Entry<String, MonthWiseExpense> expensesBlock;

    public ExpenseMonthWiseBlock(Context context, @Nullable AttributeSet attrs, Map.Entry<String, MonthWiseExpense> expensesBlock, HomeScreenView homeScreenView, ExpenseListener expenseListener, int index) {
        super(context, attrs);
        this.expensesBlock = expensesBlock;
        inflate(context, R.layout.expense_month_block, this);

        int colourIndex = index % timeLineColors.size();
        View monthBlockContainer = findViewById(R.id.monthBlockContainer);
        monthBlockContainer.setBackgroundColor(Color.parseColor(timeLineColors.get(colourIndex)));

        TextView blockName = findViewById(R.id.expenseBlockName);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expensesBlock.getKey());
        String expenseLimit = String.format("\n %s", expensesBlock.getValue().monthlyLimitExceededDetails());
        blockName.setText(readableMonthAndYear[0] + "\n" + readableMonthAndYear[1]);

        ((TextView) findViewById(R.id.expenseLimitValue)).setText(expenseLimit);
        ((TextView) findViewById(R.id.expenseSpentValue)).setText("\nSpent : " + expensesBlock.getValue().getTotalExpenditure());

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
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(expenseListener);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.deleteAMonthExpense(expensesBlock.getKey());
                    expenseListener.loadDisplayArea(DashboardView.HOME, null);
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
