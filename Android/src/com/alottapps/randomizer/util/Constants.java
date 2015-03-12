package com.alottapps.randomizer.util;

public class Constants {
    
    // Constants for Intent.
    public static final String TYPE_RANDOM = "type_random";
    public static final String SELECTIONS_LIST = "selections_list";
    public static final String START_NUMBER = "start_number";
    public static final String END_NUMBER = "end_number";
    public static final String DATA_ID = "data_id";
    public static final String DATA = "data";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final int SINGLE_RANDOM = 0;
    public static final int LIST_RANDOM = 1;
    public static final int NUMBER_RANGE_RANDOM = 2;
    
    public static final String ALERT_TYPE = "alert_type ";
    public static final int ALERT_EMPTY_RANDOMIZER = 100;
    public static final int ALERT_EMPTY_SAVE = 101;
    public static final int ALERT_WHY = 102;
    public static final int ALERT_CONFIMATION = 103;
    public static final int ALERT_TY_DONATION = 104;
    public static final int ALERT_SAVED_FILE = 105;
    public static final int ALERT_SAVE_FILE_FAIL = 106;
    public static final int ALERT_SELECT_TEXT_FILE = 107;
    public static final int ALERT_NOT_TEXT_FILE = 108;
    public static final int ALERT_FILE_DNE = 109;
    public static final int ALERT_LARGE_LIST = 110;
    
    public static final String TYPE_SELECTED_RANDOMIZED = "type_selected_randomized";
    public static final int PREV_RANDOMIZED = 10;
    public static final int SAVED_LIST_RANDOMIZED = 11;
    
    // Constants for Date Formats.
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_PRESENT = "MMM. dd, yyyy";
    
    // List Parsing Constants.
    public static final String LIST_DELIMITER = "||";
    public static final String LIST_SPLIT_DELIMITER = "\\|\\|";
    
    // Skip login info.
    public static final String TEMP_EMAIL = "--none@none.com--";
    public static final String TEMP_PASS = "none";
    
    // Links for HTTP Request.
    public static final String MAIN_ADDRESS = "http://****";
    public static final String QUERY_REGISTER_USER = "/registerUser";
    public static final String QUERY_LOGIN = "/login";
    public static final String QUERY_GET_ALL_DATA = "/getAllData";
    public static final String QUERY_SAVE_DATA = "/saveData";
    public static final String QUERY_DELETE_DATA = "/deleteData";
    
    public static final String QUERY_VAR_EMAIL = "email";
    public static final String QUERY_VAR_PASSWORD = "password";
    public static final String QUERY_VAR_DATA_ID = "dataId";
    public static final String QUERY_VAR_DATA_NAME = "dataName";
    public static final String QUERY_VAR_DATA = "data";
    public static final String QUERY_VAR_DATE = "date";
    public static final String QUERY_VAR_RANDOMIZED = "randomized";
    
    public static final int HTTP_TIMEOUT = 1000 * 20; // 20 seconds.
    
    // HTTP Result Codes.
    public static final String RC_SUCCESSFUL = "100";
    public static final String RC_UNSUCCESSFUL = "888";
    public static final String RC_ERROR = "999";
    public static final String RC_USER_EXISTS = "101";
    public static final String RC_INVALID_PASS = "201";
    public static final String RC_USER_DNE = "202";
    
    // HTTP Received Data JSON Key values.
    public static final String JSON_RESULT_CODE = "result_code";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_DATA = "data";
    public static final String JSON_DATA_ID = "data_id";
    public static final String JSON_DATA_NAME = "data_name";
    public static final String JSON_DATE = "date";
    public static final String JSON_RANDOMIZED = "randomized";
    
    // Files.
    public static final String TEXTFILE_EXTENSION = ".txt";
}
