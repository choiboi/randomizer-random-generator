package com.alottapps.randomizer.utils;

public class Constants {
    
    // Constants for Intent.
    public static final String TYPE_RANDOM = "type_random";
    public static final String START_NUMBER = "start_number";
    public static final String END_NUMBER = "end_number";
    public static final String DATA = "data";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String SELECTION_OBJ_ID = "selection_obj_id";
    public static final String SELECTIONS_LIST = "selections_list";
    public static final String SELECTIONS_LIST_NAME = "selections_list_name";

    public static final int SINGLE_RANDOM = 0;
    public static final int LIST_RANDOM = 1;
    public static final int NUMBER_RANGE_RANDOM = 2;

    // Intent codes for getting list file name.
    public static final int RANDOMIZED_LIST = 1;
    public static final String LIST_TYPE = "list_type";

    // Intent codes for alert.
    public static final String ALERT_TYPE = "alert_type ";
    public static final String ALERT_INTENT_LIST = "intent_list";
    public static final int ALERT_EMPTY_RANDOMIZER = 100;
    public static final int ALERT_EMPTY_SAVE = 101;
    public static final int ALERT_WHY = 102;
    public static final int ALERT_CONFIMATION = 103;
    public static final int ALERT_SAVED_FILE = 105;
    public static final int ALERT_SAVE_FILE_FAIL = 106;
    public static final int ALERT_SELECT_TEXT_FILE = 107;
    public static final int ALERT_LARGE_LIST = 110;
    public static final int ALERT_NOT_LOGIN_WARNING = 111;
    public static final int ALERT_ALL_DELETE_CONFIMATION = 112;
    public static final int ALERT_INVALID_FILENAME = 113;
    public static final int ALERT_DISPLAY_LIST = 114;
    
    public static final String TYPE_SELECTED_RANDOMIZED = "type_selected_randomized";
    public static final int PREV_RANDOMIZED = 10;
    public static final int SAVED_LIST_RANDOMIZED = 11;
    
    // Constants for Date Formats.
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_PRESENT = "MMM. dd, yyyy";
    
    // List Parsing Constants.
    public static final String LIST_DELIMITER = "||";
    public static final String LIST_SPLIT_DELIMITER = "\\|\\|";
    public static final String NUMBER_BTW_SPLIT = "to";

    // Files.
    public static final String DEFAULT_LIST_NAME = "List";
    public static final String TEXTFILE_EXTENSION = ".txt";

    // In-app billing.
    public static final String INAPP_BASE64 = "<BASE64 string>":
    public static final String SKU_REMOVE_ADS = "remove_ads";

}
