package com.alottapps.randomizer.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.adapters.ResultListAdapter;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.util.Utils;

import java.util.ArrayList;

public class AlertDialogActivity extends Activity {
    
    // Members.
    private Button mCancelButton;
    private int mType;
    private String mObjID;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_alert);
        
        mCancelButton = (Button) findViewById(R.id.aea_cancel_button);
        
        TextView tv = (TextView) findViewById(R.id.aea_alert_msg_textview);
        mType = getIntent().getExtras().getInt(Constants.ALERT_TYPE, -1);
        if (mType == Constants.ALERT_EMPTY_RANDOMIZER) {
            tv.setText(R.string.empty_selection_alert_message);
        } else if (mType == Constants.ALERT_EMPTY_SAVE) {
            tv.setText(R.string.empty_selection_save_alert_message);
        } else if (mType == Constants.ALERT_WHY) {
            tv.setText(R.string.alert_why_description_message);
        } else if (mType == Constants.ALERT_CONFIMATION) {
            mCancelButton.setVisibility(View.VISIBLE);
            tv.setText(R.string.alert_confirm_delete_list_message);
            setResult(RESULT_CANCELED);
            mObjID = getIntent().getExtras().getString(Constants.SELECTION_OBJ_ID);
        } else if (mType == Constants.ALERT_SAVED_FILE) {
            String filepath = getIntent().getExtras().getString(Constants.FILEPATH);
            String msg = "File saved in: " + filepath;
            tv.setText(msg);
        } else if (mType == Constants.ALERT_SAVE_FILE_FAIL) {
            tv.setText(R.string.alert_save_failed_message);
        } else if (mType == Constants.ALERT_SELECT_TEXT_FILE) {
            tv.setText(R.string.alert_select_text_file_message);
            setResult(RESULT_CANCELED);
        } else if (mType == Constants.ALERT_LARGE_LIST) {
            tv.setText(R.string.alert_large_list_message);
            mCancelButton.setVisibility(View.VISIBLE);
            mObjID = getIntent().getExtras().getString(Constants.SELECTION_OBJ_ID);
            setResult(RESULT_CANCELED);
        } else if (mType == Constants.ALERT_NOT_LOGIN_WARNING) {
            tv.setText(R.string.alert_not_logging_in_message);
            setResult(RESULT_OK);
        } else if (mType == Constants.ALERT_ALL_DELETE_CONFIMATION) {
            mCancelButton.setVisibility(View.VISIBLE);
            tv.setText(R.string.alert_confirm_delete_all_message);
            setResult(RESULT_CANCELED);
        } else if (mType == Constants.ALERT_INVALID_FILENAME) {
            tv.setText(R.string.alert_invalid_filename);
            setResult(RESULT_CANCELED);
        } else if (mType == Constants.ALERT_DISPLAY_LIST) {
            tv.setVisibility(View.GONE);
            ListView lv = (ListView) findViewById(R.id.aea_listview);
            ArrayList<String> selections = Utils.stringToList(getIntent().getStringExtra(Constants.ALERT_INTENT_LIST));
            ResultListAdapter resAdapter = new ResultListAdapter(selections, this);
            lv.setAdapter(resAdapter);
            lv.setVisibility(View.VISIBLE);

            setResult(RESULT_CANCELED);
        } else {
            finish();
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.aea_ok_button) {
            if (mType == Constants.ALERT_CONFIMATION) {
                Intent intent = new Intent();
                intent.putExtra(Constants.SELECTION_OBJ_ID, mObjID);
                setResult(RESULT_OK, intent);
            } else if (mType == Constants.ALERT_SELECT_TEXT_FILE) {
                setResult(RESULT_OK);
            } else if (mType == Constants.ALERT_LARGE_LIST) {
            	Intent intent = new Intent();
                intent.putExtra(Constants.SELECTION_OBJ_ID, mObjID);
                setResult(RESULT_OK, intent);
            } else if (mType == Constants.ALERT_ALL_DELETE_CONFIMATION) {
                setResult(RESULT_OK);
            }
        }
        finish();
    }
}
