package com.alottapps.randomizer.util;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    
    public static String encryptString(String str) {
        StringBuffer hexString = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            // convert the byte to hex format method 2
            hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hexString.toString();
    }
}
