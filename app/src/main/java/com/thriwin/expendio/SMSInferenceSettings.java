package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SMSInferenceSettings {
    boolean isEnabled = false;

    ArrayList<String> smsPhrases = new ArrayList<>();
    @JsonIgnore
    private static SMSProcessor smsProcessor;

    public void setSmsPhrase(String smsPhrase) {
        smsPhrases.add(smsPhrase);
    }

    public Expense getProbableExpenses(String completMessages) {
        if (containPhrases(completMessages)) {
            return smsProcessor.getExpense(completMessages);
        }
        return null;
    }

    private boolean containPhrases(String completMessages) {
        for (String smsPhrase : smsPhrases) {
            boolean contains = completMessages.contains(smsPhrase);
            if (contains) {
                return true;
            }
        }
        return false;
    }
}
