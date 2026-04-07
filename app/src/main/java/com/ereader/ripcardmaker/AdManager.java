package com.ereader.ripcardmaker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.ereader.ripcardmaker.BuildConfig;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

/**
 * Centralized AdMob manager.
 * Handles: App Open, Interstitial, Rewarded, Rewarded Interstitial.
 * No banner ads.
 *
 * Usage:
 *   AdManager.getInstance(context).showInterstitialAd(activity, onComplete);
 *   AdManager.getInstance(context).showRewardedAd(activity, onRewarded, onComplete);
 */
public class AdManager {

    private static final String TAG = "AdManager";
    // App Open ads expire after 4 hours
    private static final long APP_OPEN_AD_EXPIRY_MS = 4 * 60 * 60 * 1000L;

    private static AdManager instance;

    private final String appOpenAdUnitId;
    private final String interstitialAdUnitId;
    private final String rewardedAdUnitId;
    private final String rewardedInterstitialAdUnitId;

    private AppOpenAd appOpenAd;
    private long appOpenAdLoadTime = 0;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    private boolean isLoadingAppOpen = false;
    private boolean isLoadingInterstitial = false;
    private boolean isLoadingRewarded = false;
    private boolean isLoadingRewardedInterstitial = false;

    // Google's official test ad unit IDs — always fill, no AdMob account needed
    private static final String TEST_APP_OPEN       = "ca-app-pub-3940256099942544/9257395921";
    private static final String TEST_INTERSTITIAL   = "ca-app-pub-3940256099942544/1033173712";
    private static final String TEST_REWARDED       = "ca-app-pub-3940256099942544/5224354917";
    private static final String TEST_REWARDED_INTER = "ca-app-pub-3940256099942544/5354046379";

    private AdManager(Context context) {
        if (BuildConfig.DEBUG) {
            // Use Google's test IDs during development — always serve test ads
            appOpenAdUnitId           = TEST_APP_OPEN;
            interstitialAdUnitId      = TEST_INTERSTITIAL;
            rewardedAdUnitId          = TEST_REWARDED;
            rewardedInterstitialAdUnitId = TEST_REWARDED_INTER;
        } else {
            appOpenAdUnitId           = context.getString(R.string.admob_app_open_ad_unit_id);
            interstitialAdUnitId      = context.getString(R.string.admob_interstitial_ad_unit_id);
            rewardedAdUnitId          = context.getString(R.string.admob_rewarded_ad_unit_id);
            rewardedInterstitialAdUnitId = context.getString(R.string.admob_rewarded_interstitial_ad_unit_id);
        }
    }

