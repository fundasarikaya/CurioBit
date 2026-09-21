package com.genelkultur.app

import android.content.Context
import android.content.Intent
import android.net.Uri

lateinit var appContext: Context

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}
