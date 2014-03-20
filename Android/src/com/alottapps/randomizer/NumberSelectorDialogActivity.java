package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alottapps.randomizer.util.Constants;

public class NumberSelectorDialogActivity extends Activity {
    
    // Members.
    private EditText mFromEt;
    private EditText mToEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_selector);
        
        mFromEt = (EditText) findViewById(R.id.ans_start_number_edittext);
        mToEt = (EditText) findViewById(R.id.ans_end_number_edittext);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ans_ok_button) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(Constants.TYPE_RANDOM, Constants.NUMBER_RANGE_RANDOM);
            intent.putExtra(Constants.START_NUMBER, mFromEt.getText().toString());
            intent.putExtra(Constants.END_NUMBER, mToEt.getText().toString());
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.ans_cancel_button) {
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
                mFromEt.clearFocus();
                mToEt.clearFocus();
            }
        }
        return ret;
    }
}
