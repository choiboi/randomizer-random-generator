package com.alottapps.randomizer.parse;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("PreviousSelectedChoiceData")
public class PreviousSelectedChoiceData extends ParseObject {

    // Debug.
    private static final String TAG = "PSCData-Parse";

    private static final String CLASS = "PreviousSelectedChoiceData";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_DATA = "data";
    private static final String KEY_SELECTED_CHOICE = "selected_choice";
    private static final String KEY_SIZE = "size";
    private static final String KEY_IS_DELETED = "is_deleted";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_IS_RANDOMIZED_NUMBER = "is_randomized_number";

    /*
     * This method retrieves all the previously selected choices from
     * Parse local store.
     */
    public static void queryAllDataFromLocalstore(FindCallback fcb) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(CLASS);
        query1.fromLocalDatastore();
        query1.whereEqualTo(KEY_OWNER, user);
        query1.whereEqualTo(KEY_IS_DELETED, false);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(CLASS);
        query2.fromLocalDatastore();
        query2.whereEqualTo(KEY_OWNER, user);
        query2.whereDoesNotExist(KEY_IS_DELETED);

        List<ParseQuery<ParseObject>> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);
        ParseQuery<ParseObject> queryFinal = ParseQuery.or(list);
        queryFinal.orderByDescending(KEY_CREATED_AT);
        queryFinal.setLimit(1000);
        queryFinal.fromLocalDatastore();
        queryFinal.findInBackground(fcb);
    }

    /*
     * Retrives all the previously selected choices from Parse remote database.
     */
    public static void retrieveAllDataFromParse(ParseUser user, FindCallback fcb) {
        // Querying for non deleted data.
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(CLASS);
        query1.whereEqualTo(KEY_OWNER, user);
        query1.whereEqualTo(KEY_IS_DELETED, false);

        // Query for data with no is_deleted info.
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(CLASS);
        query2.whereEqualTo(KEY_OWNER, user);
        query2.whereDoesNotExist(KEY_IS_DELETED);

        List<ParseQuery<ParseObject>> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);
        ParseQuery<ParseObject> queryFinal = ParseQuery.or(list);
        queryFinal.setLimit(1000);
        queryFinal.findInBackground(fcb);
    }

    /*
     * Mark all PreviousSelectedChoiceData stored locally deleted.
     */
    public static void markAllDataAsDeleted() {
        queryAllDataFromLocalstore(new FindCallback<PreviousSelectedChoiceData>() {
            @Override
            public void done(List<PreviousSelectedChoiceData> list, ParseException e) {
                if (e == null) {
                    for (PreviousSelectedChoiceData d : list) {
                        d.unpinInBackground();
                    }
                } else {
                    Log.e(TAG, "Retrieve list error: " + e.getMessage(), e);
                }
            }
        });
    }

    /*
     * Deletes every associated PreviousSelectedChoiceData from Parse local store.
     */
    public static void deletedAllData() {
        ParseObject.unpinAllInBackground();
    }

    public PreviousSelectedChoiceData() {
        super();
    }

    public PreviousSelectedChoiceData(String data, String choice, int size, boolean isRandNum) {
        setData(data);
        setSelectedChoice(choice);
        setSize(size);
        setIsDeleted(false);
        setIsRandomizedNumber(isRandNum);

        ParseUser user = ParseUser.getCurrentUser();
        put(KEY_OWNER, user);
        ParseACL acl;
        if (user != null) {
            acl = new ParseACL(user);
        } else {
            acl = new ParseACL();
        }
        acl.setPublicReadAccess(true);
        setACL(acl);
    }

    public void setData(String data) {
        put(KEY_DATA, data);
    }

    public String getData() {
        return getString(KEY_DATA);
    }

    public void setSelectedChoice(String choice) {
        put(KEY_SELECTED_CHOICE, choice);
    }

    public String getSelectedChoice() {
        return getString(KEY_SELECTED_CHOICE);
    }

    public void setSize(int size) {
        put(KEY_SIZE, size);
    }

    public int getSize() {
        return getInt(KEY_SIZE);
    }

    public void setIsDeleted(boolean b) {
        put(KEY_IS_DELETED, b);
    }

    public void setIsRandomizedNumber(boolean b) {
        put(KEY_IS_RANDOMIZED_NUMBER, b);
    }

    public boolean getIsRandomizedNumber() {
        return getBoolean(KEY_IS_RANDOMIZED_NUMBER);
    }
}
