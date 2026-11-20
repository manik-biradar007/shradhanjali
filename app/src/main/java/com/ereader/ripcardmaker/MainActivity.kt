package com.ereader.ripcardmaker

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.ereader.ripcardmaker.ui.AppNavigation
import com.ereader.ripcardmaker.ui.theme.ShradhanjaliTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var loading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splash.setKeepOnScreenCondition { loading }

        val coldStart = savedInstanceState == null
        lifecycleScope.launch {
            app.drafts.load()
            val previousLaunches = if (coldStart) app.prefs.registerLaunch() else 1
            loading = false
            if (coldStart) startAds(firstLaunch = previousLaunches == 0)
        }

        setContent {
            ShradhanjaliTheme {
                AppNavigation()
            }
        }
    }

    /** Consent first; then ads. The app open ad only shows if it is ready right after start-up. */
    private fun startAds(firstLaunch: Boolean) {
        val startedAt = SystemClock.elapsedRealtime()
        if (app.consent.canRequestAds) app.ads.initialize()
        app.consent.gather(this) { canRequestAds ->
            if (!canRequestAds) return@gather
            app.ads.initialize {
                if (firstLaunch) return@initialize
                app.ads.loadAppOpen {
                    val fresh = SystemClock.elapsedRealtime() - startedAt < APP_OPEN_WINDOW_MS
                    if (fresh && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        app.ads.showAppOpen(this)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        app.ads.cancelPendingAppOpen()
    }

    private companion object {
        const val APP_OPEN_WINDOW_MS = 5_000L
    }
}
