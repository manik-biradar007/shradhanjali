package com.ereader.ripcardmaker.text

import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.Deceased
import com.ereader.ripcardmaker.model.Gender
import com.ereader.ripcardmaker.model.Meeting
import com.ereader.ripcardmaker.model.MeetingType

private fun LanguagePack.nameOrBlank(d: Deceased) = d.fullName.ifEmpty { namePlaceholder }

// ═════════════════════════════════════════════════════════════════════════════
// Kannada — ಅವರು / ನಿಧನರಾದರು are respectful for both genders;
// gender is expressed through the honorific (ದಿ. ಶ್ರೀ / ದಿ. ಶ್ರೀಮತಿ).
// ═════════════════════════════════════════════════════════════════════════════

object KannadaPack : LanguagePack() {
    override val language = CardLanguage.KANNADA
    override val ui = UiStrings.KANNADA
    override val nativeZero = '೦'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("ದಿ. ಶ್ರೀ", "ದಿ.", "ದಿ. ಡಾ.")
        Gender.FEMALE -> listOf("ದಿ. ಶ್ರೀಮತಿ", "ದಿ. ಕುಮಾರಿ", "ದಿ.", "ದಿ. ಡಾ.")
    }

    override val defaultTitle = "ಭಾವಪೂರ್ಣ ಶ್ರದ್ಧಾಂಜಲಿ"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "ದೂರವಾಣಿ ಮೂಲಕ ಸಂತಾಪ" else "ಶ್ರದ್ಧಾಂಜಲಿ ಸಭೆ"
    override val defaultFamilyTitle = "ದುಃಖತಪ್ತ ಕುಟುಂಬ ವರ್ಗ"
    override val venueLabel = "ಸ್ಥಳ"
    override val weekdays = listOf("ಸೋಮವಾರ", "ಮಂಗಳವಾರ", "ಬುಧವಾರ", "ಗುರುವಾರ", "ಶುಕ್ರವಾರ", "ಶನಿವಾರ", "ಭಾನುವಾರ")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("ಅತ್ಯಂತ ದುಃಖದಿಂದ ತಿಳಿಸುವುದೇನೆಂದರೆ,")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" ನಿವಾಸಿಗಳಾದ")
        append(" ನಮ್ಮ ಪ್ರೀತಿಯ ")
        append(nameOrBlank(d))
        if (d.age.isNotEmpty()) append(" (").append(d.age).append(" ವರ್ಷ)")
        if (d.fullName.isNotEmpty()) append(" ಅವರು")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append("ದಂದು") }
        append(" ನಿಧನರಾದರು.")
        append(" ಅವರ ಆತ್ಮಕ್ಕೆ ಭಗವಂತನು ಶಾಂತಿಯನ್ನು ಕರುಣಿಸಲಿ ಎಂದು ಪ್ರಾರ್ಥಿಸುತ್ತೇವೆ.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            if (m.type != MeetingType.TELEPHONIC) add("ಶ್ರದ್ಧಾಂಜಲಿ ಸಭೆಯನ್ನು")
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)}ದಂದು") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "ದೂರವಾಣಿ ಮೂಲಕ ಸಂತಾಪ ಸೂಚಿಸಬಹುದು."
                else "ಏರ್ಪಡಿಸಲಾಗಿದೆ."
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("ಬೆಳಿಗ್ಗೆ", "ಮಧ್ಯಾಹ್ನ", "ಸಂಜೆ", "ರಾತ್ರಿ")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} ಗಂಟೆಗೆ"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} ರಿಂದ $endText ರವರೆಗೆ"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Tamil — அவர்கள் / இறைவனடி சேர்ந்தார்கள் are respectful for both genders;
// gender is expressed through the honorific (அமரர் திரு. / அமரர் திருமதி).
// ═════════════════════════════════════════════════════════════════════════════

