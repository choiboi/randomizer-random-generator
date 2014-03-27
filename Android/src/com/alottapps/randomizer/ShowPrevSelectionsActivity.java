package com.alottapps.randomizer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.DatabaseHandler;

public class ShowPrevSelectionsActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_prev_selections);
        
        mListLayout = (LinearLayout) findViewById(R.id.asps_list_layout);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        displayPrevSelectedList();
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.asps_back_button || 
                v.getId() == R.id.asps_back_nav_button) {
            finish();
        }
    }
    
    private void displayPrevSelectedList() {
        Cursor cursor = mDB.getPrevData();
    }
}
