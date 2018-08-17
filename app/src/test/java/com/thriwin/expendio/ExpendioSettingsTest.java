package com.thriwin.expendio;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ExpendioSettingsTest {

    @Test
    public void shouldDisplayNotificationAfterTImeElapsedAfterTheLastNotification() {
        Context context = InstrumentationRegistry.getTargetContext();
        Utils.loadLocalStorageForPreferences(context);
        Utils.lastNotifiedOn(new Date());

        ExpendioSettings expendioSettings = new ExpendioSettings("1", "2", ReminderOption.EVERY_15_MIN);

        assertTrue(expendioSettings.canRemindUser());
    }

}