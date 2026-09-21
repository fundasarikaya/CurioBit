package com.genelkultur.app

import androidx.compose.ui.window.ComposeUIViewController
import com.genelkultur.app.data.DatabaseDriverFactory
import platform.Foundation.NSUserDefaults

private const val PENDING_FACT_ID_KEY = "pending_fact_id"

fun MainViewController() = ComposeUIViewController {
    val initialFactId = NSUserDefaults.standardUserDefaults.stringForKey(PENDING_FACT_ID_KEY)
    if (initialFactId != null) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(PENDING_FACT_ID_KEY)
    }
    App(DatabaseDriverFactory(PlatformContext()), initialFactId)
}

/** Swift tarafında bildirime dokunulduğunda çağrılır. */
fun setPendingFactId(factId: String) {
    NSUserDefaults.standardUserDefaults.setObject(factId, forKey = PENDING_FACT_ID_KEY)
}

fun setupBackgroundRefresh() {
    val driverFactory = DatabaseDriverFactory(PlatformContext())
    com.genelkultur.app.notification.NotificationScheduler.register(driverFactory)
    com.genelkultur.app.notification.NotificationScheduler(PlatformContext()).scheduleDaily()
}
