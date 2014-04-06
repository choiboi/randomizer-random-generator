package com.alottapps.randomizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alottapps.randomizer.util.Constants;

public class AlertDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_alert);
        
        TextView tv = (TextView) findViewById(R.id.aea_alert_msg_textview);
        int type = getIntent().getExtras().getInt(Constants.ALERT_TYPE, -1);
        if (type == Constants.ALERT_EMPTY_RANDOMIZER) {
            tv.setText(R.string.empty_selection_alert_message);
        } else if (type == Constants.ALERT_EMPTY_SAVE) {
            tv.setText(R.string.empty_selection_save_alert_message);
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.aea_ok_button) {
            finish();
        }
    }
}
