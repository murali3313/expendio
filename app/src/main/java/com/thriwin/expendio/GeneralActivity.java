package com.thriwin.expendio;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import static com.thriwin.expendio.Utils.isNull;
import static java.util.Arrays.asList;

public class GeneralActivity extends CommonActivity implements NavigationView.OnNavigationItemSelectedListener {
    private com.google.android.gms.ads.AdView googleAdView;
    private int index = 0;
    LinearLayout adContainer;
    LinearLayout afterManiAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        super.onCreate(savedInstanceState);
    }


    public static int getBackGround(BackgroundTheme backgroundThemeValue) {
        int bgTheme;
        if (isNull(backgroundThemeValue)) {
            BackgroundTheme backgroundTheme = ExpendioThemeSettings.loadExpendioThemeSettings().getBackgroundTheme();
            bgTheme = backgroundTheme.getResurceId();
        } else {
            bgTheme = backgroundThemeValue.getResurceId();
        }
        return bgTheme;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tags) {
            Intent i = new Intent(GeneralActivity.this, ExpenseTagsEditView.class);
            startActivity(i);

        } else if (id == R.id.nav_expendio_theme) {
            Intent i = new Intent(GeneralActivity.this, ExpendioThemeSettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_usual_expenses) {
            Intent i = new Intent(GeneralActivity.this, RecurringExpensesView.class);
            startActivity(i);

        } else if (id == R.id.nav_download) {
            downloadAllExpenses();

        } else if (id == R.id.nav_google_sync) {
            Intent i = new Intent(GeneralActivity.this, GoogleCloudSynchActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_restore_expense) {
            Intent i = new Intent(GeneralActivity.this, ExpenseRestoreActivity.class);
            startActivity(i);


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
        } else if (id == R.id.nav_offers) {
            Intent i = new Intent(GeneralActivity.this, OfferScreenActivity.class);
            startActivity(i);
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
            Intent i = new Intent(GeneralActivity.this, ExpendioSettingsView.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadAllExpenses() {

        SortedMap<String, MonthWiseExpense> allExpensesMonthWise = Utils.getAllExpensesMonthWise();
        File all_expenses = generator.genarateExcelForAllMonths(getBaseContext(), allExpensesMonthWise, "All_Expenses");
        presentTheFileToTheUser(all_expenses);
    }

    protected void addAdMobOffer(String adUnitId, com.google.android.gms.ads.AdSize adSize, List<String> keyWords) {
        adContainer = (LinearLayout) findViewById(R.id.banner_container);
        afterManiAd = (LinearLayout) findViewById(R.id.afterMainAd);
//        if (1 == 1)
//            return;
        if (ExpendioSettings.loadExpendioSettings().getBlockAds()) {
            ifAdsNotLoading();
            return;
        }

        googleAdView = new com.google.android.gms.ads.AdView(this);
        googleAdView.setAdSize(adSize);
        googleAdView.setAdUnitId(adUnitId);


        googleAdView.setAdListener(new com.google.android.gms.ads.AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                findViewById(R.id.banner_container).setVisibility(View.VISIBLE);
                View viewById = findViewById(R.id.offerLoadingMessage);
                if (!isNull(viewById))
                    viewById.setVisibility(View.GONE);
                if (!isNull(afterManiAd)) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, (int) DimentionUtils.converPixelsToDP(75, GeneralActivity.this), 0, 0);
                    afterManiAd.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                ifAdsNotLoading();
            }


        });
        googleAdView.setPadding(0, 3, 0, 7);
        adContainer.addView(googleAdView);
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String keyWord : keyWords) {
            builder.addKeyword(keyWord);
        }
        builder.addTestDevice(com.google.ads.AdRequest.TEST_EMULATOR);
        googleAdView.loadAd(builder.build());
    }


    private void ifAdsNotLoading() {
        if (!isNull(afterManiAd)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 90, 0, 0);
            afterManiAd.setLayoutParams(layoutParams);
        }
        findViewById(R.id.banner_container).setVisibility(View.GONE);
    }


    //todo: Google Keywords
    protected List<String> getKeyWordsForGoogle() {
        return asList();
//        List<String> values = ExpenseTags.getSavedExpenseTags().getWords();
//
//        List<String> keyWords=new ArrayList<>();
//
//        for (; index < values.size() && keyWords.size() <= 5; index++) {
//            keyWords.add(values.get(index));
//
//        }
//        if (index == values.size() - 1) {
//            index = 0;
//        }
//        return keyWords;

    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackGroundTheme(null);
    }

    public static boolean isPermissionDenied(Context ctx, String[] permissions) {
        boolean isAnyPermissionDenied = false;
        for (String permission : permissions) {
            isAnyPermissionDenied = ContextCompat.checkSelfPermission(ctx, permission)
                    != PackageManager.PERMISSION_GRANTED;
            if (isAnyPermissionDenied) {
                return true;
            }
        }
        return isAnyPermissionDenied;
    }

}
