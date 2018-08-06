package com.thriwin.expendio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ExpenseTimelineView extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button okButton, cancelButton;
    MonthWiseExpenses monthWiseExpenses;
    ObjectMapper obj = new ObjectMapper();
    String expenseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.expense_visualization_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton addExpenseInCurrentMonth = findViewById(R.id.addExpenseInCurrentMonth);
        expenseKey = this.getIntent().getStringExtra("ExpenseKey");
        addExpenseInCurrentMonth.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MonthWiseExpenseAdd.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("LatestDate", getDeserializedMonthWiseExpenseslocal(expenseKey).getLatestDate(expenseKey));
            ContextCompat.startActivity(getApplicationContext(), i, null);
        });

        loadTimeLineView(expenseKey);
        super.onCreate(savedInstanceState);


    }

    public void loadTimeLineView(String expenseKey) {
        this.monthWiseExpenses = getDeserializedMonthWiseExpenseslocal(expenseKey);

        TextView monthWiseTotalExpenditure = findViewById(R.id.monthWiseTotalExpenditure);
        monthWiseTotalExpenditure.setText("Total expense : " + monthWiseExpenses.getTotalExpenditure());

        LinearLayout timeMarker = findViewById(R.id.timeMarker);
        timeMarker.removeAllViews();
        int i = 0;
        for (String key : monthWiseExpenses.getSortedKeys()) {
            Map.Entry<String, Expenses> dayWiseExpense = monthWiseExpenses.getDayWiseExpenses(key);
            ExpensesTimeView expensesTimeView = new ExpensesTimeView(getBaseContext(), null, dayWiseExpense, this, i % 2 == 0);
            timeMarker.addView(expensesTimeView);
            i++;
        }
    }

    private MonthWiseExpenses getDeserializedMonthWiseExpenseslocal(String expenseKey) {
        return Utils.getDeserializedMonthWiseExpenses(Utils.getLocalStorageForPreferences().getString(expenseKey, "[]"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadTimeLineView(expenseKey);
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

        if (id == R.id.nav_download) {
            downloadMonthWiseExpense();

        } else if (id == R.id.nav_expense_limit) {

        } else if (id == R.id.nav_open_generated_excel) {
            openFolder();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void downloadMonthWiseExpense() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_DOWNLOAD);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_DOWNLOAD: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
                        showToast(R.string.downloadOptionIsNotEnabled);
                        return;
                    }

                    writeAndPresentTheFile();

                } else {

                    showToast(R.string.downloadOptionIsNotEnabled);
                }
                return;
            }
        }
    }

    private void writeAndPresentTheFile() {
        MonthWiseExpenses monthWiseExpenses = getDeserializedMonthWiseExpenseslocal(expenseKey);
        File file = generator.genarateExcelForMonthExpenses(getBaseContext(), monthWiseExpenses, monthWiseExpenses.getMonthYearHumanReadable());
        presentTheFileToTheUser(file);
    }


}
