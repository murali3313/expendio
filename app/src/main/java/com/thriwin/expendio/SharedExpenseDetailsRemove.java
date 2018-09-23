package com.thriwin.expendio;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.SortedMap;

import static com.thriwin.expendio.GeneralActivity.getBackGround;
import static com.thriwin.expendio.Utils.isNull;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SharedExpenseDetailsRemove extends Activity {
    String selectedMonthOfExpensesStorageKey;

    public void setBackGroundTheme(BackgroundTheme backGroundTheme) {
        View viewById = this.findViewById(R.id.firstContainer);
        if (!isNull(viewById)) {
            viewById.setBackgroundResource(getBackGround(backGroundTheme));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_expense_details_remove);
        selectedMonthOfExpensesStorageKey = getIntent().getStringExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(selectedMonthOfExpensesStorageKey);
        TextView header = findViewById(R.id.expenseShareRemovalForMonthHeader);
        header.setText(format("Shared Expenses %s - %s", readableMonthAndYear[0], readableMonthAndYear[1]));
        loadExpenseToDelete();
        setBackGroundTheme(null);
    }

    private void loadExpenseToDelete() {
        SortedMap<String, MonthWiseExpense> allSharedExpensesFor = Utils.getAllSharedExpensesFor(selectedMonthOfExpensesStorageKey);
        if (allSharedExpensesFor.size() == 0) {
            SharedExpenseDetailsRemove.this.finish();
        }
        LinearLayout container = findViewById(R.id.otherUsersExpenses);
        container.removeAllViews();
        for (Map.Entry<String, MonthWiseExpense> userWiseExpenses : allSharedExpensesFor.entrySet()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout linearLayout = new LinearLayout(SharedExpenseDetailsRemove.this, null);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setBackgroundResource(R.drawable.edit_outline);
            layoutParams.setMargins(5, 10, 5, 5);
            TextView nameView = new TextView(SharedExpenseDetailsRemove.this, null);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            nameParams.weight = 2;
            nameParams.leftMargin = 20;
            nameView.setTextSize(18);
            nameView.setTextColor(getResources().getColor(R.color.white));
            nameView.setLayoutParams(nameParams);

            nameView.setText("Sharer : " + userWiseExpenses.getKey());

            TextView totalExpenses = new TextView(SharedExpenseDetailsRemove.this, null);

            LinearLayout.LayoutParams expenseParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            totalExpenses.setLayoutParams(expenseParams);
            expenseParams.weight = 2;
            totalExpenses.setTextSize(18);
            totalExpenses.setTextColor(getResources().getColor(R.color.white));

            totalExpenses.setText("Spent :" + userWiseExpenses.getValue().getTotalExpenditure());


            ImageButton removeButton = new ImageButton(SharedExpenseDetailsRemove.this, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 90);

            removeButton.setLayoutParams(params);
            removeButton.setBackgroundResource(R.drawable.ic_remove);

            linearLayout.addView(nameView);
            linearLayout.addView(totalExpenses);
            linearLayout.addView(removeButton);
            container.addView(linearLayout);

            removeButton.setOnClickListener(v -> {
                View sheetView = View.inflate(getApplicationContext(), R.layout.bottom_delete_other_expense_confirmation, null);
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(SharedExpenseDetailsRemove.this);
                TextView note = sheetView.findViewById(R.id.sharerExpenseRemoveNote);
                note.setText(format(note.getText().toString(), userWiseExpenses.getKey()));
                mBottomSheetDialog.setContentView(sheetView);
                ((View)sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
                mBottomSheetDialog.show();

                mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(v1 -> {
                    Utils.removeAllSharerInfo(asList(userWiseExpenses.getKey()));
                    loadExpenseToDelete();
                    mBottomSheetDialog.cancel();
                });

                mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(v12 -> mBottomSheetDialog.cancel());
            });
        }
    }
}
