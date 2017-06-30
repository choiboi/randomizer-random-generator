package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.billing.IabBroadcastReceiver;
import com.alottapps.randomizer.utils.billing.IabHelper;
import com.alottapps.randomizer.utils.billing.IabResult;
import com.alottapps.randomizer.utils.billing.Inventory;
import com.alottapps.randomizer.utils.billing.Purchase;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.parse.ParseUser;

public class LoginActivity extends Activity implements IabBroadcastReceiver.IabBroadcastListener {

    // Debug.
    private final String TAG = "LoginActivity";
    
    // Members.
    private RandomizerSharedPreferences mSharedPrefs;
    private IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPrefs = new RandomizerSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if remove ads purchases have been made.
        checkInAppPurchases();
        checkPrevLogin();

        // Display notice alert if need to do so.
//        boolean showNotification = mSharedPrefs.getNoticeViewed(RandomizerSharedPreferences.KEY_DISPLAYED_2_0_NOTICE);
//        if (!showNotification) {
//            // Display alert notification.
//            startActivity(new Intent(this, NoticeDialogActivity.class));
//        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     * Bypass login if user already logged in previously.
     */
    private void checkPrevLogin() {
        mSharedPrefs.setIsAnonymousLogin(true);

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            goToMainActivity();
        } else {
            ParseUser.enableAutomaticUser();
            ParseUser.getCurrentUser().pinInBackground();
            goToMainActivity();
        }
    }

    /*
     * This method checks if any in-app purchases have been made.
     */
    private void checkInAppPurchases() {
        mHelper = new IabHelper(this, Constants.INAPP_BASE64);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                if (mHelper == null) return;

                // Query purchased inventory.
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Purchase purchase = inventory.getPurchase(Constants.SKU_REMOVE_ADS);
            boolean removeAdsPurchased = (purchase != null);
            mSharedPrefs.setRemoveAds(removeAdsPurchased);
        }
    };

    @Override
    public void receivedBroadcast() {
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }
}
