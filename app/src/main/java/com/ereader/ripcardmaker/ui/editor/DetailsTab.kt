package com.ereader.ripcardmaker.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.Deceased
import com.ereader.ripcardmaker.model.FamilyMember
import com.ereader.ripcardmaker.model.Gender
import com.ereader.ripcardmaker.model.Meeting
import com.ereader.ripcardmaker.model.MeetingType
import com.ereader.ripcardmaker.text.Fmt
import com.ereader.ripcardmaker.text.LanguagePack
import com.ereader.ripcardmaker.ui.components.EditorSection
import com.ereader.ripcardmaker.ui.components.VSpace

private const val DAY_MS = 86_400_000L

@Composable
fun DetailsTab(draft: CardDraft, pack: LanguagePack, onUpdate: ((CardDraft) -> CardDraft) -> Unit) {
    val ui = pack.ui
    val fmt = Fmt(pack, nativeDigits = false)
    fun deceased(t: (Deceased) -> Deceased) = onUpdate { it.copy(deceased = t(it.deceased)) }
    fun meeting(t: (Meeting) -> Meeting) = onUpdate { it.copy(meeting = t(it.meeting)) }

    DeceasedSection(draft.deceased, pack, fmt, ::deceased)
    MeetingSection(draft.meeting, pack, fmt, ::meeting)
    FamilySection(draft.family, pack, onChange = { list -> onUpdate { it.copy(family = list) } })
}

// ── Deceased ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DeceasedSection(d: Deceased, pack: LanguagePack, fmt: Fmt, update: ((Deceased) -> Deceased) -> Unit) {
    val ui = pack.ui
    EditorSection(ui.sectionDeceased) {
        Text(ui.gender, style = MaterialTheme.typography.labelLarge)
        VSpace(4.dp)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf(Gender.MALE to ui.male, Gender.FEMALE to ui.female).forEachIndexed { i, (g, label) ->
                SegmentedButton(
                    selected = d.gender == g,
                    // A new gender means the old honorific no longer fits: go back to the default.
                    onClick = { if (d.gender != g) update { it.copy(gender = g, honorific = null) } },
                    shape = SegmentedButtonDefaults.itemShape(i, 2),
                ) { Text(label) }
            }
        }

        VSpace(12.dp)
        Text(ui.honorific, style = MaterialTheme.typography.labelLarge)
        VSpace(4.dp)
        val selectedHonorific = pack.resolvedHonorific(d)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pack.honorifics(d.gender).forEach { h ->
                FilterChip(selected = selectedHonorific == h, onClick = { update { it.copy(honorific = h) } }, label = { Text(h) })
            }
            FilterChip(selected = selectedHonorific.isEmpty(), onClick = { update { it.copy(honorific = "") } }, label = { Text(ui.noHonorific) })
        }

        VSpace(8.dp)
        NameField(ui.firstName, d.firstName) { v -> update { it.copy(firstName = v) } }
        NameField(ui.middleName, d.middleName) { v -> update { it.copy(middleName = v) } }
        NameField(ui.surname, d.surname) { v -> update { it.copy(surname = v) } }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = d.age,
                onValueChange = { v -> update { it.copy(age = v.filter(Char::isDigit).take(3)) } },
                label = { Text(ui.age) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.weight(0.35f).padding(vertical = 4.dp),
            )
            NameField(ui.place, d.place, Modifier.weight(0.65f)) { v -> update { it.copy(place = v) } }
        }

        DateField(
            label = ui.dateOfDeath,
            placeholder = ui.selectDate,
            epochDay = d.dateOfDeath,
            fmt = fmt,
            okLabel = ui.ok,
            cancelLabel = ui.cancel,
            onChange = { day -> update { it.copy(dateOfDeath = day) } },
        )
    }
}

