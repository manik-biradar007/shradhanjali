package com.ereader.ripcardmaker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ereader.ripcardmaker.model.CardLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class Prefs(context: Context) {

    private val store = context.applicationContext.dataStore

    /** The language picked on Home; null until the user picks one. */
    val language: Flow<CardLanguage?> = store.data.map { CardLanguage.fromCode(it[LANGUAGE]) }

    suspend fun setLanguage(language: CardLanguage) {
        store.edit { it[LANGUAGE] = language.code }
    }

    /** Returns how many times the app was opened before this launch, and counts this one. */
    suspend fun registerLaunch(): Int {
        val previous = store.data.first()[LAUNCHES] ?: 0
        store.edit { it[LAUNCHES] = previous + 1 }
        return previous
    }

    private companion object {
        val LANGUAGE = stringPreferencesKey("card_language")
        val LAUNCHES = intPreferencesKey("launch_count")
    }
}
