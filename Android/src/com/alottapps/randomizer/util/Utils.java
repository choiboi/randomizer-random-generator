package com.alottapps.randomizer.util;

import java.util.ArrayList;

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
        String[] selectionList= selections.split(Constants.LIST_DELIMITER);
        
        for (int i = 0; i < selectionList.length; i++) {
            list.add(selectionList[i]);
        }
        
        return list;
    }
}
