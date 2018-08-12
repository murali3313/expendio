package com.thriwin.expendio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;

import static com.thriwin.expendio.Utils.isNull;

public class ExpenseListener extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {
    ExpenseTagsEditView tagEditView;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadDisplayArea(selectedDashboardView, getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_expense_listener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Utils.loadLocalStorageForPreferences(this.getApplicationContext());
        homeScreenView = new HomeScreenView(getApplicationContext(), null, this);
        analyticsView = new ExpenseAnalyticsView(getApplicationContext(), null);
        notificationView = new NotificationView(getApplicationContext(), null);
        tagEditView = new ExpenseTagsEditView(getApplicationContext(), null, this);

        super.onCreate(savedInstanceState);
        String displayView = getIntent().getStringExtra("DISPLAY_VIEW");
        if (isNull(displayView)) {
            loadDisplayArea(DashboardView.HOME, getIntent());
        } else {
            loadDisplayArea(DashboardView.valueOf(displayView), getIntent());
        }
    }


    public void addExpense(View view) {
        Intent i = new Intent(ExpenseListener.this, NewExpensesCreation.class);
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tags) {
            loadDisplayArea(DashboardView.TAG_EDIT, getIntent());

        } else if (id == R.id.nav_sms_keywords) {

        } else if (id == R.id.nav_download) {
            downloadAllExpenses();

        } else if (id == R.id.nav_accept_expenses) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_open_generated_excel) {
            openFolder();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void downloadAllExpenses() {
        File all_expenses = generator.genarateExcelForAllMonths(getBaseContext(), Utils.getAllExpensesMonthWise(), "All_Expenses");
        presentTheFileToTheUser(all_expenses);
    }

    public void loadDisplayArea(DashboardView dashboardView, Intent intent) {
        LinearLayout displayArea = findViewById(R.id.displayArea);
        displayArea.removeAllViews();
        IDisplayAreaView displayAreaView = getAppropriateView(dashboardView);
        displayArea.addView((View) displayAreaView);
        displayAreaView.load(this, intent);
        selectedDashboardView = dashboardView;
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        View barChart = findViewById(R.id.bar_chart);
        barChart.setVisibility(View.GONE);
        switch (dashboardView) {
            case HOME:
                bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                itemSelected = getResources().getString(R.string.title_home);
                break;
            case ANALYTICS:
                barChart.setVisibility(View.VISIBLE);
                itemSelected = getResources().getString(R.string.title_expense_analysis);
                bottomNavigation.getMenu().findItem(R.id.navigation_analytics).setChecked(true);
                break;
            case NOTIFICATION:
                itemSelected = getResources().getString(R.string.title_notifications);
                bottomNavigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
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
            case TAG_EDIT:
                displayAreaView = tagEditView;
                break;
        }
        return displayAreaView;
    }

    public void loadDisplayAreaWithHomeScreen() {
        loadDisplayArea(DashboardView.HOME, getIntent());
    }



}
