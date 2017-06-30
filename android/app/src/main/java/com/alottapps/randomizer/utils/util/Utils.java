package com.alottapps.randomizer.utils.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.alottapps.randomizer.R;
import com.alottapps.randomizer.utils.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {
	
	// Constants.
    private static final int MAX_NUMBER_LINES = 4;

    public static String listToString(ArrayList<String> selections) {
        String str = "";
        
        for (int i = 0; i < selections.size(); i++) {
            if (i == selections.size() - 1) {
                str += selections.get(i);
            } else {
                str += selections.get(i) + Constants.LIST_DELIMITER;
            }
        }
        
        return str;
    }
    
    public static ArrayList<String> stringToList(String selections) {
        ArrayList<String> list = new ArrayList<String>();
        String[] selectionList = selections.split(Constants.LIST_SPLIT_DELIMITER);
        
        for (int i = 0; i < selectionList.length; i++) {
            list.add(selectionList[i]);
        }
        
        return list;
    }
    
    public static String dbStrListToTextStr(String str) {
        while (str.contains(Constants.LIST_DELIMITER)) {
            str = str.replace(Constants.LIST_DELIMITER, "\n");
        }
        return str;
    }
    
    public static String strListToHtmlList(String s) {
        String htmlStr = "";
        String[] l = s.split(Constants.LIST_SPLIT_DELIMITER);
        
        for (int i = 0; i < l.length; i++) {
            if (i == l.length - 1) {
                htmlStr += "<b>" + (i + 1) + ".</b> " + l[i];
            } else {
                htmlStr += "<b>" + (i + 1) + ".</b> " + l[i] + "<br>";
            }
            
            if (i > MAX_NUMBER_LINES && l.length > MAX_NUMBER_LINES + 2) {
            	htmlStr += "<b>&#32;&#32;&#46;&#46;&#46;</b>";
            	break;
            }
        }
        
        return htmlStr;
    }
    
    public static int getListSize(String s) {
    	String[] l = s.split(Constants.LIST_SPLIT_DELIMITER);
    	return l.length;
    }

    /*
     * Convert Parse Date to String version with the format MMM. dd, yyyy.
     */
    public static String processDateToString(Date date) {
        try {
            SimpleDateFormat sdf_res = new SimpleDateFormat(Constants.DATE_FORMAT_PRESENT, Locale.US);
            return sdf_res.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<String> readFromFile(Uri uri, Context c) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            InputStream inputStream = c.getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (!receiveString.equals("")) {
                        list.add(receiveString);
                    }
                }

                bufferedReader.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(c, c.getResources().getText(R.string.alert_file_dne_message), Toast.LENGTH_LONG).show();
            Log.e("Utils", "File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            Toast.makeText(c, c.getResources().getText(R.string.something_went_wrong_textfile_message), Toast.LENGTH_LONG).show();
            Log.e("Utils", "io exception: " + e.getMessage(), e);
        }

        return list;
    }

    /*
     * Checks if the filename that the user wants to save it to is proper.
     */
    public static boolean checkSaveFilenameString(String filename) {
        return filename.contains("/");
    }

    /*
     * Converts provided string str into base64 encryption.
     */
    public static String base64Encode(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    /*
     * Check if email is valid. Only check if it has an @ symbol.
     */
    public static boolean isEmailValid(String email) {
        if (email.contains("@")) {
            String domain = email.substring(email.indexOf('@'), email.length());
            if (domain.contains(".")) {
                return true;
            }
        }
        return false;
    }
}
