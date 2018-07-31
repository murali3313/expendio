package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ExpenseTalkView extends LinearLayout implements IDisplayAreaView{
    public ExpenseTalkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.expense_talk, null);
    }

    @Override
    public void load() {

    }
}
