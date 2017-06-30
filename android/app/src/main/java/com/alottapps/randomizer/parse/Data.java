package com.alottapps.randomizer.parse;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Data")
public class Data extends ParseObject {

    private static final String CLASS = "Data";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_DATA_NAME = "data_name";
    private static final String KEY_DATA = "data";
    private static final String KEY_SIZE = "size";
    private static final String KEY_IS_DELETED = "is_deleted";
    private static final String KEY_NUMBER_MODIFIED = "number_modified";

    /*
     * This will retrieve all the data from Parse local store.
     */
    public static void queryAllDataFromLocalstore(FindCallback fcb) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.fromLocalDatastore();
        query.whereEqualTo(KEY_OWNER, user);
        query.whereEqualTo(KEY_IS_DELETED, false);
        query.findInBackground(fcb);
    }

    /*
     * This will the data from Parse local store by the objectId.
     */
    public static void querySingleDataFromLocalstore(String objId, GetCallback gcb) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.fromLocalDatastore();
        query.whereEqualTo(KEY_OWNER, user);
        query.getInBackground(objId, gcb);
    }

    /*
     * Deletes every associated data from Parse local store.
     */
    public static void deletedAllData() {
        ParseObject.unpinAllInBackground();
        Data.unpinAllInBackground();
    }

    /*
     * Retrives all the data from Parse remote database.
     */
    public static void retrieveAllDataFromParse(ParseUser user, FindCallback fcb) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS);
        query.whereEqualTo(KEY_OWNER, user);
        query.findInBackground(fcb);
    }

    public Data() {
        super();
    }

    public Data(String name, String data, int size) {
        setDataName(name);
        setData(data);
        setSize(size);
        setIsDeleted(false);
        put(KEY_NUMBER_MODIFIED, 0);

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

    public void setDataName(String name) {
        put(KEY_DATA_NAME, name);
    }

    public String getDataName() {
        return getString(KEY_DATA_NAME);
    }

    public void setData(String data) {
        put(KEY_DATA, data);
    }

    public String getData() {
        return getString(KEY_DATA);
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

    public void incrementNumModifiedCount() {
        int count = getInt(KEY_NUMBER_MODIFIED) + 1;
        put(KEY_NUMBER_MODIFIED, count);
    }
}
