package com.thriwin.expendio;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
public class ExpendioThemeSettings {

    private static String expendio_theme_settings = "EXPENDIO_THEME_SETTINGS";
    private BackgroundTheme backgroundTheme = BackgroundTheme.MOUNTAIN;


    public ExpendioThemeSettings(BackgroundTheme backgroundTheme) {
        this.backgroundTheme = backgroundTheme;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ExpendioThemeSettings expendioThemeSettings;


    @Nullable
    @JsonIgnore
    public static ExpendioThemeSettings loadExpendioThemeSettings() {

        if (isNull(expendioThemeSettings)) {

            String expendioThemeSettingString = Utils.getLocalStorageForPreferences().getString(expendio_theme_settings, "");

            if (isEmpty(expendioThemeSettingString)) {
                saveExpendioThemeSettings(new ExpendioThemeSettings());
            } else {
                try {
                    expendioThemeSettings = objectMapper.readValue(expendioThemeSettingString, ExpendioThemeSettings.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return expendioThemeSettings;
    }

    @Nullable
    @JsonIgnore
    public static void saveExpendioThemeSettings(ExpendioThemeSettings settings) {

        try {
            String expendio_settings = objectMapper.writeValueAsString(settings);
            Utils.getLocalStorageForPreferences().edit().putString(expendio_theme_settings, expendio_settings).commit();
            expendioThemeSettings = settings;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @JsonIgnore
    public static void clear() {

        expendioThemeSettings = null;

    }
}
