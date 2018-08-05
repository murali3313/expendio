package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NotificationView extends LinearLayout implements IDisplayAreaView {

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.notification, this);
    }

    @Override
    public void load() {
        Expenses allUnAcceptedExpenses = new Expenses();
        RelativeLayout container = findViewById(R.id.unAcceptedExpensesContainer);
        container.removeAllViews();
        Expenses unAcceptedExpenses = Utils.getUnAcceptedExpenses();
        allUnAcceptedExpenses.addAll(unAcceptedExpenses);
        if (!unAcceptedExpenses.isEmpty()) {
            container.addView(new UnAcceptedExpensesAudioView(getContext(), null, unAcceptedExpenses, R.string.audioUnApprovedExpenses));
        }

        Expenses unAcceptedExpensesViaSms = Utils.getUnAcceptedExpensesViaSMS();
        allUnAcceptedExpenses.addAll(unAcceptedExpenses);
        if (!unAcceptedExpensesViaSms.isEmpty()) {
            container.addView(new UnAcceptedExpensesAudioView(getContext(), null, unAcceptedExpensesViaSms, R.string.smsUnApprovedExpenses));
        }

        View viewById = findViewById(R.id.noUnAcceptedExpensePresent);
        viewById.setVisibility(allUnAcceptedExpenses.size() == 0 ? VISIBLE : GONE);


    }
}
