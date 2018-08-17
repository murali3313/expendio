package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thriwin.expendio.Utils.showToast;

public class ExpenseTagsEditView extends FlowLayout implements IDisplayAreaView {
    ObjectMapper obj = new ObjectMapper();
    private ExpenseListener expenseListener;

    public ExpenseTagsEditView(Context context, @Nullable AttributeSet attrs, ExpenseListener expenseListener) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflate(context, R.layout.tags_edit_view, this);
    }

    @Override
    public void load(CommonActivity expenseListener, Intent intent) {
        ExpenseTags expenseTags = ExpenseTags.getSavedExpenseTags();
        LinearLayout tagContainer = findViewById(R.id.tagsEditContainer);
        tagContainer.removeAllViews();
        for (Map.Entry<String, List<String>> tags : expenseTags.getTagAndWordsAssociated()) {
            ExpenseTagEditView tagEditView = new ExpenseTagEditView(getContext(), null, tags.getKey(), tags.getValue(), tagContainer);
            tagContainer.addView(tagEditView);

        }

        View createTag = findViewById(R.id.createNewTag);

        createTag.setOnClickListener(v -> {
            ExpenseTagEditView tagEditView = new ExpenseTagEditView(getContext(), null, "", new ArrayList<>(), tagContainer);
            tagContainer.addView(tagEditView, 0);
        });

        View saveTags = findViewById(R.id.saveTags);

        saveTags.setOnClickListener(v -> {
            HashMap<String, List<String>> allTagAndWords = new HashMap<>();
            for (int i = 0; i < tagContainer.getChildCount(); i++) {
                HashMap<String, List<String>> tagAndWords = ((ExpenseTagEditView) tagContainer.getChildAt(i)).getTagAndWords();
                allTagAndWords.putAll(tagAndWords);
            }
            ExpenseTags.saveExpenseTags(allTagAndWords);
            showToast(expenseListener, R.string.tagSavedSuccessfully);
            this.expenseListener.loadDisplayAreaWithHomeScreen();
        });

    }


}
