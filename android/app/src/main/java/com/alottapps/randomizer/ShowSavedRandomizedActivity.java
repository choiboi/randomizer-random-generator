package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.adapters.PreviouslySavedChoicesAdapter;
import com.alottapps.randomizer.adapters.RandomizedSavedListAdapter;
import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.dialog.GetFileNameDialogActivity;
import com.alottapps.randomizer.parse.PreviousSelectedChoiceData;
import com.alottapps.randomizer.parse.SavedRandomizedListData;
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
import java.util.List;

public class ShowSavedRandomizedActivity extends Activity {

    // Debug.
    private final String TAG = "PreviousSelectedActivity";
    
    // Members.
    private ProgressBar mProgressBar;
    private TextView mTitleTv;
    private ListView mListview;
    private PreviouslySavedChoicesAdapter mListAdapterChoices;
    private RandomizedSavedListAdapter mListAdapterList;
    private int mTypeShown;
    
    // Constants.
    private final int GET_NAME_ALERT = 100;
    private final int DELETE_ALERT = 101;
    private final int DELETE_ALL_ALERT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_randomized);

        mProgressBar = (ProgressBar) findViewById(R.id.assr_progressbar);
        mTitleTv = (TextView) findViewById(R.id.assr_page_title_textview);
        mListview = (ListView) findViewById(R.id.assr_listview);
        mTypeShown = getIntent().getExtras().getInt(Constants.TYPE_SELECTED_RANDOMIZED, -1);
        RandomizerSharedPreferences sharedPrefs = new RandomizerSharedPreferences(this);

        // Load admob ad.
        if (SystemUtils.hasDataConnection(this) && !sharedPrefs.getRemoveAds()) {
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        onStartTask();
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.assr_back_button) {
            finish();
        } else if (v.getId() == R.id.assr_delete_all_button) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_ALL_DELETE_CONFIMATION);
            startActivityForResult(intent, DELETE_ALL_ALERT);
        }
    }
    
    private void onStartTask() {
        if (mTypeShown == Constants.PREV_RANDOMIZED) {
            mTitleTv.setText(R.string.prev_selected_choices_text);
            displayPrevSelectedList();
        } else if (mTypeShown == Constants.SAVED_LIST_RANDOMIZED) {
            mTitleTv.setText(R.string.saved_randomized_list_text);
            findViewById(R.id.assr_delete_all_button).setVisibility(View.GONE);
            displaySavedRandomizedLists();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    /*
     * This method queries through the Parse local store to retrieve
     * all the previously selected choices.
     */
    private void displayPrevSelectedList() {
        PreviousSelectedChoiceData.queryAllDataFromLocalstore(new FindCallback<PreviousSelectedChoiceData>() {
            @Override
            public void done(List<PreviousSelectedChoiceData> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        findViewById(R.id.assr_none_textview).setVisibility(View.VISIBLE);
                    } else {
                        mListAdapterChoices = new PreviouslySavedChoicesAdapter(ShowSavedRandomizedActivity.this, list);
                        mListview.setAdapter(mListAdapterChoices);
                    }
                } else {
                    Log.e(TAG, "Retrieve list error: " + e.getMessage(), e);
                }
            }
        });
    }


    /*
     * This method queries through the Parse local store to retrieve
     * all the previously randomized lists.
     */
    private void displaySavedRandomizedLists() {
        final ShowSavedRandomizedActivity me = this;
        SavedRandomizedListData.queryAllDataFromLocalstore(new FindCallback<SavedRandomizedListData>() {
            @Override
            public void done(List<SavedRandomizedListData> list, ParseException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        findViewById(R.id.assr_none_textview).setVisibility(View.VISIBLE);
                    } else {
                        mListAdapterList = new RandomizedSavedListAdapter(list, me);
                        mListview.setAdapter(mListAdapterList);
                    }
                } else {
                    Log.e(TAG, "Retrieve list error: " + e.getMessage(), e);
                }
            }
        });
    }

    /*
     * This method opens up a dialog confirming user if they would like to
     * delete the list.
     */
    public void deleteListConfirmation(String id) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_CONFIMATION);
        intent.putExtra(Constants.SELECTION_OBJ_ID, id);
        startActivityForResult(intent, DELETE_ALERT);
    }

    public void saveToFile(String id) {
        Intent intent = new Intent(ShowSavedRandomizedActivity.this, GetFileNameDialogActivity.class);
        intent.putExtra(Constants.SELECTION_OBJ_ID, id);
        intent.putExtra(Constants.LIST_TYPE, Constants.RANDOMIZED_LIST);
        startActivityForResult(intent, GET_NAME_ALERT);
    }

    /*
     * Deletes the randomized list from Parse local store and flags the Parse DB as deleted.
     */
    private void deleteFromDB(String id) {
        SavedRandomizedListData.querySingleDataFromLocalstore(id, new GetCallback<SavedRandomizedListData>() {
            @Override
            public void done(final SavedRandomizedListData obj, ParseException e) {
                if (e == null) {
                    obj.unpinInBackground();
                    Toast.makeText(ShowSavedRandomizedActivity.this, "Randomized list has successfully been deleted!!", Toast.LENGTH_LONG).show();
                    mListAdapterList.clear();
                    displaySavedRandomizedLists();
                } else {
                    Log.e(TAG, "Delete list Error: " + e.getMessage(), e);
                }
            }
        });
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
            } else if (requestCode == DELETE_ALERT) {
                // Deletes the randomized list.
                String dataID = data.getExtras().getString(Constants.SELECTION_OBJ_ID);
                deleteFromDB(dataID);
            } else if (requestCode == DELETE_ALL_ALERT) {
                // Mark all the previously selected choices as deleted.
                PreviousSelectedChoiceData.markAllDataAsDeleted();
                // Clear the list and display NONE text.
                if (mListAdapterChoices != null) {
                    mListAdapterChoices.clear();
                }
                findViewById(R.id.assr_none_textview).setVisibility(View.VISIBLE);
            }
        }
    }
}
