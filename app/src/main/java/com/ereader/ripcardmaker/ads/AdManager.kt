package com.ereader.ripcardmaker.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.ereader.ripcardmaker.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Ad placements:
 *  - App open: every cold start, and when the user comes back after [RESUME_AD_AFTER_MS] away.
 *  - Banner: bottom of Preview. See [BannerAd].
 *  - Interstitial: opening/creating a card, Editor → Preview, and Done; at most once per [INTERSTITIAL_GAP_MS].
 *  - Rewarded interstitial: before saving to the gallery.
 *  - Rewarded: before sharing, and to unlock the Status size.
 *
 * Rewarded formats are always preceded by an in-app prompt the user accepts (AdMob policy).
 * If no ad can be loaded in [REWARDED_WAIT_MS], the reward is granted so the user is never stuck.
 * Nothing loads until [ConsentManager] reports that ads may be requested.
 */
class AdManager(context: Context) {

    private val appContext = context.applicationContext
    private val initialized = AtomicBoolean(false)
    private val main = Handler(Looper.getMainLooper())

    private val appOpenId: String
    private val interstitialId: String
    private val rewardedId: String
    private val rewardedInterstitialId: String
    val bannerId: String

    init {
        appOpenId = appContext.getString(R.string.admob_app_open_ad_unit_id)
        interstitialId = appContext.getString(R.string.admob_interstitial_ad_unit_id)
        rewardedId = appContext.getString(R.string.admob_rewarded_ad_unit_id)
        rewardedInterstitialId = appContext.getString(R.string.admob_rewarded_interstitial_ad_unit_id)
        bannerId = appContext.getString(R.string.admob_banner_ad_unit_id)
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
    private var rewardedWaiter: (() -> Unit)? = null

    private var rewardedInterstitial: RewardedInterstitialAd? = null
    private var rewardedInterstitialLoading = false
    private var rewardedInterstitialWaiter: (() -> Unit)? = null

    private var showingFullScreen = false

    /** Set when the app leaves the foreground on purpose (ad, share sheet, photo picker). */
    private var skipNextResumeAd = false

    private val _ready = MutableStateFlow(false)
    /** True once consent allows ads and the SDK is initialised; banners observe this. */
    val ready: StateFlow<Boolean> = _ready
    val isReady: Boolean get() = _ready.value

    /** Initialises the SDK once consent allows it, then preloads ads. */
    fun initialize(onReady: () -> Unit = {}) {
        if (!initialized.compareAndSet(false, true)) {
            if (isReady) onReady() else pendingReady += onReady
            return
        }
        pendingReady += onReady
        MobileAds.initialize(appContext) {
            _ready.value = true
            loadInterstitial()
            loadRewarded()
            loadRewardedInterstitial()
            loadAppOpen()
            pendingReady.toList().forEach { it() }
            pendingReady.clear()
        }
    }

    private val pendingReady = mutableListOf<() -> Unit>()

    // ── Foreground tracking ─────────────────────────────────────────────────

    /** Call before launching another app or a system screen (share sheet, photo picker). */
    fun leavingAppOnPurpose() {
        skipNextResumeAd = true
    }

    /** Returns true (and resets) if the last trip away from the app should not trigger an app open ad. */
    fun consumeSkipResumeAd(): Boolean {
        val skip = skipNextResumeAd || showingFullScreen
        skipNextResumeAd = false
        return skip
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
            }
        })
    }

    /** Shows the app open ad if one is loaded. Returns true if it is being shown. */
    fun showAppOpen(activity: Activity): Boolean {
        val ad = appOpenAd?.takeIf { appOpenValid() }
        if (ad == null || showingFullScreen) {
            loadAppOpen()
            return false
        }
        appOpenListener = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { appOpenAd = null; showingFullScreen = false; loadAppOpen() }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { appOpenAd = null; showingFullScreen = false; loadAppOpen() }
        }
        showingFullScreen = true
        skipNextResumeAd = true
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
        skipNextResumeAd = true
        ad.show(activity)
    }

    // ── Rewarded ────────────────────────────────────────────────────────────

    private fun loadRewarded() {
        if (!isReady || rewarded != null || rewardedLoading) return
        rewardedLoading = true
        RewardedAd.load(appContext, rewardedId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewarded = ad; rewardedLoading = false
                rewardedWaiter?.invoke(); rewardedWaiter = null
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedLoading = false
                rewardedWaiter?.invoke(); rewardedWaiter = null
            }
        })
    }

    /**
     * Shows a rewarded ad the user agreed to watch. Waits briefly for one to load; if none is
     * available the reward is granted anyway. [onFinished] runs either way, after any ad closes.
     */
    fun showRewarded(activity: Activity, onReward: () -> Unit, onFinished: () -> Unit = {}) {
        whenLoaded(
            isLoaded = { rewarded != null },
            load = ::loadRewarded,
            setWaiter = { rewardedWaiter = it },
        ) {
            val ad = rewarded
            if (ad == null || showingFullScreen || activity.isFinishing) {
                onReward(); onFinished(); return@whenLoaded
            }
            var earned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewarded = null; showingFullScreen = false; loadRewarded()
                    if (earned) onReward()
                    onFinished()
                }
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    rewarded = null; showingFullScreen = false; loadRewarded(); onReward(); onFinished()
                }
            }
            showingFullScreen = true
            skipNextResumeAd = true
            ad.show(activity) { earned = true }
        }
    }

    // ── Rewarded interstitial ───────────────────────────────────────────────

    private fun loadRewardedInterstitial() {
        if (!isReady || rewardedInterstitial != null || rewardedInterstitialLoading) return
        rewardedInterstitialLoading = true
        RewardedInterstitialAd.load(
            appContext, rewardedInterstitialId, AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitial = ad; rewardedInterstitialLoading = false
                    rewardedInterstitialWaiter?.invoke(); rewardedInterstitialWaiter = null
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedInterstitialLoading = false
                    rewardedInterstitialWaiter?.invoke(); rewardedInterstitialWaiter = null
                }
            },
        )
    }

    /** Same contract as [showRewarded], using the rewarded interstitial unit (falls back to rewarded). */
    fun showRewardedInterstitial(activity: Activity, onReward: () -> Unit, onFinished: () -> Unit = {}) {
        whenLoaded(
            isLoaded = { rewardedInterstitial != null },
            load = ::loadRewardedInterstitial,
            setWaiter = { rewardedInterstitialWaiter = it },
        ) {
            val ad = rewardedInterstitial
            if (ad == null) {
                // No rewarded interstitial: try the plain rewarded unit instead.
                showRewarded(activity, onReward, onFinished); return@whenLoaded
            }
            if (showingFullScreen || activity.isFinishing) {
                onReward(); onFinished(); return@whenLoaded
            }
            var earned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitial = null; showingFullScreen = false; loadRewardedInterstitial()
                    if (earned) onReward()
                    onFinished()
                }
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    rewardedInterstitial = null; showingFullScreen = false; loadRewardedInterstitial()
                    onReward(); onFinished()
                }
            }
            showingFullScreen = true
            skipNextResumeAd = true
            ad.show(activity) { earned = true }
        }
    }

    /** Runs [then] as soon as an ad is loaded, the load fails, or [REWARDED_WAIT_MS] passes. */
    private fun whenLoaded(isLoaded: () -> Boolean, load: () -> Unit, setWaiter: ((() -> Unit)?) -> Unit, then: () -> Unit) {
        if (!isReady || isLoaded()) {
            then(); return
        }
        var done = false
        val finish = {
            if (!done) {
                done = true
                setWaiter(null)
                then()
            }
        }
        setWaiter(finish)
        load()
        main.postDelayed({ finish() }, REWARDED_WAIT_MS)
    }

    companion object {
        private const val APP_OPEN_EXPIRY_MS = 4 * 60 * 60 * 1000L
        private const val INTERSTITIAL_GAP_MS = 30 * 1000L
        private const val REWARDED_WAIT_MS = 5_000L
        const val RESUME_AD_AFTER_MS = 30_000L
    }
}
