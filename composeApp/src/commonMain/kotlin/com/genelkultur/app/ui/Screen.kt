package com.genelkultur.app.ui

sealed interface Screen {
    data object Home : Screen
    data object History : Screen
    data class Detail(val factId: String) : Screen
}
