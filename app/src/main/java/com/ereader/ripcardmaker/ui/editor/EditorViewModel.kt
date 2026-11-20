package com.ereader.ripcardmaker.ui.editor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ereader.ripcardmaker.app
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.Photo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Holds the card being edited and saves it automatically shortly after each change. */
class EditorViewModel(application: Application, private val draftId: String) : AndroidViewModel(application) {

    private val app = application.app
    private val repo = app.drafts

    private val _draft = MutableStateFlow<CardDraft?>(null)
    val draft: StateFlow<CardDraft?> = _draft.asStateFlow()

    private val _importingPhoto = MutableStateFlow(false)
    val importingPhoto: StateFlow<Boolean> = _importingPhoto.asStateFlow()

    private var saveJob: Job? = null
    private var dirty = false

    init {
        viewModelScope.launch { _draft.value = repo.get(draftId) }
        // Fields changed on the Preview screen (size, unlock, export time) flow back in here.
        viewModelScope.launch {
            repo.drafts.collect { list ->
                val stored = list.firstOrNull { it.id == draftId } ?: return@collect
                val current = _draft.value ?: return@collect
                if (stored.format != current.format || stored.statusUnlocked != current.statusUnlocked ||
                    stored.lastExportedAt != current.lastExportedAt
                ) {
                    _draft.value = current.copy(
                        format = stored.format,
                        statusUnlocked = stored.statusUnlocked,
                        lastExportedAt = stored.lastExportedAt,
                    )
                }
            }
        }
    }

    fun update(transform: (CardDraft) -> CardDraft) {
        val current = _draft.value ?: return
        _draft.value = transform(current)
        dirty = true
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(SAVE_DELAY_MS)
            flush()
        }
    }

    /** Saves immediately (on navigation and when the screen goes away). */
    fun flush() {
        if (!dirty) return
        val toSave = _draft.value ?: return
        dirty = false
        saveJob?.cancel()
        app.appScope.launch { repo.save(toSave) }
    }

    fun importPhoto(uri: Uri) {
        _importingPhoto.value = true
        viewModelScope.launch {
            val photo = repo.importPhoto(draftId, uri)
            _importingPhoto.value = false
            if (photo != null) update { it.copy(photo = photo) }
        }
    }

    fun removePhoto() {
        update { it.copy(photo = Photo()) }
        viewModelScope.launch { repo.deletePhoto(draftId) }
    }

    override fun onCleared() {
        flush()
    }

    private companion object {
        const val SAVE_DELAY_MS = 600L
    }
}
