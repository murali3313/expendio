package com.thriwin.expendio;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ExpenseAudioStatements {
    private final String keyEndOfStatement = "END_OF_STATEMENT_MARKER";
    public static final String defaultEndOfStatement = "STOP";
    public static final String CLEAR = "Clear";
    public static final String REFRESH = "Refresh";

    private List<String> userStatements = new ArrayList<>();
    SpeechToExpenseEngine speechToExpenseEngine;


    private HashMap<String, SpeechAction> speechActions;

    public ExpenseAudioStatements(SharedPreferences localStorageForPreferences) {
        speechActions = new HashMap<String, SpeechAction>() {{
            put(CLEAR, SpeechAction.CLEAR_LAST_STATEMENT);
            put("Refresh", SpeechAction.REFRESH);
            put("Stop", SpeechAction.STOP);
        }};

        speechToExpenseEngine = new SpeechToExpenseEngine(localStorageForPreferences);
    }


    public boolean isExpenseStatementCompleteFromUser() {
        return lastSpokeStatement().equalsIgnoreCase(defaultEndOfStatement);
    }

    private String lastSpokeStatement() {
        return userStatements.get(userStatements.size() - 1);
    }

    public void addStatement(String userWords) {
        this.userStatements.add(userWords);
    }


    public String getAllUserFormattedStatements() {
        String allStatements = "";
        for (String s : userStatements) {
            allStatements = String.format("%s \r\n%s", allStatements, s);
        }
        return allStatements;
    }

    public String getAllStatements() {
        String allStatements = "";
        for (String s : userStatements) {
            allStatements += " and " + s;
        }
        return allStatements;
    }

    public List<String> getStatements() {
        return userStatements;
    }

    public void clear() {
        this.userStatements = new ArrayList<>();
    }

    public SpeechAction performSpecificActions() {
        SpeechAction action = SpeechAction.CONTINUE;
        for (Map.Entry<String, SpeechAction> entry : speechActions.entrySet()) {
            if (lastSpokeStatement().equalsIgnoreCase(entry.getKey())) {
                action = entry.getValue();
            }
        }
        switch (action) {
            case CLEAR_LAST_STATEMENT:
                removeLastAndPreviousStatement();
                break;
            case REFRESH:
                removeAllStatements();
                break;
            case STOP:
                removeLastStatement();
                break;
        }
        return action;
    }

    public void removeLastAndPreviousStatement() {
        removeLastStatement();
        if (this.userStatements.size() > 0) {
            removeLastStatement();
        }
    }

    private void removeLastStatement() {
        this.userStatements.remove(this.userStatements.size() - 1);
    }

    public void removeAllStatements() {
        this.userStatements = new ArrayList<>();
    }

    public Expenses getProcessedExpenses() {
        return speechToExpenseEngine.processAudio(this.getAllStatements());
    }

    public boolean isNotEmpty() {
        return !this.userStatements.isEmpty();
    }
}
