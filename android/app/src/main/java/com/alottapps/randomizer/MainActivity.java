package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.dialog.GetListNameDialogActivity;
import com.alottapps.randomizer.dialog.NoticeDialogActivity;
import com.alottapps.randomizer.dialog.NumberSelectorDialogActivity;
import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.SystemUtils;
import com.alottapps.randomizer.utils.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends Activity {

    // Debug.
    private final String TAG = "MainActivity";
    
    // Members.
	private Button mLoadMoreBut;
    private EditText mSelectionsEt;
    private LinearLayout mSelectionsListview;
    private LinearLayout mMainMenu;
    private RelativeLayout mHttpStatusLayout;
    private View mMenuBg;
    private boolean mMenuOpen;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mSelections;
    private RandomizerSharedPreferences mSharedPrefs;
    
    private int mListSize;
    private int mCurrentListIndex;
    
    // Constants.
    private final int LOAD_SAVED_LIST = 1;
    private final int GET_LIST_NAME = 2;
    private final int LINE_LOAD_INCREMENT = 20;
    private final int INTENT_ABOUT_ACTIVITY = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSelectionsEt = (EditText) findViewById(R.id.am_selection_edittext);
        mSelectionsListview = (LinearLayout) findViewById(R.id.am_selections_listview);
        mMainMenu = (LinearLayout) findViewById(R.id.am_menu_layout);
        mMenuBg = findViewById(R.id.am_menu_background_view);
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.am_http_loading_screen);
        mLoadMoreBut = (Button) findViewById(R.id.am_load_more_button);
        
        mMenuOpen = false;
        mSelections = new ArrayList<>();
        mLayoutInflater = getLayoutInflater();
        mSharedPrefs = new RandomizerSharedPreferences(this);
        
        mCurrentListIndex = LINE_LOAD_INCREMENT; // Initial load size is 20 lines.
        mListSize = mSelections.size();
        drawSelectionsListview();
        
        mMenuBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCloseMenu();
            }
        });

        // Load admob ad.
        if (SystemUtils.hasDataConnection(this) && !mSharedPrefs.getRemoveAds()) {
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Display notice alert if need to do so.
        boolean showNotification = mSharedPrefs.getNoticeViewed(RandomizerSharedPreferences.KEY_DISPLAY_TERMINATION_NOTICE);
        if (ParseUser.getCurrentUser() != null) {
            if (!showNotification && !ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                // Display alert notification.
                startActivity(new Intent(this, NoticeDialogActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mMenuOpen) {
            openCloseMenu();
        } else {
            super.onBackPressed();
        }
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.am_menu_button) {
            openCloseMenu();
        } else if (v.getId() == R.id.am_add_button) {
            addSelection();
        } else if (v.getId() == R.id.am_clear_all_button) {
            clearSelectionsList();
        } else if (v.getId() == R.id.am_load_list_button) {
            Intent intent = new Intent(this, SavedListsActivity.class);
            startActivityForResult(intent, LOAD_SAVED_LIST);
        } else if (v.getId() == R.id.am_save_list_button) {
            if (!isSelectionsListEmpty()) {
                // Prompt user for list name.
                Intent intent = new Intent(this, GetListNameDialogActivity.class);
                startActivityForResult(intent, GET_LIST_NAME);
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_SAVE);
            }
        } else if (v.getId() == R.id.am_randomize_number_button) {
            Intent intent = new Intent(this, NumberSelectorDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.am_pick_choice_button) {
            // When Pick a Choice button is pressed.
            if (!isSelectionsListEmpty()) {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(Constants.TYPE_RANDOM, Constants.SINGLE_RANDOM);
                intent.putStringArrayListExtra(Constants.SELECTIONS_LIST, mSelections);
                startActivity(intent);
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_RANDOMIZER);
            }
        } else if (v.getId() == R.id.am_randomize_order_button) {
            // When Randomized Order button is pressed.
            if (!isSelectionsListEmpty()) {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(Constants.TYPE_RANDOM, Constants.LIST_RANDOM);
                intent.putStringArrayListExtra(Constants.SELECTIONS_LIST, mSelections);
                startActivity(intent);
            } else {
                listEmptyAlertDialog(Constants.ALERT_EMPTY_RANDOMIZER);
            }
        } else if (v.getId() == R.id.am_load_more_button) {
        	loadMore();
        }
    }

    /*
     * Click event listener for buttons in the menu.
     */
    public void onMenuButtonClick(View v) {
        if (v.getId() == R.id.am_previous_choice_menu_button) {
            Intent intent = new Intent(this, ShowSavedRandomizedActivity.class);
            intent.putExtra(Constants.TYPE_SELECTED_RANDOMIZED, Constants.PREV_RANDOMIZED);
            startActivity(intent);
        } else if (v.getId() == R.id.am_saved_ordered_list_menu_button) {
            Intent intent = new Intent(this, ShowSavedRandomizedActivity.class);
            intent.putExtra(Constants.TYPE_SELECTED_RANDOMIZED, Constants.SAVED_LIST_RANDOMIZED);
            startActivity(intent);
        } else if (v.getId() == R.id.am_about_menu_button) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityForResult(intent, INTENT_ABOUT_ACTIVITY);
        } 
        openCloseMenu();
    }

    /*
     * Populate the view listing all the selections.
     */
    private void drawSelectionsListview() {
        mSelectionsListview.removeAllViews();
        
        if (mSelections.size() == 0) {
            View v = mLayoutInflater.inflate(R.layout.container_main_selections, mSelectionsListview, false);
            v.findViewById(R.id.cs_delete_button).setVisibility(View.INVISIBLE);
            mSelectionsListview.addView(v);
        }
        
        int maxInd = mListSize > mCurrentListIndex ? mCurrentListIndex : mListSize;
        
        for (int i = 0; i < maxInd; i++) {
            View v = mLayoutInflater.inflate(R.layout.container_main_selections, mSelectionsListview, false);
            ImageButton imb = (ImageButton) v.findViewById(R.id.cs_delete_button);
            final int pos = i;
            imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelections.remove(pos);
                    mCurrentListIndex--;
                    mListSize = mSelections.size();
                    drawSelectionsListview();
                }
            });
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(mSelections.get(i));
            mSelectionsListview.addView(v);
        }
    }

    /*
     * If the list is longer than LINE_LOAD_INCREMENT then load additional LINE_LOAD_INCREMENT
     * selections.
     */
    private void loadMore() {
    	int prevInd = mCurrentListIndex;
    	mCurrentListIndex += LINE_LOAD_INCREMENT;
    	
    	int maxInd = mListSize > mCurrentListIndex ? mCurrentListIndex : mListSize;
    	for (int i = prevInd; i < maxInd; i++) {
    		View v = mLayoutInflater.inflate(R.layout.container_main_selections, mSelectionsListview, false);
            ImageButton imb = (ImageButton) v.findViewById(R.id.cs_delete_button);
            final int pos = i;
            imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelections.remove(pos);
                    mCurrentListIndex--;
                    drawSelectionsListview();
                }
            });
            TextView tv = (TextView) v.findViewById(R.id.cs_selection_textview);
            tv.setText(mSelections.get(i));
            mSelectionsListview.addView(v);
    	}
    	
    	if (mListSize <= mCurrentListIndex) {
        	mLoadMoreBut.setVisibility(View.GONE);
        }
    }

    /*
     * Add the new selection into the list and populate it.
     */
    private void addSelection() {
        String s = mSelectionsEt.getText().toString();
        if (!s.equals("")) {
            mSelections.add(0, s);
            mListSize = mSelections.size();
            mCurrentListIndex++;
            drawSelectionsListview();
        }
        mSelectionsEt.setText("");
    }

    /*
     * Remove all the selections in the list.
     */
    private void clearSelectionsList() {
        mLoadMoreBut.setVisibility(View.GONE);
    	mListSize = 0;
    	mCurrentListIndex = LINE_LOAD_INCREMENT;
        mSelections = new ArrayList<>();
        mSelectionsListview.removeAllViews();
        
        drawSelectionsListview();
    }

    /*
     * This method returns true if the selections list is empty, false otherwise.
     */
    private boolean isSelectionsListEmpty() {
        return mSelections.isEmpty();
    }
    
    private void openCloseMenu() {
        if (!mMenuOpen) {
            mMainMenu.setVisibility(View.VISIBLE);
            mMenuBg.setVisibility(View.VISIBLE);
            mMenuOpen = true;
        } else {
            mMainMenu.setVisibility(View.INVISIBLE);
            mMenuBg.setVisibility(View.INVISIBLE);
            mMenuOpen = false;
        }
    }

    /*
     * Bring up an alert dialog indicating to the user that the selection list is empty.
     */
    private void listEmptyAlertDialog(int type) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(Constants.ALERT_TYPE, type);
        startActivity(intent);
    }

    /*
     * Save new list locally into Parse datastore.
     */
    private void saveListToParse( final String listName) {
        Data data = new Data(listName, Utils.listToString(mSelections), mListSize);
        long id = mSharedPrefs.getObjectIdNum();
        data.setObjectId(Long.toString(id++));
        mSharedPrefs.setObjectIdNum(id);
        data.pinInBackground();
        Toast.makeText(MainActivity.this, "List " + listName + " has been saved!!", Toast.LENGTH_LONG).show();
        mHttpStatusLayout.setVisibility(View.GONE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == LOAD_SAVED_LIST) {
                // Retrieve the selected list to load via Data object ID.
                String objId = data.getExtras().getString(Constants.SELECTION_OBJ_ID);
                Data.querySingleDataFromLocalstore(objId, new GetCallback<Data>() {
                    @Override
                    public void done(Data data, ParseException e) {
                        if (e == null) {
                            mSelections = Utils.stringToList(data.getData());
                            mListSize = mSelections.size();
                            mCurrentListIndex = LINE_LOAD_INCREMENT;
                            if (mListSize <= mCurrentListIndex) {
                                mLoadMoreBut.setVisibility(View.GONE);
                            } else {
                                mLoadMoreBut.setVisibility(View.VISIBLE);
                            }
                            drawSelectionsListview();
                        } else {
                            Log.e(TAG, "Failed to retrieve Data by ID: " + e.getMessage(), e);
                        }
                    }
                });
            } else if (requestCode == GET_LIST_NAME) {
                // Return from dialog activity after asking user for list name.
                mHttpStatusLayout.setVisibility(View.VISIBLE);
                String listName = data.getExtras().getString(Constants.SELECTIONS_LIST_NAME);
                saveListToParse(listName);
            } else if (requestCode == INTENT_ABOUT_ACTIVITY && mSharedPrefs.getRemoveAds()) {
                findViewById(R.id.adView).setVisibility(View.GONE);
            }
        }
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
                mSelectionsEt.clearFocus();
            }
        }
        return ret;
    }
}
