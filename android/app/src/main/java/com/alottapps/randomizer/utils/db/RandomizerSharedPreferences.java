package com.alottapps.randomizer.utils.db;


import android.content.Context;
import android.content.SharedPreferences;

public class RandomizerSharedPreferences {

    // Members.
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;

    // Constants.
    private final String PREFS_NAME = "RandomizerSharedPreferences";
//    public static final String KEY_DISPLAYED_2_0_NOTICE = "2_0_notice";
    public static final String KEY_DISPLAY_TERMINATION_NOTICE = "termination_notice";
    private final String KEY_SAVED_ANONY_USER ="saved_anony_user";
    private final String KEY_REMOVE_ADS = "remove_ads";
    private final String KEY_OBJECT_NEXT_NUM = "object_next_num";

    private final String KEY_ANONYMOUS_LOGIN = "anonymous_login";

    public RandomizerSharedPreferences(Context c) {
        mSharedPrefs = c.getSharedPreferences(PREFS_NAME, 0);
        mSharedPrefsEditor = mSharedPrefs.edit();
    }

    @Deprecated
    public boolean getAnonyUserSaved() {
        return mSharedPrefs.getBoolean(KEY_SAVED_ANONY_USER, true);
    }

    @Deprecated
    public void setAnonyUserSaved(boolean b) {
        mSharedPrefsEditor.putBoolean(KEY_SAVED_ANONY_USER, b);
        mSharedPrefsEditor.commit();
    }

    public boolean isAnonymousUser() {
        return mSharedPrefs.getBoolean(KEY_ANONYMOUS_LOGIN, false);
    }

    public void setIsAnonymousLogin(boolean b) {
        mSharedPrefsEditor.putBoolean(KEY_ANONYMOUS_LOGIN, b);
        mSharedPrefsEditor.commit();
    }

    public boolean getNoticeViewed(String key) {
        return mSharedPrefs.getBoolean(key, false);
    }

    public void setNoticedViewed(String key, boolean b) {
        mSharedPrefsEditor.putBoolean(key, b);
        mSharedPrefsEditor.commit();
    }

    public void setRemoveAds(boolean b) {
        mSharedPrefsEditor.putBoolean(KEY_REMOVE_ADS, b);
        mSharedPrefsEditor.commit();
    }

    public boolean getRemoveAds() {
        return mSharedPrefs.getBoolean(KEY_REMOVE_ADS, false);
    }

    public void setObjectIdNum(long num) {
        mSharedPrefsEditor.putLong(KEY_OBJECT_NEXT_NUM, num);
        mSharedPrefsEditor.commit();
    }

    public long getObjectIdNum() {
        return mSharedPrefs.getLong(KEY_OBJECT_NEXT_NUM, 0);
    }
}
