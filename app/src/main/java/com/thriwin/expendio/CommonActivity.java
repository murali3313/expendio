package com.thriwin.expendio;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageButton;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.thriwin.expendio.ExpenseAudioStatements.CLEAR;
import static com.thriwin.expendio.ExpenseAudioStatements.defaultEndOfStatement;

public class CommonActivity extends AppCompatActivity {
    protected static final int REQ_CODE_SPEECH_INPUT = 55;
    protected static final int REQ_CODE_DOWNLOAD = 57;
    protected static final int FILE_SELECTION_CODE = 59;
    private ExpenseAudioListener expenseAudioListener = null;
    View sheetView;
    GifImageButton indicator;
    BottomSheetDialog mBottomSheetDialog;
    private static TextView indicatorText;
    SwipeButton clearLastStatementSlider;
    SwipeButton stopStatementSlider;

    HomeScreenView homeScreenView;
    ExpenseAnalyticsView analyticsView;
    NotificationView notificationView;

    DashboardView selectedDashboardView;


    protected ExcelGenerator generator = new ExcelGenerator();


    public void updateWithUserSpeech(ExpenseAudioStatements expenseAudioStatements) {
        TextView textTalk = sheetView.findViewById(R.id.talkText);
        textTalk.setText(expenseAudioStatements.getAllUserFormattedStatements());
        clearLastStatementSlider.setEnabled(!expenseAudioStatements.getAllStatements().isEmpty());
    }

    protected void showToast(int resourceId) {
        Toast toast = Toast.makeText(CommonActivity.this, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }

    public void displayExpenseForCorrection(List<Expense> processedExpenses) {
        SharedPreferences.Editor edit = Utils.getLocalStorageForPreferences().edit();
        edit.putString(Utils.UNACCEPTED_EXPENSES, serializeExpenses(processedExpenses));
        edit.apply();
        doneWithListening();

        Intent i = new Intent(CommonActivity.this, ExpenseAcceptance.class);
        startActivity(i);
    }

    public void doneWithListening() {
        expenseAudioListener.clearAudioStatements();
        expenseAudioListener.stopListening();
        mBottomSheetDialog.cancel();
    }

    @Nullable
    protected String serializeExpenses(List<Expense> processedExpenses) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
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
                indicator.setImageResource(R.drawable.listening);
                indicatorText.setText(R.string.listening);
                break;
            case DEAF:
                indicator.setImageResource(R.drawable.wait);
                indicatorText.setText(R.string.deaf);
                break;
            case PROCESSING:
                indicator.setImageResource(R.drawable.processing);
                indicatorText.setText(R.string.processing);

                break;
        }

    }

    private void listenExpense(View v) {
        sheetView = View.inflate(CommonActivity.this, R.layout.bottom_listening_indicator, null);
        mBottomSheetDialog = new BottomSheetDialog(CommonActivity.this);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.show();
        indicator = sheetView.findViewById(R.id.audio_processor_indicator);
        indicatorText = sheetView.findViewById(R.id.audio_processor_indicator_text);
        clearLastStatementSlider = sheetView.findViewById(R.id.clear_last_statement);
        clearLastStatementSlider.setOnActiveListener(() -> {
            expenseAudioListener.processAudioResult(CLEAR);

        });
        clearLastStatementSlider.setEnabled(false);

        stopStatementSlider = sheetView.findViewById(R.id.stop_listening);
        stopStatementSlider.setOnActiveListener(() -> {
            expenseAudioListener.processAudioResult(defaultEndOfStatement);
        });

        mBottomSheetDialog.setOnCancelListener(dialog -> expenseAudioListener.stopListening());
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View listenButton = this.findViewById(R.id.addExpenseVoice);
        listenButton.setOnClickListener(v -> listenExpense(v));
        expenseAudioListener = new ExpenseAudioListener(Utils.getLocalStorageForPreferences(), this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    protected void presentTheFileToTheUser(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uriForFile = FileProvider.getUriForFile(getBaseContext(),
                    getBaseContext().getPackageName() + ".GenericFileProvider",
                    file);
            intent.setDataAndType(uriForFile, "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CommonActivity.this, "No Application Available to View Excel." +
                    "\n The file is stored in the following location: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }

    protected Intent intent;

    protected void openFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getApplicationContext().getPackageName() + "/files");
        File[] files = file.listFiles();
        String fileList = "";
        for (File f : files) {
            fileList += f.getAbsolutePath() + ",";
        }
        fileList = fileList.substring(0, fileList.lastIndexOf(","));
        Intent i = new Intent(CommonActivity.this, FileListActivity.class);
        i.putExtra("FileList", fileList);
        startActivityForResult(i, FILE_SELECTION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECTION_CODE: {
                try {
                    String path = data.getStringExtra("SELECTED_FILE");
                    presentTheFileToTheUser(new File(path));
                } catch (Exception e) {
                    Toast.makeText(CommonActivity.this, "Sorry couldn't open the file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public static String itemSelected = "Home";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i = new Intent(getApplicationContext(), ExpenseListener.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            if (!shouldRedirectToNewActivity(item)) {
                return true;
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    itemSelected = getResources().getString(R.string.title_home);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("DISPLAY_VIEW", DashboardView.HOME.toString());
                    ContextCompat.startActivity(getApplicationContext(), i, null);
                    return true;
                case R.id.navigation_analytics:
                    itemSelected = getResources().getString(R.string.title_expense_analysis);
                    i.putExtra("DISPLAY_VIEW", DashboardView.ANALYTICS.toString());
                    i.putExtra("ANALYTICS_MONTH", getMonthForAnalytics());
                    ContextCompat.startActivity(getApplicationContext(), i, null);
                    return true;
                case R.id.navigation_notifications:
                    itemSelected = getResources().getString(R.string.title_notifications);
                    i.putExtra("DISPLAY_VIEW", DashboardView.NOTIFICATION.toString());
                    ContextCompat.startActivity(getApplicationContext(), i, null);
                    return true;
            }
            return false;
        }
    };

    public boolean shouldRedirectToNewActivity(@NonNull MenuItem item) {
        return !itemSelected.equalsIgnoreCase(item.getTitle().toString()) || (!(CommonActivity.this instanceof ExpenseListener) && item.getTitle().toString().equals("Home"));
    }

    protected String getMonthForAnalytics() {
        return null;
    }

    @Override
    public void onBackPressed() {
        if (this instanceof ExpenseListener && itemSelected.equalsIgnoreCase("Home")) {
            createDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void createDialog() {

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Are you sure you want to exit?");
        alertDlg.setCancelable(false); // We avoid that the dialong can be cancelled, forcing the user to choose one of the options
        alertDlg.setPositiveButton("Yes", (dialog, id) -> CommonActivity.super.onBackPressed());

        alertDlg.setNegativeButton("No", (dialog, which) -> {
        });

        alertDlg.create().show();
    }

    public static void setupParent(View view, Activity activity) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView, activity);
            }
        }
    }

    private static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }


}