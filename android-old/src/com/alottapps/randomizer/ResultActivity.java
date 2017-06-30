package com.alottapps.randomizer;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.application.RandomizerApplication;
import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.DatabaseHandler;
import com.alottapps.randomizer.util.RandomGenerator;
import com.alottapps.randomizer.util.SystemUtils;
import com.alottapps.randomizer.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ResultActivity extends Activity {
    
    // Members.
    private RelativeLayout mHttpStatusLayout;
    private TextView mMainTv;
    private int mType;
    private ArrayList<String> mSelections;
    private ArrayList<String> mRandOrderedList;
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
        mHttpStatusLayout = (RelativeLayout) findViewById(R.id.ar_http_loading_screen);
        mRandOrderedList = new ArrayList<String>();
        
        mType = getIntent().getExtras().getInt(Constants.TYPE_RANDOM, -1);
        if (mType == Constants.SINGLE_RANDOM || mType == Constants.LIST_RANDOM) {
            mSelections = getIntent().getExtras().getStringArrayList(Constants.SELECTIONS_LIST);
            
            if (mType == Constants.SINGLE_RANDOM) {
                mMainTv.setText(R.string.randomizer_has_selected_text);
                findViewById(R.id.ar_save_button).setVisibility(View.GONE);
            } else if (mType == Constants.LIST_RANDOM){
                mMainTv.setText(R.string.randomized_order_text);
                findViewById(R.id.ar_again_button).setVisibility(View.GONE);
                findViewById(R.id.ar_randomized_order_button_bar).setVisibility(View.VISIBLE);
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
            ll.removeAllViews();
            LayoutInflater inflater = getLayoutInflater();
            mRandOrderedList.clear();
            mReorderedList = "";
            
            for (int i = 0; i < orderedIndexL.size(); i++) {
                View v = inflater.inflate(R.layout.container_list_line_randomized, ll, false);
                TextView numTv = (TextView) v.findViewById(R.id.cllr_number_textview);
                numTv.setText((i + 1) + ".");
                TextView selectionTv = (TextView) v.findViewById(R.id.cllr_selections_textview);
                mRandOrderedList.add(mSelections.get(orderedIndexL.get(i)));
                selectionTv.setText(mSelections.get(orderedIndexL.get(i)));
                if (i == orderedIndexL.size() - 1) {
                    mReorderedList += mSelections.get(orderedIndexL.get(i));
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
    
    private void reverseListOrder() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ar_result_list_layout);
        ll.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        ArrayList<String> temp = new ArrayList<String>();
        mReorderedList = "";
        
        int num = 1;
        for (int i = mRandOrderedList.size() - 1; i >= 0; i--) {
            View v = inflater.inflate(R.layout.container_list_line_randomized, ll, false);
            TextView numTv = (TextView) v.findViewById(R.id.cllr_number_textview);
            numTv.setText(num + ".");
            TextView selectionTv = (TextView) v.findViewById(R.id.cllr_selections_textview);
            selectionTv.setText(mRandOrderedList.get(i));
            temp.add(mRandOrderedList.get(i));
            if (i == 0) {
                mReorderedList += mRandOrderedList.get(i);
            } else {
                mReorderedList += mRandOrderedList.get(i) + Constants.LIST_DELIMITER;
            }
            ll.addView(v);
            num++;
        }
        mRandOrderedList = temp;
        ll.setVisibility(View.VISIBLE);
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ar_back_button || v.getId() == R.id.ar_back_nav_button) {
            finish();
        } else if (v.getId() == R.id.ar_save_button) {
            String date = Utils.getCurrentDateTime();
            String id = mDB.addData(mReorderedList, date, 1, "");
            saveToDB(id);
        } else if (v.getId() == R.id.ar_again_button) {
            randomizeAndDisplay();
        } else if (v.getId() == R.id.ar_list_again_button) {
            randomizeAndDisplay();
        } else if (v.getId() == R.id.ar_reverse_order_button) {
            reverseListOrder();
        }
    }
    
    private void saveToDB(final String id) {
        final Cursor c = mDB.getDataByID(id);
        
        if (c.moveToFirst() && !Utils.skippedLogin(mDB) && SystemUtils.hasDataConnection(this)) {
            mHttpStatusLayout.setVisibility(View.VISIBLE);
            String httpLink = Constants.MAIN_ADDRESS + Constants.QUERY_SAVE_DATA;
            
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.HTTP_TIMEOUT);
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
                        Toast.makeText(ResultActivity.this, "Randomized list has successfully been saved!!" , Toast.LENGTH_LONG).show();
                        mHttpStatusLayout.setVisibility(View.GONE);
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(ResultActivity.this, "Randomized list has successfully been saved!!" , Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
