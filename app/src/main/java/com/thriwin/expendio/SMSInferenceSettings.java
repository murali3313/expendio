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
    private static SMSProcessor smsProcessor = new SMSProcessor();

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
            String[] multiPhrases = smsPhrase.split(",");
            boolean contains = true;
            for (String multiPhrase : multiPhrases) {
                if (!completMessages.trim().toLowerCase().contains(multiPhrase.trim().toLowerCase())) {
                    contains = false;
                    break;
                }

            }
            if (contains) {
                return true;
            }
        }
        return false;
    }
}
