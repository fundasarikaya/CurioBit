package com.genelkultur.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import genelkultur.composeapp.generated.resources.Res
import genelkultur.composeapp.generated.resources.libre_caslon_bold
import genelkultur.composeapp.generated.resources.libre_caslon_italic
import genelkultur.composeapp.generated.resources.libre_caslon_regular
import genelkultur.composeapp.generated.resources.libre_franklin_bold
import genelkultur.composeapp.generated.resources.libre_franklin_medium
import genelkultur.composeapp.generated.resources.libre_franklin_semibold
import genelkultur.composeapp.generated.resources.old_standard_bold
import genelkultur.composeapp.generated.resources.old_standard_italic
import genelkultur.composeapp.generated.resources.old_standard_regular
import org.jetbrains.compose.resources.Font

/**
 * "Sepya Arşiv" paleti: eskimiş gazete kâğıdı, sepya mürekkep ve tek bir pas
 * kahvesi vurgu. Gazete dilinde tek spot renk kullanılır; bilgiye göre
 * değişen renkler yoktur.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF7B3F1D),          // vurgu: pas kahvesi
    onPrimary = Color(0xFFFBF5E8),
    secondary = Color(0xFF3F5A48),
    background = Color(0xFFEADFC8),       // kâğıt
    onBackground = Color(0xFF2E2116),     // mürekkep
    surface = Color(0xFFF3EBD8),
    onSurface = Color(0xFF2E2116),
    surfaceVariant = Color(0xFFE2D5B8),
    onSurfaceVariant = Color(0xFF5E4A36), // soluk mürekkep
    outline = Color(0xFFC4B089)           // ince çizgi
)

/** Aynı paletin gece baskısı: koyu sepya zemin, krem mürekkep. */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFD9955F),
    onPrimary = Color(0xFF1A140E),
    secondary = Color(0xFF9DB8A5),
    background = Color(0xFF1A140E),
    onBackground = Color(0xFFEDE1C8),
    surface = Color(0xFF241C14),
    onSurface = Color(0xFFEDE1C8),
    surfaceVariant = Color(0xFF30261B),
    onSurfaceVariant = Color(0xFFBFAE90),
    outline = Color(0xFF4A3B2A)
)

/** Künye ve manşetler: Old Standard TT. */
@Composable
fun displayFamily() = FontFamily(
    Font(Res.font.old_standard_regular, FontWeight.Normal),
    Font(Res.font.old_standard_bold, FontWeight.Bold),
    Font(Res.font.old_standard_italic, FontWeight.Normal, FontStyle.Italic)
)

/** Gövde metni: Libre Caslon Text. */
@Composable
private fun bodyFamily() = FontFamily(
    Font(Res.font.libre_caslon_regular, FontWeight.Normal),
    Font(Res.font.libre_caslon_bold, FontWeight.Bold),
    Font(Res.font.libre_caslon_italic, FontWeight.Normal, FontStyle.Italic)
)

/** Büyük harfli küçük etiketler: Libre Franklin. */
@Composable
private fun labelFamily() = FontFamily(
    Font(Res.font.libre_franklin_medium, FontWeight.Medium),
    Font(Res.font.libre_franklin_semibold, FontWeight.SemiBold),
    Font(Res.font.libre_franklin_bold, FontWeight.Bold)
)

@Composable
private fun newspaperTypography(): Typography {
    val display = displayFamily()
    val body = bodyFamily()
    val label = labelFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 44.sp,
            lineHeight = 46.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 38.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 30.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 28.sp
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 25.sp
        ),
        titleMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            lineHeight = 21.sp
        ),
        labelLarge = TextStyle(
            fontFamily = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 2.sp
        ),
        labelMedium = TextStyle(
            fontFamily = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 1.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            letterSpacing = 1.2.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    )
}

/** Gazete sayfası gibi keskin köşeler. */
private val NewspaperShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

@Composable
fun GenelKulturTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = newspaperTypography(),
        shapes = NewspaperShapes,
        content = content
    )
}

/** Künyenin altındaki kalın + ince çift çizgi. */
@Composable
fun DoubleRule(modifier: Modifier = Modifier) {
    val ink = MaterialTheme.colorScheme.onBackground
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Box(Modifier.fillMaxWidth().height(3.dp).background(ink))
        Box(Modifier.fillMaxWidth().height(1.dp).background(ink))
    }
}

/** "1979 — Olay metni" biçimindeki metni yıl ve olay olarak ayırır. */
fun splitYear(text: String): Pair<String?, String> {
    val match = Regex("""^\s*(\d{1,4})\s*[—–-]\s*(.+)$""", RegexOption.DOT_MATCHES_ALL).find(text)
    return if (match != null) match.groupValues[1] to match.groupValues[2] else null to text
}

/** Türkçe büyük harf: "i" → "İ" dönüşümünü doğru yapar. */
fun String.trUppercase(): String = replace('i', 'İ').uppercase()
