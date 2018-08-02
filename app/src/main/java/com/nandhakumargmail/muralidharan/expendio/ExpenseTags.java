package com.nandhakumargmail.muralidharan.expendio;

import android.content.SharedPreferences;
import android.util.ArraySet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.nandhakumargmail.muralidharan.expendio.Utils.*;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

public class ExpenseTags {
    ExpenseTag defaultTags = new ExpenseTag();
    private SharedPreferences localStorageForPreferences;
    ObjectMapper objectMapper = new ObjectMapper();

    public ExpenseTags(SharedPreferences localStorageForPreferences) {
        this.localStorageForPreferences = localStorageForPreferences;
        objectMapper = new ObjectMapper();
        defaultTags.put("eat", asList("Food"));
        defaultTags.put("hotel", asList("Food"));
        defaultTags.put("restaurant", asList("Food"));
        defaultTags.put("food", asList("Food"));
        defaultTags.put("bakery", asList("Food"));
        defaultTags.put("grocery", asList("Food"));
        defaultTags.put("groceries", asList("Food"));
        defaultTags.put("hospital", asList("Health"));
        defaultTags.put("meat", asList("Food"));
        defaultTags.put("medicine", asList("Health"));
        defaultTags.put("pharmacy", asList("Health"));
        defaultTags.put("bus", asList("Travel"));
        defaultTags.put("travel", asList("Travel"));
        defaultTags.put("miscellaneous", asList("Misc."));
        loadDefaultExpenseTagsIfNotInitialized();

    }


    public void loadDefaultExpenseTagsIfNotInitialized() {
        ExpenseTag tags = getSavedExpenseTags();
        if (isNull(tags) || tags.isEmpty()) {
            SharedPreferences.Editor edit = localStorageForPreferences.edit();
            String defaultExpenseTagsAsString = null;
            try {
                defaultExpenseTagsAsString = objectMapper.writeValueAsString(defaultTags);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            edit.putString(TAGS, defaultExpenseTagsAsString);
            edit.apply();
        }
    }

    public Set<String> getAssociatedExpenseTags(List<String> words) {
        ExpenseTag tags = getSavedExpenseTags();
        Set<String> tagWords = new ArraySet<>();
        for (String word : words) {
            List<String> t = tags.get(word.toLowerCase());
            if (!isNull(t)) {
                tagWords.addAll(t);
            }
        }
        return tagWords;
    }

    private ExpenseTag getSavedExpenseTags() {
        try {
            return objectMapper.readValue(localStorageForPreferences.getString(TAGS, "{}"), ExpenseTag.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
