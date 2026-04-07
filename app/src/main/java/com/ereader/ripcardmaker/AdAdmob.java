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

    public static final String BannerAdID = "/21849154601,22991801446/Ad.Plus-APP-Banner";
    public static final String FullscreenAdID = "/21849154601,22991801446/Ad.Plus-APP-Rewarded";

    private static long lastAdTimestamp = 0;
    private static final long AD_INTERVAL_MS = 10000;
    private static final boolean showAds = true;

    private static ProgressDialog progressDialog;
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
                    Log.e("BannerAd", "Ad Loaded");
                }

                @Override
                public void onAdOpened() {
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("BannerAd", "Ad Opened");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mAdView.destroy();
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("BannerAd", "Failed to load: " + loadAdError.getMessage());
                }
            });
        }
    }

    /** Auto-triggered interstitial ad with 10-second throttle. */
    public static void FullscreenAd(final Activity activity) {
        if (!showAds) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAdTimestamp < AD_INTERVAL_MS) {
            Log.e("FullscreenAd", "Throttled");
            return;
        }
        lastAdTimestamp = currentTime;
        showProgressDialog(activity, "Ad Loading . . .");
        loadAndShowRewardedAd(activity, null);
    }

    /**
     * User-initiated rewarded ad (bypasses throttle).
     * @param onRewarded called on the main thread when the user earns a reward.
     */
    public static void FullscreenAdWithReward(final Activity activity, final Runnable onRewarded) {
        if (!showAds) {
            if (onRewarded != null) onRewarded.run();
            return;
        }
        showProgressDialog(activity, "Ad Loading . . .");
        loadAndShowRewardedAd(activity, onRewarded);
    }

    private static void loadAndShowRewardedAd(final Activity activity, final Runnable onRewarded) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, FullscreenAdID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                dismissProgressDialog();
                Log.e("FullscreenAd", "Ad loaded");
                ad.show(activity, rewardItem -> {
                    Log.d("FullscreenAd", "User earned reward: " + rewardItem.getAmount());
                    dismissProgressDialog();
                    if (onRewarded != null) {
                        activity.runOnUiThread(onRewarded);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e("FullscreenAd", "Failed: " + adError.getMessage());
                dismissProgressDialog();
            }
        });
    }

    private static void showProgressDialog(Activity activity, String message) {
        dismissProgressDialog();
        try {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                progressDialog = ProgressDialog.show(activity, "", message, true);
                progressDialog.setCancelable(true);
            }
        } catch (Exception e) {
            progressDialog = null;
        }
    }

    private static void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {
        } finally {
            progressDialog = null;
        }
    }
}
