package com.thriwin.expendio;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

import static java.lang.String.format;

public class ExpenseRestoreActivity extends GeneralActivity {
    ProcessPastedExpenses processPastedExpenses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.restore_expense);
        super.onCreate(savedInstanceState);
        findViewById(R.id.restoreExpense).setOnClickListener(v -> processPastedEntries());
        EditText restorableExpensesView = findViewById(R.id.restorableExpenses);

        restorableExpensesView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View v) {
                EditText restorableExpensesView = findViewById(R.id.restorableExpenses);

                String restorableExpenses = restorableExpensesView.getText().toString().trim();
                restorableExpensesView.setText(restorableExpenses);
                return false;
            }
        });
    }

    private void processPastedEntries() {
        EditText restorableExpensesView = findViewById(R.id.restorableExpenses);

        String restorableExpenses = restorableExpensesView.getText().toString();
        restorableExpensesView.setText(restorableExpenses);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Expenses processPastedExpenses = ExpenseRestoreActivity.this.processPastedExpenses.getExpenses();
                if (processPastedExpenses.size() == 0) {
                    showToast(R.string.noExpenseToRestore);
                    return;
                }
                View sheetView = View.inflate(ExpenseRestoreActivity.this, R.layout.bottom_accept_expense_confirmation, null);
                TextView confirmation = sheetView.findViewById(R.id.restoreExpenseAcceptanceConfirmation);
                String formattedext = format(confirmation.getText().toString(), processPastedExpenses.size());
                confirmation.setText(formattedext);
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ExpenseRestoreActivity.this);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(v1 -> {
                    Set<String> uniqueStorageExpenseKeys = processPastedExpenses.getUniqueStorageExpenseKeys();
                    for (String uniqueStorageExpenseKey : uniqueStorageExpenseKeys) {
                        Utils.deleteAMonthExpense(uniqueStorageExpenseKey);
                    }

                    Utils.saveExpenses(processPastedExpenses);
                    mBottomSheetDialog.cancel();
                    showToast(R.string.expenseRestoredSuccessfully);
                    HomeScreenActivity.glowFor = processPastedExpenses.getStorageKey();
                    AnalyticsScreenActivity.glowFor = processPastedExpenses.getStorageKey();
                    ExpenseRestoreActivity.this.finish();
                });

                mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(v12 -> mBottomSheetDialog.cancel());
            }
        };
        processPastedExpenses = new ProcessPastedExpenses(restorableExpenses, handler);
        processPastedExpenses.start();
    }

}
