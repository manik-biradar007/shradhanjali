package com.ereader.ripcardmaker.text

import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.CardStyle
import com.ereader.ripcardmaker.model.CardTexts
import com.ereader.ripcardmaker.model.Deceased
import com.ereader.ripcardmaker.model.FamilyMember
import com.ereader.ripcardmaker.model.Gender
import com.ereader.ripcardmaker.model.Meeting
import com.ereader.ripcardmaker.model.MeetingType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate

class CardTextBuilderTest {

    private val monday = LocalDate.of(2025, 5, 12).toEpochDay()
    private val wednesday = LocalDate.of(2025, 5, 14).toEpochDay()

    private fun draft(
        language: CardLanguage,
        gender: Gender,
        full: Boolean = true,
        meetingType: MeetingType = MeetingType.IN_PERSON,
        nativeDigits: Boolean = false,
    ) = CardDraft(
        id = "t",
        language = language,
        deceased = Deceased(
            gender = gender,
            firstName = if (gender == Gender.MALE) "Ramesh" else "Sunita",
            middleName = if (full) "Kumar" else "",
            surname = if (full) "Patil" else "",
            age = if (full) "72" else "",
            place = if (full) "Pune" else "",
            dateOfDeath = if (full) monday else null,
        ),
        meeting = Meeting(type = meetingType, date = wednesday, startMinutes = 16 * 60, endMinutes = 18 * 60, venue = "Ganesh Hall"),
        family = listOf(FamilyMember("Suresh Patil", "Son", "9876543210")),
        style = CardStyle(nativeDigits = nativeDigits),
    )

    // ── Gender agreement ────────────────────────────────────────────────────

    @Test fun hindi_male_and_female_agree() {
        val m = CardTextBuilder.build(draft(CardLanguage.HINDI, Gender.MALE))
        assertTrue(m.message.contains("हमारे प्रिय"))
        assertTrue(m.message.contains("स्वर्गवासी हो गए हैं"))
        assertTrue(m.name.startsWith("स्व. श्री "))

        val f = CardTextBuilder.build(draft(CardLanguage.HINDI, Gender.FEMALE))
        assertTrue(f.message.contains("हमारी प्रिय"))
        assertTrue(f.message.contains("स्वर्गवासी हो गई हैं"))
        assertFalse(f.message.contains("हो गए"))
        assertTrue(f.name.startsWith("स्व. श्रीमती "))
    }

    @Test fun marathi_male_and_female_agree() {
        val m = CardTextBuilder.build(draft(CardLanguage.MARATHI, Gender.MALE))
        assertTrue(m.message.contains("आमचे प्रिय"))
        assertTrue(m.name.startsWith("कै. "))
        val f = CardTextBuilder.build(draft(CardLanguage.MARATHI, Gender.FEMALE))
        assertTrue(f.message.contains("आमच्या प्रिय"))
        assertTrue(f.name.startsWith("कै. सौ. "))
    }

    @Test fun english_pronoun_follows_gender() {
        assertTrue(CardTextBuilder.build(draft(CardLanguage.ENGLISH, Gender.MALE)).message.contains("grant his soul"))
        val f = CardTextBuilder.build(draft(CardLanguage.ENGLISH, Gender.FEMALE))
        assertTrue(f.message.contains("grant her soul"))
        assertTrue(f.name.startsWith("Late Smt. "))
    }

    @Test fun gujarati_and_telugu_honorifics_follow_gender() {
        assertTrue(CardTextBuilder.build(draft(CardLanguage.GUJARATI, Gender.FEMALE)).name.startsWith("સ્વ. "))
        assertTrue(CardTextBuilder.build(draft(CardLanguage.TELUGU, Gender.MALE)).name.startsWith("కీ.శే. శ్రీ "))
        assertTrue(CardTextBuilder.build(draft(CardLanguage.TELUGU, Gender.FEMALE)).name.startsWith("కీ.శే. శ్రీమతి "))
    }

    @Test fun chosen_or_empty_honorific_is_respected() {
        val d = draft(CardLanguage.MARATHI, Gender.FEMALE)
        assertEquals("कै. गं. भा. Sunita Kumar Patil",
            CardTextBuilder.build(d.copy(deceased = d.deceased.copy(honorific = "कै. गं. भा."))).name)
        assertEquals("Sunita Kumar Patil", CardTextBuilder.build(d.copy(deceased = d.deceased.copy(honorific = ""))).name)
    }

    // ── Optional parts ──────────────────────────────────────────────────────

