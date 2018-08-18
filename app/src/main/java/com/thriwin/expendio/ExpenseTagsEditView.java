package com.thriwin.expendio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ExpenseTagsEditView extends Activity{
    ObjectMapper obj = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_edit_view);
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
        });

        View saveTags = findViewById(R.id.saveTags);

        saveTags.setOnClickListener(v -> {
            HashMap<String, List<String>> allTagAndWords = new HashMap<>();
            for (int i = 0; i < tagContainer.getChildCount(); i++) {
                HashMap<String, List<String>> tagAndWords = ((ExpenseTagEditView) tagContainer.getChildAt(i)).getTagAndWords();
                allTagAndWords.putAll(tagAndWords);
            }
            ExpenseTags.saveExpenseTags(allTagAndWords);
            showToast(this, R.string.tagSavedSuccessfully);
            this.finish();
        });

        View resetTag = findViewById(R.id.resetTags);
        resetTag.setOnClickListener(v -> {
            View sheetView = View.inflate(this, R.layout.bottom_factory_reset_tag_confirmation, null);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setContentView(sheetView);
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
