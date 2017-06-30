package com.alottapps.randomizer.dialog;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;

public class NoticeDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice_alert);

    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.ana_ok_button) {
            CheckBox cb = (CheckBox) findViewById(R.id.ana_not_show_checkbox);
            if (cb.isChecked()) {
                RandomizerSharedPreferences pref = new RandomizerSharedPreferences(this);
//                pref.setNoticedViewed(RandomizerSharedPreferences.KEY_DISPLAYED_2_0_NOTICE, true);
                pref.setNoticedViewed(RandomizerSharedPreferences.KEY_DISPLAY_TERMINATION_NOTICE, true);
            }
            finish();
        }
    }
}
