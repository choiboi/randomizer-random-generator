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
    public void onCreate(SQLiteDatabase db) {
        // Create user info table.
        String createUserTableQry = "CREATE TABLE " + TABLE_USER + "(" +
                                    KEY_EMAIL + " TEXT," + 
                                    KEY_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQry);
        
        // Create data table.
        String dataTableQry = "CREATE TABLE " + TABLE_DATA + "(" +
                              KEY_DATA_ID + " TEXT," +
                              KEY_DATA_NAME + " TEXT," +
                              KEY_DATA + " TEXT," + 
                              KEY_DATE + " TEXT," + 
                              KEY_RANDOMIZED + " INT," +
                              KEY_SAVED_TO_SERVER + " INT)";
        db.execSQL(dataTableQry);
        
        // Create previous data table.
        String prevDataTabelQry = "CREATE TABLE " + TABLE_PREV_SELECTIONS + "(" +
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
    
    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String getQry = "SELECT * FROM " + TABLE_USER + ";";
        return db.rawQuery(getQry,  null);
    }
    
    public String getUserEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        String getQry = "SELECT * FROM " + TABLE_USER + ";";
        Cursor c = db.rawQuery(getQry, null);
        
        if (c.moveToFirst()) {
            return c.getString(0);
        }
        return null;
    }
    
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_USER + ";";
        db.execSQL(deleteQry);
        db.close();
    }
    
    /*
     * Handles all Data table related queries here.
     */
    public String addData(String data, String date, int randomized, String name) {
        String dataId = "android-" + RandomGenerator.randomIDString();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA_ID, dataId);
        values.put(KEY_DATA_NAME, name);
        values.put(KEY_DATA, data);
        values.put(KEY_RANDOMIZED, randomized);
        values.put(KEY_DATE, date);
        values.put(KEY_SAVED_TO_SERVER, 0);
        insertData(values);
        return dataId;
    }
    
    public String addData(String dataId, String data, String date, int randomized, String name, int toServer) {
        ContentValues values = new ContentValues();
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
    
    public Cursor retrieveSavedData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA, KEY_DATE };
        String condition = KEY_RANDOMIZED + "=?";
        String[] compare = new String[]{ "1" };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);
        return cursor;
    }
    
    public Cursor retrieveListData() {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA_NAME, KEY_DATA, KEY_DATE };
        String condition = KEY_RANDOMIZED + "=?";
        String[] compare = new String[]{ "0" };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);
        return cursor;
    }
    
    public void updateListData(String id, String newData) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATA, newData);
        String whereCond = KEY_DATA_ID + "=?";
        String[] whereArgs = new String[]{ id };
        db.update(TABLE_DATA, values, whereCond, whereArgs);
        
        db.close();
    }
    
    public Cursor getDataByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        
        String[] columns = new String[]{ KEY_DATA_ID, KEY_DATA_NAME, KEY_DATA, KEY_DATE};
        String condition = KEY_DATA_ID + "=?";
        String[] compare = new String[]{ id };
        Cursor cursor = db.query(TABLE_DATA, columns, condition, compare, null, null, null, null);

        return cursor;
    }
    
    public void deleteSingleData(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DATA, KEY_DATA_ID + "=?", new String[]{ id });
        db.close();
    }
    
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_DATA + ";";
        db.execSQL(deleteQry);
        db.close();
    }
    
    /*
     * Handles all Previous Data table related queries here.
     */
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
    
    public Cursor getAllPrevData() {
        SQLiteDatabase db = getReadableDatabase();
        String getAllQry = "SELECT * FROM " + TABLE_PREV_SELECTIONS;
        Cursor cursor = db.rawQuery(getAllQry, null);
        return cursor;
    }
    
    public void deleteAllPrevData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQry = "DELETE FROM " + TABLE_PREV_SELECTIONS + ";";
        db.execSQL(deleteQry);
        db.close();
    }
}