    @Test fun missing_fields_are_left_out_not_dotted() {
        for (lang in CardLanguage.entries) for (g in Gender.entries) {
            val c = CardTextBuilder.build(draft(lang, g, full = false))
            assertFalse("$lang $g: ${c.message}", c.message.contains("..."))
            assertFalse("$lang $g: ${c.message}", c.message.contains("()"))
            assertFalse("$lang $g: ${c.message}", c.message.contains("  "))
            assertTrue("$lang $g: ${c.message}", c.message.contains(if (g == Gender.MALE) "Ramesh" else "Sunita"))
        }
        val hi = CardTextBuilder.build(draft(CardLanguage.HINDI, Gender.MALE, full = false)).message
        assertFalse(hi.contains("आयु"))
        assertFalse(hi.contains("निवासी"))
        assertFalse(hi.contains("दिनांक"))
    }

    @Test fun empty_name_shows_placeholder() {
        val d = draft(CardLanguage.HINDI, Gender.MALE).let { it.copy(deceased = it.deceased.copy(firstName = "", middleName = "", surname = "")) }
        val c = CardTextBuilder.build(d)
        assertEquals("……", c.name)
        assertFalse(c.message.contains(" जी"))
    }

    @Test fun no_meeting_and_no_family_hide_sections() {
        val d = draft(CardLanguage.ENGLISH, Gender.MALE, meetingType = MeetingType.NONE).copy(family = listOf(FamilyMember()))
        val c = CardTextBuilder.build(d)
        assertNull(c.meetingTitle)
        assertTrue(c.meetingLines.isEmpty())
        assertNull(c.familyTitle)
    }

    // ── Dates, times, digits ────────────────────────────────────────────────

    @Test fun weekday_is_computed_from_date() {
        assertTrue(CardTextBuilder.build(draft(CardLanguage.ENGLISH, Gender.MALE)).message.contains("on Monday, 12/05/2025"))
        assertTrue(CardTextBuilder.build(draft(CardLanguage.MARATHI, Gender.MALE)).message.contains("12/05/2025, सोमवार रोजी"))
    }

    @Test fun time_phrases_use_time_of_day_words() {
        val hi = HindiPack
        val f = Fmt(hi, false)
        assertEquals("शाम 4:00 बजे से 6:00 बजे तक", hi.timeRange(16 * 60, 18 * 60, f))
        assertEquals("दोपहर 12:30 बजे से शाम 4:00 बजे तक", hi.timeRange(12 * 60 + 30, 16 * 60, f))
        assertEquals("सुबह 10:00 बजे", hi.timeSingle(10 * 60, f))
        assertEquals("from 4:00 PM to 6:00 PM", EnglishPack.timeRange(16 * 60, 18 * 60, Fmt(EnglishPack, false)))
        assertEquals("at 12:00 AM", EnglishPack.timeSingle(0, Fmt(EnglishPack, false)))
    }

    @Test fun native_digits_convert_numbers() {
        val c = CardTextBuilder.build(draft(CardLanguage.MARATHI, Gender.MALE, nativeDigits = true))
        assertTrue(c.message.contains("१२/०५/२०२५"))
        assertTrue(c.message.contains("वय ७२ वर्षे"))
        assertTrue(c.familyLines.first().contains("९८७६५४३२१०"))
        val g = CardTextBuilder.build(draft(CardLanguage.GUJARATI, Gender.MALE, nativeDigits = true))
        assertTrue(g.message.contains("૧૨/૦૫/૨૦૨૫"))
    }

    @Test fun custom_texts_override_automatic_ones() {
        val d = draft(CardLanguage.ENGLISH, Gender.MALE).copy(texts = CardTexts(title = "Om Shanti", message = "Custom words."))
        val c = CardTextBuilder.build(d)
        assertEquals("~ Om Shanti ~", c.title)
        assertEquals("Custom words.", c.message)
    }

    /** Writes example output for every language and gender, for human review. */
    @Test fun write_samples() {
        val out = StringBuilder()
        for (lang in CardLanguage.entries) for (g in Gender.entries) for (type in listOf(MeetingType.IN_PERSON, MeetingType.TELEPHONIC)) {
            if (type == MeetingType.TELEPHONIC && g == Gender.FEMALE) continue
            val c = CardTextBuilder.build(draft(lang, g, meetingType = type))
            out.appendLine("===== $lang · $g · $type")
            out.appendLine(c.title)
            out.appendLine(c.name)
            out.appendLine(c.message)
            out.appendLine(c.meetingTitle)
            c.meetingLines.forEach { out.appendLine(it) }
            out.appendLine(c.familyTitle)
            c.familyLines.forEach { out.appendLine(it) }
            out.appendLine()
        }
        File("build/card-text-samples.txt").apply { parentFile?.mkdirs() }.writeText(out.toString())
    }
}
