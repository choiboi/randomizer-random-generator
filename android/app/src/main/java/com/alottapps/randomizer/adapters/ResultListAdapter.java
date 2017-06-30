package com.alottapps.randomizer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.ResultActivity;

import java.util.ArrayList;

public class ResultListAdapter extends BaseAdapter {

    // Members.
    private ArrayList<String> mSelections;
    private ArrayList<Integer> mOrderedL;
    private Activity mActivity;

    public ResultListAdapter(ArrayList<String> selections, ArrayList<Integer> orderedL, ResultActivity a) {
        mSelections = selections;
        mOrderedL = orderedL;
        mActivity = a;
    }

    public ResultListAdapter(ArrayList<String> selections, Activity a) {
        mSelections = selections;
        mActivity = a;
        mOrderedL = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getOrderedIntList() {
        return mOrderedL;
    }

    @Override
    public int getCount() {
        return mSelections.size();
    }

    @Override
    public String getItem(int position) {
        return mSelections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.container_list_line_randomized, parent, false);
        }

        TextView numTv = (TextView) convertView.findViewById(R.id.cllr_number_textview);
        String num = String.valueOf(position + 1) + ".";
        numTv.setText(num);
        TextView selectionTv = (TextView) convertView.findViewById(R.id.cllr_selections_textview);
        if (mOrderedL.size() == 0) {
            selectionTv.setText(getItem(position));
        } else {
            selectionTv.setText(getItem(mOrderedL.get(position)));
        }

        return convertView;
    }
}
