package com.genelkultur.app.data

/**
 * Bilgilerin hangi yıllardan seçileceği. [range] null ise tüm yıllar.
 * [label] mesajlarda, kısa [shortLabel] dönem seçicide kullanılır.
 */
enum class Era(val label: String, val shortLabel: String, val range: IntRange?) {
    ALL("Tümü", "Tümü", null),
    SINCE_2010("2010 sonrası", "2010+", 2010..Int.MAX_VALUE),
    DECADE_2000("2000–2009", "2000–09", 2000..2009),
    CENTURY_20("1900–1999", "1900–99", 1900..1999),
    BEFORE_1900("1900 öncesi", "1900 öncesi", Int.MIN_VALUE..1899);

    fun contains(year: Int?): Boolean = range == null || (year != null && year in range)
}

/** Kullanıcının "başka bir bilgi" isteğinin sonucu. */
sealed interface PickResult {
    data class Picked(val fact: Fact) : PickResult
    /** Seçilen dönemde bugün için gösterilmemiş bilgi kalmadı. */
    data object NoneLeft : PickResult
    /** Vikipedi'ye ulaşılamadı. */
    data object Offline : PickResult
}

/** "1979 — Olay" biçimindeki metinden yılı okur (MÖ yıllar eksi işaretlidir). */
internal fun yearOf(fullText: String): Int? =
    Regex("""^\s*(-?\d{1,4})\s*[—–-]\s""").find(fullText)?.groupValues?.get(1)?.toIntOrNull()

/** Bilginin yılı (metnin başındaki "1979 — " kısmından). */
val Fact.year: Int? get() = yearOf(fullText)
