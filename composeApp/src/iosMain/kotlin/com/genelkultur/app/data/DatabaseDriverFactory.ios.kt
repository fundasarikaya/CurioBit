package com.genelkultur.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.genelkultur.app.PlatformContext
import com.genelkultur.app.db.AppDatabase

actual class DatabaseDriverFactory actual constructor(context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "genelkultur.db")
    }
}
