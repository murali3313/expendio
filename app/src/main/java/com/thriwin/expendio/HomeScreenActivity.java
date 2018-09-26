package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;

import java.util.Date;

import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;

public class HomeScreenActivity extends GeneralActivity {
    static String glowFor;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadDisplayArea(selectedDashboardView, getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_expense_listener);

        Utils.loadLocalStorageForPreferences(this.getApplicationContext());
        Context applicationContext = getApplicationContext();
        homeScreenView = new HomeScreenView(applicationContext, null, this);
        analyticsView = new ExpenseAnalyticsView(applicationContext, null);
        notificationView = new NotificationView(applicationContext, null);

        super.onCreate(savedInstanceState);

        selectedDashboardView = DashboardView.HOME;
        if (!Utils.isReminderAlreadySet()) {
            NotificationScheduler.setReminder(applicationContext, RecurringExpensesAlarmReceiver.class);
            Utils.setReminder();
            Utils.lastNotifiedOn(new Date());

        }
        String displayView = getIntent().getStringExtra("DISPLAY_VIEW");
        if (!isEmpty(displayView) && displayView.equals("NOTIFICATION")) {
            Intent i = new Intent(applicationContext, NotificationScreenActivity.class);
            itemSelected = getResources().getString(R.string.title_notifications);
            startActivity(i, null);
        }

        Intent service = new Intent(applicationContext, SMSReceiverService.class);
        applicationContext.startService(service);
        addAdMobOffer("ca-app-pub-8899454204921425/1884781486", AdSize.SMART_BANNER, getKeyWordsForGoogle());
    }


    public void addExpense(View view) {
        Intent i = new Intent(HomeScreenActivity.this, NewExpensesCreation.class);
        if (itemSelected.equalsIgnoreCase(getResources().getString(R.string.title_expense_analysis))) {
            i.putExtra("SELECTED_STORAGE_KEY", analyticsView.selectedMonthStorageKey);

        }
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(HomeScreenActivity.this, ExpendioSettingsView.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadDisplayArea(DashboardView dashboardView, Intent intent) {
        LinearLayout displayArea = (LinearLayout)findViewById(R.id.displayArea);
        displayArea.removeAllViews();
        IDisplayAreaView displayAreaView = getAppropriateView(dashboardView);
        displayArea.addView((View) displayAreaView);
        displayAreaView.load(this, intent);
        selectedDashboardView = dashboardView;
        BottomNavigationView bottomNavigation =(BottomNavigationView) findViewById(R.id.navigation);
        View barChart = findViewById(R.id.bar_chart);
        barChart.setVisibility(View.GONE);

        if (!isNull(glowFor) && selectedDashboardView.equals(DashboardView.HOME)) {
            homeScreenView.glow(glowFor);
            glowFor = null;
        }

        TextView headerText =(TextView) findViewById(R.id.headingText);
        switch (dashboardView) {
            case HOME:
                bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                itemSelected = getResources().getString(R.string.title_home);
                headerText.setText("Month wise expenses");
                break;
            case ANALYTICS:
                barChart.setVisibility(View.VISIBLE);
                itemSelected = getResources().getString(R.string.title_expense_analysis);
                bottomNavigation.getMenu().findItem(R.id.navigation_analytics).setChecked(true);
                headerText.setText(itemSelected);
                break;
            case NOTIFICATION:
                itemSelected = getResources().getString(R.string.title_notifications);
                bottomNavigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
                headerText.setText(itemSelected);
                break;

        }
    }

    private IDisplayAreaView getAppropriateView(DashboardView dashboardView) {
        IDisplayAreaView displayAreaView = homeScreenView;
        switch (dashboardView) {
            case HOME:
                displayAreaView = homeScreenView;
                break;
            case ANALYTICS:
                displayAreaView = analyticsView;
                break;
            case NOTIFICATION:
                displayAreaView = notificationView;
                break;
        }
        return displayAreaView;
    }


}
