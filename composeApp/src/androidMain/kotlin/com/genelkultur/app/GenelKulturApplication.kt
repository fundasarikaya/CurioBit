package com.genelkultur.app

import android.app.Application
import com.genelkultur.app.notification.NotificationScheduler

class GenelKulturApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        NotificationScheduler(PlatformContext(this)).scheduleDaily()
    }
}
