package com.nandhakumargmail.muralidharan.expendio;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageButton;

import static com.nandhakumargmail.muralidharan.expendio.ExpenseAudioStatements.CLEAR;
import static com.nandhakumargmail.muralidharan.expendio.ExpenseAudioStatements.defaultEndOfStatement;
import static com.nandhakumargmail.muralidharan.expendio.Utils.UNACCEPTED_EXPENSES;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;

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
        SharedPreferences.Editor edit = getLocalStorageForPreferences().edit();
        edit.putString(UNACCEPTED_EXPENSES, serializeExpenses(processedExpenses));
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

    private void listenExpense() {
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
        listenButton.setOnClickListener(v -> listenExpense());
        expenseAudioListener = new ExpenseAudioListener(getLocalStorageForPreferences(), this);
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
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/" + getApplicationContext().getPackageName());
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);


        try {
            startActivityForResult(intent.createChooser(intent, "Open"), FILE_SELECTION_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CommonActivity.this, "Sorry couldn't open the folder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECTION_CODE: {
                try {
                    String path = data.getData().getPath();
                    String selectedFile = path.substring(path.indexOf("Android"));

                    presentTheFileToTheUser(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + selectedFile));
                } catch (Exception e) {
                    Toast.makeText(CommonActivity.this, "Sorry couldn't open the file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
