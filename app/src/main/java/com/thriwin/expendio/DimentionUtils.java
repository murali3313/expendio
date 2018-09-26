package com.thriwin.expendio;

import android.content.Context;

final class DimentionUtils {

    private DimentionUtils() {
    }

    static float converPixelsToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    static float converPixelsToDP(float px, Context context) {
        return px * context.getResources().getDisplayMetrics().scaledDensity;
    }
}