package com.alottapps.randomizer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    // Members.
    private EditText mSelectionsEt;
    private LinearLayout mSelectionsListview;
    private LinearLayout mMainMenu;
    private View mMenuBg;
    private boolean mMenuOpen;
    private LayoutInflater mLayoutInflater;
    private List<String> mSelections;
    
    // Constants.
    private final String NONE = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSelectionsEt = (EditText) findViewById(R.id.am_selection_edittext);
        mSelectionsListview = (LinearLayout) findViewById(R.id.am_selections_listview);
        mMainMenu = (LinearLayout) findViewById(R.id.am_menu_layout);
        mMenuBg = findViewById(R.id.am_menu_background_view);
        
        mMenuOpen = false;
        mSelections = new ArrayList<String>();
        mLayoutInflater = getLayoutInflater();
        
        drawSelectionsListview();
        
        mMenuBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCloseMenu();
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        if (mMenuOpen) {
            openCloseMenu();
        } else {
            super.onBackPressed();
        }
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.am_menu_button) {
            openCloseMenu();
        } else if (v.getId() == R.id.am_add_button) {
            addSelection();
        } else if (v.getId() == R.id.am_clear_all_button) {
            clearSelectionsList();
        } else if (v.getId() == R.id.am_load_list_button) {
            
        } else if (v.getId() == R.id.am_save_list_button) {
            
        } else if (v.getId() == R.id.am_randomize_number_button) {
            
        } else if (v.getId() == R.id.am_pick_choice_button) {
            
        } else if (v.getId() == R.id.am_randomize_order_button) {
            
        }
    }
    
    public void onMenuButtonClick(View v) {
        if (v.getId() == R.id.am_previous_choice_menu_button) {
            
        } else if (v.getId() == R.id.am_saved_ordered_list_menu_button) {
            
        } else if (v.getId() == R.id.am_about_menu_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        openCloseMenu();
    }
    
    private void drawSelectionsListview() {
        mSelectionsListview.removeAllViews();
        
        if (mSelections.size() == 0) {
            View v = mLayoutInflater.inflate(R.layout.container_selections, mSelectionsListview, false);
            v.findViewById(R.id.cs_delete_button).setVisibility(View.INVISIBLE);
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(NONE);
            mSelectionsListview.addView(v);
        }
        
        for (int i = 0; i < mSelections.size(); i++) {
            View v = mLayoutInflater.inflate(R.layout.container_selections, mSelectionsListview, false);
            ImageButton imb = (ImageButton) v.findViewById(R.id.cs_delete_button);
            final int pos = i;
            imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelections.remove(pos);
                    drawSelectionsListview();
                }
            });
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(mSelections.get(i));
            mSelectionsListview.addView(v);
        }
    }
    
    private void addSelection() {
        String s = mSelectionsEt.getText().toString();
        if (!s.equals("")) {
            mSelections.add(s);
            drawSelectionsListview();
        }
        mSelectionsEt.setText("");
    }
    
    private void clearSelectionsList() {
        mSelections = new ArrayList<String>();
        mSelectionsListview.removeAllViews();
        
        drawSelectionsListview();
    }
    
    private void openCloseMenu() {
        if (!mMenuOpen) {
            mMainMenu.setVisibility(View.VISIBLE);
            mMenuBg.setVisibility(View.VISIBLE);
            mMenuOpen = true;
        } else {
            mMainMenu.setVisibility(View.INVISIBLE);
            mMenuBg.setVisibility(View.INVISIBLE);
            mMenuOpen = false;
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
                mSelectionsEt.clearFocus();
            }
        }
        return ret;
    }
}
