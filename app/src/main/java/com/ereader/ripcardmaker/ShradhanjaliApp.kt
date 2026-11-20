package com.ereader.ripcardmaker

import android.app.Application
import android.content.Context
import com.ereader.ripcardmaker.ads.AdManager
import com.ereader.ripcardmaker.ads.ConsentManager
import com.ereader.ripcardmaker.data.DraftRepository
import com.ereader.ripcardmaker.data.Prefs
import com.ereader.ripcardmaker.export.CardExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Holds the app-wide singletons. */
class ShradhanjaliApp : Application() {

    /** Outlives screens; used for saves that must finish even if the user leaves. */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var drafts: DraftRepository private set
    lateinit var prefs: Prefs private set
    lateinit var ads: AdManager private set
    lateinit var consent: ConsentManager private set
    lateinit var exporter: CardExporter private set

    override fun onCreate() {
        super.onCreate()
        drafts = DraftRepository(this)
        prefs = Prefs(this)
        ads = AdManager(this)
        consent = ConsentManager(this)
        exporter = CardExporter(this)
    }
}

val Context.app: ShradhanjaliApp get() = applicationContext as ShradhanjaliApp
