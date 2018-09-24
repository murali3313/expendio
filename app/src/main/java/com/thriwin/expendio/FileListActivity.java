package com.thriwin.expendio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FileListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);
        LinearLayout fileListContainer = (LinearLayout) findViewById(R.id.listFiles);
        this.findViewById(R.id.container).setBackgroundResource(GeneralActivity.getBackGround(null));
        String[] fileLists = this.getIntent().getStringExtra("FileList").split(",");
        for (String fileList : fileLists) {
            TextView textView = new TextView(getApplicationContext());
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setText(fileList.substring(fileList.lastIndexOf("/") + 1));
            textView.setTextSize(23);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 20);
            textView.setPadding(10,10,10,10);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.tag_block_border);
            fileListContainer.addView(textView);
            textView.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                String text = ((TextView) v).getText().toString();
                String filePath = getFilePath(fileLists, text);
                resultIntent.putExtra("SELECTED_FILE", filePath);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            });
        }

    }

    @NonNull
    private String getFilePath(String[] fileLists, String text) {
        String filePath = "";
        for (String list : fileLists) {
            if (list.contains(text)) {
                filePath = list;
                break;
            }
        }
        return filePath;
    }

}
