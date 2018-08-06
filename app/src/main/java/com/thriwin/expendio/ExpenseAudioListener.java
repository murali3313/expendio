package com.thriwin.expendio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.RecognizerIntent.EXTRA_LANGUAGE;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL;
import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

public class ExpenseAudioListener
        implements RecognitionListener {
    private final String SPOKEN_WORDS = "SPOKEN_WORDS";
    private final String DAILY_EXPENSER = "DAILY_EXPENSER";
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private SharedPreferences localStorageForPreferences;
    private CommonActivity expenseMain;
    private static ExpenseAudioStatements expenseAudioStatements;
    boolean userStopped;


    public ExpenseAudioListener(SharedPreferences localStorageForPreferences, CommonActivity expenseMain) {
        this.localStorageForPreferences = localStorageForPreferences;
        this.expenseMain = expenseMain;
        speech = SpeechRecognizer.createSpeechRecognizer(expenseMain);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(EXTRA_LANGUAGE, Locale.getDefault());

        expenseAudioStatements = new ExpenseAudioStatements(localStorageForPreferences);
    }

    public void clearAudioStatements() {
        expenseAudioStatements.clear();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        expenseMain.listeningInfo(ListeningQueues.READY);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        expenseMain.listeningInfo(ListeningQueues.DEAF);
    }

    @Override
    public void onError(int error) {
        if (error == 7 && !userStopped) {
            speech.cancel();
            speech.startListening(recognizerIntent);
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String userWords = matches.get(0);
        processAudioResult(userWords);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void stopListening() {
        speech.stopListening();
    }

    public void startListening() {
        speech.startListening(recognizerIntent);
    }

    public void startOver() {
        expenseAudioStatements.clear();
        startListening();
    }

    public void processAudioResult(String userWords) {
        expenseAudioStatements.addStatement(userWords);
        if (expenseAudioStatements.isExpenseStatementCompleteFromUser()) {
            userStopped = true;
            speech.stopListening();
            expenseMain.listeningInfo(ListeningQueues.PROCESSING);
            expenseMain.updateWithUserSpeech(expenseAudioStatements);
            expenseAudioStatements.performSpecificActions();
            if (expenseAudioStatements.isNotEmpty()) {
                expenseMain.displayExpenseForCorrection(expenseAudioStatements.getProcessedExpenses());
            } else {
                expenseMain.doneWithListening();
            }

        } else {
            userStopped = false;

            expenseAudioStatements.performSpecificActions();
            expenseMain.updateWithUserSpeech(expenseAudioStatements);
            speech.startListening(recognizerIntent);
        }
    }
}
