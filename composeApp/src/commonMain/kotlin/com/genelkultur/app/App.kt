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
import com.genelkultur.app.data.Era
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.FactRepository
import com.genelkultur.app.data.PickResult
import com.genelkultur.app.data.year
import com.genelkultur.app.data.Reaction
import com.genelkultur.app.ui.DetailScreen
import com.genelkultur.app.ui.GenelKulturTheme
import com.genelkultur.app.ui.HistoryScreen
import com.genelkultur.app.ui.HomeScreen
import com.genelkultur.app.ui.Screen
import kotlinx.coroutines.launch

/** Platforma özel bir link açıcı (tarayıcı) sağlar. */
expect fun openUrl(url: String)

/** Sistem geri tuşunu (Android) yakalar; iOS'ta etkisizdir. */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)

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

    var isRefreshing by remember { mutableStateOf(false) }
    var refreshMessage by remember { mutableStateOf<String?>(null) }
    var era by remember { mutableStateOf(Era.ALL) }

    LaunchedEffect(Unit) {
        era = repository.getEra()
        todaysFact = repository.getOrPickTodaysFact()
    }

    /** Yeni bilgi isteğini çalıştırır ve sonucu ana sayfaya yansıtır. */
    fun runPick(showNoneLeft: Boolean = true, request: suspend () -> PickResult) {
        if (isRefreshing) return
        scope.launch {
            isRefreshing = true
            refreshMessage = null
            when (val result = request()) {
                is PickResult.Picked -> todaysFact = result.fact
                PickResult.NoneLeft -> if (showNoneLeft) {
                    refreshMessage = if (era == Era.ALL) {
                        "Bugün için gösterilecek başka bilgi yok. Yarın yeni bilgiler gelecek."
                    } else {
                        "Bugün için ${era.label} döneminden gösterilecek başka bilgi yok. Başka bir dönem seçebilirsin."
                    }
                }
                PickResult.Offline -> refreshMessage = "Vikipedi'ye ulaşılamadı. Bağlantını kontrol edip tekrar dene."
            }
            isRefreshing = false
        }
    }

    // Ana sayfa dışındaki ekranlarda geri tuşu uygulamadan çıkmak yerine ana sayfaya döner.
    PlatformBackHandler(enabled = screen !is Screen.Home) {
        screen = Screen.Home
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
                    archivePreview = recentFacts.filter { it.id != todaysFact?.id },
                    onOpenTodaysFact = {
                        todaysFact?.let { screen = Screen.Detail(it.id) }
                    },
                    onOpenFact = { screen = Screen.Detail(it.id) },
                    isRefreshing = isRefreshing,
                    refreshMessage = refreshMessage,
                    onRefresh = { runPick { repository.pickAnotherFact() } },
                    era = era,
                    onEraChange = { selected ->
                        if (selected != era && !isRefreshing) {
                            era = selected
                            refreshMessage = null
                            scope.launch {
                                repository.setEra(selected)
                                // Ekrandaki bilgi yeni dönemin dışındaysa o dönemden bir bilgi getir.
                                if (!selected.contains(todaysFact?.year)) {
                                    // Dönemde hiç bilgi yoksa ana sayfadaki not zaten bunu söylüyor.
                                    runPick(showNoneLeft = false) { repository.pickForEra() }
                                }
                            }
                        }
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
