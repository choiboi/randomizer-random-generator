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
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.parse.SavedRandomizedListData;
import com.alottapps.randomizer.utils.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;

public class GetFileNameDialogActivity extends Activity {
    
    // Members.
    private String mInitName;
    private String mData;
    private EditText mNameEt;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_file_name);

        // Make sure the dialog fills up 80% width of the screen.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        mNameEt = (EditText) findViewById(R.id.agfn_name_edittext);

        int type = getIntent().getIntExtra(Constants.LIST_TYPE, 0);
        String objID = getIntent().getExtras().getString(Constants.SELECTION_OBJ_ID);

        if (type == Constants.RANDOMIZED_LIST) {
            SavedRandomizedListData.querySingleDataFromLocalstore(objID, new GetCallback<SavedRandomizedListData>() {
                @Override
                public void done(SavedRandomizedListData data, ParseException e) {
                    mData = data.getRandomizedData();
                    mInitName = "";
                    mNameEt.setText(mInitName, TextView.BufferType.EDITABLE);
                }
            });
        } else {
            Data.querySingleDataFromLocalstore(objID, new GetCallback<Data>() {
                @Override
                public void done(Data data, ParseException e) {
                    mData = data.getData();
                    mInitName = data.getDataName();
                    mNameEt.setText(mInitName, TextView.BufferType.EDITABLE);
                }
            });
        }
        
        setResult(RESULT_CANCELED);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.agfn_cancel_button) {
            finish();
        } else if (v.getId() == R.id.agfn_save_button) {
            String filename = mNameEt.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA, mData);

            if (filename.equals("") && mInitName != null && !mInitName.equals("")) {
                intent.putExtra(Constants.FILENAME, mInitName);
            } else if (filename.equals("")) {
                intent.putExtra(Constants.FILENAME, "LIST");
            } else {
                intent.putExtra(Constants.FILENAME, filename);
            }
            
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
