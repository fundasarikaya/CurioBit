package com.genelkultur.app

import androidx.compose.runtime.Composable

/** iOS'ta sistem geri tuşu yok; ekrandaki geri oku yeterli. */
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
