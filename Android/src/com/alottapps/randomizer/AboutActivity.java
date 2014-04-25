package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {
    
    // Members.
    private TextView mMainTv;
    
    // Constants.
    private final String MAIN_TEXT_INFO = 
        "Randomizer is an application where users provide choices and " +
        "it will randomly select one or randomly order them. Users " +
        "also have the option to give a range of numbers and Randomizer will " +
        "randomly select one from the given range. This application is meant " +
        "for fun use and not recommended for serious decisions.<br><br>" +
        "Randomizer was developed by Alottapps+ThinkingWhileTrue. Feel free to " +
        "contact us with suggestions to make this application better or let us know if you " +
        "encounter any errors.<br><b>Email: </b>thinking.while.true@gmail.com<br><br>" +
        "<b>Randomizer Version 1.5</b>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        mMainTv = (TextView) findViewById(R.id.ab_main_textview);
        mMainTv.setText(Html.fromHtml(MAIN_TEXT_INFO));
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ab_back_button) {
            finish();
        } else if (v.getId() == R.id.ab_donate_button) {
            Intent intent = new Intent(this, DonationDialogActivity.class);
            startActivity(intent);
        }
    }
}
