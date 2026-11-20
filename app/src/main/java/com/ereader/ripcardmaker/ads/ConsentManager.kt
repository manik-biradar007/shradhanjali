package com.ereader.ripcardmaker.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Google User Messaging Platform (GDPR / privacy) consent.
 * Ads are only requested once [canRequestAds] is true.
 */
class ConsentManager(context: Context) {

    private val info: ConsentInformation = UserMessagingPlatform.getConsentInformation(context.applicationContext)

    val canRequestAds: Boolean get() = info.canRequestAds()

    /** Whether the user must be offered a "Privacy choices" entry in the app. */
    val privacyOptionsRequired: Boolean
        get() = info.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /** Updates consent info and shows the consent form if needed, then calls [onDone]. */
    fun gather(activity: Activity, onDone: (canRequestAds: Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder().build()
        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    onDone(info.canRequestAds())
                }
            },
            { onDone(info.canRequestAds()) },
        )
    }

    fun showPrivacyOptions(activity: Activity, onDone: () -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { onDone() }
    }
}
