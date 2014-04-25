package com.alottapps.randomizer.util;

import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class SystemUtils {
    
    // Debugging and file system naming purposes.
    private final static String APP_NAME = "Randomizer";

    public static boolean hasDataConnection(Activity a) {
        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    
    public static File getOutputMediaFile(String filename) {
        return new File(getOutputLink(filename));
    }

    public static String getOutputLink(String filename) {
        String directory = "";

        // Check if storage is mounted.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), APP_NAME);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(APP_NAME, "Failed to Create Directory!!");
                    return null;
                }
            }
            directory = mediaStorageDir.getPath() + File.separator + filename;
        }

        return directory;
    }
    
    public static boolean saveTextFile(File file, String text) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(text);
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
