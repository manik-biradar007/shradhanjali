package com.ereader.ripcardmaker

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.ereader.ripcardmaker.ads.AdManager
import com.ereader.ripcardmaker.ui.AppNavigation
import com.ereader.ripcardmaker.ui.theme.ShradhanjaliTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var loading = true
    private var stoppedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splash.setKeepOnScreenCondition { loading }

        val coldStart = savedInstanceState == null
        lifecycleScope.launch {
            app.drafts.load()
            if (coldStart) app.prefs.registerLaunch()
            loading = false
            if (coldStart) startAds()
        }

        setContent {
            ShradhanjaliTheme {
                AppNavigation()
            }
        }
    }

    /** Consent first; then ads. The app open ad shows if it is ready soon after start-up. */
    private fun startAds() {
        val startedAt = SystemClock.elapsedRealtime()
        if (app.consent.canRequestAds) app.ads.initialize()
        app.consent.gather(this) { canRequestAds ->
            if (!canRequestAds) return@gather
            app.ads.initialize {
                app.ads.loadAppOpen {
                    val fresh = SystemClock.elapsedRealtime() - startedAt < APP_OPEN_WINDOW_MS
                    if (fresh && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        app.ads.showAppOpen(this)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val awayFor = if (stoppedAt == 0L) 0L else SystemClock.elapsedRealtime() - stoppedAt
        stoppedAt = 0L
        // Coming back after a while (not from our own ad, share sheet or picker): show an app open ad.
        if (!app.ads.consumeSkipResumeAd() && awayFor >= AdManager.RESUME_AD_AFTER_MS && app.ads.isReady) {
            app.ads.showAppOpen(this)
        }
    }

    override fun onStop() {
        super.onStop()
        stoppedAt = SystemClock.elapsedRealtime()
        app.ads.cancelPendingAppOpen()
    }

    private companion object {
        const val APP_OPEN_WINDOW_MS = 8_000L
    }
}
