package com.thriwin.expendio;

import android.view.MenuItem;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonActivityTest {

    @Test
    public void shouldRedirectIfTitleAreDifferentButForHomeAllowIfItIsOutsideExpenseListenerActivity() {
        HomeScreenActivity homeScreenActivity = new HomeScreenActivity();
        ExpenseTimelineView expenseTimelineView = new ExpenseTimelineView();
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getTitle()).thenReturn("Home");
        CommonActivity.itemSelected = "Home";
        assertFalse(homeScreenActivity.shouldRedirectToNewActivity(menuItem));

        assertTrue(expenseTimelineView.shouldRedirectToNewActivity(menuItem));

        CommonActivity.itemSelected = "Expense";
        assertTrue(homeScreenActivity.shouldRedirectToNewActivity(menuItem));

        when(menuItem.getTitle()).thenReturn("Expense");
        CommonActivity.itemSelected = "Expense";
        assertFalse(homeScreenActivity.shouldRedirectToNewActivity(menuItem));

        when(menuItem.getTitle()).thenReturn("Expense");
        CommonActivity.itemSelected = "Expense";
        assertFalse(expenseTimelineView.shouldRedirectToNewActivity(menuItem));

    }

}