object TamilPack : LanguagePack() {
    override val language = CardLanguage.TAMIL
    override val ui = UiStrings.TAMIL
    override val nativeZero = '௦'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("அமரர் திரு.", "அமரர்", "அமரர் டாக்டர்")
        Gender.FEMALE -> listOf("அமரர் திருமதி", "அமரர் செல்வி", "அமரர்", "அமரர் டாக்டர்")
    }

    override val defaultTitle = "கண்ணீர் அஞ்சலி"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "தொலைபேசி மூலம் இரங்கல்" else "இரங்கல் கூட்டம்"
    override val defaultFamilyTitle = "துயருறும் குடும்பத்தினர்"
    override val venueLabel = "இடம்"
    override val weekdays = listOf(
        "திங்கள்கிழமை", "செவ்வாய்க்கிழமை", "புதன்கிழமை", "வியாழக்கிழமை",
        "வெள்ளிக்கிழமை", "சனிக்கிழமை", "ஞாயிற்றுக்கிழமை",
    )

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("ஆழ்ந்த வருத்தத்துடன் தெரிவித்துக்கொள்வது,")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" ஊரைச் சேர்ந்த")
        append(" எங்கள் அன்புக்குரிய ")
        append(nameOrBlank(d))
        if (d.age.isNotEmpty()) append(" (வயது ").append(d.age).append(")")
        if (d.fullName.isNotEmpty()) append(" அவர்கள்")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" அன்று") }
        append(" இறைவனடி சேர்ந்தார்கள்.")
        append(" அவரது ஆன்மா சாந்தியடைய இறைவனைப் பிரார்த்திக்கிறோம்.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            if (m.type != MeetingType.TELEPHONIC) add("இரங்கல் கூட்டம்")
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)} அன்று") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "தொலைபேசி மூலம் இரங்கல் தெரிவிக்கலாம்."
                else "நடைபெறும்."
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("காலை", "மதியம்", "மாலை", "இரவு")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} மணிக்கு"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} மணி முதல் $endText மணி வரை"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Bengali — verbs carry no gender; gender is shown by the honorific
// (স্বর্গীয় for men, স্বর্গীয়া for women).
// ═════════════════════════════════════════════════════════════════════════════

object BengaliPack : LanguagePack() {
    override val language = CardLanguage.BENGALI
    override val ui = UiStrings.BENGALI
    override val nativeZero = '০'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("স্বর্গীয়", "স্বর্গীয় শ্রী", "প্রয়াত", "স্বর্গীয় ডাঃ")
        Gender.FEMALE -> listOf("স্বর্গীয়া", "স্বর্গীয়া শ্রীমতী", "স্বর্গীয়া কুমারী", "প্রয়াতা", "স্বর্গীয়া ডাঃ")
    }

    override val defaultTitle = "বিনম্র শ্রদ্ধাঞ্জলি"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "দূরভাষে সমবেদনা" else "স্মরণসভা"
    override val defaultFamilyTitle = "শোকসন্তপ্ত পরিবারবর্গ"
    override val venueLabel = "স্থান"
    override val weekdays = listOf("সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার", "রবিবার")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("গভীর দুঃখের সঙ্গে জানাচ্ছি যে,")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" নিবাসী")
        append(" আমাদের প্রিয় ")
        append(nameOrBlank(d))
        if (d.age.isNotEmpty()) append(" (বয়স ").append(d.age).append(" বছর)")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" দিন") }
        append(" পরলোকগমন করেছেন।")
        append(" তাঁর আত্মার চিরশান্তি কামনা করি।")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            if (m.type != MeetingType.TELEPHONIC) add("স্মরণসভা")
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)}") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "দূরভাষে সমবেদনা জানাতে পারেন।"
                else "অনুষ্ঠিত হবে।"
            )
        }
        return parts.joinToString(" ")
    }

    /** Bengali splits the day more finely: বিকেল (4–6 PM) comes before সন্ধ্যা. */
    private fun bnPeriod(minutes: Int): String = when ((minutes / 60) % 24) {
        in 4..11 -> "সকাল"
        in 12..15 -> "দুপুর"
        in 16..17 -> "বিকেল"
        in 18..19 -> "সন্ধ্যা"
        else -> "রাত"
    }
    private fun clock(minutes: Int, f: Fmt) = "${bnPeriod(minutes)} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)}টায়"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (bnPeriod(start) == bnPeriod(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)}টা থেকে ${endText}টা পর্যন্ত"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Punjabi (Gurmukhi) — ਸਾਡੇ ਸਤਿਕਾਰਯੋਗ … ਜੀ and ਅਕਾਲ ਚਲਾਣਾ ਕਰ ਗਏ ਹਨ are the
