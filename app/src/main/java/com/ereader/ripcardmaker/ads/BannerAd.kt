package com.ereader.ripcardmaker.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ereader.ripcardmaker.app
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/** Anchored adaptive banner. Takes no space until an ad has actually loaded. */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ads = context.app.ads
    val ready by ads.ready.collectAsState()
    if (!ready) return

    BoxWithConstraints(modifier.fillMaxWidth().navigationBarsPadding(), contentAlignment = Alignment.Center) {
        val widthDp = maxWidth.value.toInt()
        val size = remember(widthDp) { AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp) }
        var loaded by remember(widthDp) { mutableStateOf(false) }
        val adView = remember(widthDp) {
            AdView(context).apply {
                adUnitId = ads.bannerId
                setAdSize(size)
                adListener = object : AdListener() {
                    override fun onAdLoaded() { loaded = true }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
        DisposableEffect(adView) { onDispose { adView.destroy() } }
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth().height(if (loaded) size.height.dp else 0.dp),
        )
    }
}
