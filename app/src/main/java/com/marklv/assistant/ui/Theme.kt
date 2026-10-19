package com.marklv.assistant.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Cyan = Color(0xFF22D3EE)
val Amber = Color(0xFFF59E0B)
val HudBackground = Color(0xFF070B10)
val HudSurface = Color(0xFF0D141C)
val HudSurfaceHigh = Color(0xFF14202C)
val HudText = Color(0xFFDCE8F0)
val HudTextDim = Color(0xFF9FB4C4)

@Composable
fun MarkLVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Cyan,
            onPrimary = Color(0xFF00222B),
            primaryContainer = HudSurfaceHigh,
            onPrimaryContainer = Cyan,
            secondary = Amber,
            onSecondary = Color(0xFF2B1A00),
            background = HudBackground,
            onBackground = HudText,
            surface = HudSurface,
            onSurface = HudText,
            surfaceVariant = HudSurfaceHigh,
            onSurfaceVariant = HudTextDim
        ),
        content = content
    )
}
