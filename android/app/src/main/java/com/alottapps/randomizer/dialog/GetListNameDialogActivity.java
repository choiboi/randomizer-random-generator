package com.alottapps.randomizer.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.utils.Constants;

public class GetListNameDialogActivity extends Activity {
    
    // Members.
    private EditText mNameEt;
    private String mListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_list_name);

        // Make sure the dialog fills up 80% width of the screen.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    
        mNameEt = (EditText) findViewById(R.id.agln_name_edittext);
        mListStr = getIntent().getStringExtra(Constants.SELECTIONS_LIST);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.agln_cancel_button) {
            finish();
        } else if (v.getId() == R.id.agln_save_button) {
            String listName = Constants.DEFAULT_LIST_NAME;
            if (!mNameEt.getText().toString().equals("")) {
                listName = mNameEt.getText().toString();
            }

            Intent intent = new Intent();
            intent.putExtra(Constants.SELECTIONS_LIST_NAME, listName);
            intent.putExtra(Constants.SELECTIONS_LIST, mListStr);
            setResult(RESULT_OK, intent);
            
            finish();
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Remove virtual keyboard when EditText is out of focus.
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP &&
                    (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                mNameEt.clearFocus();
            }
        }
        return ret;
    }
}
