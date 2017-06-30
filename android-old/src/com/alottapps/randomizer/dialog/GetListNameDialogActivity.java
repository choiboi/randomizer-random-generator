package com.alottapps.randomizer.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.Utils;

public class GetListNameDialogActivity extends Activity {
    
    // Members.
    private EditText mNameEt;
    private String mSelectionsListStr;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    
    // Constants.
    private String DEFAULT_LIST_NAME = "List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_list_name);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mSelectionsListStr = getIntent().getExtras().getString(Constants.SELECTIONS_LIST);
    
        mNameEt = (EditText) findViewById(R.id.agln_name_edittext);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.agln_cancel_button) {
            finish();
        } else if (v.getId() == R.id.agln_save_button) {
            String date = Utils.getCurrentDateTime();
            String dataId = "";
            if (mNameEt.getText().toString().equals("")) {
                dataId = mDB.addData(mSelectionsListStr, date, 0, DEFAULT_LIST_NAME);
            } else {
                dataId = mDB.addData(mSelectionsListStr, date, 0, mNameEt.getText().toString());
            }

            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_ID, dataId);
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
