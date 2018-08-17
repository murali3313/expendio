package com.thriwin.expendio;

import java.util.Date;

import static java.lang.Math.abs;

enum ReminderOption {
    DO_NOT_DISTURB(0),
    ONCE_A_DAY(1440),
    TWICE_A_DAY(720),
    EVERY_TWO_HOUR(120),
    EVERY_ONE_HOUR(60),
    EVERY_15_MIN(15);


    private Integer value;

    ReminderOption(Integer s) {
        value = s;
    }

    public boolean isTimeForNotification(Date lastNotified) {
        long howMuchMinutesPassed = abs((new Date().getTime() - lastNotified.getTime()) / (1000 * 60));

        return howMuchMinutesPassed >= this.value && this.value != 0;
    }
}
