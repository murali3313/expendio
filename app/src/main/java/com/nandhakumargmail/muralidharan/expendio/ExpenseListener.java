package com.nandhakumargmail.muralidharan.expendio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import static com.nandhakumargmail.muralidharan.expendio.Utils.loadLocalStorageForPreferences;

public class ExpenseListener extends SpeechActivity implements NavigationView.OnNavigationItemSelectedListener {


    HomeScreenView homeScreenView;
    ExpenseTalkView analyticsView;
    NotificationView notificationView;
    ExpenseTagsEditView tagEditView;
    DashboardView selectedDashboardView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadDisplayArea(homeScreenView, DashboardView.HOME);
                    return true;
                case R.id.navigation_analytics:
                    loadDisplayArea(analyticsView, DashboardView.ANALYTICS);
                    return true;
                case R.id.navigation_notifications:
                    loadDisplayArea(notificationView, DashboardView.NOTIFICATION);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onPostResume() {
        super.onPostResume();
        IDisplayAreaView dashboardView = this.homeScreenView;
        switch (selectedDashboardView) {
            case HOME:
                dashboardView = homeScreenView;
                break;
            case ANALYTICS:
                dashboardView = analyticsView;
                break;
            case NOTIFICATION:
                dashboardView = notificationView;
                break;
        }
        loadDisplayArea(dashboardView, selectedDashboardView);
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadLocalStorageForPreferences(this.getApplicationContext());
        homeScreenView = new HomeScreenView(ExpenseListener.this.getApplicationContext(), null, this);
        analyticsView = new ExpenseTalkView(ExpenseListener.this.getApplicationContext(), null);
        notificationView = new NotificationView(ExpenseListener.this.getApplicationContext(), null);
        tagEditView = new ExpenseTagsEditView(ExpenseListener.this.getApplicationContext(), null, this);
        loadDisplayArea(homeScreenView, DashboardView.HOME);
        super.onCreate(savedInstanceState);

    }

    public void loadDisplayAreaWithHomeScreen() {
        loadDisplayArea(homeScreenView, DashboardView.HOME);
    }

    public void loadDisplayArea(IDisplayAreaView displayAreaView, DashboardView dashboardView) {
        LinearLayout displayArea = findViewById(R.id.displayArea);
        displayArea.removeAllViews();
        displayArea.addView((View) displayAreaView);
        displayAreaView.load();
        selectedDashboardView = dashboardView;
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        switch (dashboardView) {
            case HOME:
                bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                break;
            case ANALYTICS:
                bottomNavigation.getMenu().findItem(R.id.navigation_analytics).setChecked(true);
                break;
            case NOTIFICATION:
                bottomNavigation.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
                break;

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
            loadDisplayArea(tagEditView, DashboardView.TAG_EDIT);

        } else if (id == R.id.nav_sms_keywords) {

        } else if (id == R.id.nav_download) {

        } else if (id == R.id.nav_accept_expenses) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
