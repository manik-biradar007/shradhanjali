package com.ereader.ripcardmaker.model

import kotlinx.serialization.Serializable

/** Language the card text is written in. The app UI follows the same language. */
@Serializable
enum class CardLanguage(val code: String, val nativeName: String, val englishName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "हिन्दी", "Hindi"),
    MARATHI("mr", "मराठी", "Marathi"),
    GUJARATI("gu", "ગુજરાતી", "Gujarati"),
    TELUGU("te", "తెలుగు", "Telugu");

    companion object {
        fun fromCode(code: String?): CardLanguage? = entries.firstOrNull { it.code == code }
    }
}

@Serializable
enum class Gender { MALE, FEMALE }

@Serializable
enum class MeetingType { NONE, IN_PERSON, TELEPHONIC }

/** Output size. Artwork is 3:5, so STANDARD matches it exactly; STATUS is the 9:16 phone-status size. */
@Serializable
enum class CardFormat(val widthPx: Int, val heightPx: Int) {
    STANDARD(1080, 1800),
    STATUS(1080, 1920);

    val aspectRatio: Float get() = widthPx.toFloat() / heightPx
}

@Serializable
data class Deceased(
    val gender: Gender = Gender.MALE,
    /** null = use the language's default honorific for [gender]; "" = no honorific; otherwise the chosen text. */
    val honorific: String? = null,
    val firstName: String = "",
    /** Father's or husband's name, as commonly written between first name and surname. */
    val middleName: String = "",
    val surname: String = "",
    val age: String = "",
    val place: String = "",
    /** Date of death as epoch day (java.time.LocalDate.toEpochDay), or null if not set. */
    val dateOfDeath: Long? = null,
) {
    val fullName: String
        get() = listOf(firstName, middleName, surname).map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
}

@Serializable
data class Meeting(
    val type: MeetingType = MeetingType.IN_PERSON,
    val date: Long? = null,
    /** Minutes after midnight. */
    val startMinutes: Int? = null,
    val endMinutes: Int? = null,
    val venue: String = "",
)

@Serializable
data class FamilyMember(
    val name: String = "",
    val relation: String = "",
    val phone: String = "",
)

@Serializable
data class Photo(
    /** Absolute path of the downscaled copy kept in app storage. */
    val path: String? = null,
    /** Bumped whenever the file changes, so caches refresh. */
    val version: Long = 0,
    /** Zoom inside the circle; 1 = photo just fills the circle. */
    val scale: Float = 1f,
    /** Pan as a fraction of the circle diameter. */
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)

@Serializable
data class CardStyle(
    val frameId: String? = "k41",
    /** ARGB tint for the frame line-art, or null for its original colours. */
    val frameTint: Int? = null,
    val backgroundColor: Int = 0xFFFDF8EE.toInt(),
    val patternId: String? = "backh2",
    val ringId: String? = "r23",
    val diyaId: String? = "d9",
    val textColor: Int = 0xFF1F1F1F.toInt(),
    val accentColor: Int = 0xFF7A1F1F.toInt(),
    /** User text size multiplier. Auto-fit may shrink further so everything fits. */
    val textScale: Float = 1f,
    val nativeDigits: Boolean = false,
)

/** Optional user overrides for generated text. null = automatic. */
@Serializable
data class CardTexts(
    val title: String? = null,
    val message: String? = null,
    val meetingTitle: String? = null,
    val familyTitle: String? = null,
)

@Serializable
data class CardDraft(
    val id: String,
    val language: CardLanguage,
    val deceased: Deceased = Deceased(),
    val meeting: Meeting = Meeting(),
    val family: List<FamilyMember> = listOf(FamilyMember()),
    val style: CardStyle = CardStyle(),
    val texts: CardTexts = CardTexts(),
    val photo: Photo = Photo(),
    val format: CardFormat = CardFormat.STANDARD,
    /** Set once the user watched a rewarded ad (or none was available) for the Status size. */
    val statusUnlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastExportedAt: Long? = null,
) {
    companion object {
        const val MAX_FAMILY = 8
    }
}
