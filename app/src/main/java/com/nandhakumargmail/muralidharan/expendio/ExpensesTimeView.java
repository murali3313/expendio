package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.util.List;
import java.util.Map;

import static com.nandhakumargmail.muralidharan.expendio.Utils.saveDayWiseExpenses;

public class ExpensesTimeView extends LinearLayout {

    private List<Expense> expense;
    TextView spentOn;
    EditText amount;
    EditText reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private Context context;
    private ExpenseTimelineView parentView;
    Map.Entry<String, Expenses> expenses;


    public ExpensesTimeView(Context context, @Nullable AttributeSet attrs, Map.Entry<String, Expenses> expenses,
                            ExpenseTimelineView parentView, boolean isEven) {
        super(context, attrs);
        this.context = context;
        this.parentView = parentView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.expenses_time_view, this);
        this.expenses = expenses;
        RelativeLayout timeMarkerContainer = findViewById(R.id.expenseTimeDay);
        if (isEven) {
            timeMarkerContainer.setBackgroundResource(R.color.colorAlternateDark1);
        } else {
            timeMarkerContainer.setBackgroundResource(R.color.colorAlternateDark2);
        }
        TextView totalExpenseView = findViewById(R.id.totalExpenseDayWise);
        totalExpenseView.setText(expenses.getValue().getTotalExpenditure());


        TextView dateWiseRepresentation = findViewById(R.id.dateWiseString);
        dateWiseRepresentation.setText(expenses.getValue().getDateMonthHumanReadable());
        this.expense = expenses.getValue();
        FlowLayout expensesPerDay = findViewById(R.id.expensesPerDay);
        for (Expense expens : this.expense) {
            ExpenseTimeView expenseTimeView = new ExpenseTimeView(context, null, expens, this);
            expensesPerDay.addView(expenseTimeView);
        }

        setAllChildWithFollowParentState(this);
        this.setClickable(true);

        inflate.setLongClickable(true);
        inflate.setOnLongClickListener(v -> {
            View sheetView = View.inflate(context, R.layout.bottom_delete_month_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(parentView);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(v1 -> {
                saveDayWiseExpenses(expenses.getValue().getStorageKey(), expenses.getValue().getDateMonth(), new Expenses());
                parentView.loadTimeLineView(expenses.getValue().getStorageKey());
                mBottomSheetDialog.cancel();
            });

            mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(v12 -> mBottomSheetDialog.cancel());
            return false;
        });

        inflate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DayWiseExpenseEdit.class);
                i.putExtra("DayWiseExpenses", Utils.getSerializedExpenses(expenses.getValue()));
                ContextCompat.startActivity(context, i, null);
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            Intent i = new Intent(context, DayWiseExpenseEdit.class);
            i.putExtra("DayWiseExpenses", Utils.getSerializedExpenses(expenses.getValue()));
            ContextCompat.startActivity(context, i, null);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void setAllChildWithFollowParentState(View view) {

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setAllChildWithFollowParentState(group.getChildAt(i));
            }
            group.setDuplicateParentStateEnabled(true);
            group.setClickable(false);

        } else {
            view.setDuplicateParentStateEnabled(true);
            view.setClickable(false);
        }

    }

}
