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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.GeneralActivity.getBackGround;
import static com.thriwin.expendio.Utils.SHARED;
import static java.lang.String.format;

public class UnAcceptedExpensesBaseView extends LinearLayout {
    private CommonActivity expenseListener;
    UnAcceptedExpensesBaseView inflatedView;
    private Expenses unAcceptedExpenses;
    private String key;
    private NotificationView notificationView;

    public UnAcceptedExpensesBaseView(CommonActivity expenseListener, Context context, @Nullable AttributeSet attrs, Expenses unAcceptedExpenses, String expensesHeader, String key, NotificationView notificationView) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflatedView = (UnAcceptedExpensesBaseView) inflate(context, R.layout.un_accepted_expenses_view, this);
        this.unAcceptedExpenses = unAcceptedExpenses;
        this.key = key;
        this.notificationView = notificationView;
        TextView header = inflatedView.findViewById(R.id.unApprovedExpense);
        header.setText(expensesHeader);

        TextView count = inflatedView.findViewById(R.id.totalExpensesUnApproved);
        count.setText(format("Total unapproved expenses: %d", unAcceptedExpenses.size()));

        setClickAction();

    }

    protected void setClickAction() {
        inflatedView.setOnClickListener(v -> {
            ObjectMapper objectMapper = new ObjectMapper();
            Intent i = new Intent(getContext(), ExpenseAcceptance.class);
            try {
                i.putExtra("UNACCEPTED_EXPENSES", objectMapper.writeValueAsString(unAcceptedExpenses));
                i.putExtra("EXPENSE_KEY_TO_REMOVE", key);
                if (key.endsWith(SHARED)) {
                    i.putExtra("USER_NAME", unAcceptedExpenses.getNameOfSharer(key));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            ContextCompat.startActivity(getContext(), i, null);
        });

        inflatedView.setOnLongClickListener(v -> {
            View sheetView = View.inflate(getContext(), R.layout.bottom_delete_month_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(expenseListener);
            mBottomSheetDialog.setContentView(sheetView);
            ((View)sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.clearUnAcceptedExpense(key);
                    notificationView.load(expenseListener, null);
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
