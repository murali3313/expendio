package com.thriwin.expendio;

import android.view.MenuItem;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonActivityTest {

    @Test
    public void shouldRedirectIfTitleAreDifferentButForHomeAllowIfItIsOutsideExpenseListenerActivity() {
        ExpenseListener expenseListener = new ExpenseListener();
        ExpenseTimelineView expenseTimelineView = new ExpenseTimelineView();
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getTitle()).thenReturn("Home");
        CommonActivity.itemSelected = "Home";
        assertFalse(expenseListener.shouldRedirectToNewActivity(menuItem));

        assertTrue(expenseTimelineView.shouldRedirectToNewActivity(menuItem));

        CommonActivity.itemSelected = "Expense";
        assertTrue(expenseListener.shouldRedirectToNewActivity(menuItem));

        when(menuItem.getTitle()).thenReturn("Expense");
        CommonActivity.itemSelected = "Expense";
        assertFalse(expenseListener.shouldRedirectToNewActivity(menuItem));

        when(menuItem.getTitle()).thenReturn("Expense");
        CommonActivity.itemSelected = "Expense";
        assertFalse(expenseTimelineView.shouldRedirectToNewActivity(menuItem));

    }

}