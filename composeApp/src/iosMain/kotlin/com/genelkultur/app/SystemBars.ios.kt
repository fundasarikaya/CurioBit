package com.genelkultur.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.genelkultur.app.data.ThemeMode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle

/**
 * iOS'ta durum çubuğu pencerenin arayüz stiline uyar; uygulama açık/koyu seçildiğinde
 * pencereyi o stile sabitliyoruz, "Sistem"de sabitlemeyi kaldırıyoruz.
 */
@Composable
actual fun PlatformSystemBars(mode: ThemeMode, darkTheme: Boolean) {
    LaunchedEffect(mode) {
        UIApplication.sharedApplication.keyWindow?.overrideUserInterfaceStyle = when (mode) {
            ThemeMode.SYSTEM -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
            ThemeMode.LIGHT -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            ThemeMode.DARK -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
        }
    }
}
