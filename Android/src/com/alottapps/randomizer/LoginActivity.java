package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.Utils;

public class LoginActivity extends Activity {
    
    // Members.
    private LinearLayout mInputLayout;
    private ProgressBar mProgressbar;
    private EditText mEmailEt;
    private EditText mPassEt;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mInputLayout = (LinearLayout) findViewById(R.id.al_input_layout);
        mProgressbar = (ProgressBar) findViewById(R.id.al_progressbar);
        
        mEmailEt = (EditText) findViewById(R.id.al_email_edittext);
        mPassEt = (EditText) findViewById(R.id.al_pass_edittext);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        checkPrevLogin();
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.al_login_button) {
            mInputLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            
            if (isEmailValid() && !mPassEt.getText().toString().equals("")) {
                String email = mEmailEt.getText().toString();
                mDB.addUser(email, Utils.encryptString(mPassEt.getText().toString()));
            }
            
            // TODO: Make Async Http request to login.
            
            goToMainActivity();
        }
    }
    
    public void onWhyClick(View v) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_WHY);
        startActivity(intent);
    }
    
    public void onSkipLoginClick(View v) {
        mDB.addUser(Constants.TEMP_EMAIL, Constants.TEMP_PASS);
        goToMainActivity();
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void checkPrevLogin() {
        Cursor c = mDB.getUser();
        if (c.moveToFirst()) {
            mInputLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            
            if (c.getString(0).equals(Constants.TEMP_EMAIL)) {
                goToMainActivity();
            } else {
                // TODO: Download lists and sync with local.
            }
        }
    }
    
    private boolean isEmailValid() {
        String email = mEmailEt.getText().toString();
        return email.contains("@");
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Remove virtual keyboard when EditText is out of focus.
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP &&
                    (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                mEmailEt.clearFocus();
                mPassEt.clearFocus();
            }
        }
        return ret;
    }
}
