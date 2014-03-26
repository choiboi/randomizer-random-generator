package com.alottapps.randomizer.util;

import android.app.Application;

public class RandomizerApplication extends Application {
    private DatabaseHandler mDB;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mDB = new DatabaseHandler(this);
    }
    
    public DatabaseHandler getUserDB() {
        return mDB;
    }
}
