package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.Utils;

public class SavedListsActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    
    // Constants.
    private final String NONE = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lists);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mListLayout = (LinearLayout) findViewById(R.id.asl_list_listview);
        
        displayLists();
        
        setResult(RESULT_CANCELED);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.asl_back_button || v.getId() == R.id.asl_back_nav_button) {
            finish();
        }
    }
    
    private void displayLists() {
        mListLayout.setVisibility(View.INVISIBLE);
        mListLayout.removeAllViews();
        Cursor c = mDB.retrieveListData();
        LayoutInflater inflater = getLayoutInflater();
        
        if (c.moveToFirst()) {
            setupListContainer(c, inflater);
            
            while (c.moveToNext()) {
                setupListContainer(c, inflater);
            }
        } else {
            // Display NONE.
            View v = inflater.inflate(R.layout.container_main_selections, mListLayout, false);
            v.findViewById(R.id.cs_delete_button).setVisibility(View.INVISIBLE);
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(NONE);
            mListLayout.addView(v);
        }
        mListLayout.setVisibility(View.VISIBLE);
    }
    
    private void setupListContainer(Cursor c, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.container_list, mListLayout, false);
        TextView nameTv = (TextView) v.findViewById(R.id.cl_list_name_textview);
        nameTv.setText(c.getString(1));
        TextView listTv = (TextView) v.findViewById(R.id.cl_list_textview);
        String dataL = Utils.strListToHtmlList(c.getString(2));
        listTv.setText(Html.fromHtml(dataL));
        TextView dateTv = (TextView) v.findViewById(R.id.cl_date_textview);
        String date = Utils.processDateString(c.getString(3));
        if (date != null) {
            dateTv.setText("Last Modified on " + date);
        }
        
        final String dataID = c.getString(0);
        ImageButton deleteBut = (ImageButton) v.findViewById(R.id.cl_delete_button);
        deleteBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDB.deleteSingleData(dataID);
                displayLists();
            }
        });
        final LinearLayout mainV = (LinearLayout) v.findViewById(R.id.cl_main_view);
        mainV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_ID, dataID);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        mListLayout.addView(v);
    }
}
