package com.alottapps.randomizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.Utils;

public class MainActivity extends Activity {
    
    // Members.
    private EditText mSelectionsEt;
    private LinearLayout mSelectionsListview;
    private LinearLayout mMainMenu;
    private RelativeLayout mHttpStatusLayout;
    private View mMenuBg;
    private boolean mMenuOpen;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mSelections;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    
    // Constants.
    private final String NONE = "None";
    private final int LOAD_SAVED_LIST = 1;
    private final int GET_LIST_NAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSelectionsEt = (EditText) findViewById(R.id.am_selection_edittext);
        mSelectionsListview = (LinearLayout) findViewById(R.id.am_selections_listview);
        mMainMenu = (LinearLayout) findViewById(R.id.am_menu_layout);
        mMenuBg = findViewById(R.id.am_menu_background_view);
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.am_http_loading_screen);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
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
            Intent intent = new Intent(this, SavedListsActivity.class);
            startActivityForResult(intent, LOAD_SAVED_LIST);
        } else if (v.getId() == R.id.am_save_list_button) {
            if (!isSelectionsListEmpty()) {
                Intent intent = new Intent(this, GetListNameDialogActivity.class);
                intent.putExtra(Constants.SELECTIONS_LIST, Utils.listToString(mSelections));
                startActivityForResult(intent, GET_LIST_NAME );
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_SAVE);
            }
        } else if (v.getId() == R.id.am_randomize_number_button) {
            Intent intent = new Intent(this, NumberSelectorDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.am_pick_choice_button) {
            if (!isSelectionsListEmpty()) {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(Constants.TYPE_RANDOM, Constants.SINGLE_RANDOM);
                intent.putStringArrayListExtra(Constants.SELECTIONS_LIST, mSelections);
                startActivity(intent);
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_RANDOMIZER);
            }
        } else if (v.getId() == R.id.am_randomize_order_button) {
            if (!isSelectionsListEmpty()) {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(Constants.TYPE_RANDOM, Constants.LIST_RANDOM);
                intent.putStringArrayListExtra(Constants.SELECTIONS_LIST, mSelections);
                startActivity(intent);
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_RANDOMIZER);
            }
        }
    }
    
    public void onMenuButtonClick(View v) {
        if (v.getId() == R.id.am_previous_choice_menu_button) {
            Intent intent = new Intent(this, ShowSavedRandomizedActivity.class);
            intent.putExtra(Constants.TYPE_SELECTED_RANDOMIZED, Constants.PREV_RANDOMIZED);
            startActivity(intent);
        } else if (v.getId() == R.id.am_saved_ordered_list_menu_button) {
            Intent intent = new Intent(this, ShowSavedRandomizedActivity.class);
            intent.putExtra(Constants.TYPE_SELECTED_RANDOMIZED, Constants.SAVED_LIST_RANDOMIZED);
            startActivity(intent);
        } else if (v.getId() == R.id.am_about_menu_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.am_logout_menu_button) {
            mDB.deleteUser();
            mDB.deleteAllPrevData();
            mDB.deleteAllData();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        openCloseMenu();
    }
    
    private void drawSelectionsListview() {
        mSelectionsListview.removeAllViews();
        
        if (mSelections.size() == 0) {
            View v = mLayoutInflater.inflate(R.layout.container_main_selections, mSelectionsListview, false);
            v.findViewById(R.id.cs_delete_button).setVisibility(View.INVISIBLE);
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(NONE);
            mSelectionsListview.addView(v);
        }
        
        for (int i = 0; i < mSelections.size(); i++) {
            View v = mLayoutInflater.inflate(R.layout.container_main_selections, mSelectionsListview, false);
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
    
    private boolean isSelectionsListEmpty() {
        return mSelections.isEmpty();
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
    
    private void listEmptyAlertDialog(int type) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, type);
        startActivity(intent);
    }
    
    private void savedListToServer(String id) {
        mHttpStatusLayout.setVisibility(View.VISIBLE);
        Cursor c = mDB.getDataByID(id);
        
        // TODO: HTTP Async request to add list.
        
        if (c.moveToFirst()) {
            Toast.makeText(this, "List " + c.getString(1) + " has successfully been saved!!" , Toast.LENGTH_LONG).show();
        }
        mHttpStatusLayout.setVisibility(View.GONE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == LOAD_SAVED_LIST) {
                String id = data.getExtras().getString(Constants.DATA_ID);
                Cursor c = mDB.getDataByID(id);
                if (c.moveToFirst()) {
                    mSelections = Utils.stringToList(c.getString(2));
                }
                drawSelectionsListview();
            } else if (requestCode == GET_LIST_NAME) {
                String id = data.getExtras().getString(Constants.DATA_ID);
                savedListToServer(id);
            }
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
