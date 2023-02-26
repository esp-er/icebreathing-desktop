package io.github.esp_er.icebreathing.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = BlueLight,
    primaryVariant = DeepBlue,
    secondary = MagentaLight,
    secondaryVariant = Magenta2,
    onSurface = SkyBlue,
    background = SeaBlue
)

private val LightColorPalette = lightColors(
    primary = BlueLight,
    primaryVariant = DeepBlue,
    secondary = MagentaLight,
    secondaryVariant = Magenta2,
    onSurface = SkyBlue,
    background = SeaBlue

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun IceBreathingTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    /*val colors = if (darkTheme) { //We might add dark/light themes later
        DarkColorPalette
    } else {
        LightColorPalette
    }*/

    val colors = LightColorPalette

    MaterialTheme(
        colors = colors,
        content = content
    )
}