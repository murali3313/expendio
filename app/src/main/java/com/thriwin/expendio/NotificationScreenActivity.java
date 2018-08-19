package com.thriwin.expendio;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import static com.thriwin.expendio.Utils.isNull;

public class NotificationScreenActivity extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {
    static String glowFor;

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
        notificationView = new NotificationView(getApplicationContext(), null);

        super.onCreate(savedInstanceState);
        loadDisplayArea(DashboardView.NOTIFICATION, getIntent());
    }


    public void addExpense(View view) {
        Intent i = new Intent(NotificationScreenActivity.this, NewExpensesCreation.class);
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
            Intent i = new Intent(NotificationScreenActivity.this, ExpendioSettingsView.class);
            startActivity(i);
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
            Intent i = new Intent(NotificationScreenActivity.this, ExpenseTagsEditView.class);
            startActivity(i);
            loadDisplayArea(DashboardView.TAG_EDIT, getIntent());

        } else if (id == R.id.nav_usual_expenses) {
            Intent i = new Intent(NotificationScreenActivity.this, RecurringExpensesView.class);
            startActivity(i);

        } else if (id == R.id.nav_download) {
            downloadAllExpenses();

        } else if (id == R.id.nav_general_expense_limit) {
            Intent i = new Intent(NotificationScreenActivity.this, ExpenseDefaultLimit.class);
            startActivity(i);

        }  else if (id == R.id.nav_sms_receiver) {
            Intent i = new Intent(NotificationScreenActivity.this, ExpenseSMSPattern.class);
            startActivity(i);

        }
        else if (id == R.id.nav_rate_us) {

            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }
        } else if (id == R.id.nav_feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:thriwin.solutions@gmail.com?subject=Expendio%20App%20Feedback"));
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                showToast(R.string.noEmailAppAvailable);
            }

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

        if (!isNull(glowFor) && selectedDashboardView.equals(DashboardView.HOME)) {
            homeScreenView.glow(glowFor);
            glowFor = null;
        }

        TextView headerText = findViewById(R.id.headingText);
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
