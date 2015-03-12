package com.alottapps.randomizer.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.util.Constants;

public class AlertDialogActivity extends Activity {
    
    // Members.
    private Button mCancelButton;
    private int mType;
    private String mID;
    
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
            mID = getIntent().getExtras().getString(Constants.DATA_ID);
        } else if (mType == Constants.ALERT_TY_DONATION) {
            tv.setText(R.string.alert_ty_message);
        } else if (mType == Constants.ALERT_SAVED_FILE) {
            String filepath = getIntent().getExtras().getString(Constants.FILEPATH);
            tv.setText("File saved in: " + filepath);
        } else if (mType == Constants.ALERT_SAVE_FILE_FAIL) {
            tv.setText(R.string.alert_save_failed_message);
        } else if (mType == Constants.ALERT_NOT_TEXT_FILE) {
            tv.setText(R.string.alert_not_text_file_message);
        } else if (mType == Constants.ALERT_SELECT_TEXT_FILE) {
            tv.setText(R.string.alert_select_text_file_message);
            setResult(RESULT_CANCELED);
        } else if (mType == Constants.ALERT_FILE_DNE) {
            tv.setText(R.string.alert_file_dne_message);
        } else {
            finish();
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.aea_ok_button) {
            if (mType == Constants.ALERT_CONFIMATION) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_ID, mID);
                setResult(RESULT_OK, intent);
            } else if (mType == Constants.ALERT_SELECT_TEXT_FILE) {
                setResult(RESULT_OK);
            }
        }
        finish();
    }
}
