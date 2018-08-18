package com.thriwin.expendio;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.thriwin.expendio.Utils.isNull;

@NoArgsConstructor
@Getter
public class ExpendioSettings {

    private Integer startDayOfMonth = 1;
    private Integer notificationHour = 20;

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ExpendioSettings expendioSetting;

    public ExpendioSettings(String startDayOfMonth, String notificationHour) {
        this.setStartDayOfMonth(Integer.parseInt(startDayOfMonth));
        this.setNotificationHour(Integer.parseInt(notificationHour));
    }

    private void setNotificationHour(int notificationHour) {
        this.notificationHour = notificationHour >= 0 && notificationHour <= 23 ? notificationHour : 20;
    }

    private void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth >= 1 && startDayOfMonth <= 28 ? startDayOfMonth : 1;
    }

    public Integer getStartDayOfMonth() {
        return loadExpendioSettings().startDayOfMonth;
    }

    @Nullable
    @JsonIgnore
    public static ExpendioSettings loadExpendioSettings() {

        if (isNull(expendioSetting)) {
            String expendioSettings = Utils.getLocalStorageForPreferences().getString("EXPENDIO_SETTINGS", "");

            try {
                expendioSetting = objectMapper.readValue(expendioSettings, ExpendioSettings.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        expendioSetting = isNull(expendioSetting) ? new ExpendioSettings() : expendioSetting;
        return expendioSetting;
    }

    @Nullable
    @JsonIgnore
    public static void saveExpendioSettings(ExpendioSettings settings) {

        try {
            String expendio_settings = objectMapper.writeValueAsString(settings);
            Utils.getLocalStorageForPreferences().edit().putString("EXPENDIO_SETTINGS", expendio_settings);
            expendioSetting = settings;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
