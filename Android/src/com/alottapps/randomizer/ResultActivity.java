package com.alottapps.randomizer;

import java.util.ArrayList;

import com.alottapps.randomizer.util.Constants;
import com.alottapps.randomizer.util.RandomGenerator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {
    
    // Members.
    private TextView mMainTv;
    private TextView mResultTv;
    private int mType;
    private ArrayList<String> mSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        mMainTv = (TextView) findViewById(R.id.ar_main_textview);
        mResultTv = (TextView) findViewById(R.id.ar_result_textview);
        
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
            int index = RandomGenerator.singleRandomNumber(mSelections.size());
            mResultTv.setText(mSelections.get(index));
        } else if (mType == Constants.LIST_RANDOM) {
            ArrayList<Integer> orderedIndexL 
                = RandomGenerator.listUniqueRandomNumber(mSelections.size(), mSelections.size() - 1);
            String htmlStr = "";
            for (int i = 0; i < orderedIndexL.size(); i++) {
                htmlStr += "</b>" + i + "</b> ";
                htmlStr += mSelections.get(orderedIndexL.get(i));
                
                if (i < orderedIndexL.size() - 2) {
                    htmlStr += "<br>";
                }
            }
            mResultTv.setText(Html.fromHtml(htmlStr));
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ar_back_button) {
            finish();
        } else if (v.getId() == R.id.ar_save_button) {
            // TODO: save the list.
            finish();
        }
    }
}
