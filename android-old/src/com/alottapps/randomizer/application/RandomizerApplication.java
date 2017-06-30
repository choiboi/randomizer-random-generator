package com.alottapps.randomizer.application;

import com.alottapps.randomizer.util.DatabaseHandler;

import android.app.Application;

public class RandomizerApplication extends Application {
    private DatabaseHandler mDB;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mDB = new DatabaseHandler(this);
    }
    
    public DatabaseHandler getDB() {
        return mDB;
    }
}
