package com.privacyfilemanager.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ===== Brand Colors =====
private val PrimaryLight = Color(0xFF1B6B4E)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFA5F2CC)
private val OnPrimaryContainerLight = Color(0xFF002115)

private val SecondaryLight = Color(0xFF4D6356)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFD0E8D8)

private val TertiaryLight = Color(0xFF3D6373)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFC1E8FB)

private val SurfaceLight = Color(0xFFFBFDF8)
private val OnSurfaceLight = Color(0xFF191C1A)
private val SurfaceVariantLight = Color(0xFFDCE5DC)

private val ErrorLight = Color(0xFFBA1A1A)

// ===== Dark Colors =====
private val PrimaryDark = Color(0xFF89D6B1)
private val OnPrimaryDark = Color(0xFF003828)
private val PrimaryContainerDark = Color(0xFF005139)

private val SecondaryDark = Color(0xFFB4CCBC)
private val OnSecondaryDark = Color(0xFF203529)
private val SecondaryContainerDark = Color(0xFF364B3F)

private val TertiaryDark = Color(0xFFA5CCDE)
private val OnTertiaryDark = Color(0xFF073543)

private val SurfaceDark = Color(0xFF191C1A)
private val OnSurfaceDark = Color(0xFFE1E3DE)
private val SurfaceVariantDark = Color(0xFF414941)

private val ErrorDark = Color(0xFFFFB4AB)

// ===== Color Schemes =====
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    error = ErrorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    error = ErrorDark
)

@Composable
fun PrivacyFileManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Material You dynamic colors on Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
