package com.genelkultur.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.genelkultur.app.PlatformContext
import com.genelkultur.app.db.AppDatabase

actual class DatabaseDriverFactory actual constructor(private val context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context.context, "genelkultur.db")
    }
}
