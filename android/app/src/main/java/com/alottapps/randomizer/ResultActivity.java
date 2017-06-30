package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.adapters.ResultListAdapter;
import com.alottapps.randomizer.parse.PreviousSelectedChoiceData;
import com.alottapps.randomizer.parse.SavedRandomizedListData;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.RandomGenerator;
import com.alottapps.randomizer.utils.util.SystemUtils;
import com.alottapps.randomizer.utils.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ResultActivity extends Activity {
    
    // Members.
    private int mType;
    private ArrayList<String> mSelections;
    private int mStartNum;
    private int mEndNum;
    private String mReorderedList;
    private ProgressBar mMainProgressbar;
    private ResultListAdapter mResAdapter;
    private ListView mResListview;
    private RandomizerSharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get all associated references to views.
        TextView mainTv = (TextView) findViewById(R.id.ar_main_textview);
        mMainProgressbar = (ProgressBar) findViewById(R.id.ar_main_progressbar);
        mResListview = (ListView) findViewById(R.id.aea_listview);
        mSharedPrefs = new RandomizerSharedPreferences(this);

        // Determine what is being randomized and setup page accordingly.
        mType = getIntent().getExtras().getInt(Constants.TYPE_RANDOM, -1);
        if (mType == Constants.SINGLE_RANDOM || mType == Constants.LIST_RANDOM) {
            // For list random or single random from list.
            mSelections = getIntent().getExtras().getStringArrayList(Constants.SELECTIONS_LIST);

            if (mType == Constants.SINGLE_RANDOM) {
                mainTv.setText(R.string.randomizer_has_selected_text);
                findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            } else if (mType == Constants.LIST_RANDOM){
                mainTv.setText(R.string.randomized_order_text);
                findViewById(R.id.ar_again_button).setVisibility(View.GONE);
                findViewById(R.id.ar_randomized_order_button_bar).setVisibility(View.VISIBLE);
            }
        } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
            // For number random.
            Intent intent = getIntent();
            mStartNum = Integer.parseInt(intent.getStringExtra(Constants.START_NUMBER));
            mEndNum = Integer.parseInt(intent.getStringExtra(Constants.END_NUMBER));
            
            if (mStartNum > mEndNum) {
                mStartNum = mEndNum;
                mEndNum = Integer.parseInt(intent.getStringExtra(Constants.START_NUMBER));
            }

            mainTv.setText(R.string.randomizer_has_selected_text);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            
            TextView rangeTv = (TextView) findViewById(R.id.ar_number_range_textview);
            String betweenRange = "Between " + mStartNum + " to " + mEndNum;
            rangeTv.setText(betweenRange);
            rangeTv.setVisibility(View.VISIBLE);
        }

        // Load admob ad.
        if (SystemUtils.hasDataConnection(this) && !mSharedPrefs.getRemoveAds()) {
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        }

        // Start the randomization process.
        randomizeAndDisplay();
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.ar_back_button || v.getId() == R.id.ar_back_nav_button) {
            finish();
        } else if (v.getId() == R.id.ar_save_button) {
            // Save the randomized list.
            saveRandomizedList();
        } else if (v.getId() == R.id.ar_again_button || v.getId() == R.id.ar_list_again_button) {
            randomizeAndDisplay();
        } else if (v.getId() == R.id.ar_reverse_order_button) {
            reverseListOrder();
        }
    }

    /*
     * This method will determine how to randomize the data and display it accordingly.
     */
    private void randomizeAndDisplay() {
        if (mType == Constants.SINGLE_RANDOM) {
            // Select single choice to generator.
            findViewById(R.id.ar_result_single_textview).setVisibility(View.INVISIBLE);
            new RandomizeSingleAsyncTask().execute();
        } else if (mType == Constants.LIST_RANDOM) {
            // Randomize list.
            mResListview.setVisibility(View.INVISIBLE);
            mMainProgressbar.setVisibility(View.VISIBLE);
            new RandomizeListAsyncTask().execute();
        } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
            findViewById(R.id.ar_result_single_textview).setVisibility(View.INVISIBLE);
            // Random number generator - single number.
            new RandomizeSingleAsyncTask().execute();
        }
    }

    /*
     * Display the randomized data using the UI thread.
     */
    private void displayResult(int index, ArrayList<Integer> orderedIndexL) {
        if (mType == Constants.SINGLE_RANDOM) {
            // Select single choice to generator.
            TextView resultTv = (TextView) findViewById(R.id.ar_result_single_textview);
            resultTv.setText(mSelections.get(index));
            resultTv.setVisibility(View.VISIBLE);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);

            saveSelectedChoiceData(mSelections.get(index));
        } else if (mType == Constants.LIST_RANDOM) {
            // Randomize list.
            mResAdapter = new ResultListAdapter(mSelections, orderedIndexL, this);
            mResListview.setAdapter(mResAdapter);
            mMainProgressbar.setVisibility(View.GONE);
            mResListview.setVisibility(View.VISIBLE);
        } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
            // Random number generator - single number.
            TextView resultTv = (TextView) findViewById(R.id.ar_result_single_textview);
            resultTv.setText(String.valueOf(index));
            resultTv.setVisibility(View.VISIBLE);

            saveSelectedChoiceData(Integer.toString(index));
        }
    }

    /*
     * This method reverses the randomized list.
     */
    private void reverseListOrder() {
        mResListview.setVisibility(View.INVISIBLE);
        mMainProgressbar.setVisibility(View.VISIBLE);
        new ReverseRandomizedListAsyncTask().execute();
    }

    /*
     * This method saves the randomized list to Parse local store.
     */
    private void saveRandomizedList() {
        SavedRandomizedListData data = new SavedRandomizedListData(Utils.listToString(mSelections), mReorderedList, mSelections.size());
        long id = mSharedPrefs.getObjectIdNum();
        data.setObjectId(Long.toString(id++));
        mSharedPrefs.setObjectIdNum(id);
        data.pinInBackground();
        finish();
    }

    /*
     * This method saved the selected choice into Parse local store.
     */
    private void saveSelectedChoiceData(String choice) {
        PreviousSelectedChoiceData data;
        if (mType == Constants.NUMBER_RANGE_RANDOM) {
            String range = mStartNum + Constants.NUMBER_BTW_SPLIT + mEndNum;
            int size = Math.abs(mEndNum - mStartNum) + 1;
            data = new PreviousSelectedChoiceData(range, choice, size, true);
        } else {
            data = new PreviousSelectedChoiceData(Utils.listToString(mSelections), choice, mSelections.size(), false);
        }
        long id = mSharedPrefs.getObjectIdNum();
        data.setObjectId(Long.toString(id++));
        mSharedPrefs.setObjectIdNum(id);
        data.pinInBackground();
    }

    /*
     * Use a separate thread to randomize data for single results.
     */
    private class RandomizeSingleAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            if (mType == Constants.SINGLE_RANDOM) {
                // Select single choice to generator.
                return RandomGenerator.singleRandomNumber(mSelections.size() - 1);
            } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
                // Random number generator - single number.
                return RandomGenerator.singleRangeRandomNumber(mStartNum, mEndNum);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                displayResult(integer, null);
            } else {
                String s = getResources().getString(R.string.something_went_wrong_randomizing_text);
                Toast.makeText(ResultActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Use a separate thread to randomize data for re-ordered (list) results.
     */
    private class RandomizeListAsyncTask extends AsyncTask<Void, Void, ArrayList<Integer>> {
        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {
            if (mType == Constants.LIST_RANDOM) {
                // Randomize list.
                ArrayList<Integer> orderedIndexL = RandomGenerator.listUniqueRandomNumber(mSelections.size(), mSelections.size() - 1);
                mReorderedList = "";
                for (int i = 0; i < orderedIndexL.size(); i++) {
                    if (i == orderedIndexL.size() - 1) {
                        mReorderedList += mSelections.get(orderedIndexL.get(i));
                    } else {
                        mReorderedList += mSelections.get(orderedIndexL.get(i)) + Constants.LIST_DELIMITER;
                    }
                }

                return orderedIndexL;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            if (integers != null) {
                displayResult(-1, integers);
            }
        }
    }

    /*
     * Use separate thread to reverse the randomized list results.
     */
    private class ReverseRandomizedListAsyncTask extends AsyncTask<Void, Void, ArrayList<Integer>> {
        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {
            ArrayList<Integer> orderedIndexL = mResAdapter.getOrderedIntList();
            ArrayList<Integer> revOrderedL = new ArrayList<>();

            mReorderedList = "";
            for (int i = orderedIndexL.size() - 1; i >= 0; i--) {
                revOrderedL.add(orderedIndexL.get(i));
                if (i == 0) {
                    mReorderedList += mSelections.get(orderedIndexL.get(i));
                } else {
                    mReorderedList += mSelections.get(orderedIndexL.get(i)) + Constants.LIST_DELIMITER;
                }
            }

            return revOrderedL;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            if (integers != null) {
                displayResult(-1, integers);
            }
        }
    }
}
