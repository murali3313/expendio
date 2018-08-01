package com.nandhakumargmail.muralidharan.expendio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.nandhakumargmail.muralidharan.expendio.Utils.UNACCEPTED_EXPENSES;
import static com.nandhakumargmail.muralidharan.expendio.Utils.getLocalStorageForPreferences;

public class SpeechActivity extends Activity {
    protected static final int REQ_CODE_SPEECH_INPUT = 55;

    public void updateWithUserSpeech(ExpenseAudioStatements expenseAudioStatements, boolean shouldContinueRecording) {
//        TextView viewById = findViewById(R.id.capturedStatements);
//        viewById.setText(expenseAudioStatements.getAllUserFormattedStatements());
        if (shouldContinueRecording) {
//            mTextMessage.setText(R.string.listening);
        } else {
//            mTextMessage.setText(R.string.processing);
        }
    }

    protected void showToast(int resourceId) {
        Toast toast = Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }

    public void displayExpenseForCorrection(List<Expense> processedExpenses) {
        SharedPreferences.Editor edit = getLocalStorageForPreferences().edit();
        edit.putString(UNACCEPTED_EXPENSES, serializeExpenses(processedExpenses));
        edit.apply();

        Intent i = new Intent(SpeechActivity.this, ExpenseAcceptance.class);
        startActivity(i);
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
//                mTextMessage.setText(R.string.listening);
                break;
            case DEAF:
//                mTextMessage.setText(R.string.deaf);
                break;
        }

    }
}
