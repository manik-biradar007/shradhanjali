package com.ereader.ripcardmaker.ui.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.Photo
import com.ereader.ripcardmaker.render.CardRenderer
import com.ereader.ripcardmaker.text.UiStrings
import com.ereader.ripcardmaker.ui.components.EditorSection
import com.ereader.ripcardmaker.ui.components.VSpace
import kotlin.math.min

@Composable
fun PhotoTab(
    draft: CardDraft,
    ui: UiStrings,
    importing: Boolean,
    onPick: (Uri) -> Unit,
    onRemove: () -> Unit,
    onUpdate: ((CardDraft) -> CardDraft) -> Unit,
) {
    // System photo picker: no storage permission needed.
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onPick(uri)
    }
    val pick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    val hasPhoto = draft.photo.path != null
    val photoSize = CardRenderer.get(LocalContext.current).photoBitmap(draft.photo)?.let { it.width to it.height }

    EditorSection(if (hasPhoto) ui.adjustPhoto else ui.tabPhoto) {
        Text(ui.photoHint, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(16.dp)

        if (importing) {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (hasPhoto) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                PhotoAdjuster(draft.photo, onChange = { p -> onUpdate { it.copy(photo = p) } })
                VSpace(6.dp)
                Text(ui.dragToMove, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                VSpace(8.dp)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(ui.zoom, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(12.dp))
                    Slider(
                        value = draft.photo.scale,
                        onValueChange = { s -> onUpdate { d -> d.copy(photo = clampPhoto(d.photo.copy(scale = s), photoSize)) } },
                        valueRange = 1f..4f,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        VSpace(12.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = pick, enabled = !importing, modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.AddPhotoAlternate, null)
                Spacer(Modifier.width(8.dp))
                Text(if (hasPhoto) ui.changePhoto else ui.choosePhoto)
            }
            if (hasPhoto) {
                OutlinedButton(onClick = onRemove) {
                    Icon(Icons.Outlined.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text(ui.removePhoto)
                }
            }
        }
    }
}

/** Circle the user drags and pinches to frame the face. Uses the same maths as the card renderer. */
@Composable
private fun PhotoAdjuster(photo: Photo, onChange: (Photo) -> Unit) {
    val context = LocalContext.current
    val bitmap = CardRenderer.get(context).photoBitmap(photo) ?: return
    val current by rememberUpdatedState(photo)

    Canvas(
        Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(3.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            .pointerInput(photo.path, photo.version) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val d = size.width.toFloat()
                    val p = current
                    val next = p.copy(
                        scale = (p.scale * zoom).coerceIn(1f, 4f),
                        offsetX = p.offsetX + pan.x / d,
                        offsetY = p.offsetY + pan.y / d,
                    )
                    onChange(clampPhoto(next, bitmap.width to bitmap.height))
                }
            }
    ) {
        val d = size.width
        val base = d / min(bitmap.width, bitmap.height)
        val s = base * photo.scale
        drawIntoCanvas { c ->
            val m = android.graphics.Matrix().apply {
                postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
                postScale(s, s)
                postTranslate(d / 2 + photo.offsetX * d, d / 2 + photo.offsetY * d)
            }
            c.nativeCanvas.drawBitmap(bitmap, m, android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG))
        }
    }
}

/** Keeps the photo covering the whole circle. */
private fun clampPhoto(p: Photo, size: Pair<Int, Int>?): Photo {
    if (size == null) {
        // Unknown size: only make sure the pan does not exceed what zoom allows for a square photo.
        val max = (p.scale - 1f) / 2f
        return p.copy(offsetX = p.offsetX.coerceIn(-max, max), offsetY = p.offsetY.coerceIn(-max, max))
    }
    val (w, h) = size
    val m = min(w, h).toFloat()
    val maxX = (p.scale * w / m - 1f) / 2f
    val maxY = (p.scale * h / m - 1f) / 2f
    return p.copy(offsetX = p.offsetX.coerceIn(-maxX, maxX), offsetY = p.offsetY.coerceIn(-maxY, maxY))
}
