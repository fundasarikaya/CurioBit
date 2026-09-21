package com.genelkultur.app.notification

import com.genelkultur.app.PlatformContext

/** Günde 1-2 kez arka planda çalışarak yeni bir bilgi çekip bildirim gösterecek işi zamanlar. */
expect class NotificationScheduler(context: PlatformContext) {
    fun scheduleDaily()
    fun cancel()
}
