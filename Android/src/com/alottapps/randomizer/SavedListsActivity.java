package com.alottapps.randomizer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.DatabaseHandler;

public class SavedListsActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lists);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        findViewById(R.id.asl_delete_nav_button).setVisibility(View.GONE);
        mListLayout = (LinearLayout) findViewById(R.id.asl_list_listview);
        
        displayLists();
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.asl_back_button || v.getId() == R.id.asl_back_nav_button) {
            finish();
        }
    }
    
    private void displayLists() {
        mListLayout.removeAllViews();
        Cursor c = mDB.retrieveSavedData();
    }
}
