package com.thriwin.expendio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import static com.thriwin.expendio.Utils.isNull;

public class AnalyticsScreenActivity extends GeneralActivity implements NavigationView.OnNavigationItemSelectedListener {
    static String glowFor;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadDisplayArea(selectedDashboardView, getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.analytics_screen_activity);
        Utils.loadLocalStorageForPreferences(this.getApplicationContext());
        analyticsView = new ExpenseAnalyticsView(getApplicationContext(), null);

        super.onCreate(savedInstanceState);
        loadDisplayArea(DashboardView.ANALYTICS, getIntent());
        addAdMobOffer("ca-app-pub-8899454204921425/3509973900", com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE, getKeyWordsForGoogle());

    }


    public void addExpense(View view) {
        Intent i = new Intent(AnalyticsScreenActivity.this, NewExpensesCreation.class);
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



    public void loadDisplayArea(DashboardView dashboardView, Intent intent) {
        LinearLayout displayArea = (LinearLayout)findViewById(R.id.displayArea);
        displayArea.removeAllViews();
        IDisplayAreaView displayAreaView = getAppropriateView(dashboardView);
        displayArea.addView((View) displayAreaView);
        displayAreaView.load(this, intent);
        selectedDashboardView = dashboardView;
        BottomNavigationView bottomNavigation = (BottomNavigationView)findViewById(R.id.navigation);
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
