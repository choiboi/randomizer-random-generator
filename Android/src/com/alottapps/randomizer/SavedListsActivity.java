package com.alottapps.randomizer;

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
import android.widget.RelativeLayout;
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

public class SavedListsActivity extends Activity {
    
    // Members.
    private LinearLayout mListLayout;
    private RelativeLayout mHttpStatusLayout;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;
    
    // Constants.
    private final String NONE = "None";
    private final int DELETE_ALERT = 100;
    private final int EDIT_LIST = 101;
    private final int GET_NAME_ALERT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lists);
        
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mListLayout = (LinearLayout) findViewById(R.id.asl_list_listview);
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.asl_http_loading_screen);
        
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
            public void onClick(View v) {
                deleteListConfirmation(dataID);
            }
        });
        ImageButton editBut = (ImageButton) v.findViewById(R.id.cl_edit_button);
        editBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SavedListsActivity.this, EditListActivity.class);
                intent.putExtra(Constants.DATA_ID, dataID);
                startActivityForResult(intent, EDIT_LIST);
            }
        });
        ImageButton editBut = (ImageButton) v.findViewById(R.id.cl_edit_button);
        editBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SavedListsActivity.this, EditListActivity.class);
                intent.putExtra(Constants.DATA_ID, dataID);
                startActivityForResult(intent, EDIT_LIST);
            }
        });
        
        final LinearLayout mainV = (LinearLayout) v.findViewById(R.id.cl_main_view);
        mainV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_ID, dataID);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        mListLayout.addView(v);
    }
    
    private void deleteListConfirmation(String id) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_CONFIMATION);
        intent.putExtra(Constants.DATA_ID, id);
        startActivityForResult(intent, DELETE_ALERT);
    }
    
    private void deleteSavedList(final String id) {
        final Cursor c = mDB.getDataByID(id);
        
        if (c.moveToFirst() && SystemUtils.hasDataConnection(this)) {
            if (!Utils.skippedLogin(mDB)) {
                mHttpStatusLayout.setVisibility(View.VISIBLE);
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
                            mDB.deleteSingleData(id);
                            Toast.makeText(SavedListsActivity.this, "List " + c.getString(1) + " has successfully been deleted!", Toast.LENGTH_LONG).show();
                            mHttpStatusLayout.setVisibility(View.GONE);
                            displayLists();
                        }
                    }
                });
            } else {
                mDB.deleteSingleData(id);
                Toast.makeText(this, "List " + c.getString(1) + " has successfully been deleted!", Toast.LENGTH_LONG).show();
                displayLists();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE_ALERT) {
                String id = data.getExtras().getString(Constants.DATA_ID);
                deleteSavedList(id);
            } else if (requestCode == EDIT_LIST) {
                displayLists();
            }
        }
    }
}
