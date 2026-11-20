package com.ereader.ripcardmaker.text

import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.Deceased
import com.ereader.ripcardmaker.model.Gender
import com.ereader.ripcardmaker.model.Meeting
import com.ereader.ripcardmaker.model.MeetingType
import java.time.LocalDate

/**
 * Everything language-specific: UI labels, card headings, honorifics and the
 * grammar used to build the card sentences.
 *
 * Sentences are assembled from optional parts so that a missing age, place or
 * date is simply left out instead of printing "..." on the card.
 */
abstract class LanguagePack {
    abstract val language: CardLanguage
    abstract val ui: UiStrings

    /** Honorifics for the given gender. The first entry is the default. */
    abstract fun honorifics(gender: Gender): List<String>

    abstract val defaultTitle: String
    abstract fun defaultMeetingTitle(type: MeetingType): String
    abstract val defaultFamilyTitle: String
    abstract val venueLabel: String

    /** Monday..Sunday. */
    abstract val weekdays: List<String>

    /** Zero digit of the script, or null if the language uses Latin digits only. */
    open val nativeZero: Char? = null

    abstract fun message(d: Deceased, f: Fmt): String
    abstract fun meetingText(m: Meeting, f: Fmt): String

    /** "4:00 PM" style time with the language's time-of-day word. */
    abstract fun timeSingle(minutes: Int, f: Fmt): String
    abstract fun timeRange(start: Int, end: Int, f: Fmt): String

    open fun decorateTitle(text: String) = "॥ $text ॥"
    open fun decorateSection(text: String) = "॥ $text ॥"
    open fun decorateFamily(text: String) = "✻ $text ✻"

    /** Shown in place of the name while it is still empty. */
    open val namePlaceholder: String = "……"

    fun defaultHonorific(gender: Gender) = honorifics(gender).first()

    fun resolvedHonorific(d: Deceased): String = d.honorific ?: defaultHonorific(d.gender)

    fun displayName(d: Deceased): String {
        val name = d.fullName.ifEmpty { return namePlaceholder }
        val hon = resolvedHonorific(d).trim()
        return if (hon.isEmpty()) name else "$hon $name"
    }

    /** Builds a time phrase for a meeting, or null when no start time is set. */
    fun meetingTime(m: Meeting, f: Fmt): String? {
        val start = m.startMinutes ?: return null
        val end = m.endMinutes
        return if (end == null || end == start) timeSingle(start, f) else timeRange(start, end, f)
    }

    // ── Shared helpers ──────────────────────────────────────────────────────

    protected fun hour12(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        val h12 = if (h % 12 == 0) 12 else h % 12
        return "%d:%02d".format(h12, m)
    }

    /** 0 morning, 1 afternoon, 2 evening, 3 night. */
    protected fun period(minutes: Int): Int {
        val h = (minutes / 60) % 24
        return when (h) {
            in 4..11 -> 0
            in 12..15 -> 1
            in 16..19 -> 2
            else -> 3
        }
    }
}

/** Formatting helpers bound to a language and the user's digit preference. */
class Fmt(val pack: LanguagePack, nativeDigits: Boolean) {
    private val zero: Char? = if (nativeDigits) pack.nativeZero else null

    /** Converts ASCII digits to the script's digits when enabled. */
    fun d(text: String): String {
        val z = zero ?: return text
        val sb = StringBuilder(text.length)
        for (c in text) sb.append(if (c in '0'..'9') z + (c - '0') else c)
        return sb.toString()
    }

    fun date(epochDay: Long): String {
        val dt = LocalDate.ofEpochDay(epochDay)
        return d("%02d/%02d/%04d".format(dt.dayOfMonth, dt.monthValue, dt.year))
    }

    fun weekday(epochDay: Long): String =
        pack.weekdays[LocalDate.ofEpochDay(epochDay).dayOfWeek.value - 1]
}

/** Final text blocks drawn on the card. */
data class CardContent(
    val title: String,
    val name: String,
    val message: String,
    val meetingTitle: String?,
    val meetingLines: List<String>,
    val familyTitle: String?,
    val familyLines: List<String>,
)

object CardTextBuilder {

    fun build(draft: CardDraft): CardContent {
        val pack = LanguagePacks.of(draft.language)
        val f = Fmt(pack, draft.style.nativeDigits)
        val texts = draft.texts
        val d = draft.deceased.copy(age = f.d(draft.deceased.age.trim()))

        val meeting = draft.meeting
        val hasMeeting = meeting.type != MeetingType.NONE
        val meetingLines = if (!hasMeeting) emptyList() else buildList {
            add(pack.meetingText(meeting, f))
            if (meeting.type == MeetingType.IN_PERSON && meeting.venue.isNotBlank()) {
                add("${pack.venueLabel}: ${meeting.venue.trim()}")
            }
        }

        val family = draft.family.filter { it.name.isNotBlank() }
        val familyLines = family.map { m ->
            buildString {
                append(m.name.trim())
                if (m.relation.isNotBlank()) append(" (").append(m.relation.trim()).append(")")
                if (m.phone.isNotBlank()) append("  –  ").append(f.d(m.phone.trim()))
            }
        }

        return CardContent(
            title = pack.decorateTitle(texts.title?.trim()?.ifEmpty { null } ?: pack.defaultTitle),
            name = pack.displayName(draft.deceased),
            message = texts.message?.trim()?.ifEmpty { null } ?: pack.message(d, f),
            meetingTitle = if (hasMeeting) {
                pack.decorateSection(texts.meetingTitle?.trim()?.ifEmpty { null } ?: pack.defaultMeetingTitle(meeting.type))
            } else null,
            meetingLines = meetingLines,
            familyTitle = if (familyLines.isNotEmpty()) {
                pack.decorateFamily(texts.familyTitle?.trim()?.ifEmpty { null } ?: pack.defaultFamilyTitle)
            } else null,
            familyLines = familyLines,
        )
    }

    /** The automatic message, used to pre-fill the "write my own" editor. */
    fun automaticMessage(draft: CardDraft): String {
        val pack = LanguagePacks.of(draft.language)
        val f = Fmt(pack, draft.style.nativeDigits)
        return pack.message(draft.deceased.copy(age = f.d(draft.deceased.age.trim())), f)
    }
}

object LanguagePacks {
    private val packs: Map<CardLanguage, LanguagePack> = listOf(
        EnglishPack, HindiPack, MarathiPack, GujaratiPack, TeluguPack,
    ).associateBy { it.language }

    fun of(language: CardLanguage): LanguagePack = packs.getValue(language)
}
