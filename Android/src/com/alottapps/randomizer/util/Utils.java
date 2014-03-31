package com.alottapps.randomizer.util;

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
}
