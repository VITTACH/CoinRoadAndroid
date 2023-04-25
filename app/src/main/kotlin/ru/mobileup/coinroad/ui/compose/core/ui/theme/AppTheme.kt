package ru.mobileup.coinroad.ui.compose.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}