package com.alottapps.randomizer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
        if (isEmailValid() && !mPassEt.getText().toString().equals("")) {
            mInputLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            
            final String email = mEmailEt.getText().toString();
            String pass = Utils.encryptString(mPassEt.getText().toString());
            
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.HTTP_TIMEOUT);
            RequestParams params = new RequestParams();
            params.add(Constants.QUERY_VAR_EMAIL, email);
            params.add(Constants.QUERY_VAR_PASSWORD, pass);
        
            if (v.getId() == R.id.al_login_button) {
                String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_LOGIN;
                client.get(httpLink, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, Header[] header, byte[] data) {
                        String resultCode = Utils.getResultCode(data);
                        if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                            mDB.addUser(email, Utils.encryptString(mPassEt.getText().toString()));
                            retrieveData();
                            goToMainActivity();
                        } else if (resultCode.equals(Constants.RC_INVALID_PASS)) {
                            // TODO: Display invalid password error.
                        } else if (resultCode.equals(Constants.RC_USER_DNE)) {
                            // TODO: Display user does not exists password.
                        } else {
                            // TODO: display error.
                        }
                    }
                });
            } else {
                // Register Button.
                String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_REGISTER_USER;
                client.get(httpLink, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, Header[] header, byte[] data) {
                        String resultCode = Utils.getResultCode(data);
                        if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                            mDB.addUser(email, Utils.encryptString(mPassEt.getText().toString()));
                        } else if (resultCode.equals(Constants.RC_USER_EXISTS)) {
                            // TODO: Display user exists error.
                        } else {
                            // TODO: Display error.
                        }
                    }
                });
            }
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
                retrieveData();
            }
        }
    }
    
    private void retrieveData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.HTTP_TIMEOUT);
        RequestParams params = new RequestParams();
        params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
        
        String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_GET_ALL_DATA;
        client.get(httpLink, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] header, byte[] data) {
                String resultCode = Utils.getResultCode(data);
                if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                    JSONArray array = Utils.getResultData(data);
                    updateLocalDatabase(array);
                    goToMainActivity();
                } else {
                    // TODO: Display error.
                }
            }
        });
    }
    
    private void updateLocalDatabase(JSONArray a) {
        Cursor c = null;
        JSONObject json = null;
        for (int i = 0; i < a.length(); i++) {
            try {
                json = a.getJSONObject(i);
                c = mDB.getDataByID(json.getString(Constants.DATA_ID));
                if (!c.moveToFirst()) {
                    String dataId = json.getString(Constants.DATA_ID);
                    String dataName = json.getString(Constants.JSON_DATA_NAME);
                    String data = json.getString(Constants.JSON_DATA);
                    String date = json.getString(Constants.JSON_DATE);
                    int randomized = json.getInt(Constants.JSON_RANDOMIZED);
                    mDB.addData(dataId, data, date, randomized, dataName, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
