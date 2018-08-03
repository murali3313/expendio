package com.nandhakumargmail.muralidharan.expendio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import static com.nandhakumargmail.muralidharan.expendio.Utils.loadLocalStorageForPreferences;

public class ExpenseListener extends SpeechActivity {


    HomeScreenView homeScreenView;
    ExpenseTalkView analyticsView;
    NotificationView notificationView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadDisplayArea(homeScreenView);
                    return true;
                case R.id.navigation_talk:
                    loadDisplayArea(analyticsView);
                    return true;
                case R.id.navigation_notifications:
                    loadDisplayArea(notificationView);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadDisplayArea(homeScreenView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_expense_listener);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadLocalStorageForPreferences(this.getApplicationContext());
        homeScreenView = new HomeScreenView(ExpenseListener.this.getApplicationContext(), null,this);
        analyticsView = new ExpenseTalkView(ExpenseListener.this.getApplicationContext(), null);
        notificationView = new NotificationView(ExpenseListener.this.getApplicationContext(), null);
        loadDisplayArea(homeScreenView);
        super.onCreate(savedInstanceState);

    }

    public void loadDisplayArea(IDisplayAreaView displayAreaView) {
        LinearLayout displayArea = findViewById(R.id.displayArea);
        displayArea.removeAllViews();
        displayArea.addView((View) displayAreaView);
        displayAreaView.load();
    }


    public void addExpense(View view) {
        Intent i = new Intent(ExpenseListener.this, NewExpensesCreation.class);
        startActivity(i);
    }
}
