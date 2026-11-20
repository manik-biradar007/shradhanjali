package com.ereader.ripcardmaker.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.render.CardRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Renders a card at full resolution and saves or shares it. */
class CardExporter(private val context: Context) {

    private val renderer = CardRenderer.get(context)

    /** Saving needs WRITE_EXTERNAL_STORAGE only on Android 9 and below. */
    val needsLegacyPermission: Boolean get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    /** Saves to Pictures/Shradhanjali. Returns true on success. */
    suspend fun saveToGallery(draft: CardDraft): Boolean = withContext(Dispatchers.IO) {
        val bitmap = renderer.renderBitmap(draft)
        try {
            val name = fileName(draft)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$ALBUM")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext false
                val ok = resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) } ?: false
                if (!ok) {
                    resolver.delete(uri, null, null)
                    return@withContext false
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } else {
                @Suppress("DEPRECATION")
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM)
                if (!dir.exists() && !dir.mkdirs()) return@withContext false
                val file = File(dir, name)
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/jpeg"), null)
                true
            }
        } catch (e: Exception) {
            false
        } finally {
            bitmap.recycle()
        }
    }

    /**
     * Writes the card to a private cache file and returns a share intent for it.
     * Nothing is added to the gallery, so sharing several times leaves no duplicates.
     */
    suspend fun shareIntent(draft: CardDraft, targetPackage: String? = null): Intent? = withContext(Dispatchers.IO) {
        val bitmap = renderer.renderBitmap(draft)
        try {
            val dir = File(context.cacheDir, "shared").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() }
            val file = File(dir, fileName(draft))
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, it) }
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                targetPackage?.let { setPackage(it) }
            }
        } catch (e: Exception) {
            null
        } finally {
            bitmap.recycle()
        }
    }

    private fun fileName(draft: CardDraft): String {
        val base = draft.deceased.fullName
            .replace(Regex("[^\\p{L}\\p{M}\\p{N} _-]"), "")
            .trim().replace(' ', '_')
            .take(40)
            .ifEmpty { "Shradhanjali" }
        return "${base}_${System.currentTimeMillis()}.jpg"
    }

    companion object {
        const val ALBUM = "Shradhanjali"
        const val QUALITY = 95
        const val WHATSAPP = "com.whatsapp"
    }
}