// respectful forms used for both men and women; gender is in the honorific.
// ═════════════════════════════════════════════════════════════════════════════

object PunjabiPack : LanguagePack() {
    override val language = CardLanguage.PUNJABI
    override val ui = UiStrings.PUNJABI
    override val nativeZero = '੦'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("ਸਵ. ਸ੍ਰੀ", "ਸਵਰਗੀ", "ਸਵ. ਸਰਦਾਰ", "ਸਵ. ਡਾ.")
        Gender.FEMALE -> listOf("ਸਵ. ਸ੍ਰੀਮਤੀ", "ਸਵਰਗੀ", "ਸਵ. ਸਰਦਾਰਨੀ", "ਸਵ. ਬੀਬੀ", "ਸਵ. ਡਾ.")
    }

    override val defaultTitle = "ਭਾਵਪੂਰਨ ਸ਼ਰਧਾਂਜਲੀ"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "ਫ਼ੋਨ ਰਾਹੀਂ ਦੁੱਖ ਸਾਂਝਾ" else "ਸ਼ਰਧਾਂਜਲੀ ਸਮਾਗਮ"
    override val defaultFamilyTitle = "ਸ਼ੋਕਗ੍ਰਸਤ ਪਰਿਵਾਰ"
    override val venueLabel = "ਸਥਾਨ"
    override val weekdays = listOf("ਸੋਮਵਾਰ", "ਮੰਗਲਵਾਰ", "ਬੁੱਧਵਾਰ", "ਵੀਰਵਾਰ", "ਸ਼ੁੱਕਰਵਾਰ", "ਸ਼ਨਿੱਚਰਵਾਰ", "ਐਤਵਾਰ")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("ਬੜੇ ਦੁੱਖ ਨਾਲ ਸੂਚਿਤ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਕਿ")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" ਨਿਵਾਸੀ")
        append(" ਸਾਡੇ ਸਤਿਕਾਰਯੋਗ ")
        val name = nameOrBlank(d)
        append(name)
        if (d.fullName.isNotEmpty() && !name.endsWith("ਜੀ")) append(" ਜੀ")
        if (d.age.isNotEmpty()) append(" (ਉਮਰ ").append(d.age).append(" ਸਾਲ)")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" ਨੂੰ") }
        append(" ਅਕਾਲ ਚਲਾਣਾ ਕਰ ਗਏ ਹਨ।")
        append(" ਪਰਮਾਤਮਾ ਵਿੱਛੜੀ ਰੂਹ ਨੂੰ ਸ਼ਾਂਤੀ ਬਖ਼ਸ਼ੇ।")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            if (m.type != MeetingType.TELEPHONIC) add("ਸ਼ਰਧਾਂਜਲੀ ਸਮਾਗਮ")
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)} ਨੂੰ") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "ਫ਼ੋਨ ਰਾਹੀਂ ਦੁੱਖ ਸਾਂਝਾ ਕੀਤਾ ਜਾ ਸਕਦਾ ਹੈ।"
                else "ਹੋਵੇਗਾ।"
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("ਸਵੇਰੇ", "ਦੁਪਹਿਰ", "ਸ਼ਾਮ", "ਰਾਤ")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} ਵਜੇ"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} ਵਜੇ ਤੋਂ $endText ਵਜੇ ਤੱਕ"
    }
}
