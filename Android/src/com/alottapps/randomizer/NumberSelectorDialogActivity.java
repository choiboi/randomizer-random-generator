package com.alottapps.randomizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class NumberSelectorDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_selector);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ans_ok_button) {
            finish();
        } else if (v.getId() == R.id.ans_cancel_button) {
            
        }
    }
}
