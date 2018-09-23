package com.thriwin.expendio;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseTagsEditView extends GeneralActivity {
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.tags_edit_view);
        super.onCreate(savedInstanceState);
        load();
    }

    public void load() {
        ExpenseTags expenseTags = ExpenseTags.getSavedExpenseTags();
        LinearLayout tagContainer = findViewById(R.id.tagsEditContainer);
        tagContainer.removeAllViews();
        for (Map.Entry<String, List<String>> tags : expenseTags.getTagAndWordsAssociated()) {
            ExpenseTagEditView tagEditView = new ExpenseTagEditView(this, null, tags.getKey(), tags.getValue(), tagContainer);
            tagContainer.addView(tagEditView);

        }

        View createTag = findViewById(R.id.createNewTag);

        createTag.setOnClickListener(v -> {
            ExpenseTagEditView tagEditView = new ExpenseTagEditView(this, null, "", new ArrayList<>(), tagContainer);
            tagContainer.addView(tagEditView, 0);
            tagEditView.requestFocus();
        });

        View saveTags = findViewById(R.id.saveTags);

        saveTags.setOnClickListener(v -> {
            HashMap<String, List<String>> allTagAndWords = new HashMap<>();
            for (int i = 0; i < tagContainer.getChildCount(); i++) {
                HashMap<String, List<String>> tagAndWords = ((ExpenseTagEditView) tagContainer.getChildAt(i)).getTagAndWords();
                allTagAndWords.putAll(tagAndWords);
            }
            ExpenseTags.saveExpenseTags(allTagAndWords);
            load();
            showToast(R.string.tagSavedSuccessfully);
        });

        View resetTag = findViewById(R.id.resetTags);
        resetTag.setOnClickListener(v -> {
            View sheetView = View.inflate(this, R.layout.bottom_factory_reset_tag_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setContentView(sheetView);
            ((View)sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
            mBottomSheetDialog.show();

            mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(v1 -> {
                ExpenseTags.saveExpenseTags(new HashMap<>());
                ExpenseTags.loadDefaultExpenseTagsIfNotInitialized();
                load();
                mBottomSheetDialog.cancel();
            });

            mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(v12 -> mBottomSheetDialog.cancel());
        });

    }


}
