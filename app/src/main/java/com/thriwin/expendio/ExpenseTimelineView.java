package com.thriwin.expendio;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.String.format;

public class ExpenseTimelineView extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    Button okButton, cancelButton;
    MonthWiseExpense monthWiseExpense;
    ObjectMapper obj = new ObjectMapper();
    String expenseKey;
    static String glowFor;
    NavigationView navigationView;


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


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton addExpenseInCurrentMonth = (ImageButton) findViewById(R.id.addExpenseInCurrentMonth);
        expenseKey = this.getIntent().getStringExtra("ExpenseKey");
        addExpenseInCurrentMonth.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MonthWiseExpenseAdd.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("LatestDate", Utils.getDeserializedMonthWiseExpenses(expenseKey).getLatestDate(expenseKey));
            ContextCompat.startActivity(getApplicationContext(), i, null);
        });

        super.onCreate(savedInstanceState);
        loadMonthDetails(expenseKey);
    }

    public void loadTimeLineView(String expenseKey) {

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Map<String, Object> dataFromThread = (Map<String, Object>) msg.obj;

                ExpenseTimelineView.this.monthWiseExpense = (MonthWiseExpense) dataFromThread.get("monthWiseExpense");

                SortedMap<String, MonthWiseExpense> allSharedExpenses = (SortedMap<String, MonthWiseExpense>) dataFromThread.get("allSharedExpenses");
                BigDecimal totalExpenditureOfOtherUsers = (BigDecimal) dataFromThread.get("totalExpenditureOfOtherUsers");
                String monthWiseExpenseTotalExpenditure = (String) dataFromThread.get("monthWiseExpenseTotalExpenditure");


                TextView monthWiseTotalExpenditure = (TextView) findViewById(R.id.monthWiseTotalExpenditure);
                TextView monthWiseTotalExpenditureFor = (TextView) findViewById(R.id.monthWiseTotalExpenditureFor);
                MenuItem sharerExpenseMenuItem = navigationView.getMenu().findItem(R.id.nav_remove_sharer_expense);
                if (!totalExpenditureOfOtherUsers.equals(new BigDecimal("0"))) {
                    monthWiseTotalExpenditureFor.setText("You:");
                    monthWiseTotalExpenditureFor.append("\nOthers:");
                    monthWiseTotalExpenditureFor.append("\nTotal:");
                    sharerExpenseMenuItem.setVisible(true);
                    monthWiseTotalExpenditureFor.setVisibility(View.VISIBLE);

                    monthWiseTotalExpenditure.setText(monthWiseExpenseTotalExpenditure);
                    monthWiseTotalExpenditure.append("\n" + totalExpenditureOfOtherUsers.toString());
                    monthWiseTotalExpenditure.append("\n" + totalExpenditureOfOtherUsers.add(new BigDecimal(monthWiseExpenseTotalExpenditure)).toString());
                } else {
                    monthWiseTotalExpenditure.setText("Total spent \n" + monthWiseExpense.getTotalExpenditure());
                    monthWiseTotalExpenditure.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    monthWiseTotalExpenditureFor.setVisibility(View.GONE);
                    sharerExpenseMenuItem.setVisible(false);
                }


                TextView monthWiseExpenseLimitExceeded = (TextView) findViewById(R.id.monthWiseExpenseLimitExceeded);
                monthWiseExpenseLimitExceeded.setText(format("%s", monthWiseExpense.monthlyLimitExceededDetails()));

                TextView monthWiseExpenseLimit = (TextView) findViewById(R.id.monthWiseExpenseLimit);
                monthWiseExpenseLimit.setText(format("Entries\nYours:%d\nOthers: %d", monthWiseExpense.getTotalEntries(),
                        getOtherEntries(allSharedExpenses)
                ));


                LinearLayoutCompat timeMarker = (LinearLayoutCompat) findViewById(R.id.timeMarker);
                timeMarker.removeAllViews();
                int index = 0;

                SortedSet<String> allSortedKeys = (SortedSet<String>) dataFromThread.get("allSortedKeys");
                HashMap<String, Map.Entry<String, Expenses>> expenseOfYou = (HashMap<String, Map.Entry<String, Expenses>>) dataFromThread.get("dayWiseExpenseOfYou");
                Map<String, Map<String, Map.Entry<String, Expenses>>> expenseOfOthers = (Map<String, Map<String, Map.Entry<String, Expenses>>>) dataFromThread.get("dayWiseExpenseOfOthers");

                for (String dayKey : allSortedKeys) {
                    Map.Entry<String, Expenses> dayWiseExpense = expenseOfYou.get(dayKey);
                    Map<String, Map.Entry<String, Expenses>> allDayWiseExpenseFromSharer = expenseOfOthers.get(dayKey);
                    ExpensesTimeView expensesTimeView = new ExpensesTimeView(ExpenseTimelineView.this.getBaseContext(), null, dayWiseExpense, allDayWiseExpenseFromSharer, ExpenseTimelineView.this, index);
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
                                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollParent);
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


                return false;
            }
        });
        TimeLineViewLoader timeLineViewLoader = new TimeLineViewLoader(expenseKey, handler);
        timeLineViewLoader.start();
    }

    private Integer getOtherEntries(SortedMap<String, MonthWiseExpense> allSharedExpenses) {
        Integer totalEntries = 0;
        for (MonthWiseExpense wiseExpense : allSharedExpenses.values()) {
            totalEntries += wiseExpense.getTotalEntries();
        }
        return totalEntries;
    }

    private void loadMonthDetails(String expenseKey) {
        TextView monthDetails = (TextView) findViewById(R.id.monthDetails);
        String[] readableMonthAndYear = Utils.getReadableMonthAndYear(expenseKey);
        String displayedMonthAndYear = format("%s - %s", readableMonthAndYear[0], readableMonthAndYear[1]);
        monthDetails.setText(displayedMonthAndYear);

        monthDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ExpenseTimelineView.this, monthDetails);
                List<String> allExpensesMonths = Utils.getAllExpensesMonths();
                Menu menu = popup.getMenu();
                for (String allExpensesMonth : allExpensesMonths) {
                    String[] readableMonthAndYear = Utils.getReadableMonthAndYear(allExpensesMonth);
                    String newReadableMonthAndYear = format("%s - %s", readableMonthAndYear[0], readableMonthAndYear[1]);
                    if (!displayedMonthAndYear.equalsIgnoreCase(newReadableMonthAndYear)) {
                        menu.add(newReadableMonthAndYear);
                    }
                }

                popup.setOnMenuItemClickListener(ExpenseTimelineView.this);
                popup.show();
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadTimeLineView(expenseKey);
        itemSelected = "Home";
        setBackGroundTheme(null);
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

        if (id == R.id.nav_tags) {
            Intent i = new Intent(ExpenseTimelineView.this, ExpenseTagsEditView.class);
            startActivity(i);

        } else if (id == R.id.nav_usual_expenses) {
            Intent i = new Intent(ExpenseTimelineView.this, RecurringExpensesView.class);
            startActivity(i);

        } else if (id == R.id.nav_expendio_theme) {
            Intent i = new Intent(ExpenseTimelineView.this, ExpendioThemeSettingsActivity.class);
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
        } else if (id == R.id.nav_offers) {
            Intent i = new Intent(ExpenseTimelineView.this, OfferScreenActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:thriwin.solutions@gmail.com?subject=Expendio%20App%20Feedback"));
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                showToast(R.string.noEmailAppAvailable);
            }

        } else if (id == R.id.nav_download) {
            downloadMonthWiseExpense();

        } else if (id == R.id.nav_google_sync) {
            Intent i = new Intent(ExpenseTimelineView.this, GoogleCloudSynchActivity.class);
            startActivity(i);

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

        } else if (id == R.id.nav_remove_sharer_expense) {
            Intent i = new Intent(ExpenseTimelineView.this, SharedExpenseDetailsRemove.class);
            i.putExtra(ExpenseMonthWiseLimit.EXPENSE_STORAGE_KEY, expenseKey);
            startActivity(i);

        } else if (id == R.id.nav_share_expendio) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Expendio - Free Expense manager voice enhanced. \nPlease click the below link to download and enjoy.\n" +
                    "https://play.google.com/store/apps/details?id=com.thriwin.expendio" +
                    "\ndeveloped by Thriwin Solutions.");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_sms_receiver) {
            Intent i = new Intent(ExpenseTimelineView.this, ExpenseSMSPattern.class);
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent i = new Intent(ExpenseTimelineView.this, ExpenseTimelineView.class);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("ExpenseKey", Utils.getStorageKeyFromText(item.getTitle().toString()));
        ContextCompat.startActivity(ExpenseTimelineView.this, i, null);
        return false;
    }
}
