package com.alottapps.randomizer;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.Header;

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
    private final int GET_FILE = 103;
    private final int GET_FILE_ALERT = 104;
    private final int GET_LIST_NAME = 200;

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
        } else if (v.getId() == R.id.asl_download_button) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_SELECT_TEXT_FILE);
            startActivityForResult(intent, GET_FILE_ALERT);
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
        ImageButton saveBut = (ImageButton) v.findViewById(R.id.cl_save_button);
        saveBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SavedListsActivity.this, GetFileNameDialogActivity.class);
                intent.putExtra(Constants.DATA_ID, dataID);
                startActivityForResult(intent, GET_NAME_ALERT);
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
    
    private void saveListToServer(final String id) {
        final Cursor c = mDB.getDataByID(id);
        
        if (c.moveToFirst() && !Utils.skippedLogin(mDB) && SystemUtils.hasDataConnection(this)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.HTTP_TIMEOUT);
            
            String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_SAVE_DATA;
            RequestParams params = new RequestParams();
            params.add(Constants.QUERY_VAR_EMAIL, mDB.getUserEmail());
            params.add(Constants.QUERY_VAR_DATA_ID, id);
            params.add(Constants.QUERY_VAR_DATA_NAME, c.getString(1));
            params.add(Constants.QUERY_VAR_DATA, c.getString(2));
            params.add(Constants.QUERY_VAR_DATE, c.getString(3));
            params.add(Constants.QUERY_VAR_RANDOMIZED, Integer.toString(c.getInt(4)));
            client.get(httpLink, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int status, Header[] header, byte[] data) {
                    String resultCode = Utils.getResultCode(data);
                    if (resultCode.equals(Constants.RC_SUCCESSFUL)) {
                        mDB.updateSavedToServer(id, 1);
                        mHttpStatusLayout.setVisibility(View.GONE);
                        Toast.makeText(SavedListsActivity.this, "List " + c.getString(1) + " has successfully been saved!!" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            mHttpStatusLayout.setVisibility(View.GONE);
            Toast.makeText(SavedListsActivity.this, "List " + c.getString(1) + " has successfully been saved!!" , Toast.LENGTH_LONG).show(); 
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
            } else if (requestCode == GET_NAME_ALERT) {
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
            } else if (requestCode == GET_FILE) {
                String path = Utils.getFilePathFromUri(data, this);
                Log.d("PATH", path);
                if (!path.endsWith(Constants.TEXTFILE_EXTENSION)) {
                    Intent intent = new Intent(this, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_NOT_TEXT_FILE);
                    startActivity(intent);
                } else if (!new File(path).exists()) {
                    Intent intent = new Intent(this, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_FILE_DNE);
                    startActivity(intent);
                } else {
                    ArrayList<String> list = Utils.readFromFile(path, this);
                    
                    Intent intent = new Intent(this, GetListNameDialogActivity.class);
                    intent.putExtra(Constants.SELECTIONS_LIST, Utils.listToString(list));
                    startActivityForResult(intent, GET_LIST_NAME);
                }
            } else if (requestCode == GET_FILE_ALERT) {
            	if (!SystemUtils.isAtLeastOSKitKat()) {
	                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	                intent.setType("text/*");
	                startActivityForResult(intent, GET_FILE);
            	} else {
            		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            	    intent.addCategory(Intent.CATEGORY_OPENABLE);
            	    intent.setType("text/*");
            	    startActivityForResult(intent, GET_FILE);
            	}
            } else if (requestCode == GET_LIST_NAME) {
                mHttpStatusLayout.setVisibility(View.VISIBLE);
                String id = data.getExtras().getString(Constants.DATA_ID);
                saveListToServer(id);
                displayLists();
            }
        }
    }
}
