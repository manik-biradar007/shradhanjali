package com.ereader.ripcardmaker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ereader.ripcardmaker.render.Asset
import com.ereader.ripcardmaker.ui.theme.SectionLabel

/** A titled group of controls inside an editor tab. */
@Composable
fun EditorSection(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(title, style = SectionLabel, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

/** Horizontal strip of artwork thumbnails with an optional "none" first item. */
@Composable
fun AssetStrip(
    assets: List<Asset>,
    selectedId: String?,
    noneLabel: String?,
    itemWidth: Dp,
    itemHeight: Dp,
    onSelect: (String?) -> Unit,
    tileColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(end = 8.dp)) {
        if (noneLabel != null) {
            item {
                SelectableTile(selected = selectedId == null, width = itemWidth, height = itemHeight, color = tileColor, onClick = { onSelect(null) }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Block, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(noneLabel, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        items(assets, key = { it.id }) { asset ->
            SelectableTile(selected = asset.id == selectedId, width = itemWidth, height = itemHeight, color = tileColor, onClick = { onSelect(asset.id) }) {
                AsyncImage(
                    model = asset.resId,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}

@Composable
private fun SelectableTile(
    selected: Boolean, width: Dp, height: Dp, color: Color, onClick: () -> Unit, content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = color,
        border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.secondary) else null,
        modifier = Modifier.size(width, height),
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

/** Row of colour circles. A null entry means "original / none" and is shown with [nullLabel]. */
@Composable
fun ColorSwatches(colors: List<Int?>, selected: Int?, onSelect: (Int?) -> Unit, nullLabel: String? = null) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 2.dp, horizontal = 2.dp)) {
        items(colors) { c ->
            val isSelected = c == selected
            if (c == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(20.dp),
                        )
                        .clickable { onSelect(null) }
                        .padding(horizontal = 14.dp),
                ) { Text(nullLabel.orEmpty(), style = MaterialTheme.typography.labelMedium) }
            } else {
                val color = Color(c)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape,
                        )
                        .clickable { onSelect(c) },
                ) {
                    if (isSelected) {
                        Icon(Icons.Filled.Check, null, tint = if (color.luminance() > 0.5f) Color.Black else Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun VSpace(h: Dp) = Spacer(Modifier.height(h))

object Palettes {
    /** Soft card backgrounds (includes the original app's colours). */
    val backgrounds: List<Int?> = listOf(
        0xFFFDF8EE, 0xFFFFFFFF, 0xFFDDFDEE, 0xFFFAE1DF, 0xFFF8D0C5, 0xFFFAD5E2, 0xFFFADEFF, 0xFFE1E6FD,
        0xFFE2EFFA, 0xFFE7FBFA, 0xFFEBFBEB, 0xFFFAF4E4, 0xFFF5EDDC, 0xFFF1DCC3, 0xFFF1EFDC, 0xFFE4CAB5,
        0xFFD3CEDF, 0xFFDAE5D0, 0xFFE9D5DA, 0xFFE5E8D0, 0xFFEFEFEF,
    ).map { it.toInt() }

    /** Frame tints; null keeps the artwork's own colours. */
    val frameTints: List<Int?> = listOf<Int?>(null) + listOf(
        0xFF000000, 0xFF5D1A1A, 0xFF8B5A00, 0xFFB8860B, 0xFF1F3A5F, 0xFF1E4D2B, 0xFF4A2C5E, 0xFF6B4226,
        0xFFB34700, 0xFF7A7A7A,
    ).map { it.toInt() }

    val text: List<Int?> = listOf(
        0xFF1F1F1F, 0xFF000000, 0xFF3E2723, 0xFF1F3A5F, 0xFF1E4D2B, 0xFF4A2C5E, 0xFF5D1A1A,
    ).map { it.toInt() }

    val accent: List<Int?> = listOf(
        0xFF7A1F1F, 0xFF5D1A1A, 0xFFB34700, 0xFF8B5A00, 0xFF1F3A5F, 0xFF1E4D2B, 0xFF4A2C5E, 0xFF000000,
    ).map { it.toInt() }
}
