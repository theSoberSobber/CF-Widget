package com.example.codeforces.ui.theme

import androidx.compose.ui.graphics.Color

// Material 3 color palette
val md_theme_light_primary = Color(0xFFF44336)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDAD5)
val md_theme_light_onPrimaryContainer = Color(0xFF410001)
val md_theme_light_secondary = Color(0xFF5C5D72)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE1E0F9)
val md_theme_light_onSecondaryContainer = Color(0xFF1A1A2C)
val md_theme_light_tertiary = Color(0xFF795370)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFD8EB)
val md_theme_light_onTertiaryContainer = Color(0xFF2E1129)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF1C1B1F)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE3E1EC)
val md_theme_light_onSurfaceVariant = Color(0xFF46464F)
val md_theme_light_outline = Color(0xFF767680)
val md_theme_light_inverseOnSurface = Color(0xFFF3EFF4)
val md_theme_light_inverseSurface = Color(0xFF313034)
val md_theme_light_inversePrimary = Color(0xFFBDC2FF)
val md_theme_light_surfaceTint = Color(0xFF4051D5)

val md_theme_dark_primary = Color(0xFFFF8A80)
val md_theme_dark_onPrimary = Color(0xFF690002)
val md_theme_dark_primaryContainer = Color(0xFFD32F2F)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDAD5)
val md_theme_dark_secondary = Color(0xFFC5C4DD)
val md_theme_dark_onSecondary = Color(0xFF2F2F42)
val md_theme_dark_secondaryContainer = Color(0xFF454559)
val md_theme_dark_onSecondaryContainer = Color(0xFFE1E0F9)
val md_theme_dark_tertiary = Color(0xFFEBB7D2)
val md_theme_dark_onTertiary = Color(0xFF46263F)
val md_theme_dark_tertiaryContainer = Color(0xFF5F3C57)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8EB)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = Color(0xFFE5E1E6)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE5E1E6)
val md_theme_dark_surfaceVariant = Color(0xFF46464F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC7C5D0)
val md_theme_dark_outline = Color(0xFF90909A)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE5E1E6)
val md_theme_dark_inversePrimary = Color(0xFF4051D5)
val md_theme_dark_surfaceTint = Color(0xFFBDC2FF)

// Codeforces user colors
val Red = Color(0xFFE74C3C)
val Green = Color(0xFF2ECC71)
val Blue = Color(0xFF3498DB)
val Purple = Color(0xFF9B59B6)
val Orange = Color(0xFFFF8C00)
val Black = Color(0xFFFFFFFF) // Changed to white for better visibility in dark mode
val Gray = Color(0xFFC0C0C0)
val Legendary = Color(0xFFFF8C00) // Using orange for "legendary" color
val Cyan = Color(0xFF00CED1) // Added cyan color
val Violet = Color(0xFF8A2BE2) // Added violet color
val Admin = Color(0xFFFFFFFF) // Changed to white for better visibility in dark mode
val Yellow = Color(0xFFFFD700) // Added yellow color

/**
 * Gets the appropriate color for a Codeforces user based on their color string.
 * 
 * @param colorString The color string from the API (red, blue, green, etc.)
 * @return The Color object corresponding to the user's rating color
 */
fun getCodeforcesUserColor(colorString: String): Color {
    return when (colorString.lowercase()) {
        "red" -> Red
        "blue" -> Blue
        "green" -> Green
        "purple" -> Purple
        "orange" -> Orange
        "legendary" -> Legendary
        "black" -> Black
        "gray" -> Gray
        "cyan" -> Cyan
        "violet" -> Violet
        "admin" -> Admin
        "yellow" -> Yellow
        else -> Black // Default color
    }
} 