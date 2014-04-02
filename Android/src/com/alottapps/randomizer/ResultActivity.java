package com.alottapps.randomizer;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.RandomGenerator;
import com.alottapps.randomizer.util.Utils;

public class ResultActivity extends Activity {
    
    // Members.
    private TextView mMainTv;
    private int mType;
    private ArrayList<String> mSelections;
    private int mStartNum;
    private int mEndNum;
    private String mBetweenRange;
    private String mReorderedList;
    private RandomizerApplication mApp;
    private DatabaseHandler mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mApp = (RandomizerApplication) getApplicationContext();
        mDB = mApp.getDB();
        
        mMainTv = (TextView) findViewById(R.id.ar_main_textview);
        
        mType = getIntent().getExtras().getInt(Constants.TYPE_RANDOM, -1);
        if (mType == Constants.SINGLE_RANDOM || mType == Constants.LIST_RANDOM) {
            mSelections = getIntent().getExtras().getStringArrayList(Constants.SELECTIONS_LIST);
            findViewById(R.id.ar_again_button).setVisibility(View.GONE);
            
            if (mType == Constants.SINGLE_RANDOM) {
                mMainTv.setText(R.string.randomizer_has_selected_text);
                findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            } else if (mType == Constants.LIST_RANDOM){
                mMainTv.setText(R.string.randomized_order_text);
            }
        } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
            mStartNum = Integer.parseInt(getIntent().getExtras().getString(Constants.START_NUMBER));
            mEndNum = Integer.parseInt(getIntent().getExtras().getString(Constants.END_NUMBER));
            
            if (mStartNum > mEndNum) {
                mStartNum = mEndNum;
                mEndNum = Integer.parseInt(getIntent().getExtras().getString(Constants.START_NUMBER));
            }
            
            mMainTv.setText(R.string.randomizer_has_selected_text);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            
            TextView rangeTv = (TextView) findViewById(R.id.ar_number_range_textview);
            mBetweenRange = "Between " + mStartNum + " to " + mEndNum;
            rangeTv.setText(mBetweenRange);
            rangeTv.setVisibility(View.VISIBLE);
        }
        
        randomizeAndDisplay();
    }
    
    private void randomizeAndDisplay() {
        String date = Utils.getCurrentDateTime();
        
        if (mType == Constants.SINGLE_RANDOM) {
            int index = RandomGenerator.singleRandomNumber(mSelections.size() - 1);
            TextView resultTv = (TextView) findViewById(R.id.ar_result_single_textview);
            resultTv.setText(mSelections.get(index));
            resultTv.setVisibility(View.VISIBLE);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            
            String listStr = Utils.listToString(mSelections);
            mDB.addPrevData(listStr, date, mSelections.get(index));
        } else if (mType == Constants.LIST_RANDOM) {
            ArrayList<Integer> orderedIndexL 
                = RandomGenerator.listUniqueRandomNumber(mSelections.size(), mSelections.size() - 1);
            LinearLayout ll = (LinearLayout) findViewById(R.id.ar_result_list_layout);
            LayoutInflater inflater = getLayoutInflater();
            mReorderedList = "";
            
            for (int i = 0; i < orderedIndexL.size(); i++) {
                View v = inflater.inflate(R.layout.container_list_line_randomized, ll, false);
                TextView numTv = (TextView) v.findViewById(R.id.cllr_number_textview);
                numTv.setText((i + 1) + ".");
                TextView selectionTv = (TextView) v.findViewById(R.id.cllr_selections_textview);
                selectionTv.setText(mSelections.get(orderedIndexL.get(i)));
                if (i == orderedIndexL.size() - 1) {
                    mReorderedList += mSelections.get(orderedIndexL.get(i)) + Constants.LIST_DELIMITER;
                } else {
                    mReorderedList += mSelections.get(orderedIndexL.get(i)) + Constants.LIST_DELIMITER;
                }
                ll.addView(v);
            }
            ll.setVisibility(View.VISIBLE);
        } else if (mType == Constants.NUMBER_RANGE_RANDOM) {
            int num = RandomGenerator.singleRangeRandomNumber(mStartNum, mEndNum);
            TextView resultTv = (TextView) findViewById(R.id.ar_result_single_textview);
            resultTv.setText(num + "");
            resultTv.setVisibility(View.VISIBLE);
            
            mDB.addPrevData(mBetweenRange, date, String.valueOf(num));
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ar_back_button || v.getId() == R.id.ar_back_nav_button) {
            finish();
        } else if (v.getId() == R.id.ar_save_button) {
            String date = Utils.getCurrentDateTime();
            mDB.addData(mReorderedList, date, 1, "");
            finish();
        } else if (v.getId() == R.id.ar_again_button) {
            randomizeAndDisplay();
        }
    }
}
