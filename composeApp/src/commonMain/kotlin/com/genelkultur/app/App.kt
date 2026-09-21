package com.genelkultur.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.genelkultur.app.data.DatabaseDriverFactory
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.FactRepository
import com.genelkultur.app.ui.DetailScreen
import com.genelkultur.app.ui.GenelKulturTheme
import com.genelkultur.app.ui.HistoryScreen
import com.genelkultur.app.ui.HomeScreen
import com.genelkultur.app.ui.Screen

/** Platforma özel bir link açıcı (tarayıcı) sağlar. */
expect fun openUrl(url: String)

@Composable
fun App(driverFactory: DatabaseDriverFactory, initialFactId: String? = null) {
    val repository = remember { FactRepository(driverFactory) }

    var screen by remember {
        mutableStateOf<Screen>(initialFactId?.let { Screen.Detail(it) } ?: Screen.Home)
    }
    var todaysFact by remember { mutableStateOf<Fact?>(null) }
    val recentFacts by repository.observeRecentFacts().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        todaysFact = repository.fetchAndPickTodaysFact()
    }

    GenelKulturTheme(useDarkTheme = isSystemInDarkTheme()) {
        when (val current = screen) {
            is Screen.Home -> HomeScreen(
                todaysFactShortText = todaysFact?.shortText,
                onOpenTodaysFact = {
                    todaysFact?.let { screen = Screen.Detail(it.id) }
                },
                onOpenHistory = { screen = Screen.History }
            )

            is Screen.History -> HistoryScreen(
                facts = recentFacts,
                onBack = { screen = Screen.Home },
                onOpenFact = { screen = Screen.Detail(it.id) }
            )

            is Screen.Detail -> {
                var fact by remember(current.factId) { mutableStateOf<Fact?>(null) }
                LaunchedEffect(current.factId) {
                    fact = if (todaysFact?.id == current.factId) {
                        todaysFact
                    } else {
                        repository.getFactById(current.factId)
                    }
                }
                DetailScreen(
                    fact = fact,
                    onBack = { screen = Screen.Home },
                    onOpenSource = { url -> openUrl(url) }
                )
            }
        }
    }
}
