package com.alottapps.randomizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class EmptyAlertDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_alert);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.aea_ok_button) {
            finish();
        }
    }
}
