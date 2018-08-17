package com.thriwin.expendio;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;

@NoArgsConstructor
@Getter
public class ExpendioSettings {

    private Integer startDayOfMonth = 1;
    private Integer notificationHour = 20;
    private Integer reminderOptionIndex = ReminderOption.TWICE_A_DAY.ordinal();

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ExpendioSettings expendioSetting;

    public ExpendioSettings(String startDayOfMonth, String notificationHour, ReminderOption reminderOption) {
        this.setStartDayOfMonth(isEmpty(startDayOfMonth.trim()) ? 0 : Integer.parseInt(startDayOfMonth));
        this.setNotificationHour(isEmpty(notificationHour.trim()) ? 0 : Integer.parseInt(notificationHour));
        this.reminderOptionIndex = reminderOption.ordinal();
    }

    private void setNotificationHour(int notificationHour) {
        this.notificationHour = notificationHour >= 0 && notificationHour <= 23 ? notificationHour : 20;
    }

    private void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth >= 1 && startDayOfMonth <= 28 ? startDayOfMonth : 1;
    }

    public Integer getStartDayOfMonth() {
        return this.startDayOfMonth;
    }

    @Nullable
    @JsonIgnore
    public static ExpendioSettings loadExpendioSettings() {

        if (isNull(expendioSetting)) {
            String expendioSettingSerializedString = Utils.getLocalStorageForPreferences().getString("EXPENDIO_SETTINGS", "");

            if (isEmpty(expendioSettingSerializedString)) {
                saveExpendioSettings(new ExpendioSettings());
            } else {
                try {
                    expendioSetting = objectMapper.readValue(expendioSettingSerializedString, ExpendioSettings.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return expendioSetting;
    }

    @Nullable
    @JsonIgnore
    public static void saveExpendioSettings(ExpendioSettings settings) {

        try {
            String expendio_settings = objectMapper.writeValueAsString(settings);
            Utils.getLocalStorageForPreferences().edit().putString("EXPENDIO_SETTINGS", expendio_settings).commit();
            expendioSetting = settings;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getReminderOptionIndex() {
        return reminderOptionIndex;
    }

    @JsonIgnore
    public boolean isWithinNotificationHour() {
        return new Date().getHours() == this.notificationHour;
    }

    @JsonIgnore
    public boolean canRemindUser() {
//        return true;
        Date lastNotified = Utils.lastNotiferDisplayTime();
        if (isNull(lastNotified)) {
            return true;
        }
        return ReminderOption.values()[reminderOptionIndex].isTimeForNotification(lastNotified);
    }
}
