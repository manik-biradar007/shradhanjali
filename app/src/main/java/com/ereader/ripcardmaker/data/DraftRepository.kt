package com.ereader.ripcardmaker.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import kotlin.math.max

/**
 * Stores cards as JSON files in app storage, plus a downscaled copy of each card's photo.
 * Everything stays on the device.
 */
class DraftRepository(context: Context) {

    private val appContext = context.applicationContext
    private val draftsDir = File(appContext.filesDir, "drafts").apply { mkdirs() }
    private val photosDir = File(appContext.filesDir, "photos").apply { mkdirs() }
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val mutex = Mutex()

    private val _drafts = MutableStateFlow<List<CardDraft>>(emptyList())
    /** All cards, most recently edited first. */
    val drafts: StateFlow<List<CardDraft>> = _drafts.asStateFlow()

    suspend fun load() = withContext(Dispatchers.IO) {
        mutex.withLock { _drafts.value = readAll() }
    }

    suspend fun get(id: String): CardDraft? = withContext(Dispatchers.IO) {
        _drafts.value.firstOrNull { it.id == id } ?: read(File(draftsDir, "$id.json"))
    }

    suspend fun create(language: CardLanguage): CardDraft {
        val draft = CardDraft(id = UUID.randomUUID().toString(), language = language)
        save(draft, touch = false)
        return draft
    }

    suspend fun save(draft: CardDraft, touch: Boolean = true): CardDraft = withContext(Dispatchers.IO) {
        val toSave = if (touch) draft.copy(updatedAt = System.currentTimeMillis()) else draft
        mutex.withLock {
            val file = File(draftsDir, "${toSave.id}.json")
            val tmp = File(draftsDir, "${toSave.id}.json.tmp")
            tmp.writeText(json.encodeToString(CardDraft.serializer(), toSave))
            if (!tmp.renameTo(file)) {
                file.delete()
                tmp.renameTo(file)
            }
            _drafts.value = (_drafts.value.filterNot { it.id == toSave.id } + toSave).sortedByDescending { it.updatedAt }
        }
        toSave
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            File(draftsDir, "$id.json").delete()
            File(photosDir, "$id.jpg").delete()
            _drafts.value = _drafts.value.filterNot { it.id == id }
        }
    }

    suspend fun duplicate(id: String): CardDraft? {
        val source = get(id) ?: return null
        val newId = UUID.randomUUID().toString()
        val newPhoto = source.photo.path?.let { path ->
            withContext(Dispatchers.IO) {
                val dest = File(photosDir, "$newId.jpg")
                runCatching { File(path).copyTo(dest, overwrite = true) }.getOrNull()
                    ?.let { source.photo.copy(path = it.absolutePath, version = System.currentTimeMillis()) }
            }
        } ?: Photo()
        val now = System.currentTimeMillis()
        return save(
            source.copy(id = newId, photo = newPhoto, createdAt = now, updatedAt = now, lastExportedAt = null),
            touch = false,
        )
    }

    /**
     * Copies the picked image into app storage, upright and at most [MAX_PHOTO_PX] on its long side.
     * Returns the new photo state, or null if the image could not be read.
     */
    suspend fun importPhoto(draftId: String, uri: Uri): Photo? = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = appContext.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

            var sample = 1
            while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_PHOTO_PX) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            var bmp = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
                ?: return@runCatching null

            val rotation = resolver.openInputStream(uri)?.use {
                when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f

            val longSide = max(bmp.width, bmp.height)
            val scale = if (longSide > MAX_PHOTO_PX) MAX_PHOTO_PX.toFloat() / longSide else 1f
            if (rotation != 0f || scale != 1f) {
                val m = Matrix().apply { postScale(scale, scale); postRotate(rotation) }
                val transformed = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
                if (transformed != bmp) bmp.recycle()
                bmp = transformed
            }

            val dest = File(photosDir, "$draftId.jpg")
            dest.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 92, it) }
            bmp.recycle()
            Photo(path = dest.absolutePath, version = System.currentTimeMillis())
        }.getOrNull()
    }

    suspend fun deletePhoto(draftId: String) = withContext(Dispatchers.IO) {
        File(photosDir, "$draftId.jpg").delete()
    }

    private fun readAll(): List<CardDraft> =
        draftsDir.listFiles { f -> f.extension == "json" }.orEmpty()
            .mapNotNull { read(it) }
            .sortedByDescending { it.updatedAt }

    private fun read(file: File): CardDraft? =
        runCatching { json.decodeFromString(CardDraft.serializer(), file.readText()) }.getOrNull()

    companion object {
        const val MAX_PHOTO_PX = 1080
    }
}
