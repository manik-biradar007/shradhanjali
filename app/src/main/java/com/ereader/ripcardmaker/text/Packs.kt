package com.ereader.ripcardmaker.text

import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.Deceased
import com.ereader.ripcardmaker.model.Gender
import com.ereader.ripcardmaker.model.Meeting
import com.ereader.ripcardmaker.model.MeetingType

private fun LanguagePack.nameOrPlaceholder(d: Deceased) = d.fullName.ifEmpty { namePlaceholder }

// ═════════════════════════════════════════════════════════════════════════════
// English
// ═════════════════════════════════════════════════════════════════════════════

object EnglishPack : LanguagePack() {
    override val language = CardLanguage.ENGLISH
    override val ui = UiStrings.ENGLISH

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("Late Shri", "Late Mr.", "Late Dr.", "Late")
        Gender.FEMALE -> listOf("Late Smt.", "Late Mrs.", "Late Ms.", "Late Dr.", "Late")
    }

    override val defaultTitle = "In Loving Memory"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "Telephonic Condolence" else "Prayer Meeting"
    override val defaultFamilyTitle = "Grieving Family"
    override val venueLabel = "Venue"
    override val weekdays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun decorateTitle(text: String) = "~ $text ~"
    override fun decorateSection(text: String) = text

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("With deep sorrow, we inform you of the passing of our beloved ")
        append(nameOrPlaceholder(d))
        if (d.age.isNotEmpty()) append(" (aged ").append(d.age).append(")")
        if (d.place.isNotBlank()) append(", resident of ").append(d.place.trim())
        d.dateOfDeath?.let { append(", on ").append(f.weekday(it)).append(", ").append(f.date(it)) }
        append(". May God grant ")
        append(if (d.gender == Gender.FEMALE) "her" else "his")
        append(" soul eternal peace.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String = buildString {
        append(
            if (m.type == MeetingType.TELEPHONIC) "Condolences may be conveyed by phone"
            else "A prayer meeting will be held"
        )
        m.date?.let { append(" on ").append(f.weekday(it)).append(", ").append(f.date(it)) }
        meetingTime(m, f)?.let { append(" ").append(it) }
        append(".")
    }

    private fun clock(minutes: Int, f: Fmt): String {
        val h = (minutes / 60) % 24
        return f.d(hour12(minutes)) + if (h < 12) " AM" else " PM"
    }

    override fun timeSingle(minutes: Int, f: Fmt) = "at ${clock(minutes, f)}"
    override fun timeRange(start: Int, end: Int, f: Fmt) = "from ${clock(start, f)} to ${clock(end, f)}"
}

// ═════════════════════════════════════════════════════════════════════════════
// Hindi — possessive and verb agree with gender:
//   male:   हमारे प्रिय … स्वर्गवासी हो गए हैं
//   female: हमारी प्रिय … स्वर्गवासी हो गई हैं
// ═════════════════════════════════════════════════════════════════════════════