// ── Meeting ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingSection(m: Meeting, pack: LanguagePack, fmt: Fmt, update: ((Meeting) -> Meeting) -> Unit) {
    val ui = pack.ui
    EditorSection(ui.sectionMeeting) {
        val options = listOf(
            MeetingType.IN_PERSON to ui.meetingInPerson,
            MeetingType.TELEPHONIC to ui.meetingPhone,
            MeetingType.NONE to ui.meetingNone,
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { i, (type, label) ->
                SegmentedButton(
                    selected = m.type == type,
                    onClick = { update { it.copy(type = type) } },
                    shape = SegmentedButtonDefaults.itemShape(i, options.size),
                ) { Text(label, maxLines = 1) }
            }
        }

        if (m.type != MeetingType.NONE) {
            VSpace(8.dp)
            DateField(
                label = ui.meetingDate,
                placeholder = ui.selectDate,
                epochDay = m.date,
                fmt = fmt,
                    okLabel = ui.ok,
                cancelLabel = ui.cancel,
                onChange = { day -> update { it.copy(date = day) } },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TimeField(ui.startTime, m.startMinutes, pack, fmt, ui.ok, ui.cancel, Modifier.weight(1f)) { v ->
                    update { it.copy(startMinutes = v) }
                }
                TimeField(ui.endTime, m.endMinutes, pack, fmt, ui.ok, ui.cancel, Modifier.weight(1f)) { v ->
                    update { it.copy(endMinutes = v) }
                }
            }
            if (m.type == MeetingType.IN_PERSON) {
                OutlinedTextField(
                    value = m.venue,
                    onValueChange = { v -> update { it.copy(venue = v) } },
                    label = { Text(ui.venue) },
                    minLines = 2,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                )
            }
        }
    }
}

// ── Family ──────────────────────────────────────────────────────────────────

@Composable
private fun FamilySection(family: List<FamilyMember>, pack: LanguagePack, onChange: (List<FamilyMember>) -> Unit) {
    val ui = pack.ui
    EditorSection(ui.sectionFamily) {
        Text(ui.familyHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(8.dp)
        family.forEachIndexed { index, member ->
            fun set(m: FamilyMember) = onChange(family.toMutableList().also { it[index] = m })
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            ) {
                Column(Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NameField(ui.name, member.name, Modifier.weight(1f)) { set(member.copy(name = it)) }
                        IconButton(onClick = { onChange(family.toMutableList().also { it.removeAt(index) }) }) {
                            Icon(Icons.Outlined.Close, contentDescription = ui.removeMember)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(end = 8.dp)) {
                        NameField(ui.relation, member.relation, Modifier.weight(1f)) { set(member.copy(relation = it)) }
                        OutlinedTextField(
                            value = member.phone,
                            onValueChange = { v -> set(member.copy(phone = v.filter { c -> c.isDigit() || c in "+- " }.take(16))) },
                            label = { Text(ui.phone) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
        if (family.size < CardDraft.MAX_FAMILY) {
            OutlinedButton(onClick = { onChange(family + FamilyMember()) }, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Filled.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(ui.addMember)
            }
        }
    }
}

// ── Fields ──────────────────────────────────────────────────────────────────

@Composable
private fun NameField(label: String, value: String, modifier: Modifier = Modifier.fillMaxWidth(), onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        modifier = modifier.padding(vertical = 4.dp),
    )
}

/** Read-only field that opens a picker when tapped. */
@Composable
private fun PickerField(label: String, text: String, icon: @Composable () -> Unit, onClick: () -> Unit, onClear: (() -> Unit)?, modifier: Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
    ) {
        icon()
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
        if (onClear != null) {
            IconButton(onClick = onClear) { Icon(Icons.Outlined.Close, null) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    label: String, placeholder: String, epochDay: Long?, fmt: Fmt,
    okLabel: String, cancelLabel: String, onChange: (Long?) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val text = epochDay?.let { "${fmt.date(it)}, ${fmt.weekday(it)}" } ?: placeholder
    PickerField(
        label = label,
        text = text,
        icon = { Icon(Icons.Outlined.CalendarMonth, null) },
        onClick = { open = true },
        onClear = if (epochDay != null) ({ onChange(null) }) else null,
        modifier = Modifier.fillMaxWidth(),
    )
    if (open) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (epochDay ?: (System.currentTimeMillis() / DAY_MS)) * DAY_MS,
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onChange(Math.floorDiv(it, DAY_MS)) }
                    open = false
                }) { Text(okLabel) }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text(cancelLabel) } },
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    label: String, minutes: Int?, pack: LanguagePack, fmt: Fmt,
    okLabel: String, cancelLabel: String, modifier: Modifier, onChange: (Int?) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val text = minutes?.let { pack.timeSingle(it, fmt) } ?: "—"
    PickerField(
        label = label,
        text = text,
        icon = { Icon(Icons.Outlined.Schedule, null) },
        onClick = { open = true },
        onClear = if (minutes != null) ({ onChange(null) }) else null,
        modifier = modifier,
    )
    if (open) {
        val state = rememberTimePickerState(
            initialHour = (minutes ?: (16 * 60)) / 60,
            initialMinute = (minutes ?: 0) % 60,
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = { onChange(state.hour * 60 + state.minute); open = false }) { Text(okLabel) }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text(cancelLabel) } },
            text = { TimePicker(state = state) },
        )
    }
}
