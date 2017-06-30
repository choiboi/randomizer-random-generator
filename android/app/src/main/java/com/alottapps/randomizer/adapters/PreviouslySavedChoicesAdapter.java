package com.alottapps.randomizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.parse.PreviousSelectedChoiceData;
import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.Utils;

import java.util.Date;
import java.util.List;

public class PreviouslySavedChoicesAdapter extends ArrayAdapter<PreviousSelectedChoiceData> {

    // Members.
    private List<PreviousSelectedChoiceData> mDataList;
    private Context mContext;
    private RandomizerSharedPreferences mSharedPrefs;

    public PreviouslySavedChoicesAdapter(Context context, List<PreviousSelectedChoiceData> list) {
        super(context, R.layout.container_selection_randomized, list);

        mDataList = list;
        mContext = context;
        mSharedPrefs = new RandomizerSharedPreferences(mContext);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.container_selection_randomized, parent, false);
        }

        final PreviousSelectedChoiceData d = mDataList.get(position);
        TextView selectionTv = (TextView) convertView.findViewById(R.id.csl_selection_textview);

        // Assign IDs to saved choices if they don't have one.
        if (d.getObjectId() == null) {
            long id = mSharedPrefs.getObjectIdNum();
            d.setObjectId(Long.toString(id++));
            d.pinInBackground();
            mSharedPrefs.setObjectIdNum(id);
        }

        selectionTv.setText(d.getSelectedChoice());
        TextView dataTv = (TextView) convertView.findViewById(R.id.csl_data_textview);
        String values = d.getData();
        if (d.getIsRandomizedNumber()) {
            String[] range = d.getData().split(Constants.NUMBER_BTW_SPLIT);
            values = "Between " + range[0] + " and " + range[1];
        } else {
            if (values.contains(Constants.LIST_DELIMITER)) {
                values = values.replace(Constants.LIST_DELIMITER, ", ");
            }
        }
        String selectedText = "Selected From: " + values;
        dataTv.setText(selectedText);
        TextView dateTv = (TextView) convertView.findViewById(R.id.csl_date_textview);
        Date date = d.getCreatedAt();
        if (date != null) {
            dateTv.setText(Utils.processDateToString(date));
        } else {
            dateTv.setVisibility(View.GONE);
        }

        Button deleteBut = (Button) convertView.findViewById(R.id.csl_delete_button);
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mark this selection as deleted.
                d.setIsDeleted(true);
                d.unpinInBackground();
                d.saveInBackground();

                // Remove item from list.
                mDataList.remove(d);
                notifyDataSetChanged();
                notifyDataSetInvalidated();
            }
        });

        return convertView;
    }
}
