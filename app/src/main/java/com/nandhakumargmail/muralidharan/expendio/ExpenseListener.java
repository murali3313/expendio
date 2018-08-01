package com.nandhakumargmail.muralidharan.expendio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;
import static com.nandhakumargmail.muralidharan.expendio.Utils.loadLocalStorageForPreferences;

public class ExpenseListener extends SpeechActivity {

    private static ExpenseAudioListener expenseAudioListener = null;

    HomeScreenView homeScreenView;
    ExpenseTalkView expenseTalkView;
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
                    loadDisplayArea(expenseTalkView);
                    listenExpense();
                    return true;
                case R.id.navigation_notifications:
                    loadDisplayArea(notificationView);
                    return true;
            }
            return false;
        }
    };

    private void listenExpense() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQ_CODE_SPEECH_INPUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    expenseAudioListener.startListening();
                } else {
                    showToast(R.string.audio_record_permission_denied);
                }
                return;
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadDisplayArea(homeScreenView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_listener);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadLocalStorageForPreferences(this.getApplicationContext());
        expenseAudioListener = new ExpenseAudioListener(getLocalStorageForPreferences(), this);
        homeScreenView = new HomeScreenView(ExpenseListener.this.getApplicationContext(), null);
        expenseTalkView = new ExpenseTalkView(ExpenseListener.this.getApplicationContext(), null);
        notificationView = new NotificationView(ExpenseListener.this.getApplicationContext(), null);
        loadDisplayArea(homeScreenView);
    }

    private void loadDisplayArea(IDisplayAreaView displayAreaView) {
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
