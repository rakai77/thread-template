package com.example.thread.thread.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = FindeColor.Primary,
    secondary = FindeColor.Secondary,
    tertiary = FindeColor.Tertiary,
    background = FindeColor.JetBlack.Minus90,
    surface = FindeColor.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun ThreadTheme(
    content: @Composable () -> Unit,
) {
    val threadTextStyles = findeFontSize()

    val appTypography = Typography(
        displayLarge = threadTextStyles.headlineExtraLarge,
        displayMedium = threadTextStyles.headlineLarge,
        displaySmall = threadTextStyles.headlineMediumBold,
        headlineLarge = threadTextStyles.headlineSmall,
        headlineMedium = threadTextStyles.titleLargeBold,
        headlineSmall = threadTextStyles.titleLarge,
        titleLarge = threadTextStyles.titleMediumBold,
        titleMedium = threadTextStyles.titleMedium,
        titleSmall = threadTextStyles.titleSmallBold,
        bodyLarge = threadTextStyles.bodyLarge,
        bodyMedium = threadTextStyles.bodyMedium,
        bodySmall = threadTextStyles.bodySmall,
        labelLarge = threadTextStyles.buttonText,
        labelMedium = threadTextStyles.labelLargeBold,
        labelSmall = threadTextStyles.labelMedium
    )

    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = Shapes,
        typography = appTypography,
        content = content
    )
}

val Shapes = Shapes(
    extraLarge = RoundedCornerShape(32.dp),
    large = RoundedCornerShape(24.dp),
    medium = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(8.dp),
    extraSmall = RoundedCornerShape(4.dp)
)
