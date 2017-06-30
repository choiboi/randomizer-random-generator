package com.alottapps.randomizer.application;

import android.app.Application;

import com.alottapps.randomizer.parse.Data;
import com.alottapps.randomizer.parse.PreviousSelectedChoiceData;
import com.alottapps.randomizer.parse.RemoveAdPurchases;
import com.alottapps.randomizer.parse.SavedRandomizedListData;
import com.alottapps.randomizer.utils.db.DatabaseHandler;
import com.parse.Parse;
import com.parse.ParseObject;

public class RandomizerApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Register ParseObject subclasses.
        ParseObject.registerSubclass(Data.class);
        ParseObject.registerSubclass(PreviousSelectedChoiceData.class);
        ParseObject.registerSubclass(SavedRandomizedListData.class);
        Parse.initialize(this, "<Parse API Key>", "<Parse API Key>");

        // Just start it to remove existing dbs.
        new DatabaseHandler(this);
    }
}