object HindiPack : LanguagePack() {
    override val language = CardLanguage.HINDI
    override val ui = UiStrings.HINDI
    override val nativeZero = '०'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("स्व. श्री", "स्वर्गीय श्री", "स्व. डॉ.", "स्व.")
        Gender.FEMALE -> listOf("स्व. श्रीमती", "स्वर्गीय श्रीमती", "स्व. सुश्री", "स्व. डॉ.", "स्व.")
    }

    override val defaultTitle = "भावपूर्ण श्रद्धांजलि"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "दूरभाष पर शोक संवेदना" else "श्रद्धांजलि सभा"
    override val defaultFamilyTitle = "शोकाकुल परिवार"
    override val venueLabel = "स्थान"
    override val weekdays = listOf("सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार", "रविवार")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        val female = d.gender == Gender.FEMALE
        append("अत्यंत दुःख के साथ सूचित किया जाता है कि")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" निवासी")
        append(if (female) " हमारी प्रिय " else " हमारे प्रिय ")
        val name = nameOrPlaceholder(d)
        append(name)
        if (d.fullName.isNotEmpty() && !name.endsWith("जी")) append(" जी")
        if (d.age.isNotEmpty()) append(" (आयु ").append(d.age).append(" वर्ष)")
        d.dateOfDeath?.let { append(" दिनांक ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" को") }
        append(if (female) " स्वर्गवासी हो गई हैं।" else " स्वर्गवासी हो गए हैं।")
        append(" ईश्वर दिवंगत आत्मा को शांति प्रदान करें एवं शोकाकुल परिवार को यह दुःख सहन करने की शक्ति दें।")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            m.date?.let { add("दिनांक ${f.date(it)}, ${f.weekday(it)} को") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "दूरभाष पर शोक संवेदना व्यक्त की जा सकती है।"
                else "श्रद्धांजलि सभा रखी गई है।"
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("सुबह", "दोपहर", "शाम", "रात")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} बजे"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} बजे से $endText बजे तक"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Marathi — honorific plural possessive agrees with gender:
//   male: आमचे प्रिय …   female: आमच्या प्रिय …
// ═════════════════════════════════════════════════════════════════════════════

object MarathiPack : LanguagePack() {
    override val language = CardLanguage.MARATHI
    override val ui = UiStrings.MARATHI
    override val nativeZero = '०'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("कै.", "स्व.", "कै. डॉ.")
        Gender.FEMALE -> listOf("कै. सौ.", "कै. गं. भा.", "कै. कु.", "कै.", "स्व.")
    }

    override val defaultTitle = "भावपूर्ण श्रद्धांजली"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "दूरध्वनीवरून सांत्वन" else "श्रद्धांजली सभा"
    override val defaultFamilyTitle = "शोकाकुल परिवार"
    override val venueLabel = "स्थळ"
    override val weekdays = listOf("सोमवार", "मंगळवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार", "रविवार")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("अत्यंत जड अंतःकरणाने कळविण्यात येते की,")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" येथील रहिवासी")
        append(if (d.gender == Gender.FEMALE) " आमच्या प्रिय " else " आमचे प्रिय ")
        append(nameOrPlaceholder(d))
        if (d.age.isNotEmpty()) append(" (वय ").append(d.age).append(" वर्षे)")
        append(" यांचे")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" रोजी") }
        append(" दुःखद निधन झाले.")
        append(" परमेश्वर त्यांच्या आत्म्यास चिरशांती देवो, हीच ईश्वरचरणी प्रार्थना.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)} रोजी") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "दूरध्वनीवरून सांत्वन करता येईल."
                else "श्रद्धांजली सभा आयोजित करण्यात आली आहे."
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("सकाळी", "दुपारी", "सायंकाळी", "रात्री")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} वाजता"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} ते $endText वाजेपर्यंत"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Gujarati — the respectful forms (અમારા, પામ્યા છે) suit both genders;
// gender is expressed through the honorific (સ્વ. / સ્વ. અ.સૌ. / સ્વ. ગં.સ્વ.).
// ═════════════════════════════════════════════════════════════════════════════

object GujaratiPack : LanguagePack() {
    override val language = CardLanguage.GUJARATI
    override val ui = UiStrings.GUJARATI
    override val nativeZero = '૦'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("સ્વ.", "સ્વ. શ્રી", "સ્વ. ડૉ.")
        Gender.FEMALE -> listOf("સ્વ.", "સ્વ. અ.સૌ.", "સ્વ. ગં.સ્વ.", "સ્વ. શ્રીમતી", "સ્વ. ડૉ.")
    }

    override val defaultTitle = "ભાવભીની શ્રદ્ધાંજલિ"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "ટેલિફોનિક બેસણું" else "બેસણું"
    override val defaultFamilyTitle = "શોકાતુર પરિવાર"
    override val venueLabel = "સ્થળ"
    override val weekdays = listOf("સોમવાર", "મંગળવાર", "બુધવાર", "ગુરુવાર", "શુક્રવાર", "શનિવાર", "રવિવાર")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("અત્યંત દુઃખ સાથે જણાવવાનું કે")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" નિવાસી")
        append(" અમારા પ્રિય ")
        append(nameOrPlaceholder(d))
        if (d.age.isNotEmpty()) append(" (ઉં.વ. ").append(d.age).append(")")
        d.dateOfDeath?.let { append(" તા. ").append(f.date(it)).append(", ").append(f.weekday(it)).append("ના રોજ") }
        append(" દેવલોક પામ્યા છે.")
        append(" પરમ કૃપાળુ પરમાત્મા તેમના દિવ્ય આત્માને શાંતિ અર્પે એ જ પ્રાર્થના.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            add(if (m.type == MeetingType.TELEPHONIC) "સદ્ગતનું ટેલિફોનિક બેસણું" else "સદ્ગતનું બેસણું")
            m.date?.let { add("તા. ${f.date(it)}, ${f.weekday(it)}ના રોજ") }
            meetingTime(m, f)?.let { add(it) }
            add("રાખેલ છે.")
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("સવારે", "બપોરે", "સાંજે", "રાત્રે")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} વાગ્યે"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} થી $endText વાગ્યા સુધી"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Telugu — గారు / పరమపదించారు are respectful for both genders;
// gender is expressed through the honorific (కీ.శే. శ్రీ / కీ.శే. శ్రీమతి).
// ═════════════════════════════════════════════════════════════════════════════

