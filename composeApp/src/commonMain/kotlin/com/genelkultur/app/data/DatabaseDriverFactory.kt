package com.genelkultur.app.data

import app.cash.sqldelight.db.SqlDriver
import com.genelkultur.app.PlatformContext

expect class DatabaseDriverFactory(context: PlatformContext) {
    fun createDriver(): SqlDriver
}
