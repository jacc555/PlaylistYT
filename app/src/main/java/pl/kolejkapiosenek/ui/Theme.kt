package pl.kolejkapiosenek.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PlayYellow = Color(0xFFFFC93C)
val PlayText = Color(0xFF2A1D00)

private val LightColors = lightColorScheme(
    primary = Color(0xFF2457D6),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF2F6FB),
    onBackground = Color(0xFF14233A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14233A),
    onSurfaceVariant = Color(0xFF51627C),
    outline = Color(0xFFD3DEEC),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AADFF),
    onPrimary = Color(0xFF0F1626),
    background = Color(0xFF0F1626),
    onBackground = Color(0xFFEAF0FA),
    surface = Color(0xFF172037),
    onSurface = Color(0xFFEAF0FA),
    onSurfaceVariant = Color(0xFF9FB0CC),
    outline = Color(0xFF26334D),
)

@Composable
fun PlaylistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
