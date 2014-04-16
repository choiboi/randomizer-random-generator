package com.alottapps.randomizer;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alottapps.randomizer.billing.IabHelper;
import com.alottapps.randomizer.billing.IabResult;
import com.alottapps.randomizer.billing.Purchase;
import com.alottapps.randomizer.util.Constants;

public class DonationDialogActivity extends Activity {
    
    // Members.
    private RadioGroup mMainRg;
    private IabHelper mHelper;
    private String mPayload;
    
    // Constants.
    private final String TAG = "DonationDialogActivity";
    public static final String SKU_1_DONATION = "one_dollar_donation";
    public static final String SKU_2_DONATION = "two_dollar_donation";
    public static final String SKU_3_DONATION = "three_dollar_donation";
    public static final int RC_REQUEST = 111111;
    private final int PAYLOAD_LEN = 40;
    
    // Developer key removed for security reasons.
    private final String KEY_1 = "****";
    private final String KEY_2 = "****";
    private final String KEY_3 = "****";
    private final String KEY_4 = "****";
    
    
    // For developer Payload.
    private static final char[] symbols = new char[36];
    static {
        for (int idx = 0; idx < 10; ++idx)
            symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            symbols[idx] = (char) ('a' + idx - 10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_dialog);
        
        mMainRg = (RadioGroup) findViewById(R.id.add_radio_group);
        
        // Setup Payment System.
        String base64EncodedPublicKey = KEY_1 + KEY_2 + KEY_3 + KEY_4;
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    Toast.makeText(DonationDialogActivity.this, "Setting-Up In-App Purchase Gone Wrong.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.d(TAG, "In-app Billing Set Up: " + result);
                }
            }
        });
        
        mPayload = "";
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindBilling();
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.add_cancel_button) {
            finish();
        } else if (v.getId() == R.id.add_donate_button) {
            RandomString randomString = new RandomString(PAYLOAD_LEN);
            mPayload = randomString.nextString();
            int selected = mMainRg.getCheckedRadioButtonId();
            if (selected == R.id.add_one_dollar_radio) {
                mHelper.launchPurchaseFlow(this, SKU_1_DONATION, RC_REQUEST, mPurchaseFinishedListener, mPayload);
            } else if (selected == R.id.add_two_dollar_radio) {
                mHelper.launchPurchaseFlow(this, SKU_2_DONATION, RC_REQUEST, mPurchaseFinishedListener, mPayload);
            } else if (selected == R.id.add_three_dollar_radio) {
                mHelper.launchPurchaseFlow(this, SKU_3_DONATION, RC_REQUEST, mPurchaseFinishedListener, mPayload);
            }
            
            finish();
        }
    }
    
    private void unbindBilling() {
        // Unbind in-app billing services.
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }
    
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                Toast.makeText(DonationDialogActivity.this, "Error purchasing: " + result, Toast.LENGTH_LONG).show();
                return;
            }
            
            if (!verifyDeveloperPayload(purchase)) {
                Log.d(TAG, "Error purchasing. Authenticity verification failed.");
                Toast.makeText(DonationDialogActivity.this, "Error Purchasing.", Toast.LENGTH_LONG).show();
                return;
            }

            if (purchase.getSku().equals(SKU_1_DONATION) && purchase.getSku().equals(SKU_2_DONATION) &&
                    purchase.getSku().equals(SKU_3_DONATION)) {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };
    
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. result: " + result);

            if (mHelper == null) {
                return;
            }

            if (result.isSuccess()) {
                Intent intent = new Intent(DonationDialogActivity.this, AlertDialogActivity.class);
                intent.putExtra(Constants.ALERT_TYPE, Constants.ALERT_TY_DONATION);
                startActivity(intent);
            } else {
                Toast.makeText(DonationDialogActivity.this, "Something went wrong with your purchase." + result, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    };
    
    private boolean verifyDeveloperPayload(Purchase purchase) {
        String sku = purchase.getSku();
        String payload = purchase.getDeveloperPayload();
        
        if (!payload.equals(mPayload)) { 
            return false;
        } else if (!sku.equals(SKU_1_DONATION) && !sku.equals(SKU_2_DONATION)
                && !sku.equals(SKU_3_DONATION)) {
            return false;
        }
        
        mPayload = "";
        
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) {
            return;
        }
        
        if (resultCode == RESULT_OK) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            unbindBilling();
            finish();
        }
    }
    
    // For developer Payload.
    public class RandomString {
        private final Random random = new Random();
        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}
