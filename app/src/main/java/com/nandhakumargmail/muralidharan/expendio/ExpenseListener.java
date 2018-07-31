package com.nandhakumargmail.muralidharan.expendio;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.*;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;
import static com.nandhakumargmail.muralidharan.expendio.Utils.UNACCEPTED_EXPENSES;
import static com.nandhakumargmail.muralidharan.expendio.Utils.loadLocalStorageForPreferences;
import static java.util.Arrays.asList;

public class ExpenseListener extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 55;
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

    public void updateWithUserSpeech(ExpenseAudioStatements expenseAudioStatements, boolean shouldContinueRecording) {
//        TextView viewById = findViewById(R.id.capturedStatements);
//        viewById.setText(expenseAudioStatements.getAllUserFormattedStatements());
        if (shouldContinueRecording) {
//            mTextMessage.setText(R.string.listening);
        } else {
//            mTextMessage.setText(R.string.processing);
        }
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

    private void showToast(int resourceId) {
        Toast toast = Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }


    public void displayExpenseForCorrection(List<Expense> processedExpenses) {
        SharedPreferences.Editor edit = getLocalStorageForPreferences().edit();
        edit.putString(UNACCEPTED_EXPENSES, serializeExpenses(processedExpenses));
        edit.apply();

        Intent i = new Intent(ExpenseListener.this, ExpenseAcceptance.class);
        startActivity(i);
    }

    @Nullable
    protected String serializeExpenses(List<Expense> processedExpenses) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        try {
            return objectMapper.writeValueAsString(processedExpenses);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void listeningInfo(ListeningQueues listeningQueues) {
        switch (listeningQueues) {
            case READY:
//                mTextMessage.setText(R.string.listening);
                break;
            case DEAF:
//                mTextMessage.setText(R.string.deaf);
                break;
        }

    }

    public void addExpense(View view) {
        Intent i = new Intent(ExpenseListener.this, NewExpensesCreation.class);
        startActivity(i);
    }
}
