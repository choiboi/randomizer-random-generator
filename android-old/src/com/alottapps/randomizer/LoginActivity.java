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
import android.widget.TextView;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.SystemUtils;
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
    private TextView mErrorTv;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    private AsyncHttpClient mHttpClient;
    
    // Constants.
    private final String USER_EXIST_MSG = "This Email is registered.";
    private final String USER_DNE_MSG = "Email Does Not Exist.";
    private final String INVALID_PASS_MSG = "Invalid password.";
    private final String ERROR_LOGIN_REGISTER_MSG = "Login/Register Error.";
    private final String INVALID_EMAIL = "Invalid Email.";
    private final String EMPTY_FIELDS = "Email and/or Password fields empty.";
    private final String NO_DATA_CONNECTION = "You have no internet connection.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mInputLayout = (LinearLayout) findViewById(R.id.al_input_layout);
        mProgressbar = (ProgressBar) findViewById(R.id.al_progressbar);
        
        mEmailEt = (EditText) findViewById(R.id.al_email_edittext);
        mPassEt = (EditText) findViewById(R.id.al_pass_edittext);
        mErrorTv = (TextView) findViewById(R.id.al_error_textview);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mHttpClient = new AsyncHttpClient();
        mHttpClient.setTimeout(Constants.HTTP_TIMEOUT);
        
        checkPrevLogin();
    }
    
    public void onButtonClick(View v) {
        mErrorTv.setVisibility(View.INVISIBLE);
        
        if (!SystemUtils.hasDataConnection(this)) {
            mErrorTv.setText(NO_DATA_CONNECTION);
            mErrorTv.setVisibility(View.VISIBLE);
        } else if (isEmailValid() && !mPassEt.getText().toString().equals("")) {
            mInputLayout.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
            
            final String email = mEmailEt.getText().toString();
            String pass = Utils.encryptString(mPassEt.getText().toString());
            
            RequestParams params = new RequestParams();
            params.add(Constants.QUERY_VAR_EMAIL, email);
            params.add(Constants.QUERY_VAR_PASSWORD, pass);
        
            if (v.getId() == R.id.al_login_button) {
                String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_LOGIN;
                mHttpClient.get(httpLink, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, Header[] header, byte[] data) {
                        String resultCode = Utils.getResultCode(data);
                        if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                            mDB.addUser(email, Utils.encryptString(mPassEt.getText().toString()));
                            retrieveData();
                        } else {
                            if (resultCode.equals(Constants.RC_INVALID_PASS)) {
                                mErrorTv.setText(INVALID_PASS_MSG);
                                mErrorTv.setVisibility(View.VISIBLE);
                            } else if (resultCode.equals(Constants.RC_USER_DNE)) {
                                mErrorTv.setText(USER_DNE_MSG);
                                mErrorTv.setVisibility(View.VISIBLE);
                            } else {
                                mErrorTv.setText(ERROR_LOGIN_REGISTER_MSG);
                                mErrorTv.setVisibility(View.VISIBLE);
                            }
                            
                            mInputLayout.setVisibility(View.VISIBLE);
                            mProgressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            } else if (v.getId() == R.id.al_register_button) {
                // Register Button.
                String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_REGISTER_USER;
                mHttpClient.get(httpLink, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, Header[] header, byte[] data) {
                        String resultCode = Utils.getResultCode(data);
                        if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                            mDB.addUser(email, Utils.encryptString(mPassEt.getText().toString()));
                            goToMainActivity();
                        } else {
                            if (resultCode.equals(Constants.RC_USER_EXISTS)) {
                                mErrorTv.setText(USER_EXIST_MSG);
                                mErrorTv.setVisibility(View.VISIBLE);
                            } else {
                                mErrorTv.setText(ERROR_LOGIN_REGISTER_MSG);
                                mErrorTv.setVisibility(View.VISIBLE);
                            }
                            
                            mInputLayout.setVisibility(View.VISIBLE);
                            mProgressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        } else {
            if (!isEmailValid()) {
                mErrorTv.setText(INVALID_EMAIL);
            } else {
                mErrorTv.setText(EMPTY_FIELDS);
            }
            mErrorTv.setVisibility(View.VISIBLE);
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
            
            if (c.getString(0).equals(Constants.TEMP_EMAIL)) {
                goToMainActivity();
            } else if (SystemUtils.hasDataConnection(this)) {
                checkNotSavedData();
            } else {
                goToMainActivity();
            }
        }
    }
    
    private void retrieveData() {
        RequestParams params = new RequestParams();
        params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
        
        String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_GET_ALL_DATA;
        mHttpClient.get(httpLink, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] header, byte[] data) {
                String resultCode = Utils.getResultCode(data);
                if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                    JSONArray array = Utils.getResultData(data);
                    updateLocalDatabase(array);
                    goToMainActivity();
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
    
    private void checkNotSavedData() {
        Cursor cursor = mDB.getNotSavedToDBData();
        
        if (cursor.moveToFirst()) {
            saveDataToDB(cursor);
        } else {
            retrieveData();
        }
    }
    
    private void saveDataToDB(final Cursor c) {
        String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_SAVE_DATA;
        RequestParams params = new RequestParams();
        params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
        params.add(Constants.QUERY_VAR_DATA_ID, c.getString(0));
        params.add(Constants.QUERY_VAR_DATA_NAME, c.getString(1));
        params.add(Constants.QUERY_VAR_DATA, c.getString(2));
        params.add(Constants.QUERY_VAR_DATE, c.getString(3));
        params.add(Constants.QUERY_VAR_RANDOMIZED, Integer.toString(c.getInt(4)));
        mHttpClient.get(httpLink, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] header, byte[] data) {
                String resultCode = Utils.getResultCode(data);
                if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                    mDB.updateSavedToServer(c.getString(0), 1);
                    retrieveData();
                }
            }
        });
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
