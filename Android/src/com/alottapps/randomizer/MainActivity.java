package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    
    // Members.
    private EditText mSelectionsEt;
    private LinearLayout mSelectionsListview;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSelectionsEt = (EditText) findViewById(R.id.am_selection_edittext);
        mSelectionsListview = (LinearLayout) findViewById(R.id.am_selections_listview);
        
        mLayoutInflater = getLayoutInflater();
        
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.am_menu_button) {
            
        } else if (v.getId() == R.id.am_add_button) {
            
        } else if (v.getId() == R.id.am_clear_all_button) {
            
        } else if (v.getId() == R.id.am_load_list_button) {
            
        } else if (v.getId() == R.id.am_save_list_button) {
            
        } else if (v.getId() == R.id.am_randomize_number_button) {
            
        } else if (v.getId() == R.id.am_pick_choice_button) {
            
        } else if (v.getId() == R.id.am_randomize_order_button) {
            
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
            }
        }
        return ret;
    }
}
