package com.alottapps.randomizer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class Utils {

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
    
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        String date = sdf.format(new Date());
        return date;
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
        }
        
        return htmlStr;
    }
    
    public static String processDateString(String date) {
        SimpleDateFormat sdf  = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        Date d = null;
        try {
            d = sdf.parse(date);
            SimpleDateFormat sdf_res = new SimpleDateFormat(Constants.DATE_FORMAT_PRESENT, Locale.US);
            return sdf_res.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getResultCode(byte[] data) {
        String resultCode = "";
        try {
            String resultStr = new String(data, "UTF-8");
            JSONObject json = new JSONObject(resultStr);
            resultCode = json.getString(Constants.JSON_RESULT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultCode;
    }
    
    public static JSONArray getResultData(byte[] data) {
        JSONArray jsonArray = null;
        try {
            String resultStr = new String(data, "UTF-8");
            JSONObject json = new JSONObject(resultStr);
            jsonArray = json.getJSONArray(Constants.JSON_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return jsonArray;
    }
    
    public static boolean skippedLogin(DatabaseHandler db) {
        if (db.getUserEmail().equals(Constants.TEMP_EMAIL)) {
            return true;
        }
        return false;
    }
    
    public static String getFilePathFromUri(Intent data, Context context) {
        Uri imgUri = data.getData();
        String filePath = "";
        
        if (SystemUtils.isAtLeastOSKitKat()) {
            String wholeID = DocumentsContract.getDocumentId(imgUri);
			String partialPath = wholeID.split(":")[1];
			filePath = SystemUtils.getOutputLinkWithPartial(partialPath);
        } else if (data.getType() == null) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        
        if (filePath.equals("")) {
            filePath = imgUri.getPath();
        }
        
        return filePath;
    }
    
    public static ArrayList<String> readFromFile(String filePath, Context context) {
        ArrayList<String> list = new ArrayList<String>();
         
        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
             
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                 
                while ((receiveString = bufferedReader.readLine()) != null) {
                    if (!receiveString.equals("")) {
                        list.add(receiveString);
                    }
                }
                
                bufferedReader.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return list;
    }
    
    public static String encryptString(String str) {
        /*
         * Removed for security reasons.
         */
        return str;
    }
}
