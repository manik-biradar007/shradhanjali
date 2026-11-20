package com.ereader.ripcardmaker.ui.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextDecrease
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardStyle
import com.ereader.ripcardmaker.model.CardTexts
import com.ereader.ripcardmaker.model.MeetingType
import com.ereader.ripcardmaker.text.CardTextBuilder
import com.ereader.ripcardmaker.text.LanguagePack
import com.ereader.ripcardmaker.ui.components.ColorSwatches
import com.ereader.ripcardmaker.ui.components.EditorSection
import com.ereader.ripcardmaker.ui.components.Palettes

@Composable
fun TextTab(draft: CardDraft, pack: LanguagePack, onUpdate: ((CardDraft) -> CardDraft) -> Unit) {
    val ui = pack.ui
    val s = draft.style
    fun style(t: (CardStyle) -> CardStyle) = onUpdate { it.copy(style = t(it.style)) }
    fun texts(t: (CardTexts) -> CardTexts) = onUpdate { it.copy(texts = t(it.texts)) }

    EditorSection(ui.textSize) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.TextDecrease, null)
            Slider(
                value = s.textScale,
                onValueChange = { v -> style { it.copy(textScale = v) } },
                valueRange = 0.8f..1.3f,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            Icon(Icons.Outlined.TextIncrease, null)
        }
    }
    EditorSection(ui.textColor) {
        ColorSwatches(Palettes.text, s.textColor, { c -> if (c != null) style { it.copy(textColor = c) } })
    }
    EditorSection(ui.accentColor) {
        ColorSwatches(Palettes.accent, s.accentColor, { c -> if (c != null) style { it.copy(accentColor = c) } })
    }

    if (pack.nativeZero != null) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(ui.nativeDigits, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(checked = s.nativeDigits, onCheckedChange = { v -> style { it.copy(nativeDigits = v) } })
        }
    }

    EditorSection(ui.headings) {
        HeadingField(ui.cardTitle, draft.texts.title, pack.defaultTitle) { v -> texts { it.copy(title = v) } }
        if (draft.meeting.type != MeetingType.NONE) {
            HeadingField(ui.meetingTitle, draft.texts.meetingTitle, pack.defaultMeetingTitle(draft.meeting.type)) { v ->
                texts { it.copy(meetingTitle = v) }
            }
        }
        HeadingField(ui.familyTitle, draft.texts.familyTitle, pack.defaultFamilyTitle) { v -> texts { it.copy(familyTitle = v) } }
    }

    EditorSection(ui.ownMessage) {
        val custom = draft.texts.message
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(ui.ownMessage, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(
                checked = custom != null,
                onCheckedChange = { on ->
                    texts { it.copy(message = if (on) CardTextBuilder.automaticMessage(draft) else null) }
                },
            )
        }
        if (custom != null) {
            OutlinedTextField(
                value = custom,
                onValueChange = { v -> texts { it.copy(message = v) } },
                label = { Text(ui.ownMessageHint) },
                minLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )
            TextButton(onClick = { texts { it.copy(message = null) } }) { Text(ui.resetAutomatic) }
        }
    }
}

/** Empty means "use the automatic heading", which is shown as the placeholder. */
@Composable
private fun HeadingField(label: String, value: String?, default: String, onChange: (String?) -> Unit) {
    OutlinedTextField(
        value = value.orEmpty(),
        onValueChange = { v -> onChange(v.ifEmpty { null }) },
        label = { Text(label) },
        placeholder = { Text(default) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    )
}
