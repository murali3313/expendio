package com.thriwin.expendio;

import android.content.SharedPreferences;
import android.util.ArraySet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.thriwin.expendio.Utils.*;
import static java.util.Arrays.asList;

public class ExpenseTags {
    static ExpenseTag defaultTags = new ExpenseTag();
    private static SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
    static ObjectMapper objectMapper = new ObjectMapper();
    private ExpenseTag tags;

    public ExpenseTags(ExpenseTag tags) {
        this.tags = tags;
    }


    public static void loadDefaultExpenseTagsIfNotInitialized() {
        ExpenseTags tags = getSavedExpenseTags();
        if (isNull(tags) || tags.isEmpty()) {
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
            writeToPersistence(defaultTags);
        }
    }

    private boolean isEmpty() {
        return this.tags.isEmpty();
    }

    public static Set<String> getAssociatedExpenseTags(List<String> words) {
        ExpenseTags tags = getSavedExpenseTags();
        Set<String> tagWords = new ArraySet<>();
        for (String word : words) {
            List<String> t = tags.tags.get(word.toLowerCase());
            if (!isNull(t)) {
                tagWords.addAll(t);
            }
        }
        return tagWords;
    }

    public static ExpenseTags getSavedExpenseTags() {
        try {
            ExpenseTag expenseTag = objectMapper.readValue(localStorageForPreferences.getString(TAGS, "{}"), ExpenseTag.class);
            return new ExpenseTags(expenseTag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, List<String>> getTags() {
        return this.tags.getTags();
    }

    public Set<Map.Entry<String, List<String>>> getTagAndWordsAssociated() {
        HashMap<String, List<String>> tagAndWords = new HashMap<>();
        for (Map.Entry<String, List<String>> wordAndTag : this.getTags().entrySet()) {
            List<String> tags = wordAndTag.getValue();
            for (String tag : tags) {
                if (isNull(tagAndWords.get(tag))) {
                    tagAndWords.put(tag, new ArrayList<String>() {{
                        add(wordAndTag.getKey());
                    }});
                } else {
                    tagAndWords.get(tag).add(wordAndTag.getKey());
                }
            }
        }
        return tagAndWords.entrySet();
    }

    public static void saveExpenseTags(HashMap<String, List<String>> allTagAndWords) {
        HashMap<String, List<String>> wordAndTags = new HashMap<>();
        for (Map.Entry<String, List<String>> tagAndWords : allTagAndWords.entrySet()) {
            for (String word : tagAndWords.getValue()) {
                if (isNull(wordAndTags.get(word))) {
                    wordAndTags.put(word, new ArrayList<String>() {{
                        add(tagAndWords.getKey());
                    }});
                } else {
                    wordAndTags.get(word).add(tagAndWords.getKey());
                }
            }
        }
        writeToPersistence(new ExpenseTag(wordAndTags));
    }

    private static void writeToPersistence(Object wordAndTags) {
        String expenseTagsAsString = null;
        SharedPreferences.Editor edit = localStorageForPreferences.edit();
        try {
            expenseTagsAsString = objectMapper.writeValueAsString(wordAndTags);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        edit.putString(TAGS, expenseTagsAsString);
        edit.apply();
    }
}
