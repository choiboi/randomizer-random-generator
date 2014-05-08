package com.alottapps.randomizer;

import java.util.ArrayList;

import org.apache.http.Header;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.SystemUtils;
import com.alottapps.randomizer.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class EditListActivity extends Activity {
    
    // Members.
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    private String mID;
    private Cursor mDataCursor;
    private RelativeLayout mHttpStatusLayout;
    private LinearLayout mSelectionsLayout;
    private ArrayList<EditText> mSelectionsEt;
    private EditText mTitleEt;
    private ArrayList<String> mSelections;
    
    // Constants.
    private String DEFAULT_LIST_NAME = "List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        
        setResult(RESULT_CANCELED);
        
        mID = getIntent().getExtras().getString(Constants.DATA_ID);
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        mDataCursor = mDB.getDataByID(mID);
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.ael_http_loading_screen);
        
        if (mDataCursor.moveToFirst()) {
            mSelectionsLayout = (LinearLayout) findViewById(R.id.ael_selection_layout);
            mSelectionsEt = new ArrayList<EditText>();
            mTitleEt = (EditText) findViewById(R.id.ael_title_edittext);
            mTitleEt.setText(mDataCursor.getString(1), TextView.BufferType.EDITABLE);
            
            setupPage();
        }
    }
    
    private void setupPage() {
       mSelections = Utils.stringToList(mDataCursor.getString(2));
       
       for (int i = 0; i < mSelections.size(); i++) {
           View v = getLayoutInflater().inflate(R.layout.container_edittext, mSelectionsLayout, false);
           EditText et = (EditText) v.findViewById(R.id.ce_edittext);
           et.setText(mSelections.get(i), TextView.BufferType.EDITABLE);
           mSelectionsEt.add(et);
           mSelectionsLayout.addView(v);
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ael_back_nav_button || v.getId() == R.id.ael_back_button) {
            finish();
        } else if (v.getId() == R.id.ael_save_nav_button) {
            String newList = getListAsString();
            if (newList.equals("")) {
                Intent intent = new Intent(this, AlertDialogActivity.class);
                intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_EMPTY_SAVE);
                startActivity(intent);
            } else {
                mHttpStatusLayout.setVisibility(View.VISIBLE);
                String name = mTitleEt.getText().toString();
                if (name.equals("")) {
                    name = DEFAULT_LIST_NAME;
                }
                
                if (!Utils.skippedLogin(mDB) && SystemUtils.hasDataConnection(this)) {
                    mDB.updateListData(mID, newList, name);
                    updateSavedList(name, newList);
                } else {
                    mDB.updateListData(mID, newList, name);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } else if (v.getId() == R.id.ael_add_new_selection_button) {
            View view = getLayoutInflater().inflate(R.layout.container_edittext, mSelectionsLayout, false);
            EditText et = (EditText) view.findViewById(R.id.ce_edittext);
            mSelectionsEt.add(et);
            mSelectionsLayout.addView(view);
        }
    }
    
    private String getListAsString() {
        ArrayList<String> newSelections = new ArrayList<String>();
        for (int i = 0; i < mSelectionsEt.size(); i++) {
            if (!mSelectionsEt.get(i).getText().toString().equals("")) {
                newSelections.add(mSelectionsEt.get(i).getText().toString());
            }
        }
        
        if (newSelections.size() == 0) {
            return "";
        }
        return Utils.listToString(newSelections);
    }
    
    private void saveToDB(String name, String listStr) {
        String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_SAVE_DATA;
        RequestParams params = new RequestParams();
        AsyncHttpClient httpClient = new AsyncHttpClient();
        params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
        params.add(Constants.QUERY_VAR_DATA_ID, mID);
        params.add(Constants.QUERY_VAR_DATA_NAME, name);
        params.add(Constants.QUERY_VAR_DATA, listStr);
        params.add(Constants.QUERY_VAR_DATE, Utils.getCurrentDateTime());
        params.add(Constants.QUERY_VAR_RANDOMIZED, "0");
        httpClient.get(httpLink, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] header, byte[] data) {
                String resultCode = Utils.getResultCode(data);
                if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                    mDB.updateSavedToServer(mID, 1);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
    
    private void updateSavedList(final String name, final String listStr) {
        mHttpStatusLayout.setVisibility(View.VISIBLE);
        String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_DELETE_DATA;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.HTTP_TIMEOUT);
        RequestParams params = new RequestParams();
        params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
        params.add(Constants.QUERY_VAR_DATA_ID, mID);
        client.get(httpLink, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int status, Header[] header, byte[] data) {
                String resultCode = Utils.getResultCode(data);
                if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                    saveToDB(name, listStr);
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
            }
        }
        return ret;
    }
}
