package com.questsmith.app.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorPalette = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    background = Cloud,
    surface = Color.White,
    onSurface = Slate,
    secondary = Slate,
    onSecondary = Color.White,
)

private val DarkColorPalette = darkColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    background = Night,
    surface = Slate,
    onSurface = Color.White,
    secondary = Dove,
    onSecondary = Color.Black,
)

private val QuestSmithTypography = Typography()

private val LocalSpacing = compositionLocalOf { Spacing() }

@Composable
fun QuestSmithTheme(
    useDarkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    val animatedScheme by animateColorScheme(colorScheme)

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = animatedScheme,
            typography = QuestSmithTypography,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}

private data class AnimatedColorScheme(val scheme: ColorScheme)

@Composable
private fun animateColorScheme(target: ColorScheme): androidx.compose.runtime.State<ColorScheme> {
    val primary by animateColorAsState(targetValue = target.primary, label = "primary")
    val onPrimary by animateColorAsState(targetValue = target.onPrimary, label = "onPrimary")
    val background by animateColorAsState(targetValue = target.background, label = "background")
    val surface by animateColorAsState(targetValue = target.surface, label = "surface")
    val onSurface by animateColorAsState(targetValue = target.onSurface, label = "onSurface")
    return androidx.compose.runtime.remember(primary, onPrimary, background, surface, onSurface) {
        androidx.compose.runtime.mutableStateOf(
            target.copy(
                primary = primary,
                onPrimary = onPrimary,
                background = background,
                surface = surface,
                onSurface = onSurface
            )
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
