package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {
    
    // Members.
    private LinearLayout mInputLayout;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mInputLayout = (LinearLayout) findViewById(R.id.al_input_layout);
        mProgressbar = (ProgressBar) findViewById(R.id.al_progressbar);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.al_login_button) {
            mInputLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            
            // TODO: Make Async Http request to login.
            goToMainActivity();
        } else if (v.getId() == R.id.al_login_without_button) {
            goToMainActivity();
        }
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
