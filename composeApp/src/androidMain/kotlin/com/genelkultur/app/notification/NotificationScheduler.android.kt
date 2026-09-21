package com.genelkultur.app.notification

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.genelkultur.app.PlatformContext
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "genelkultur_daily_fact"

actual class NotificationScheduler actual constructor(private val context: PlatformContext) {
    actual fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<FactNotificationWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context.context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    actual fun cancel() {
        WorkManager.getInstance(context.context).cancelUniqueWork(WORK_NAME)
    }
}
