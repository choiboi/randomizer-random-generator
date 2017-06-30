package com.alottapps.randomizer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alottapps.randomizer.utils.Constants;
import com.alottapps.randomizer.utils.billing.IabBroadcastReceiver;
import com.alottapps.randomizer.utils.billing.IabHelper;
import com.alottapps.randomizer.utils.billing.IabResult;
import com.alottapps.randomizer.utils.billing.Purchase;
import com.alottapps.randomizer.utils.db.RandomizerSharedPreferences;
import com.alottapps.randomizer.utils.util.Utils;

import java.util.Date;

public class AboutActivity extends Activity implements IabBroadcastReceiver.IabBroadcastListener {

    // Debug.
    private final String TAG = "AboutActivity";
    
    // Members.
    private IabHelper mHelper;
    private IabBroadcastReceiver mBroadcastReceiver;
    private static final int RC_REQUEST = 10001;
    private RandomizerSharedPreferences mSharedPrefs;

    private String mPayload = "";
    
    // Constants.
    private final String MAIN_TEXT_INFO = 
        "Randomizer is an application where users provide choices and " +
        "it will randomly select one or randomly order them. Users " +
        "also have the option to give a range of numbers and Randomizer will " +
        "randomly select one from the given range. This application is meant " +
        "for fun use and not recommended for serious decisions.<br><br>" +
        "Randomizer was developed by Alottapps+ThinkingWhileTrue. Feel free to " +
        "contact us with suggestions to make this application better or let us know if you " +
        "encounter any errors.<br><b>Email: </b>thinking.while.true@gmail.com<br><br>" +
        "<b>Randomizer Version 2.13</b>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        TextView mainTv = (TextView) findViewById(R.id.ab_main_textview);
        mainTv.setText(Html.fromHtml(MAIN_TEXT_INFO));

        mSharedPrefs = new RandomizerSharedPreferences(this);
        if (mSharedPrefs.getRemoveAds()) {
            findViewById(R.id.ab_remove_add_button).setVisibility(View.GONE);
        }

        mHelper = new IabHelper(this, Constants.INAPP_BASE64);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(AboutActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
            }
        });
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove broadcast receiver.
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // Destroy help object.
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // Issue with purchase.
            if (result.isFailure()) {
                Log.e(TAG, "Error purchasing: " + result);
                return;
            }

            // Verify dev payload.
            if (!mPayload.equals(purchase.getDeveloperPayload())) {
                Log.e(TAG, "Error purchasing. Authenticity verification failed.");
                String s = getResources().getString(R.string.error_with_purchase_msg);
                Toast.makeText(AboutActivity.this, s, Toast.LENGTH_SHORT).show();
                return;
            }

            if (purchase.getSku().equals(Constants.SKU_REMOVE_ADS)) {
                setResult(RESULT_OK);
                mSharedPrefs.setRemoveAds(true);
                findViewById(R.id.ab_remove_add_button).setVisibility(View.GONE);
            }
        }
    };
    
    public void onButtonClick(View v) {
        if (v.getId() == R.id.ab_back_button) {
            finish();
        } else if (v.getId() == R.id.ab_remove_add_button) {
            mPayload = Utils.base64Encode(new Date().toString());
            mHelper.launchPurchaseFlow(this, Constants.SKU_REMOVE_ADS, RC_REQUEST, mPurchaseFinishedListener, mPayload);
        }
    }

    @Override
    public void receivedBroadcast() { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        // In-case the user did not complete purchase process, handle it here.
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
