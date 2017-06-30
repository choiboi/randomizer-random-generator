package com.alottapps.randomizer.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.SavedListsActivity;
import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.Utils;

import java.util.Date;
import java.util.List;

public class SavedListAdapter extends ArrayAdapter<Data> {

    // Members.
    private List<Data> mDataList;
    private SavedListsActivity mActivity;
    private RandomizerSharedPreferences mSharedPrefs;

    public SavedListAdapter(List<Data> list, SavedListsActivity a) {
        super(a, R.layout.container_list, list);

        mDataList = list;
        mActivity = a;
        mSharedPrefs = new RandomizerSharedPreferences(mActivity);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.container_list, parent, false);
        }

        final Data data = mDataList.get(position);
        TextView nameTv = (TextView) v.findViewById(R.id.cl_list_name_textview);
        nameTv.setText(data.getDataName());
        TextView listTv = (TextView) v.findViewById(R.id.cl_list_textview);
        String dataL = Utils.strListToHtmlList(data.getData());
        listTv.setText(Html.fromHtml(dataL));
        TextView dateTv = (TextView) v.findViewById(R.id.cl_date_textview);
        Date date = data.getUpdatedAt();
        if (date != null) {
            String msg = "Last Modified on " + Utils.processDateToString(data.getUpdatedAt());
            dateTv.setText(msg);
        } else {
            dateTv.setVisibility(View.GONE);
        }

        // Check if the saved list has ID.
        if (data.getObjectId() == null) {
            long objId = mSharedPrefs.getObjectIdNum();
            data.setObjectId(Long.toString(objId++));
            data.pinInBackground();
            mSharedPrefs.setObjectIdNum(objId);
        }

        // Setup delete button.
        Button deleteBut = (Button) v.findViewById(R.id.cl_delete_button);
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.deleteListConfirmation(data.getObjectId());
            }
        });

        // Setup edit button.
        Button editBut = (Button) v.findViewById(R.id.cl_edit_button);
        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.editList(data.getObjectId(), data.getSize());
            }
        });

        Button saveBut = (Button) v.findViewById(R.id.cl_save_button);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveToFile(data.getObjectId());
            }
        });

        // Setup the whole view as a button for selection of list to load.
        // Goes to Main page with this list to load in main page.
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.loadList(data.getObjectId());
            }
        });

        return v;
    }
}
