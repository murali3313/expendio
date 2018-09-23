package com.thriwin.expendio;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.nex3z.flowlayout.FlowLayout;

public class ExpendioThemeSettingsActivity extends GeneralActivity {
    BackgroundTheme selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.expendio_theme_activity);
        super.onCreate(savedInstanceState);

        FlowLayout themeSelection = findViewById(R.id.themeSelection);

        selectedTheme = ExpendioThemeSettings.loadExpendioThemeSettings().getBackgroundTheme();

        for (BackgroundTheme backgroundTheme : BackgroundTheme.values()) {
            ImageButton child = new ImageButton(ExpendioThemeSettingsActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 0, 0, 0);
            child.setLayoutParams(params);
            child.setMaxHeight(100);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTheme = backgroundTheme;
                    setBackGroundTheme(selectedTheme);
                }
            });
            child.setBackgroundResource(backgroundTheme.getSmallResurceId());
            themeSelection.addView(child);
        }

        findViewById(R.id.saveExpendioSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpendioThemeSettings.saveExpendioThemeSettings(new ExpendioThemeSettings(selectedTheme));
                showToast(R.string.expendioThemeSettingSavedSuccessfully);
                ExpendioThemeSettingsActivity.this.finish();

            }
        });
    }
}
