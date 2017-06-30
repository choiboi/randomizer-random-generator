package com.alottapps.randomizer.parse;

import android.os.Build;

import com.alottapps.randomizer.BuildConfig;
import com.alottapps.randomizer.utils.util.Utils;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@Deprecated
@ParseClassName("RemoveAdPurchases")
public class RemoveAdPurchases extends ParseObject {

    private static final String CLASS = "RemoveAdPurchases";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PAYLOAD = "payload";
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_PURCHASE_TOKEN = "purchase_token";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_IS_ANONYMOUS = "is_anonymous";
    private static final String KEY_APP_VERSION = "app_version";
    private static final String KEY_OS = "os";
    private static final String KEY_OS_VERSION = "os_version";
    private static final String KEY_PROVIDED_REFUND = "provided_refund";

    private static final String ANDROID = "android";

    /*
     * Formats the new purchase into ParseObject and then saves it to Parse DB.
     */
    public static void sendNewPurchase(String email, String payload, String orderId, String token) {
        RemoveAdPurchases data = new RemoveAdPurchases(email, payload, orderId, token);
        data.saveEventually();
    }

    public RemoveAdPurchases() {
        super();
    }

    public RemoveAdPurchases(String email, String payload, String orderId, String token) {
        put(KEY_EMAIL, email);
        put(KEY_PAYLOAD, payload);
        put(KEY_ORDER_ID, orderId);
        put(KEY_PURCHASE_TOKEN, token);
        put(KEY_OS, ANDROID);
        put(KEY_OS_VERSION, Build.VERSION.RELEASE);
        put(KEY_APP_VERSION, BuildConfig.VERSION_CODE);
        put(KEY_PROVIDED_REFUND, false);

        ParseUser user = ParseUser.getCurrentUser();
        put(KEY_OWNER, user);
        put(KEY_IS_ANONYMOUS, !Utils.isEmailValid(user.getUsername()));

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        setACL(acl);
    }
}
