package com.ereader.ripcardmaker;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdAdmob {

    public static String BannerAdID = "/21849154601,22991801446/Ad.Plus-APP-Banner";
    public static String FullscreenAdID = "/21849154601,22991801446/Ad.Plus-APP-Rewarded";

    private static long lastAdTimestamp = 0;
    private static final long AD_INTERVAL_MS = 10000;
    private static final boolean showAds = true;


    private static boolean isPaidByAds = false;
    static ProgressDialog progressDialog;  // Declare a static ProgressDialog to track it.
    private static RewardedAd rewardedAd = null;

    public AdAdmob(Activity activity) {
        if (showAds) {
            MobileAds.initialize(activity, initializationStatus -> {});
        }
    }

    public void BannerAd(final RelativeLayout Ad_Layout, Activity activity) {
        if (showAds) {
            AdView mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.LARGE_BANNER);
            mAdView.setAdUnitId(BannerAdID);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            Ad_Layout.addView(mAdView);

            mAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    Ad_Layout.setVisibility(View.VISIBLE);
                    super.onAdLoaded();
                    Log.e("BannerAd", "Ad Loaded");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("BannerAd", "Ad Opened");
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("BannerAd", "Failed to load: " + loadAdError.getMessage());
                }
                @Override
                public void onAdClicked() {
                    isPaidByAds = true;
                }
            });
        }
    }

    public static void FullscreenAd(final Activity activity) {
        if (showAds) {
            long currentTime = System.currentTimeMillis();
            long remainingTime = currentTime - lastAdTimestamp;

            Log.e("FullscreenAd", "Called");

            if (remainingTime < AD_INTERVAL_MS) {
                Log.e("FullscreenAd", "Time issue");
                // Not enough time has passed since the last ad
                return;
            }

            // Update the last ad timestamp
            lastAdTimestamp = currentTime;

            // Show progress dialog if needed
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();  // Ensure any ongoing progress dialog is dismissed before showing new ones.
            }

            progressDialog = new ProgressDialog(activity);  // Instantiate the progress dialog
            progressDialog.setMessage("Loading Ad");
            progressDialog.setCancelable(false);
            progressDialog.show();  // Show the progress dialog

            // Show popup if needed
            Ad_Popup(activity);

            // Load and show the rewarded ad
            loadRewardedAd(activity);
        }
    }

    private static void loadRewardedAd(final Activity activity) {
        Log.e("FullscreenAd", "Called loadRewardedAd");

//        if (rewardedAd != null) {
//            // If there's already a rewarded ad loaded, show it.
//            Log.e("FullscreenAd", "Ad already loaded, showing ad...");
//            showRewardedAd(activity);
//            dismissProgressDialog();
//            return;
//        }

        // Create an AdRequest
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load the rewarded ad
        RewardedAd.load(activity, FullscreenAdID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                // The rewarded ad is loaded successfully.
                rewardedAd = ad;
                Log.e("FullscreenAd", "Ad loaded successfully");
                showRewardedAd(activity);
                dismissProgressDialog();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Failed to load the ad
                Log.e("FullscreenAd", "Failed to load rewarded ad: " + adError.getMessage());

                // Dismiss the progress dialog if ad loading fails
                dismissProgressDialog();
            }
        });
    }

    private static void showRewardedAd(final Activity activity) {
        if (rewardedAd != null) {
            // Show the rewarded ad when it's ready
            Log.e("FullscreenAd", "Showing the rewarded ad");

            rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward the user gets
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Log.d("FullscreenAd", "User earned reward: " + rewardAmount + " " + rewardType);

                    // You can also give rewards here, like in-game currency
                    // Toast.makeText(activity, "Rewarded", Toast.LENGTH_SHORT).show();

                    // Dismiss the progress dialog after the ad is completed
                    dismissProgressDialog();
                }
            });

            // Log when the ad is shown
            Log.e("FullscreenAd", "Ad is showing.");
        } else {
            // If the ad is not ready yet, you can display a fallback UI or message
            Log.d("FullscreenAd", "Rewarded ad is not ready.");
            dismissProgressDialog();
        }
    }

    // Helper method to dismiss the progress dialog
    private static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private static void Ad_Popup(Context mContext) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(mContext, "", "Ad Loading . . .", true);
            progressDialog.setCancelable(true);
        }
    }
}
