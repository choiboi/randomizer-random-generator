package com.alottapps.randomizer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // DB INFOMATION CONSTANTS.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RandomizerDatabase";
    private static final String TABLE_USER = "User";
    private static final String TABLE_DATA = "Data";
    private static final String TABLE_PREV_SELECTIONS = "PreviousSelections";
    
    // TABLE COLUMNS.
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_DATA_ID = "data_id";
    private static final String KEY_DATA = "data";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final String KEY_RANDOMIZED = "randomized";
    private static final String KEY_SELECTED_VALUE = "selected_value";
    private static final String KEY_DATE = "date";
    
    // OTHER CONSTANTS.
    private final int MAX_NUMBER_PREV_CHOICES = 30;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create user info table.
        String createUserTableQry = "CREATE TABLE " + TABLE_USER + "(" +
                                    KEY_EMAIL + " TEXT," + 
                                    KEY_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQry);
        
        // Create data table.
        String dataTableQry = "CREATE TABLE " + TABLE_DATA + "(" +
                              KEY_DATA_ID + " TEXT," +
                              KEY_EMAIL + " TEXT," +
                              KEY_DATA + " TEXT," + 
                              KEY_LAST_UPDATE + " TEXT," + 
                              KEY_RANDOMIZED + " INT)";
        db.execSQL(dataTableQry);
        
        // Create previous data table.
        String prevDataTabelQry = "CREATE TABLE " + TABLE_PREV_SELECTIONS + "(" +
                                  KEY_EMAIL + " TEXT," +
                                  KEY_DATA + " TEXT," + 
                                  KEY_SELECTED_VALUE + " TEXT," + 
                                  KEY_DATE + " TEXT)";
        db.execSQL(prevDataTabelQry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREV_SELECTIONS);
        onCreate(db);
    }
    
    /*
     * Handle all User table related queries here.
     */
    public void addUser(String email, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, pass);
        
        db.insert(TABLE_USER, null, values);
        db.close();
    }
    
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_USER + ";";
        db.execSQL(deleteQry);
        db.close();
    }
    
    private String getUserEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = new String[]{ KEY_EMAIL };
        Cursor cursor = db.query(TABLE_USER, columns, null, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return "EmailNotProvided";
        }
        db.close();
        return cursor.getString(0);
    }
    
    /*
     * Handles all Data table related queries here.
     */
    public void addData(String data, String date, int randomized) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA_ID, "android-" + RandomGenerator.randomIDString());
        values.put(KEY_EMAIL, getUserEmail());
        values.put(KEY_DATA, data);
        values.put(KEY_RANDOMIZED, randomized);
        values.put(KEY_LAST_UPDATE, date);
        
        db.insert(TABLE_DATA, null, values);
        db.close();
    }
    
    public Cursor retrieveSavedData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_EMAIL, KEY_DATA, KEY_LAST_UPDATE };
        String condition = KEY_RANDOMIZED + "=?";
        String[] compare = new String[]{ "1" };
        Cursor cursor = db.query(TABLE_USER, columns, condition, compare, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        db.close();
        return cursor;
    }
    
    public Cursor retrieveListData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_EMAIL, KEY_DATA, KEY_LAST_UPDATE };
        String condition = KEY_RANDOMIZED + "=?";
        String[] compare = new String[]{ "0" };
        Cursor cursor = db.query(TABLE_USER, columns, condition, compare, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        db.close();
        return cursor;
    }
    
    public void updateListData(String id, String email, String newData) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA, newData);
        String whereCond = KEY_DATA_ID + "=? AND " + KEY_EMAIL + "=?";
        String[] whereArgs = new String[]{ id, email };
        db.update(TABLE_DATA, values, whereCond, whereArgs);
        
        db.close();
    }
    
    public void deleteData(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DATA, KEY_DATA_ID + "=?", new String[]{ id });
        db.close();
    }
    
    /*
     * Handles all Previous Data table related queries here.
     */
    public void addPrevData(String data, String date, String selectedData) {
        removePrevDataTableIfMax();
        
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, getUserEmail());
        values.put(KEY_DATA, data);
        values.put(KEY_SELECTED_VALUE, selectedData);
        values.put(KEY_DATE, date);
        
        db.insert(TABLE_DATA, null, values);
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
}
