package com.genelkultur.app.notification

import com.genelkultur.app.PlatformContext
import com.genelkultur.app.data.DatabaseDriverFactory
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.FactRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.BackgroundTasks.BGTask
import platform.Foundation.NSDate
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge

const val BG_REFRESH_TASK_ID = "com.curiobit.app.refresh"
private const val REFRESH_INTERVAL_SECONDS = 12.0 * 60.0 * 60.0

/**
 * iOS'ta arka planda çalışacak işi BGTaskScheduler ile zamanlar. iOS bu görevin
 * TAM olarak ne zaman çalışacağına kendisi karar verir (garanti edilen bir saat yoktur),
 * bu App Store'un politikası gereği normaldir.
 */
actual class NotificationScheduler actual constructor(private val context: PlatformContext) {
    actual fun scheduleDaily() {
        requestNotificationPermission()
        submitNextRefresh()
    }

    actual fun cancel() {
        BGTaskScheduler.sharedScheduler.cancelAllTaskRequests()
    }

    private fun requestNotificationPermission() {
        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(options) { _, _ -> }
    }

    companion object {
        @OptIn(ExperimentalForeignApi::class)
        private fun submitNextRefresh() {
            val request = BGAppRefreshTaskRequest(BG_REFRESH_TASK_ID)
            val referenceNow = NSDate().timeIntervalSinceReferenceDate
            request.earliestBeginDate = NSDate(timeIntervalSinceReferenceDate = referenceNow + REFRESH_INTERVAL_SECONDS)
            try {
                BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
            } catch (_: Throwable) {
                // Simülatörde veya izin verilmeyen durumlarda submit başarısız olabilir; sessizce geç.
            }
        }

        /**
         * Uygulama başlarken (Swift App/AppDelegate içinde) bir kere çağrılmalıdır.
         * Görev tetiklendiğinde bugünün bilgisini çekip local notification gösterir,
         * ardından bir sonraki çalışmayı yeniden zamanlar.
         */
        @OptIn(ExperimentalForeignApi::class)
        fun register(driverFactory: DatabaseDriverFactory) {
            BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
                identifier = BG_REFRESH_TASK_ID,
                usingQueue = null
            ) { task ->
                if (task != null) {
                    handleRefresh(task, driverFactory)
                }
            }
        }

        private fun handleRefresh(task: BGTask, driverFactory: DatabaseDriverFactory) {
            submitNextRefresh()

            val scope = CoroutineScope(Dispatchers.Default)
            task.expirationHandler = { scope.cancel() }

            scope.launch {
                val repository = FactRepository(driverFactory)
                val fact = repository.fetchAndPickTodaysFact()
                if (fact != null) {
                    postLocalNotification(fact)
                }
                task.setTaskCompletedWithSuccess(fact != null)
            }
        }

        private fun postLocalNotification(fact: Fact) {
            val content = UNMutableNotificationContent().apply {
                setTitle("Bugün ne öğrendin?")
                setBody(fact.shortText)
                setSound(UNNotificationSound.defaultSound)
                setUserInfo(mapOf(EXTRA_FACT_ID to fact.id))
            }
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = fact.id,
                content = content,
                trigger = trigger
            )
            UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
        }
    }
}
