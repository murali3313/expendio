package com.thriwin.expendio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTagsEditView extends FlowLayout implements IDisplayAreaView {
    ObjectMapper obj = new ObjectMapper();
    private ExpenseListener expenseListener;

    public ExpenseTagsEditView(Context context, @Nullable AttributeSet attrs, ExpenseListener expenseListener) {
        super(context, attrs);
        this.expenseListener = expenseListener;
        inflate(context, R.layout.tags_edit_view, this);
    }

    @Override
    public void load(ExpenseListener expenseListener) {
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
            showToast(R.string.tagSavedSuccessfully);
            this.expenseListener.loadDisplayAreaWithHomeScreen();
        });
    }

    protected void showToast(int resourceId) {
        Toast toast = Toast.makeText(expenseListener, resourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 500);
        toast.show();
    }
}
