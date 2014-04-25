package com.alottapps.randomizer;

import java.io.File;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.SystemUtils;
import com.alottapps.randomizer.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ShowSavedRandomizedActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private ProgressBar mProgressBar;
    private TextView mTitleTv;
    private int mTypeShown;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    
    // Constants.
    private final int GET_NAME_ALERT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_randomized);
        
        mListLayout = (LinearLayout) findViewById(R.id.assr_list_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.assr_progressbar);
        mTitleTv = (TextView) findViewById(R.id.assr_page_title_textview);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mTypeShown = getIntent().getExtras().getInt(Constants.TYPE_SELECTED_RANDOMIZED, -1);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        onStartTask();
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.assr_back_button || 
                v.getId() == R.id.assr_back_nav_button) {
            finish();
        }
    }
    
    private void onStartTask() {
        mListLayout.removeAllViews();

        if (mTypeShown == Constants.PREV_RANDOMIZED) {
            mTitleTv.setText(R.string.prev_selected_choices_text);
            displayPrevSelectedList();
        } else if (mTypeShown == Constants.SAVED_LIST_RANDOMIZED) {
            mTitleTv.setText(R.string.saved_randomized_list_text);
            displaySavedRandomizedLists();
        }
        mProgressBar.setVisibility(View.GONE);
    }
    
    private void displayPrevSelectedList() {
        Cursor cursor = mDB.getAllPrevData();
        LayoutInflater inflater = getLayoutInflater();
        
        if (cursor.moveToLast()) {
            inflatePrevSelectedLayout(cursor, inflater);
            while (cursor.moveToPrevious()) {
                inflatePrevSelectedLayout(cursor, inflater);
            }
            mListLayout.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.assr_none_textview).setVisibility(View.VISIBLE);
        }
    }
    
    private void displaySavedRandomizedLists() {
        Cursor cursor = mDB.retrieveSavedData();
        LayoutInflater inflater = getLayoutInflater();
        
        if (cursor.moveToFirst()) {
            inflateSavedListLayout(cursor, inflater);
            while (cursor.moveToNext()) {
                inflateSavedListLayout(cursor, inflater);
            }
            mListLayout.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.assr_none_textview).setVisibility(View.VISIBLE);
        }
    }
    
    private void inflatePrevSelectedLayout(Cursor c, LayoutInflater inflater) {
        View v  = inflater.inflate(R.layout.container_selection_randomized, mListLayout, false);
        TextView selectionTv = (TextView) v.findViewById(R.id.csl_selection_textview);
        selectionTv.setText(c.getString(1));
        TextView dataTv = (TextView) v.findViewById(R.id.csl_data_textview);
        String values = c.getString(0);
        if (values.contains(Constants.LIST_DELIMITER)) {
            values = values.replace(Constants.LIST_DELIMITER, ", ");
        }
        dataTv.setText("Selected From: " + values);
        TextView dateTv = (TextView) v.findViewById(R.id.csl_date_textview);
        String date = Utils.processDateString(c.getString(2));
        if (date != null) {
            dateTv.setText(date);
        }
        
        mListLayout.addView(v);
    }
    
    private void inflateSavedListLayout(Cursor c, LayoutInflater inflater) {
        View v  = inflater.inflate(R.layout.container_list_randomized, mListLayout, false);
        TextView selectionTv = (TextView) v.findViewById(R.id.clr_list_textview);
        String htmlStr = Utils.strListToHtmlList(c.getString(1));
        selectionTv.setText(Html.fromHtml(htmlStr));
        TextView dateTv = (TextView) v.findViewById(R.id.clr_date_textview);
        String date = Utils.processDateString(c.getString(2));
        if (date != null) {
            dateTv.setText("Randomized on " + date);
        }
        
        final String dataID = c.getString(0);
        ImageButton deleteBut = (ImageButton) v.findViewById(R.id.clr_delete_button);
        deleteBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDB.deleteSingleData(dataID);
                deleteFromDB(dataID);
            }
        });
        ImageButton saveBut = (ImageButton) v.findViewById(R.id.clr_save_button);
        saveBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ShowSavedRandomizedActivity.this, GetFileNameDialogActivity.class);
                intent.putExtra(Constants.DATA_ID, dataID);
                startActivityForResult(intent, GET_NAME_ALERT);
            }
        });
        
        mListLayout.addView(v);
    }
    
    private void deleteFromDB(String id) {
        if (!Utils.skippedLogin(mDB)) {
            String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_DELETE_DATA;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.HTTP_TIMEOUT);
            RequestParams params = new RequestParams();
            params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
            params.add(Constants.QUERY_VAR_DATA_ID, id);
            client.get(httpLink, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int status, Header[] header, byte[] data) {
                    String resultCode = Utils.getResultCode(data);
                    if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                        Toast.makeText(ShowSavedRandomizedActivity.this, "Randomized list has successfully been deleted!!" , Toast.LENGTH_LONG).show();
                        onStartTask();
                    }
                }
            });
        } else {
            Toast.makeText(ShowSavedRandomizedActivity.this, "Randomized list has successfully been deleted!!" , Toast.LENGTH_LONG).show();
            onStartTask();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_NAME_ALERT) {
                String dataList = data.getExtras().getString(Constants.DATA);
                String filenameOrig = data.getExtras().getString(Constants.FILENAME);
                File file = SystemUtils.getOutputMediaFile(filenameOrig + Constants.TEXTFILE_EXTENSION);
                if (file.exists()) {
                    int i = 1;
                    while (file.exists()) {
                        file = SystemUtils.getOutputMediaFile(filenameOrig + "-" + i + Constants.TEXTFILE_EXTENSION);
                        i++;
                    }
                }
                
                dataList = Utils.dbStrListToTextStr(dataList);
                if (SystemUtils.saveTextFile(file, dataList)) {
                    Intent intent = new Intent(this, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_SAVED_FILE);
                    intent.putExtra(Constants.FILEPATH, file.getAbsolutePath());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_SAVE_FILE_FAIL);
                    startActivity(intent);
                }
            }
        }
    }
}
