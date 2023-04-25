package ru.mobileup.coinroad.ui.compose.core.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// TODO: настроить цвета
val GrayC = Color(0xFF292929)
val GreenA = Color(0xFF00BA98)
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val PurpleB = Color(0xFF170a33)
val RedA = Color(0xFFE26060)

val LightColors = lightColors(
    primary = GrayC,
    primaryVariant = GreenA,
    secondary = White,
    background = Black,
    surface = PurpleB,
    error = RedA
)