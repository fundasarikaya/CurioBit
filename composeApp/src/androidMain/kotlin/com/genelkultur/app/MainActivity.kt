package com.genelkultur.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.genelkultur.app.data.DatabaseDriverFactory

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Durum çubuğu simgeleri açık temada koyu, koyu temada açık renkte çizilir.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val driverFactory = DatabaseDriverFactory(PlatformContext(applicationContext))
        val initialFactId = intent?.getStringExtra(
            com.genelkultur.app.notification.EXTRA_FACT_ID
        )
        setContent {
            App(driverFactory, initialFactId)
        }
    }
}
