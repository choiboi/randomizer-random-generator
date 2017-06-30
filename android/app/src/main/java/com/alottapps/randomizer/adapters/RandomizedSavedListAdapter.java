package com.alottapps.randomizer.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.ShowSavedRandomizedActivity;
import com.alottapps.randomizer.dialog.AlertDialogActivity;
import com.alottapps.randomizer.parse.SavedRandomizedListData;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.Utils;

import java.util.Date;
import java.util.List;

public class RandomizedSavedListAdapter extends ArrayAdapter<SavedRandomizedListData> {

    // Members.
    private List<SavedRandomizedListData> mDataList;
    private ShowSavedRandomizedActivity mActivity;
    private RandomizerSharedPreferences mSharedPrefs;

    public RandomizedSavedListAdapter(List<SavedRandomizedListData> list, ShowSavedRandomizedActivity a) {
        super(a, R.layout.container_list_randomized, list);

        mDataList = list;
        mActivity = a;
        mSharedPrefs = new RandomizerSharedPreferences(mActivity);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.container_list_randomized, parent, false);
        }

        final SavedRandomizedListData d = mDataList.get(position);
        TextView selectionTv = (TextView) convertView.findViewById(R.id.clr_list_textview);
        String htmlStr = Utils.strListToHtmlList(d.getRandomizedData());
        selectionTv.setText(Html.fromHtml(htmlStr));
        TextView dateTv = (TextView) convertView.findViewById(R.id.clr_date_textview);
        Date date = d.getUpdatedAt();
        if (date != null) {
            String str = "Randomized on " + Utils.processDateToString(d.getCreatedAt());
            dateTv.setText(str);
        } else {
            dateTv.setVisibility(View.GONE);
        }

        // Give each randomized saved lists an ID if they don't have.
        if (d.getObjectId() == null) {
            long id = mSharedPrefs.getObjectIdNum();
            d.setObjectId(Long.toString(id++));
            d.pinInBackground();
            mSharedPrefs.setObjectIdNum(id);
        }

        // Create delete button listener.
        Button deleteBut = (Button) convertView.findViewById(R.id.clr_delete_button);
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mActivity.deleteListConfirmation(d.getObjectId());
            }
        });
        // Create save to file button listener.
        Button saveBut = (Button) convertView.findViewById(R.id.clr_save_button);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mActivity.saveToFile(d.getObjectId());
            }
        });

        // Create view button if list has more than 5 lines.
        Button viewBut = (Button) convertView.findViewById(R.id.clr_view_button);
        if (d.getSize() <= 5) {
            viewBut.setVisibility(View.GONE);
        } else {
            viewBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, AlertDialogActivity.class);
                    intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_DISPLAY_LIST);
                    intent.putExtra(Constants.ALERT_INTENT_LIST, d.getRandomizedData());
                    mActivity.startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
