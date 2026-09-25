package com.genelkultur.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Kağıt kokan, sıcak bir "gazete" paleti — canlı bir aksan rengiyle dengelenir. */
private val PaperLight = Color(0xFFFBF5E9)
private val PaperSurfaceLight = Color(0xFFFFFCF5)
private val InkLight = Color(0xFF2A2420)

private val PaperDark = Color(0xFF1B1815)
private val PaperSurfaceDark = Color(0xFF262220)
private val InkDark = Color(0xFFF3EADA)

private val LightColors = lightColorScheme(
    primary = Color(0xFF7A2E2E),
    onPrimary = Color(0xFFFFFCF5),
    secondary = Color(0xFF2E5240),
    background = PaperLight,
    onBackground = InkLight,
    surface = PaperSurfaceLight,
    onSurface = InkLight,
    surfaceVariant = Color(0xFFF0E6D2),
    onSurfaceVariant = Color(0xFF6B5F4F),
    outline = Color(0xFFD8CBAE)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE0A05B),
    onPrimary = Color(0xFF1B1815),
    secondary = Color(0xFF8FBBA4),
    background = PaperDark,
    onBackground = InkDark,
    surface = PaperSurfaceDark,
    onSurface = InkDark,
    surfaceVariant = Color(0xFF332E29),
    onSurfaceVariant = Color(0xFFC9BCA8),
    outline = Color(0xFF473F37)
)

/**
 * Günün bilgisine göre değişen, gazete manşetlerinde kullanılabilecek
 * canlı bir vurgu rengi paleti. `accentColorFor` bir kimlikten (fact id)
 * deterministik olarak bunlardan birini seçer.
 */
private val AccentPalette = listOf(
    Color(0xFFC1442B), // kiremit
    Color(0xFF1F6E5C), // çam yeşili
    Color(0xFF9A5A1D), // hardal/karamel
    Color(0xFF3C5B8F), // çini mavisi
    Color(0xFF8A3A63), // erik
    Color(0xFF456B2E)  // zeytin
)

fun accentColorFor(id: String): Color {
    val index = (id.hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }) % AccentPalette.size
    return AccentPalette[index]
}

private val SerifDisplay = FontFamily.Serif

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SerifDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
)

@Composable
fun GenelKulturTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