object TeluguPack : LanguagePack() {
    override val language = CardLanguage.TELUGU
    override val ui = UiStrings.TELUGU
    override val nativeZero = '౦'

    override fun honorifics(gender: Gender) = when (gender) {
        Gender.MALE -> listOf("కీ.శే. శ్రీ", "కీ.శే.", "కీ.శే. డా.")
        Gender.FEMALE -> listOf("కీ.శే. శ్రీమతి", "కీ.శే. కుమారి", "కీ.శే.", "కీ.శే. డా.")
    }

    override val defaultTitle = "శ్రద్ధాంజలి"
    override fun defaultMeetingTitle(type: MeetingType) =
        if (type == MeetingType.TELEPHONIC) "ఫోన్ ద్వారా సంతాపం" else "సంతాప సభ"
    override val defaultFamilyTitle = "దుఃఖతప్త కుటుంబ సభ్యులు"
    override val venueLabel = "స్థలం"
    override val weekdays = listOf("సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం", "ఆదివారం")

    override fun message(d: Deceased, f: Fmt): String = buildString {
        append("అత్యంత విచారంతో తెలియజేయునది ఏమనగా,")
        if (d.place.isNotBlank()) append(" ").append(d.place.trim()).append(" వాస్తవ్యులు,")
        append(" మా ప్రియమైన ")
        append(nameOrPlaceholder(d))
        if (d.fullName.isNotEmpty()) append(" గారు")
        if (d.age.isNotEmpty()) append(" (వయస్సు ").append(d.age).append(" సంవత్సరాలు)")
        d.dateOfDeath?.let { append(" ").append(f.date(it)).append(", ").append(f.weekday(it)).append(" నాడు") }
        append(" పరమపదించారు.")
        append(" వారి ఆత్మకు శాంతి కలగాలని భగవంతుని ప్రార్థిస్తున్నాము.")
    }

    override fun meetingText(m: Meeting, f: Fmt): String {
        val parts = buildList {
            if (m.type != MeetingType.TELEPHONIC) add("సంతాప సభ")
            m.date?.let { add("${f.date(it)}, ${f.weekday(it)} నాడు") }
            meetingTime(m, f)?.let { add(it) }
            add(
                if (m.type == MeetingType.TELEPHONIC) "ఫోన్ ద్వారా సంతాపం తెలియజేయవచ్చు."
                else "నిర్వహించబడును."
            )
        }
        return parts.joinToString(" ")
    }

    private val periods = listOf("ఉదయం", "మధ్యాహ్నం", "సాయంత్రం", "రాత్రి")
    private fun clock(minutes: Int, f: Fmt) = "${periods[period(minutes)]} ${f.d(hour12(minutes))}"

    override fun timeSingle(minutes: Int, f: Fmt) = "${clock(minutes, f)} గంటలకు"
    override fun timeRange(start: Int, end: Int, f: Fmt): String {
        val endText = if (period(start) == period(end)) f.d(hour12(end)) else clock(end, f)
        return "${clock(start, f)} గంటల నుండి $endText గంటల వరకు"
    }
}
