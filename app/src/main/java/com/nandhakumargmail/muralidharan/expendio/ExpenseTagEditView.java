package com.nandhakumargmail.muralidharan.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nandhakumargmail.muralidharan.expendio.Utils.isEmpty;

public class ExpenseTagEditView extends FlowLayout {
    FlowLayout taggedWordsContainer;

    public ExpenseTagEditView(Context context, @Nullable AttributeSet attrs, String tagKey, List<String> tagWords) {
        super(context, attrs);
        inflate(context, R.layout.tag_edit_view, this);
        TextView tagHeader = findViewById(R.id.tagHeader);
        tagHeader.setText(tagKey);
        taggedWordsContainer = findViewById(R.id.tagWords);
        for (String word : tagWords) {
            addTags(taggedWordsContainer, word);
        }

        findViewById(R.id.addWordUnderTag).setOnClickListener(v -> {
            EditText newTagWord = findViewById(R.id.tagWord);
            String newWord = newTagWord.getText().toString();

            if (!tagWordContain(newWord) && !isEmpty(newWord)) {
                addTags(taggedWordsContainer, newWord);
            }

            newTagWord.setText("");
        });
    }

    private boolean tagWordContain(String newWord) {
        return getAllTaggedWords().contains(newWord);
    }

    private ArrayList<String> getAllTaggedWords() {
        ArrayList<String> tagWords = new ArrayList<>();
        for (int i = 0; i < taggedWordsContainer.getChildCount(); i++) {
            TextView tagWord = (TextView) taggedWordsContainer.getChildAt(i);
            tagWords.add(tagWord.getText().toString());
        }
        return tagWords;
    }

    private void addTags(FlowLayout taggedWordsContainer, String word) {
        TextView tagWord = new TextView(getContext(), null);
        tagWord.setText(word);
        tagWord.setTextSize(17);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        tagWord.setLayoutParams(params);
        tagWord.setPadding(20, 10, 20, 10);
        tagWord.setBackgroundResource(R.drawable.tag_border);
        tagWord.setOnClickListener(v -> {
            ((EditText) findViewById(R.id.tagWord)).setText(((TextView) v).getText().toString());
            taggedWordsContainer.removeView(v);
        });
        taggedWordsContainer.addView(tagWord);
    }


    public HashMap<String, List<String>> getTagAndWords() {
        EditText tagWord = findViewById(R.id.tagHeader);
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < taggedWordsContainer.getChildCount(); i++) {
            words.add(((TextView) taggedWordsContainer.getChildAt(i)).getText().toString());
        }
        if (isEmpty(tagWord.getText().toString()) || words.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<String, List<String>> tagAndWords = new HashMap<>();
        tagAndWords.put(tagWord.getText().toString(), words);
        return tagAndWords;
    }
}
