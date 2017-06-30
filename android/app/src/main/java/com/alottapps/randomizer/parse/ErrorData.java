package com.alottapps.randomizer.parse;

import android.os.Build;

import com.alottapps.randomizer.BuildConfig;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@Deprecated
@ParseClassName("ErrorData")
public class ErrorData extends ParseObject {

    private static final String KEY_ERROR = "error";
    private static final String KEY_ERROR_MSG = "error_message";
    private static final String KEY_ERROR_ADDTIONAL_INFO = "error_addtional_info";
    private static final String KEY_OS = "os";
    private static final String KEY_OS_VERSION = "os_version";
    private static final String KEY_APP_VERSION = "app_version";

    private static final String ANDROID = "android";

    /*
     * Formats the new error into ParseObject and then saves it to Parse DB.
     */
    public static void sendNewError(String error, String msg, String addInfo) {
        ErrorData data = new ErrorData(error, msg, addInfo);
        data.saveEventually();
    }

    public ErrorData() {
        super();
    }

    public ErrorData(String error, String msg, String addInfo) {
        put(KEY_ERROR, error);
        put(KEY_ERROR_MSG, msg);
        put(KEY_ERROR_ADDTIONAL_INFO, addInfo);
        put(KEY_OS, ANDROID);
        put(KEY_OS_VERSION, Build.VERSION.RELEASE);
        put(KEY_APP_VERSION, BuildConfig.VERSION_CODE);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        setACL(acl);
    }
}
