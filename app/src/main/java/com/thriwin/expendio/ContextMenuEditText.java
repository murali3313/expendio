package com.thriwin.expendio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class ContextMenuEditText extends android.support.v7.widget.AppCompatEditText {

    private final Context context;

    /*
        Just the constructors to create a new EditText...
     */
    public ContextMenuEditText(Context context) {
        super(context);
        this.context = context;
    }

    public ContextMenuEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ContextMenuEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }


    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {

            case android.R.id.paste:
                onTextPaste();
                break;

        }
        return consumed;
    }


    public void onTextPaste() {
        EditText restorableExpensesView = ContextMenuEditText.this;
        String restorableExpenses = restorableExpensesView.getText().toString().trim();
        restorableExpensesView.setText(restorableExpenses);
    }
}