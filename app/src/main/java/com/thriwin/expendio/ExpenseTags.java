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

import static com.thriwin.expendio.Utils.TAGS;
import static com.thriwin.expendio.Utils.getLocalStorageForPreferences;
import static com.thriwin.expendio.Utils.isNull;
import static java.util.Arrays.asList;

public class ExpenseTags {
    public static final String MISCELLANEOUS_TAG = "UnCategorized";
    static ExpenseTag defaultTags = new ExpenseTag();
    private static SharedPreferences localStorageForPreferences = getLocalStorageForPreferences();
    static ObjectMapper objectMapper = new ObjectMapper();
    private static ExpenseTags savedExpenseTags = null;
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
            defaultTags.put("break fast", asList("Food"));
            defaultTags.put("breakfast", asList("Food"));
            defaultTags.put("lunch", asList("Food"));
            defaultTags.put("dinner", asList("Food"));
            defaultTags.put("restaurant", asList("Food"));
            defaultTags.put("bakery", asList("Food"));
            defaultTags.put("meat", asList("Food"));
            defaultTags.put("chicken", asList("Food"));
            defaultTags.put("grocery", asList("Food"));
            defaultTags.put("groceries", asList("Food"));
            defaultTags.put("food", asList("Food"));

            defaultTags.put("hospital", asList("Health"));
            defaultTags.put("pharmacy", asList("Health"));
            defaultTags.put("tablet", asList("Health"));
            defaultTags.put("capsules", asList("Health"));
            defaultTags.put("medicine", asList("Health"));
            defaultTags.put("pharmacy", asList("Health"));
            defaultTags.put("health", asList("Health"));

            defaultTags.put("bus", asList("Travel"));
            defaultTags.put("travel", asList("Travel"));
            defaultTags.put("bus ticket", asList("Travel"));
            defaultTags.put("train", asList("Travel"));
            defaultTags.put("train ticket", asList("Travel"));
            defaultTags.put("flight", asList("Travel"));
            defaultTags.put("travel", asList("Travel"));

            defaultTags.put("cinema", asList("Entertainment"));
            defaultTags.put("cinema ticket", asList("Entertainment"));
            defaultTags.put("play", asList("Entertainment"));
            defaultTags.put("games", asList("Entertainment"));
            defaultTags.put("video games", asList("Entertainment"));
            defaultTags.put("entertainment", asList("Entertainment"));

            defaultTags.put("petrol", asList("Vehicle"));
            defaultTags.put("diesel", asList("Vehicle"));
            defaultTags.put("bike", asList("Vehicle"));
            defaultTags.put("repair", asList("Vehicle"));
            defaultTags.put("vehicle", asList("Vehicle"));

            defaultTags.put("amazon", asList("Online Shopping"));
            defaultTags.put("flipkart", asList("Online Shopping"));
            defaultTags.put("online shopping", asList("Online Shopping"));

            defaultTags.put(MISCELLANEOUS_TAG, asList(MISCELLANEOUS_TAG));
            defaultTags.put("misc", asList("Misc."));
            defaultTags.put("Miscellaneous", asList("Misc."));
            writeToPersistence(defaultTags);
        }
    }

    private boolean isEmpty() {
        return this.tags.isEmpty();
    }

    public static Set<String> getAssociatedExpenseTags(String expenseStatement) {
        expenseStatement = Utils.isEmpty(expenseStatement) ? "" : expenseStatement;
        ExpenseTags tags = getSavedExpenseTags();
        String lowerCase = expenseStatement.toLowerCase();
        Set<String> tagWords = new ArraySet<>();
        for (Map.Entry<String, List<String>> tagWord : tags.getTags().entrySet()) {
            if (lowerCase.contains(tagWord.getKey().toLowerCase())) {
                tagWords.addAll(tagWord.getValue());
            }
        }

        if (tagWords.isEmpty()) {
            tagWords.add(MISCELLANEOUS_TAG);
        }
        return tagWords;
    }

    private static ArrayList<String> getLowerCase(List<String> words) {
        ArrayList<String> lowerCaseWords = new ArrayList<>();
        for (String word : words) {
            lowerCaseWords.add(word.toLowerCase());
        }
        return lowerCaseWords;
    }

    public static ExpenseTags getSavedExpenseTags() {
        if (isNull(savedExpenseTags)) {
            try {
                ExpenseTag expenseTag = objectMapper.readValue(localStorageForPreferences.getString(TAGS, "{}"), ExpenseTag.class);
                savedExpenseTags = new ExpenseTags(expenseTag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedExpenseTags;
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
        for (String tagKey : allTagAndWords.keySet()) {
            if (isNull(wordAndTags.get(tagKey))) {
                wordAndTags.put(tagKey, new ArrayList<String>() {{
                    add(tagKey);
                }});
            }
        }
        writeToPersistence(new ExpenseTag(wordAndTags));
        savedExpenseTags = null;
    }

    private static void writeToPersistence(ExpenseTag wordAndTags) {
        String expenseTagsAsString = null;
        SharedPreferences.Editor edit = localStorageForPreferences.edit();
        try {
            expenseTagsAsString = objectMapper.writeValueAsString(wordAndTags);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        edit.putString(TAGS, expenseTagsAsString);
        edit.apply();
        savedExpenseTags = new ExpenseTags(wordAndTags);
        ;
    }

    public List<String> getWords() {
        return this.tags.getWords();
    }

    public List<String> getTagsOnly() {
        List<String> tags = new ArrayList<>();
        for (Map.Entry<String, List<String>> tagEntry : getTagAndWordsAssociated()) {
            tags.add(tagEntry.getKey());
        }
        return tags;
    }
}
