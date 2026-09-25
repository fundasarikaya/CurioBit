package com.genelkultur.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.genelkultur.app.MainActivity
import com.genelkultur.app.PlatformContext
import com.genelkultur.app.R
import com.genelkultur.app.data.DatabaseDriverFactory
import com.genelkultur.app.data.FactRepository

private const val CHANNEL_ID = "genelkultur_facts"
private const val NOTIFICATION_ID = 1001

class FactNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = FactRepository(DatabaseDriverFactory(PlatformContext(applicationContext)))
        val fact = repository.fetchAndPickTodaysFact() ?: return Result.retry()

        ensureChannel()

        val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FACT_ID, fact.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Bugün ne öğrendin?")
            .setContentText(fact.shortText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fact.shortText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompatSafeNotify(applicationContext, NOTIFICATION_ID, notification)
        }

        return Result.success()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CurioBit günlük bilgi",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Günde 1-2 kez kısa genel kültür bilgileri"
            }
            manager.createNotificationChannel(channel)
        }
    }
}

private fun NotificationManagerCompatSafeNotify(
    context: Context,
    id: Int,
    notification: android.app.Notification
) {
    androidx.core.app.NotificationManagerCompat.from(context).notify(id, notification)
}
