package com.ereader.ripcardmaker.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.render.CardRegion
import com.ereader.ripcardmaker.render.CardRenderer
import com.ereader.ripcardmaker.text.CardTextBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Live card drawn with the same renderer as the exported image.
 * Tapping a part of the card reports which [CardRegion] was tapped.
 */
@Composable
fun CardPreview(
    draft: CardDraft,
    modifier: Modifier = Modifier,
    editorMode: Boolean = true,
    onRegionTap: ((CardRegion) -> Unit)? = null,
) {
    val renderer = CardRenderer.get(LocalContext.current)
    val content = remember(draft) { CardTextBuilder.build(draft) }
    val regions = remember { arrayOfNulls<Map<CardRegion, android.graphics.RectF>>(1) }
    val cardW = draft.format.widthPx.toFloat()

    Canvas(
        modifier
            .aspectRatio(draft.format.aspectRatio)
            .shadow(6.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if (onRegionTap == null) Modifier else Modifier.pointerInput(draft.format) {
                    detectTapGestures { offset ->
                        val k = cardW / size.width
                        val x = offset.x * k
                        val y = offset.y * k
                        regions[0]?.entries?.firstOrNull { it.value.contains(x, y) }?.let { onRegionTap(it.key) }
                    }
                }
            )
    ) {
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            native.save()
            val k = size.width / cardW
            native.scale(k, k)
            regions[0] = renderer.draw(native, draft, content, editorMode)
            native.restore()
        }
    }
}

/** Small rendered image of a card for lists; rendered off the main thread. */
@Composable
fun CardThumbnail(draft: CardDraft, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(null, draft.updatedAt, draft.id, draft.format) {
        value = withContext(Dispatchers.Default) {
            runCatching { CardRenderer.get(context).renderBitmap(draft, 360) }.getOrNull()
        }
    }
    Box(
        modifier
            .aspectRatio(draft.format.aspectRatio)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        bitmap?.let {
            Image(
                it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
