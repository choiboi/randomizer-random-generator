package com.alottapps.randomizer.parse;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("SavedRandomizedListData")
public class SavedRandomizedListData extends ParseObject {

    private static final String CLASS = "SavedRandomizedListData";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_ORIGIN_DATA = "origin_data";
    private static final String KEY_RANDOMIZED_DATA = "randomized_data";
    private static final String KEY_SIZE = "size";
    private static final String KEY_IS_DELETED = "is_deleted";

    /*
     * This method retrieves all the previously randomized lists from
     * Parse local store.
     */
    public static void queryAllDataFromLocalstore(FindCallback fcb) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.fromLocalDatastore();
        query.whereEqualTo(KEY_OWNER, user);
        query.findInBackground(fcb);
    }

    /*
     * This will get the data from Parse local store by the objectId.
     */
    public static void querySingleDataFromLocalstore(String objId, GetCallback gcb) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.fromLocalDatastore();
        query.whereEqualTo(KEY_OWNER, user);
        query.whereEqualTo(KEY_IS_DELETED, false);
        query.getInBackground(objId, gcb);
    }

    /*
     * This method retrieves all the previously randomized lists from
     * Parse local store.
     */
    public static void retrieveAllDataFromParse(ParseUser user, FindCallback fcb) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.whereEqualTo(KEY_OWNER, user);
        query.whereEqualTo(KEY_IS_DELETED, false);
        query.findInBackground(fcb);
    }

    /*
     * Deletes every associated SavedRandomizedListData from Parse local store.
     */
    public static void deletedAllData() {
        ParseObject.unpinAllInBackground();
    }

    public SavedRandomizedListData() {
        super();
    }

    public SavedRandomizedListData(String originalData, String randData, int size) {
        setOriginalData(originalData);
        setRandomizedData(randData);
        setSize(size);
        setIsDeleted(false);

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

    public void setOriginalData(String data) {
        put(KEY_ORIGIN_DATA, data);
    }

    public String getOriginalData() {
        return getString(KEY_ORIGIN_DATA);
    }

    public void setRandomizedData(String data) {
        put(KEY_RANDOMIZED_DATA, data);
    }

    public String getRandomizedData() {
        return getString(KEY_RANDOMIZED_DATA);
    }

    public void setSize(int size) {
        put(KEY_SIZE, size);
    }

    public int getSize() {
        return getInt(KEY_SIZE);
    }

    public void setIsDeleted(boolean isDeleted) {
        put(KEY_IS_DELETED, isDeleted);
    }
}
