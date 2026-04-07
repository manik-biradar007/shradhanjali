package com.ereader.ripcardmaker;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;

public class MyApplication extends Application {

    private static final String TAG = "AdMob";

    // -----------------------------------------------------------------------
    // ADD YOUR TEST DEVICE ID HERE (see instructions below)
    // -----------------------------------------------------------------------
    // 1. Run the app once with this list empty.
    // 2. Open Logcat and filter by "Ads".
    // 3. Look for a line like:
    //      Use RequestConfiguration.Builder().setTestDeviceIds(
    //          Arrays.asList("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"))
    // 4. Copy that hex ID and paste it into the list below.
    // -----------------------------------------------------------------------
    private static final String[] TEST_DEVICE_IDS = {
            "A5FACC5DFF2E1072EED8F315D6C1CD18"
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // Register test devices so real-looking test ads appear during development
        if (TEST_DEVICE_IDS.length > 0) {
            RequestConfiguration config = new RequestConfiguration.Builder()
                    .setTestDeviceIds(Arrays.asList(TEST_DEVICE_IDS))
                    .build();
            MobileAds.setRequestConfiguration(config);
        }

        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized: " + initializationStatus.getAdapterStatusMap());
            AdManager.getInstance(this).preloadAll(this);
        });
    }
}
