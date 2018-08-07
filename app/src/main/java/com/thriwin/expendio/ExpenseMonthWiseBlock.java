package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ExpenseMonthWiseBlock extends LinearLayout {
    ObjectMapper obj = new ObjectMapper();

    public ExpenseMonthWiseBlock(Context context, @Nullable AttributeSet attrs, Map.Entry<String, MonthWiseExpenses> expensesBlock, HomeScreenView homeScreenView, ExpenseListener expenseListener) {
        super(context, attrs);
        inflate(context, R.layout.expense_month_block, this);


        TextView blockName = findViewById(R.id.expenseBlockName);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expensesBlock.getKey());
        blockName.setText(readableMonthAndYear[0] + "\n" + readableMonthAndYear[1] + "\n$$: " + expensesBlock.getValue().getTotalExpenditure());
        blockName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        blockName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExpenseTimelineView.class);
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("ExpenseKey", expensesBlock.getKey());
                ContextCompat.startActivity(context, i, null);
            }
        });
        blockName.setLongClickable(true);
        blockName.setOnLongClickListener(v -> {
            View sheetView = View.inflate(context, R.layout.bottom_delete_month_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(expenseListener);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.deleteAMonthExpense(expensesBlock.getKey());
                    expenseListener.loadDisplayArea(homeScreenView, DashboardView.HOME);
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