    public static synchronized AdManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdManager(context.getApplicationContext());
        }
        return instance;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private AdRequest newRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean isAppOpenAdValid() {
        return appOpenAd != null
                && (System.currentTimeMillis() - appOpenAdLoadTime) < APP_OPEN_AD_EXPIRY_MS;
    }

    // ── App Open Ad ──────────────────────────────────────────────────────────

    public void loadAppOpenAd(Context context) {
        if (isAppOpenAdValid() || isLoadingAppOpen) return;
        isLoadingAppOpen = true;
        AppOpenAd.load(context, appOpenAdUnitId, newRequest(),
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        appOpenAdLoadTime = System.currentTimeMillis();
                        isLoadingAppOpen = false;
                        Log.d(TAG, "App open ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingAppOpen = false;
                        Log.e(TAG, "App open ad failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Shows the App Open ad if one is ready and valid.
     * Always calls onComplete when done (whether ad showed or not).
     */
    public void showAppOpenAd(Activity activity, Runnable onComplete) {
        if (!isAppOpenAdValid()) {
            loadAppOpenAd(activity);
            if (onComplete != null) onComplete.run();
            return;
        }
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                loadAppOpenAd(activity);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                appOpenAd = null;
                Log.e(TAG, "App open ad failed to show: " + error.getMessage());
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "App open ad showed");
            }
        });
        appOpenAd.show(activity);
    }

    // ── Interstitial Ad ──────────────────────────────────────────────────────

    public void loadInterstitialAd(Context context) {
        if (interstitialAd != null || isLoadingInterstitial) return;
        isLoadingInterstitial = true;
        InterstitialAd.load(context, interstitialAdUnitId, newRequest(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        isLoadingInterstitial = false;
                        Log.d(TAG, "Interstitial ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingInterstitial = false;
                        Log.e(TAG, "Interstitial ad failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Shows the interstitial ad if ready, then calls onComplete.
     * If no ad is ready, calls onComplete immediately and reloads.
     */
    public void showInterstitialAd(Activity activity, Runnable onComplete) {
        if (interstitialAd == null) {
            loadInterstitialAd(activity);
            if (onComplete != null) onComplete.run();
            return;
        }
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                interstitialAd = null;
                loadInterstitialAd(activity);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                interstitialAd = null;
                Log.e(TAG, "Interstitial ad failed to show: " + error.getMessage());
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad showed");
            }
        });
        interstitialAd.show(activity);
    }

    // ── Rewarded Ad ──────────────────────────────────────────────────────────

    public void loadRewardedAd(Context context) {
        if (rewardedAd != null || isLoadingRewarded) return;
        isLoadingRewarded = true;
        RewardedAd.load(context, rewardedAdUnitId, newRequest(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        isLoadingRewarded = false;
                        Log.d(TAG, "Rewarded ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingRewarded = false;
                        Log.e(TAG, "Rewarded ad failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Shows a rewarded ad. onRewarded fires when the user earns the reward.
     * If no ad is ready, grants the reward immediately (don't block the user).
     */
    public void showRewardedAd(Activity activity, Runnable onRewarded, Runnable onComplete) {
        if (rewardedAd == null) {
            loadRewardedAd(activity);
            if (onRewarded != null) onRewarded.run();
            if (onComplete != null) onComplete.run();
            return;
        }
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                loadRewardedAd(activity);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                rewardedAd = null;
                Log.e(TAG, "Rewarded ad failed to show: " + error.getMessage());
                if (onRewarded != null) onRewarded.run();
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad showed");
            }
        });
        rewardedAd.show(activity, rewardItem -> {
            if (onRewarded != null) onRewarded.run();
        });
    }

    // ── Rewarded Interstitial Ad ─────────────────────────────────────────────

    public void loadRewardedInterstitialAd(Context context) {
        if (rewardedInterstitialAd != null || isLoadingRewardedInterstitial) return;
        isLoadingRewardedInterstitial = true;
        RewardedInterstitialAd.load(context, rewardedInterstitialAdUnitId, newRequest(),
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        isLoadingRewardedInterstitial = false;
                        Log.d(TAG, "Rewarded interstitial ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingRewardedInterstitial = false;
                        Log.e(TAG, "Rewarded interstitial ad failed: " + error.getMessage());
                    }
                });
    }

    /**
     * Shows a rewarded interstitial ad. onRewarded fires when the user earns the reward.
     * If no ad is ready, grants the reward immediately (don't block the user).
     */
    public void showRewardedInterstitialAd(Activity activity, Runnable onRewarded, Runnable onComplete) {
        if (rewardedInterstitialAd == null) {
            loadRewardedInterstitialAd(activity);
            if (onRewarded != null) onRewarded.run();
            if (onComplete != null) onComplete.run();
            return;
        }
        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null;
                loadRewardedInterstitialAd(activity);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                rewardedInterstitialAd = null;
                Log.e(TAG, "Rewarded interstitial ad failed to show: " + error.getMessage());
                if (onRewarded != null) onRewarded.run();
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded interstitial ad showed");
            }
        });
        rewardedInterstitialAd.show(activity, rewardItem -> {
            if (onRewarded != null) onRewarded.run();
        });
    }

    /** Preload all ad types at once. Call this after MobileAds.initialize(). */
    public void preloadAll(Context context) {
        loadAppOpenAd(context);
        loadInterstitialAd(context);
        loadRewardedAd(context);
        loadRewardedInterstitialAd(context);
    }
}
