package com.ereader.ripcardmaker.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.ereader.ripcardmaker.BuildConfig
import com.ereader.ripcardmaker.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Ad placements (kept deliberately light for a remembrance app):
 *  - App open: on cold start, never on the very first launch.
 *  - Interstitial: when the user finishes with a card, at most once every [INTERSTITIAL_GAP_MS].
 *  - Rewarded: optional, only to unlock the Status (9:16) size. The user chooses to watch it.
 *
 * Nothing loads until [ConsentManager] reports that ads may be requested.
 */
class AdManager(context: Context) {

    private val appContext = context.applicationContext
    private val initialized = AtomicBoolean(false)

    private val appOpenId: String
    private val interstitialId: String
    private val rewardedId: String

    init {
        if (BuildConfig.DEBUG) {
            appOpenId = "ca-app-pub-3940256099942544/9257395921"
            interstitialId = "ca-app-pub-3940256099942544/1033173712"
            rewardedId = "ca-app-pub-3940256099942544/5224354917"
        } else {
            appOpenId = appContext.getString(R.string.admob_app_open_ad_unit_id)
            interstitialId = appContext.getString(R.string.admob_interstitial_ad_unit_id)
            rewardedId = appContext.getString(R.string.admob_rewarded_ad_unit_id)
        }
    }

    private var appOpenAd: AppOpenAd? = null
    private var appOpenLoadedAt = 0L
    private var appOpenLoading = false
    private var appOpenListener: (() -> Unit)? = null

    private var interstitial: InterstitialAd? = null
    private var interstitialLoading = false
    private var lastInterstitialAt = 0L

    private var rewarded: RewardedAd? = null
    private var rewardedLoading = false

    private var showingFullScreen = false

    val isReady: Boolean get() = initialized.get()

    /** Initialises the SDK once consent allows it, then preloads ads. */
    fun initialize(onReady: () -> Unit = {}) {
        if (!initialized.compareAndSet(false, true)) {
            onReady()
            return
        }
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(TEST_DEVICE_IDS).build()
            )
        }
        MobileAds.initialize(appContext) {
            loadInterstitial()
            loadRewarded()
            onReady()
        }
    }

    // ── App open ────────────────────────────────────────────────────────────

    fun loadAppOpen(onLoaded: (() -> Unit)? = null) {
        if (!isReady) return
        if (appOpenValid()) {
            onLoaded?.invoke()
            return
        }
        appOpenListener = onLoaded
        if (appOpenLoading) return
        appOpenLoading = true
        AppOpenAd.load(appContext, appOpenId, AdRequest.Builder().build(), object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                appOpenLoadedAt = SystemClock.elapsedRealtime()
                appOpenLoading = false
                appOpenListener?.invoke()
                appOpenListener = null
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                appOpenLoading = false
                appOpenListener = null
                Log.d(TAG, "App open failed: ${error.message}")
            }
        })
    }

    /** Shows the app open ad if one is loaded. Returns true if it is being shown. */
    fun showAppOpen(activity: Activity): Boolean {
        val ad = appOpenAd?.takeIf { appOpenValid() } ?: return false
        if (showingFullScreen) return false
        appOpenListener = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { appOpenAd = null; showingFullScreen = false }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { appOpenAd = null; showingFullScreen = false }
        }
        showingFullScreen = true
        ad.show(activity)
        return true
    }

    /** Stops a pending app open ad from showing later (e.g. the user already moved on). */
    fun cancelPendingAppOpen() {
        appOpenListener = null
    }

    private fun appOpenValid() =
        appOpenAd != null && SystemClock.elapsedRealtime() - appOpenLoadedAt < APP_OPEN_EXPIRY_MS

    // ── Interstitial ────────────────────────────────────────────────────────

    private fun loadInterstitial() {
        if (!isReady || interstitial != null || interstitialLoading) return
        interstitialLoading = true
        InterstitialAd.load(appContext, interstitialId, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad; interstitialLoading = false }
            override fun onAdFailedToLoad(error: LoadAdError) { interstitialLoading = false }
        })
    }

    /** Shows an interstitial if one is ready and the frequency cap allows; always calls [then]. */
    fun maybeShowInterstitial(activity: Activity, then: () -> Unit) {
        val ad = interstitial
        val now = SystemClock.elapsedRealtime()
        val capped = lastInterstitialAt != 0L && now - lastInterstitialAt < INTERSTITIAL_GAP_MS
        if (ad == null || capped || showingFullScreen) {
            loadInterstitial()
            then()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null; showingFullScreen = false; loadInterstitial(); then()
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                interstitial = null; showingFullScreen = false; loadInterstitial(); then()
            }
        }
        lastInterstitialAt = now
        showingFullScreen = true
        ad.show(activity)
    }

    // ── Rewarded ────────────────────────────────────────────────────────────

    private fun loadRewarded() {
        if (!isReady || rewarded != null || rewardedLoading) return
        rewardedLoading = true
        RewardedAd.load(appContext, rewardedId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewarded = ad; rewardedLoading = false }
            override fun onAdFailedToLoad(error: LoadAdError) { rewardedLoading = false }
        })
    }

    /**
     * Shows a rewarded ad the user asked for. If none is available the reward is granted
     * anyway, so the user is never blocked.
     */
    fun showRewarded(activity: Activity, onReward: () -> Unit) {
        val ad = rewarded
        if (ad == null || showingFullScreen) {
            loadRewarded()
            onReward()
            return
        }
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewarded = null; showingFullScreen = false; loadRewarded()
                if (earned) onReward()
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                rewarded = null; showingFullScreen = false; loadRewarded(); onReward()
            }
        }
        showingFullScreen = true
        ad.show(activity) { earned = true }
    }

    companion object {
        private const val TAG = "AdManager"
        private const val APP_OPEN_EXPIRY_MS = 4 * 60 * 60 * 1000L
        private const val INTERSTITIAL_GAP_MS = 3 * 60 * 1000L
        private val TEST_DEVICE_IDS = listOf("A5FACC5DFF2E1072EED8F315D6C1CD18")
    }
}
