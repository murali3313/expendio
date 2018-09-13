package com.thriwin.expendio;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.File;
import java.util.SortedMap;

public class GeneralActivity extends CommonActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tags) {
            Intent i = new Intent(GeneralActivity.this, ExpenseTagsEditView.class);
            startActivity(i);

        } else if (id == R.id.nav_usual_expenses) {
            Intent i = new Intent(GeneralActivity.this, RecurringExpensesView.class);
            startActivity(i);

        } else if (id == R.id.nav_download) {
            downloadAllExpenses();

        } else if (id == R.id.nav_general_expense_limit) {
            Intent i = new Intent(GeneralActivity.this, ExpenseDefaultLimit.class);
            startActivity(i);

        } else if (id == R.id.nav_general_data_share) {
            Intent i = new Intent(GeneralActivity.this, ExpenseShareActivity.class);
            i.putExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY, new Expense().getStorageKey());

            startActivity(i);

        } else if (id == R.id.nav_sms_receiver) {
            Intent i = new Intent(GeneralActivity.this, ExpenseSMSPattern.class);
            startActivity(i);

        } else if (id == R.id.nav_rate_us) {

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
        } else if (id == R.id.nav_share_expendio) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Expendio - Free Expense manager voice enhanced. \nPlease click the below link to download and enjoy.\n" +
                    "https://play.google.com/store/apps/details?id=com.thriwin.expendio" +
                    "\ndeveloped by Thriwin Solutions.");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void downloadAllExpenses() {

        SortedMap<String, MonthWiseExpense> allExpensesMonthWise = Utils.getAllExpensesMonthWise();
        File all_expenses = generator.genarateExcelForAllMonths(getBaseContext(), allExpensesMonthWise, "All_Expenses");
        presentTheFileToTheUser(all_expenses);
    }
}
