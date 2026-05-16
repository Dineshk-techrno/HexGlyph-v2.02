package com.hexglyph.a1.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavigationAnimations {

    fun enterTransition(): EnterTransition =
        slideInHorizontally(initialOffsetX = { it }) + fadeIn()

    fun exitTransition(): ExitTransition =
        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()

    fun popEnterTransition(): EnterTransition =
        slideInHorizontally(initialOffsetX = { -it }) + fadeIn()

    fun popExitTransition(): ExitTransition =
        slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
}
