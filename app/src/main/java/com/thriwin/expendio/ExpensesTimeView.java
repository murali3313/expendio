package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
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

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.Utils.isNull;
import static com.thriwin.expendio.Utils.saveDayWiseExpenses;
import static com.thriwin.expendio.Utils.timeLineColors;

public class ExpensesTimeView extends LinearLayout {

    private List<Expense> expense;
    TextView spentOn;
    EditText amount;
    EditText reason;
    ImageButton remove;
    LinearLayout tagsContainer;
    private Context context;
    private Map<String, Map.Entry<String, Expenses>> allDayWiseExpenseFromSharer;
    private ExpenseTimelineView parentView;
    public Map.Entry<String, Expenses> expenses;

    public ExpensesTimeView(Context context, @Nullable AttributeSet attrs, Map.Entry<String, Expenses> expenses,
                            Map<String, Map.Entry<String, Expenses>> allDayWiseExpenseFromSharer, ExpenseTimelineView parentView, int index) {
        super(context, attrs);
        this.context = context;
        this.allDayWiseExpenseFromSharer = allDayWiseExpenseFromSharer;
        this.expenses = isNull(expenses) ? new AbstractMap.SimpleEntry<>("Nothing", new Expenses()) : expenses;

        this.parentView = parentView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.expenses_time_view, this);
        RelativeLayout timeMarkerContainer = findViewById(R.id.expenseTimeDay);

        int colourIndex = index % timeLineColors.size();
        timeMarkerContainer.setBackgroundColor(Color.parseColor(timeLineColors.get(colourIndex)));

        LinearLayout totalExpenseView = findViewById(R.id.totalExpenseDayWise);
        buildNameAndExpenditure(context, totalExpenseView, "You", this.expenses.getValue().getTotalExpenditure());
        for (Map.Entry<String, Map.Entry<String, Expenses>> dayWiseExpensesFromSharer : allDayWiseExpenseFromSharer.entrySet()) {
            buildNameAndExpenditure(context, totalExpenseView, dayWiseExpensesFromSharer.getKey(), dayWiseExpensesFromSharer.getValue().getValue().getTotalExpenditure());
        }


        TextView dateWiseRepresentation = findViewById(R.id.dateWiseString);
        dateWiseRepresentation.setText(getDate(expenses, allDayWiseExpenseFromSharer).getDateMonthHumanReadable());
        this.expense = this.expenses.getValue();
        LinearLayout expensesPerDay = findViewById(R.id.expensesPerDay);
        for (Expense expens : this.expense) {
            ExpenseTimeView expenseTimeView = new ExpenseTimeView(context, null, expens, this, colourIndex);
            expensesPerDay.addView(expenseTimeView);
        }
        for (Map.Entry<String, Map.Entry<String, Expenses>> userDayExpensesEntry : this.allDayWiseExpenseFromSharer.entrySet()) {
            for (Expense userDayExpense : userDayExpensesEntry.getValue().getValue()) {
                ExpenseTimeView expenseTimeView = new ExpenseTimeView(context, null, userDayExpense, this, userDayExpensesEntry.getKey(), colourIndex);
                expensesPerDay.addView(expenseTimeView);
            }
        }


        inflate.setLongClickable(true);
        OnLongClickListener onLongClickListener = v -> {
            isLongPressed = true;
            View sheetView = View.inflate(context, R.layout.bottom_delete_month_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(parentView);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(v1 -> {
                saveDayWiseExpenses(this.expenses.getValue().getStorageKey(), this.expenses.getValue().getDateMonth(), new Expenses());
                parentView.loadTimeLineView(this.expenses.getValue().getStorageKey());
                mBottomSheetDialog.cancel();
            });

            mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(v12 -> mBottomSheetDialog.cancel());
            return false;
        };
        inflate.setOnLongClickListener(onLongClickListener);

        inflate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DayWiseExpenseEdit.class);
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                Expenses value = getMergedYoursAndSharerExpenses();

                long spentOnDate = getDate(ExpensesTimeView.this.expenses, ExpensesTimeView.this.allDayWiseExpenseFromSharer).getSpentOnDate();
                Expenses expenses = value.isEmpty() ? new Expenses(new Expense(new Date(spentOnDate))) : value;
                i.putExtra("DayWiseExpenses", Utils.getSerializedExpenses(expenses));
                i.putExtra("containsOtherExpenses", !allDayWiseExpenseFromSharer.isEmpty());
                ContextCompat.startActivity(context, i, null);
            }
        });

        setAllChildWithFollowParentState(this, onLongClickListener);
        inflate.setClickable(true);

    }

    private Expenses getMergedYoursAndSharerExpenses() {
        Expenses value = ExpensesTimeView.this.expenses.getValue();

        for (Map.Entry<String, Map.Entry<String, Expenses>> expenseFromOthers : ExpensesTimeView.this.allDayWiseExpenseFromSharer.entrySet()) {
            value = value.merge(expenseFromOthers.getValue().getValue());
        }
        return value;
    }

    @NonNull
    private void buildNameAndExpenditure(Context context, LinearLayout totalExpenseView, String userName, String expenditure) {
        if (expenditure.equalsIgnoreCase("0")) {
            return;
        }
        TextView nameView = new TextView(context, null);
        nameView.setText(userName);
        nameView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        nameView.setTextColor(getResources().getColor(R.color.primaryText));
        if (!userName.equalsIgnoreCase("You")) {
            nameView.setBackgroundResource(R.drawable.name_selected);
        }

        TextView expenditureView = new TextView(context, null);
        expenditureView.setText(expenditure + "\n");
        expenditureView.setTextColor(getResources().getColor(R.color.primaryText));
        expenditureView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        expenditureView.setTypeface(expenditureView.getTypeface(), Typeface.BOLD);


        totalExpenseView.addView(nameView);
        totalExpenseView.addView(expenditureView);


    }

    private Expenses getDate(Map.Entry<String, Expenses> expenses, Map<String, Map.Entry<String, Expenses>> allDayWiseExpenseFromSharer) {
        return isNull(expenses) || expenses.getValue().isEmpty() ?
                allDayWiseExpenseFromSharer.entrySet().iterator().next().getValue().getValue() :
                this.expenses.getValue();
    }

    boolean isLongPressed = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isLongPressed = false;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP && !isLongPressed) {
            Intent i = new Intent(context, DayWiseExpenseEdit.class);
            Expenses value = getMergedYoursAndSharerExpenses();
            long spentOnDate = getDate(ExpensesTimeView.this.expenses, ExpensesTimeView.this.allDayWiseExpenseFromSharer).getSpentOnDate();
            Expenses expenses = value.isEmpty() ? new Expenses(new Expense(new Date(spentOnDate))) : value;
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("DayWiseExpenses", Utils.getSerializedExpenses(expenses));
            i.putExtra("containsOtherExpenses", !allDayWiseExpenseFromSharer.isEmpty());
            ContextCompat.startActivity(context, i, null);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void setAllChildWithFollowParentState(View view, OnLongClickListener longClickListener) {

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setAllChildWithFollowParentState(group.getChildAt(i), longClickListener);
            }
            group.setDuplicateParentStateEnabled(true);
            group.setClickable(false);
            group.setLongClickable(true);
            group.setOnLongClickListener(longClickListener);

        } else {
            view.setDuplicateParentStateEnabled(true);
            view.setClickable(false);
        }

    }

}
