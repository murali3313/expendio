package com.thriwin.expendio;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;

public class ValueFormatter extends IndexAxisValueFormatter {

    private List<String> values;

    public ValueFormatter(List<String> values) {

        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index = (int) Math.floor(value);
        int contentWithin = 8;
        if (index < values.size()) {
            String label = values.get(index);
            return label.length() > contentWithin ? label.substring(0, contentWithin) + "..." : label;
        }
        return "";
    }
}
