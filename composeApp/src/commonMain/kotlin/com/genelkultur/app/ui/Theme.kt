package com.genelkultur.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E6E5B),
    secondaryContainer = Color(0xFFD6EFE4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FD8BF),
    secondaryContainer = Color(0xFF1F4A3C)
)

@Composable
fun GenelKulturTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        content = content
    )
}
