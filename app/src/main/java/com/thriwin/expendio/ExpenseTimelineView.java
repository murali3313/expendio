package com.thriwin.expendio;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.String.format;

public class ExpenseTimelineView extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button okButton, cancelButton;
    MonthWiseExpense monthWiseExpense;
    ObjectMapper obj = new ObjectMapper();
    String expenseKey;
    static String glowFor;


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
            i.putExtra("LatestDate", Utils.getDeserializedMonthWiseExpenses(expenseKey).getLatestDate(expenseKey));
            ContextCompat.startActivity(getApplicationContext(), i, null);
        });

        loadTimeLineView(expenseKey);
        super.onCreate(savedInstanceState);

    }

    public void loadTimeLineView(String expenseKey) {
        this.monthWiseExpense = Utils.getDeserializedMonthWiseExpenses(expenseKey);

        TextView monthWiseTotalExpenditure = findViewById(R.id.monthWiseTotalExpenditure);
        monthWiseTotalExpenditure.setText("Total Spent \n" + monthWiseExpense.getTotalExpenditure());

        TextView monthWiseExpenseLimitExceeded = findViewById(R.id.monthWiseExpenseLimitExceeded);
        monthWiseExpenseLimitExceeded.setText(monthWiseExpense.monthlyLimitExceededDetails());

        TextView monthWiseExpenseLimit = findViewById(R.id.monthWiseExpenseLimit);
        monthWiseExpenseLimit.setText(format("Expense \ncut-off \n%s", monthWiseExpense.getMonthWiseExpenseLimit().toString()));

        TextView monthDetails = findViewById(R.id.monthDetails);
        monthDetails.setText(monthWiseExpense.getMonthYearHumanReadable(expenseKey));

        LinearLayoutCompat timeMarker = findViewById(R.id.timeMarker);
        timeMarker.removeAllViews();
        int index = 0;
        for (String key : monthWiseExpense.getSortedKeys()) {
            Map.Entry<String, Expenses> dayWiseExpense = monthWiseExpense.getDayWiseExpenses(key);
            ExpensesTimeView expensesTimeView = new ExpensesTimeView(getBaseContext(), null, dayWiseExpense, this, index);
            timeMarker.addView(expensesTimeView);
            index++;
        }


        ViewTreeObserver vto = timeMarker.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                timeMarker.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                for (int i = 0; i < monthWiseExpense.getSortedKeys().size(); i++) {
                    ExpensesTimeView childAt = (ExpensesTimeView) timeMarker.getChildAt(i);
                    if (childAt.expenses.getKey().equals(glowFor)) {
                        ScrollView scrollView = findViewById(R.id.scrollParent);
                        View viewById = childAt.findViewById(R.id.expenseTimeDay);
                        ObjectAnimator.ofInt(scrollView, "scrollY", childAt.getTop()).setDuration(1500).start();
                        AppCompatResources.getDrawable(getApplicationContext(), R.drawable.expense_border);
                        Drawable[] color = {AppCompatResources.getDrawable(getApplicationContext(), R.drawable.expenses_day_block_border_transition),
                                viewById.getBackground()};
                        TransitionDrawable trans = new TransitionDrawable(color);
                        viewById.setBackground(trans);
                        trans.startTransition(3500);
                        break;
                    }
                }
                glowFor = null;
            }
        });
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
//        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent i = new Intent(ExpenseTimelineView.this, ExpenseMonthWiseLimit.class);
            i.putExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY, expenseKey);
            startActivity(i);
        } else if (id == R.id.nav_open_generated_excel) {
            openFolder();
        } else if (id == R.id.nav_general_data_share) {
            Intent i = new Intent(ExpenseTimelineView.this, ExpenseShareActivity.class);
            i.putExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY, expenseKey);
            startActivity(i);

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
        MonthWiseExpense monthWiseExpense = Utils.getDeserializedMonthWiseExpenses(expenseKey);
        File file = generator.genarateExcelForMonthExpenses(getBaseContext(), monthWiseExpense, monthWiseExpense.getMonthYearHumanReadable(expenseKey));
        presentTheFileToTheUser(file);
    }

    @Override
    protected String getMonthForAnalytics() {
        return expenseKey;
    }


}
