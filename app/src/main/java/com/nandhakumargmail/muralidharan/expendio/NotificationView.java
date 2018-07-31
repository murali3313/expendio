package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class NotificationView extends LinearLayout implements IDisplayAreaView {
    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.notification, null);
    }

    @Override
    public void load() {

    }
}
