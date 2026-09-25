package com.genelkultur.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.genelkultur.app.data.DatabaseDriverFactory
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.FactRepository
import com.genelkultur.app.data.Reaction
import com.genelkultur.app.ui.DetailScreen
import com.genelkultur.app.ui.GenelKulturTheme
import com.genelkultur.app.ui.HistoryScreen
import com.genelkultur.app.ui.HomeScreen
import com.genelkultur.app.ui.Screen
import kotlinx.coroutines.launch

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
    val favoriteFacts by repository.observeFavoriteFacts().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        todaysFact = repository.fetchAndPickTodaysFact()
    }

    GenelKulturTheme(useDarkTheme = isSystemInDarkTheme()) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(tween(260)) togetherWith fadeOut(tween(160))
            },
            label = "screen"
        ) { current ->
            when (current) {
                is Screen.Home -> HomeScreen(
                    todaysFact = todaysFact,
                    onOpenTodaysFact = {
                        todaysFact?.let { screen = Screen.Detail(it.id) }
                    },
                    onOpenHistory = { screen = Screen.History }
                )

                is Screen.History -> HistoryScreen(
                    facts = recentFacts,
                    favoriteFacts = favoriteFacts,
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
                        onOpenSource = { url -> openUrl(url) },
                        onReact = { reaction ->
                            val factId = current.factId
                            scope.launch {
                                repository.setReaction(factId, reaction)
                                fact = fact?.copy(reaction = reaction)
                                if (todaysFact?.id == factId) {
                                    todaysFact = todaysFact?.copy(reaction = reaction)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
