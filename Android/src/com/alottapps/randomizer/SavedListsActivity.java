package com.alottapps.randomizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SavedListsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lists);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.asl_back_button || v.getId() == R.id.asl_back_nav_button) {
            finish();
        }
    }
}
