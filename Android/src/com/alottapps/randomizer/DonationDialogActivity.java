package com.alottapps.randomizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DonationDialogActivity extends Activity {
    
    // Members.
    private RadioGroup mMainRg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_dialog);
        
        mMainRg = (RadioGroup) findViewById(R.id.add_radio_group);
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.add_cancel_button) {
            finish();
        } else if (v.getId() == R.id.add_donate_button) {
            int selected = mMainRg.getCheckedRadioButtonId();
            if (selected == R.id.add_one_dollar_radio) {
                Toast.makeText(this, "Selected $1.00.", Toast.LENGTH_SHORT).show();
            } else if (selected == R.id.add_two_dollar_radio) {
                Toast.makeText(this, "Selected $2.00.", Toast.LENGTH_SHORT).show();
            } else if (selected == R.id.add_three_dollar_radio) {
                Toast.makeText(this, "Selected $3.00.", Toast.LENGTH_SHORT).show();
            }
            
            finish();
        }
    }
}
