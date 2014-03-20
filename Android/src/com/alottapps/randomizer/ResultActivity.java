package com.alottapps.randomizer;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.RandomGenerator;

public class ResultActivity extends Activity {
    
    // Members.
    private TextView mMainTv;
    private int mType;
    private ArrayList<String> mSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        mMainTv = (TextView) findViewById(R.id.ar_main_textview);
        
        mSelections = getIntent().getExtras().getStringArrayList(Constants.SELECTIONS_LIST);
        mType = getIntent().getExtras().getInt(Constants.TYPE_RANDOM, -1);
        if (mType == Constants.SINGLE_RANDOM) {
            mMainTv.setText(R.string.randomizer_has_selected_text);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);
        } else if (mType == Constants.LIST_RANDOM){
            mMainTv.setText(R.string.randomized_order_text);
        }
        
        randomizeAndDisplay();
    }
    
    private void randomizeAndDisplay() {
        if (mType == Constants.SINGLE_RANDOM) {
            int index = RandomGenerator.singleRandomNumber(mSelections.size() - 1);
            TextView resultTv = (TextView) findViewById(R.id.ar_result_single_textview);
            resultTv.setText(mSelections.get(index));
            resultTv.setVisibility(View.VISIBLE);
            findViewById(R.id.ar_save_button).setVisibility(View.GONE);
        } else if (mType == Constants.LIST_RANDOM) {
            ArrayList<Integer> orderedIndexL 
                = RandomGenerator.listUniqueRandomNumber(mSelections.size(), mSelections.size() - 1);
            LinearLayout ll = (LinearLayout) findViewById(R.id.ar_result_list_layout);
            LayoutInflater inflater = getLayoutInflater();
            
            for (int i = 0; i < orderedIndexL.size(); i++) {
                View v = inflater.inflate(R.layout.container_list_randomized, ll, false);
                TextView numTv = (TextView) v.findViewById(R.id.clr_number_textview);
                numTv.setText((i + 1) + ".");
                TextView selectionTv = (TextView) v.findViewById(R.id.clr_selections_textview);
                selectionTv.setText(mSelections.get(orderedIndexL.get(i)));
                ll.addView(v);
            }
            
            ll.setVisibility(View.VISIBLE);
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ar_back_button || v.getId() == R.id.ar_back_nav_button) {
            finish();
        } else if (v.getId() == R.id.ar_save_button) {
            // TODO: save the list.
            finish();
        }
    }
}
