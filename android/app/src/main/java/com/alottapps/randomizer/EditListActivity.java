package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.util.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.ArrayList;

public class EditListActivity extends Activity {

    // Debug.
    private final String TAG = "EditListActivity";

    // Members.
    private Data mData;
    private LinearLayout mSelectionsLayout;
    private ArrayList<EditText> mSelectionsEt;
    private EditText mTitleEt;
    private ArrayList<String> mSelections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        
        setResult(RESULT_CANCELED);

        mTitleEt = (EditText) findViewById(R.id.ael_title_edittext);
        mSelectionsEt = new ArrayList<EditText>();
        
        String objID = getIntent().getExtras().getString(Constants.SELECTION_OBJ_ID);
        Data.querySingleDataFromLocalstore(objID, new GetCallback<Data>() {
            @Override
            public void done(Data data, ParseException e) {
                if (e == null) {
                    mData = data;
                    mSelections = Utils.stringToList(data.getData());
                    mSelectionsLayout = (LinearLayout) findViewById(R.id.ael_selection_layout);
                    mTitleEt.setText(data.getDataName(), TextView.BufferType.EDITABLE);

                    setupPage();
                } else {
                    Log.e(TAG, "Failed to retrieve Data by ID: " + e.getMessage(), e);
                }
            }
        });
    }

    /*
     * Goes through the list and creates a edit text field for each selection.
     */
    private void setupPage() {
       for (int i = 0; i < mSelections.size(); i++) {
           View v = getLayoutInflater().inflate(R.layout.container_edittext, mSelectionsLayout, false);
           EditText et = (EditText) v.findViewById(R.id.ce_edittext);
           et.setText(mSelections.get(i), TextView.BufferType.EDITABLE);
           mSelectionsEt.add(et);
           mSelectionsLayout.addView(v);
        }
    }
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ael_back_button) {
            finish();
        } else if (v.getId() == R.id.ael_save_nav_button) {
            // Basically checks if the list is not empty, and updates the existing Data.
            String newList = getListAsString();
            if (newList.equals("")) {
                Intent intent = new Intent(this, AlertDialogActivity.class);
                intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_EMPTY_SAVE);
                startActivity(intent);
            } else {
                String name = mTitleEt.getText().toString();
                if (name.equals("")) {
                    name = Constants.DEFAULT_LIST_NAME;
                }

                updateList(name, newList);
            }
        } else if (v.getId() == R.id.ael_add_new_selection_button) {
            View view = getLayoutInflater().inflate(R.layout.container_edittext, mSelectionsLayout, false);
            EditText et = (EditText) view.findViewById(R.id.ce_edittext);
            mSelectionsEt.add(et);
            mSelectionsLayout.addView(view);
        }
    }
    
    private String getListAsString() {
        ArrayList<String> newSelections = new ArrayList<String>();
        for (int i = 0; i < mSelectionsEt.size(); i++) {
            if (!mSelectionsEt.get(i).getText().toString().equals("")) {
                newSelections.add(mSelectionsEt.get(i).getText().toString());
            }
        }
        
        if (newSelections.size() == 0) {
            return "";
        }
        return Utils.listToString(newSelections);
    }

    /*
     * With the updated list, save to Parse and to Parse local store.
     */
    private void updateList(final String name, String listStr) {
        mData.setDataName(name);
        mData.setData(listStr);
        mData.incrementNumModifiedCount();

        mData.pinInBackground();
        Toast.makeText(this, "List " + name + " has been saved!!", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
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
            }
        }
        return ret;
    }
}
