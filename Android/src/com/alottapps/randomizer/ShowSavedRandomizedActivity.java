package com.alottapps.randomizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;

public class ShowSavedRandomizedActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private ProgressBar mProgressBar;
    private TextView mTitleTv;
    private int mTypeShown;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;

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
        
        mListLayout.removeAllViews();
        
        if (mTypeShown == Constants.PREV_RANDOMIZED) {
            mTitleTv.setText(R.string.prev_selected_choices_text);
            displayPrevSelectedList();
        } else if (mTypeShown == Constants.SAVED_LIST_RANDOMIZED) {
            mTitleTv.setText(R.string.saved_randomized_list_text);
            displaySavedRandomizedLists()
        }
        mProgressBar.setVisibility(View.GONE);
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.assr_back_button || 
                v.getId() == R.id.assr_back_nav_button) {
            finish();
        }
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
        
    }
    
    private void inflatePrevSelectedLayout(Cursor c, LayoutInflater inflater) {
        View v  = inflater.inflate(R.layout.container_selection_randomized, mListLayout, false);
        TextView selectionTv = (TextView) v.findViewById(R.id.csl_selection_textview);
        selectionTv.setText(c.getString(2));
        TextView dataTv = (TextView) v.findViewById(R.id.csl_data_textview);
        String values = c.getString(1);
        if (values.contains(Constants.LIST_DELIMITER)) {
            values = values.replace(Constants.LIST_DELIMITER, ", ");
        }
        dataTv.setText("Selected From: " + values);
        TextView dateTv = (TextView) v.findViewById(R.id.csl_date_textview);
        String date = processDateString(c.getString(3));
        if (date != null) {
            dateTv.setText(date);
        }
        
        mListLayout.addView(v);
    }
    
    private String processDateString(String date) {
        SimpleDateFormat sdf  = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        Date d = null;
        try {
            d = sdf.parse(date);
            SimpleDateFormat sdf_res = new SimpleDateFormat(Constants.DATE_FORMAT_PRESENT, Locale.US);
            return sdf_res.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
