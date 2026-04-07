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
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdAdmob {

    private static long lastAdTimestamp = 0;
    private static final long AD_INTERVAL_MS = 10000;
    private static final boolean showAds = true;

    private static ProgressDialog progressDialog;
    private static InterstitialAd interstitialAd = null;
    private static RewardedAd rewardedAd = null;

    public AdAdmob(Activity activity) {
        if (showAds) {
            MobileAds.initialize(activity, initializationStatus -> {});
        }
    }

    public void BannerAd(final RelativeLayout Ad_Layout, Activity activity) {
        if (showAds) {
            String bannerAdUnitId = activity.getString(R.string.admob_banner_ad_unit_id);
            if (!isAdUnitIdConfigured(bannerAdUnitId, "BannerAd")) {
                Ad_Layout.setVisibility(View.INVISIBLE);
                return;
            }

            AdView mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.LARGE_BANNER);
            mAdView.setAdUnitId(bannerAdUnitId);
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
        loadAndShowInterstitialAd(activity);
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

    private static void loadAndShowInterstitialAd(final Activity activity) {
        String interstitialAdUnitId = activity.getString(R.string.admob_interstitial_ad_unit_id);
        if (!isAdUnitIdConfigured(interstitialAdUnitId, "InterstitialAd")) {
            dismissProgressDialog();
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, interstitialAdUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                dismissProgressDialog();
                Log.e("FullscreenAd", "Interstitial loaded");

                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                        interstitialAd = null;
                    }
                });

                ad.show(activity);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e("FullscreenAd", "Interstitial failed: " + adError.getMessage());
                dismissProgressDialog();
            }
        });
    }

    private static void loadAndShowRewardedAd(final Activity activity, final Runnable onRewarded) {
        String rewardedAdUnitId = activity.getString(R.string.admob_rewarded_ad_unit_id);
        if (!isAdUnitIdConfigured(rewardedAdUnitId, "RewardedAd")) {
            dismissProgressDialog();
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, rewardedAdUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                dismissProgressDialog();
                Log.e("FullscreenAd", "Rewarded loaded");
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

    private static boolean isAdUnitIdConfigured(String adUnitId, String tag) {
        if (adUnitId == null || adUnitId.trim().isEmpty()) {
            Log.e(tag, "Ad unit ID is missing in strings.xml");
            return false;
        }
        return true;
    }
}
