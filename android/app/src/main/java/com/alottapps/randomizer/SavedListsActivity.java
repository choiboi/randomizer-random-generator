package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alottapps.randomizer.adapters.SavedListAdapter;
import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.dialog.GetFileNameDialogActivity;
import com.alottapps.randomizer.dialog.GetListNameDialogActivity;
import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.SystemUtils;
import com.alottapps.randomizer.utils.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedListsActivity extends Activity {

    // Debug.
    private final String TAG = "SavedListActivity";
    
    // Members.
    private ListView mListview;
    private RelativeLayout mHttpStatusLayout;
    private List<Data> mSavedLists;
    private SavedListAdapter mAdapter;
    
    // Constants.
    private final int DELETE_ALERT = 100;
    private final int EDIT_LIST = 101;
    private final int GET_NAME_ALERT = 102;
    private final int GET_FILE = 103;
    private final int GET_FILE_ALERT = 104;
    private final int GET_LIST_NAME = 200;
    
    private final int INTENT_LARGE_LIST_ALERT = 111;
    private final int LARGE_LIST_SIZE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_lists);

        mListview = (ListView) findViewById(R.id.asl_listview);
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.asl_http_loading_screen);
        RandomizerSharedPreferences sharedPrefs = new RandomizerSharedPreferences(this);
        
        displayLists();
        
        setResult(RESULT_CANCELED);

        // Load admob ad.
        if (SystemUtils.hasDataConnection(this) && !sharedPrefs.getRemoveAds()) {
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.asl_back_button) {
            finish();
        } else if (v.getId() == R.id.asl_download_button) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_SELECT_TEXT_FILE);
            startActivityForResult(intent, GET_FILE_ALERT);
        }
    }

    /*
     * Retrieves the list of saved lists, Data.
     */
    private void displayLists() {
        findViewById(R.id.asl_none_textview).setVisibility(View.GONE);
        final SavedListsActivity me = this;
        Data.queryAllDataFromLocalstore(new FindCallback<Data>() {
            @Override
            public void done(List<Data> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mSavedLists = list;
                        mAdapter = new SavedListAdapter(mSavedLists, me);
                        mListview.setAdapter(mAdapter);
                    } else {
                        findViewById(R.id.asl_none_textview).setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e(TAG, "Retrieve list error: " + e.getMessage(), e);
                }
            }
        });
    }

    /*
     * Loads the selected list to main page.
     */
    public void loadList(String id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTION_OBJ_ID, id);
        setResult(RESULT_OK, intent);
        finish();
    }

    /*
     * This method checks if the list is large and opens a large list alert warning otherwise
     * just goes to edit list page.
     */
    public void editList(String id, int listSize) {
        if (listSize > LARGE_LIST_SIZE) {
            Intent intent = new Intent(SavedListsActivity.this, AlertDialogActivity.class);
            intent.putExtra(Constants.SELECTION_OBJ_ID, id);
            intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_LARGE_LIST);
            startActivityForResult(intent, INTENT_LARGE_LIST_ALERT);
        } else {
            goToEditPage(id);
        }
    }

    /*
     * Goes to the activity that allows users to edit their saved lists.
     */
    private  void goToEditPage(String objID) {
        Intent intent = new Intent(SavedListsActivity.this, EditListActivity.class);
        intent.putExtra(Constants.SELECTION_OBJ_ID, objID);
        startActivityForResult(intent, EDIT_LIST);
    }

    /*
     * Alerts user to make sure they want to delete the list.
     */
    public void deleteListConfirmation(String id) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_CONFIMATION);
        intent.putExtra(Constants.SELECTION_OBJ_ID, id);
        startActivityForResult(intent, DELETE_ALERT);
    }

    /*
     * Asks user for filename to save the list to file.
     */
    public void saveToFile(String id) {
        Intent intent = new Intent(SavedListsActivity.this, GetFileNameDialogActivity.class);
        intent.putExtra(Constants.SELECTION_OBJ_ID, id);
        startActivityForResult(intent, GET_NAME_ALERT);
    }

    /*
     * Deletes the selected saved list.
     */
    private void deleteSavedList(String objID) {
        mHttpStatusLayout.setVisibility(View.VISIBLE);
        Data.querySingleDataFromLocalstore(objID, new GetCallback<Data>() {
            @Override
            public void done(final Data data, ParseException e) {
                if (e == null) {
                    // Updated the data indicating it has been deleted and remove from Parse
                    // local store.
                    data.setIsDeleted(true);
                    data.unpinInBackground();
                    Toast.makeText(SavedListsActivity.this, "List " + data.getDataName() + " has successfully been deleted!", Toast.LENGTH_LONG).show();
                    mAdapter.clear();
                    displayLists();
                } else {
                    Log.e(TAG, "Failed to retrieve Data by ID: " + e.getMessage(), e);
                }
                mHttpStatusLayout.setVisibility(View.GONE);
            }
        });
    }

    /*
     * Save imported list to Parse local store.
     */
    private void saveListToParse(final String name, String list) {
        final Data data = new Data(name, list, Utils.getListSize(list));
        data.pinInBackground();
        Toast.makeText(SavedListsActivity.this, "List " + name + " has successfully been saved!!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE_ALERT) {
                // Proceed to deleting saved list.
                String objID = data.getExtras().getString(Constants.SELECTION_OBJ_ID);
                deleteSavedList(objID);
            } else if (requestCode == EDIT_LIST) {
                displayLists();
            } else if (requestCode == GET_NAME_ALERT) {
                // Gets the filename what the user specified to save as.
                String dataList = data.getExtras().getString(Constants.DATA);
                String filenameOrig = data.getExtras().getString(Constants.FILENAME);
                // Checks if the filename is used and if it is, then as a number to the the end of the filename.
                File file = SystemUtils.getOutputMediaFile(filenameOrig + Constants.TEXTFILE_EXTENSION);
                if (file.exists()) {
                    int i = 1;
                    while (file.exists()) {
                        file = SystemUtils.getOutputMediaFile(filenameOrig + "-" + i + Constants.TEXTFILE_EXTENSION);
                        i++;
                    }
                }

                // Create the text file.
                dataList = Utils.dbStrListToTextStr(dataList);
                if (Utils.checkSaveFilenameString(filenameOrig)) {
                    Intent intent = new Intent(this, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_INVALID_FILENAME);
                    startActivity(intent);
                } else if (SystemUtils.saveTextFile(file, dataList)) {
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
                Uri uri = data.getData();
                ArrayList<String> list = Utils.readFromFile(uri, this);

                if (list.size() > 0) {
                    // Ask user for list name.
                    Intent intent = new Intent(this, GetListNameDialogActivity.class);
                    intent.putExtra(Constants.SELECTIONS_LIST, Utils.listToString(list));
                    startActivityForResult(intent, GET_LIST_NAME);
                } else {
                    Toast.makeText(this, getResources().getText(R.string.empty_list_or_wrong_file_message), Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == GET_FILE_ALERT) {
                // Implicit intent to get text file from system.
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
                // Save list after getting the list name from user.
                String listStr = data.getStringExtra(Constants.SELECTIONS_LIST);
                String name = data.getExtras().getString(Constants.SELECTIONS_LIST_NAME);
                saveListToParse(name, listStr);
                displayLists();
            } else if (requestCode == INTENT_LARGE_LIST_ALERT) {
                // Proceed to editing large lists.
            	String id = data.getExtras().getString(Constants.SELECTION_OBJ_ID);
            	goToEditPage(id);
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetInvalidated();
            }
        }
    }
}
