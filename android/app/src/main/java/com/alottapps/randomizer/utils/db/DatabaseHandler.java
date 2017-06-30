package com.alottapps.randomizer.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.util.RandomGenerator;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    // DB INFOMATION CONSTANTS.
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "RandomizerDatabase";
    private static final String TABLE_USER = "User";
    private static final String TABLE_DATA = "Data";
    private static final String TABLE_PREV_SELECTIONS = "PreviousSelections";
    
    // TABLE COLUMNS.
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DATA_NAME = "data_name";
    private static final String KEY_DATA_ID = "data_id";
    private static final String KEY_DATA = "data";
    private static final String KEY_SAVED_TO_SERVER = "saved_to_server";
    private static final String KEY_RANDOMIZED = "randomized";
    private static final String KEY_SELECTED_VALUE = "selected_value";
    private static final String KEY_DATE = "date";
    
    // OTHER CONSTANTS.
    private final int MAX_NUMBER_PREV_CHOICES = 30;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREV_SELECTIONS);
    }


    public String getUserEmail() {
        return ParseUser.getCurrentUser().getUsername();
    }

    /*
     * Handles all Data table related queries here.
     */
    @Deprecated
    public String addData(String data, String date, int randomized, String name) {
        String dataId = "android-" + RandomGenerator.randomIDString();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, getUserEmail());
        values.put(KEY_DATA_ID, dataId);
        values.put(KEY_DATA_NAME, name);
        values.put(KEY_DATA, data);
        values.put(KEY_RANDOMIZED, randomized);
        values.put(KEY_DATE, date);
        values.put(KEY_SAVED_TO_SERVER, 0);
        insertData(values);
        return dataId;
    }

    @Deprecated
    public String addData(String dataId, String data, String date, int randomized, String name, int toServer) {
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, getUserEmail());
        values.put(KEY_DATA_ID, dataId);
        values.put(KEY_DATA_NAME, name);
        values.put(KEY_DATA, data);
        values.put(KEY_RANDOMIZED, randomized);
        values.put(KEY_DATE, date);
        values.put(KEY_SAVED_TO_SERVER, toServer);
        insertData(values);
        
        return dataId;
    }
    
    private void insertData(ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DATA, null, cv);
        db.close();
    }

    @Deprecated
    public Cursor retrieveSavedData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA, KEY_DATE };
        String condition = KEY_RANDOMIZED + "=? AND " + KEY_EMAIL + "=?";
        String[] compare = new String[]{ "1", getUserEmail() };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);
        return cursor;
    }

    @Deprecated
    public Cursor retrieveListData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA_NAME, KEY_DATA, KEY_DATE };
        String condition = KEY_RANDOMIZED + "=? AND " + KEY_EMAIL + "=?";
        String[] compare = new String[]{ "0", getUserEmail() };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);
        return cursor;
    }

    @Deprecated
    public void updateListData(String id, String newData, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA, newData);
        values.put(KEY_DATA_NAME, newName);
        values.put(KEY_SAVED_TO_SERVER, 0);
        String whereCond = KEY_DATA_ID + "=? AND " + KEY_EMAIL + "=?";
        String[] whereArgs = new String[]{ id, getUserEmail() };
        db.update(TABLE_DATA, values, whereCond, whereArgs);
        
        db.close();
    }

    @Deprecated
    public void updateSavedToServer(String id, int saved) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_SAVED_TO_SERVER, saved);
        String whereCond = KEY_DATA_ID + "=? AND " + KEY_EMAIL + "=?";
        String[] whereArgs = new String[]{ id, getUserEmail() };
        db.update(TABLE_DATA, values, whereCond, whereArgs);
        
        db.close();
    }

    @Deprecated
    public Cursor getDataByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA_NAME, KEY_DATA, KEY_DATE, KEY_RANDOMIZED };
        String condition = KEY_DATA_ID + "=?";
        String[] compare = new String[]{ id };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);

        return cursor;
    }

    @Deprecated
    public Cursor getNotSavedToDBData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA_NAME, KEY_DATA, KEY_DATE, KEY_RANDOMIZED };
        String condition = KEY_SAVED_TO_SERVER + "=?";
        String[] compare = new String[]{ "0" };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);
        
        return cursor;
    }

    @Deprecated
    public void deleteSingleData(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DATA, KEY_DATA_ID + "=?", new String[]{ id });
        db.close();
    }

    @Deprecated
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_DATA + " WHERE " + KEY_EMAIL + "='" + getUserEmail() + "';";
        db.execSQL(deleteQry);
        db.close();
    }
    
    /*
     * Handles all Previous Data table related queries here.
     */
    @Deprecated
    public void addPrevData(String data, String date, String selectedData) {
        removePrevDataTableIfMax();
        
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA, data);
        values.put(KEY_SELECTED_VALUE, selectedData);
        values.put(KEY_DATE, date);
        
        db.insert(TABLE_PREV_SELECTIONS, null, values);
        db.close();
    }
    
    private void removePrevDataTableIfMax() {
        SQLiteDatabase db = getWritableDatabase();
        String getAllQry = "SELECT * FROM " + TABLE_PREV_SELECTIONS;
        Cursor cursor = db.rawQuery(getAllQry, null);
        
        if (cursor.getCount() >= MAX_NUMBER_PREV_CHOICES) {
            if (cursor.moveToFirst()) {
                String tempDate = "";
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
                Date prevDate = null;
                
                try {
                    while (cursor.moveToNext()) {
                        if (tempDate.equals("")) {
                            tempDate = cursor.getString(3);
                            prevDate = sdf.parse(tempDate);
                        } else {
                            Date currDate = sdf.parse(cursor.getString(3));
                            if (prevDate.after(currDate)) {
                                tempDate = cursor.getString(3);
                                prevDate = sdf.parse(tempDate);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Delete the oldest entry.
                db.delete(TABLE_PREV_SELECTIONS, KEY_DATE + "=?", new String[]{ tempDate });
            }
        }
        db.close();
    }

    @Deprecated
    public Cursor getAllPrevData() {
        SQLiteDatabase db = getReadableDatabase();
        String getAllQry = "SELECT * FROM " + TABLE_PREV_SELECTIONS + ";";
        Cursor cursor = db.rawQuery(getAllQry, null);
        return cursor;
    }

    @Deprecated
    public void deleteAllPrevData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_PREV_SELECTIONS + ";";
        db.execSQL(deleteQry);
        db.close();
    }
}
