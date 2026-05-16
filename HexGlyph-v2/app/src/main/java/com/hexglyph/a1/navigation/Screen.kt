package com.hexglyph.a1.navigation

sealed class Screen(val route: String) {
    data object Home      : Screen("home")
    data object Encode    : Screen("encode")
    data object Decode    : Screen("decode")
    data object Settings  : Screen("settings")
    data object History   : Screen("history")
    data object Export    : Screen("export")
    data object Analytics : Screen("analytics")
}
