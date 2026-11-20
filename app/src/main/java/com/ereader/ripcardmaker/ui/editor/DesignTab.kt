package com.ereader.ripcardmaker.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardStyle
import com.ereader.ripcardmaker.render.AssetCatalog
import com.ereader.ripcardmaker.text.UiStrings
import com.ereader.ripcardmaker.ui.components.AssetStrip
import com.ereader.ripcardmaker.ui.components.ColorSwatches
import com.ereader.ripcardmaker.ui.components.EditorSection
import com.ereader.ripcardmaker.ui.components.Palettes

@Composable
fun DesignTab(draft: CardDraft, ui: UiStrings, onUpdate: ((CardDraft) -> CardDraft) -> Unit) {
    val s = draft.style
    fun style(t: (CardStyle) -> CardStyle) = onUpdate { it.copy(style = t(it.style)) }

    EditorSection(ui.frame) {
        AssetStrip(AssetCatalog.frames, s.frameId, ui.none, 60.dp, 100.dp, { id -> style { it.copy(frameId = id) } })
    }
    EditorSection(ui.frameColor) {
        ColorSwatches(Palettes.frameTints, s.frameTint, { c -> style { it.copy(frameTint = c) } }, nullLabel = ui.original)
    }
    EditorSection(ui.background) {
        ColorSwatches(Palettes.backgrounds, s.backgroundColor, { c -> if (c != null) style { it.copy(backgroundColor = c) } })
    }
    EditorSection(ui.pattern) {
        AssetStrip(AssetCatalog.patterns, s.patternId, ui.none, 60.dp, 100.dp, { id -> style { it.copy(patternId = id) } },
            tileColor = androidx.compose.ui.graphics.Color(s.backgroundColor))
    }
    EditorSection(ui.ring) {
        AssetStrip(AssetCatalog.rings, s.ringId, ui.none, 84.dp, 84.dp, { id -> style { it.copy(ringId = id) } })
    }
    EditorSection(ui.diya) {
        AssetStrip(AssetCatalog.diyas, s.diyaId, ui.none, 64.dp, 84.dp, { id -> style { it.copy(diyaId = id) } })
    }
}
