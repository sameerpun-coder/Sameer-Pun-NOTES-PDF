package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val isDark: Boolean,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val onBackgroundColor: Color,
    val onSurfaceVariant: Color,
    val borderColor: Color,
    val primaryColor: Color
) {
    MONOCHROME_LIGHT(
        id = "monochrome_light",
        title = "Monochrome Light",
        subtitle = "Clean high-contrast black & white (Default)",
        isDark = false,
        backgroundColor = Color(0xFFF8FAFC),
        surfaceColor = Color(0xFFFFFFFF),
        onBackgroundColor = Color(0xFF0F172A),
        onSurfaceVariant = Color(0xFF64748B),
        borderColor = Color(0xFFE2E8F0),
        primaryColor = Color(0xFF0F172A)
    ),
    PURE_WHITE(
        id = "pure_white",
        title = "Pure White",
        subtitle = "Ultra-bright study aesthetic",
        isDark = false,
        backgroundColor = Color(0xFFFFFFFF),
        surfaceColor = Color(0xFFF8FAFC),
        onBackgroundColor = Color(0xFF000000),
        onSurfaceVariant = Color(0xFF4B5563),
        borderColor = Color(0xFFE5E7EB),
        primaryColor = Color(0xFF000000)
    ),
    WARM_SEPIA(
        id = "warm_sepia",
        title = "Warm Sepia & Cream",
        subtitle = "Gentle on the eyes for long study hours",
        isDark = false,
        backgroundColor = Color(0xFFFDFBF7),
        surfaceColor = Color(0xFFFFFFFF),
        onBackgroundColor = Color(0xFF292524),
        onSurfaceVariant = Color(0xFF78716C),
        borderColor = Color(0xFFE7E5E4),
        primaryColor = Color(0xFF44403C)
    ),
    SLATE_DARK(
        id = "slate_dark",
        title = "Charcoal Slate",
        subtitle = "Comfortable modern dark reading mode",
        isDark = true,
        backgroundColor = Color(0xFF0F172A),
        surfaceColor = Color(0xFF1E293B),
        onBackgroundColor = Color(0xFFF8FAFC),
        onSurfaceVariant = Color(0xFF94A3B8),
        borderColor = Color(0xFF334155),
        primaryColor = Color(0xFF38BDF8)
    ),
    MIDNIGHT_OLED(
        id = "midnight_oled",
        title = "Midnight AMOLED",
        subtitle = "Deep true black battery saver",
        isDark = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF121212),
        onBackgroundColor = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0xFFA3A3A3),
        borderColor = Color(0xFF262626),
        primaryColor = Color(0xFFFFFFFF)
    ),
    MINT_CALM(
        id = "mint_calm",
        title = "Sage & Mint",
        subtitle = "Calming clinical green tone",
        isDark = false,
        backgroundColor = Color(0xFFF0FDF4),
        surfaceColor = Color(0xFFFFFFFF),
        onBackgroundColor = Color(0xFF064E3B),
        onSurfaceVariant = Color(0xFF047857),
        borderColor = Color(0xFFD1FAE5),
        primaryColor = Color(0xFF059669)
    ),
    SKY_BLUE(
        id = "sky_blue",
        title = "Sky Blue Tint",
        subtitle = "Soft fresh blue daytime theme",
        isDark = false,
        backgroundColor = Color(0xFFF0F7FF),
        surfaceColor = Color(0xFFFFFFFF),
        onBackgroundColor = Color(0xFF0C4A6E),
        onSurfaceVariant = Color(0xFF0284C7),
        borderColor = Color(0xFFBAE6FD),
        primaryColor = Color(0xFF0284C7)
    );

    companion object {
        fun fromId(id: String): ThemePreset {
            return entries.firstOrNull { it.id == id } ?: MONOCHROME_LIGHT
        }
    }
}

data class AppThemeState(
    val preset: ThemePreset = ThemePreset.MONOCHROME_LIGHT,
    val customBackgroundHex: String? = null,
    val customAccentHex: String? = null,
    val showMicroTopics: Boolean = true,
    val compactDensity: Boolean = false
) {
    val backgroundColor: Color
        get() = customBackgroundHex?.let { parseHexColor(it) } ?: preset.backgroundColor

    val surfaceColor: Color
        get() = when {
            preset.isDark -> preset.surfaceColor
            customBackgroundHex != null && isLightColor(backgroundColor) -> Color.White
            customBackgroundHex != null -> Color(0xFF1E293B)
            else -> preset.surfaceColor
        }

    val onBackgroundColor: Color
        get() = when {
            isLightColor(backgroundColor) -> Color(0xFF0F172A)
            else -> Color(0xFFF8FAFC)
        }

    val onSurfaceVariant: Color
        get() = when {
            isLightColor(backgroundColor) -> Color(0xFF64748B)
            else -> Color(0xFF94A3B8)
        }

    val borderColor: Color
        get() = when {
            isLightColor(backgroundColor) -> Color(0xFFE2E8F0)
            else -> Color(0xFF334155)
        }

    val primaryColor: Color
        get() = customAccentHex?.let { parseHexColor(it) } ?: preset.primaryColor

    val isDark: Boolean
        get() = !isLightColor(backgroundColor)
}

fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = when (clean.length) {
            6 -> (0xFF000000 or clean.toLong(16)).toInt()
            8 -> clean.toLong(16).toInt()
            else -> 0xFF0F172A.toInt()
        }
        Color(colorInt)
    } catch (_: Exception) {
        Color(0xFF0F172A)
    }
}

fun isLightColor(color: Color): Boolean {
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return luminance > 0.5
}

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_theme_prefs", Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(loadThemeState())
    val themeState: StateFlow<AppThemeState> = _themeState.asStateFlow()

    private fun loadThemeState(): AppThemeState {
        val presetId = prefs.getString("theme_preset", ThemePreset.MONOCHROME_LIGHT.id) ?: ThemePreset.MONOCHROME_LIGHT.id
        val customBg = prefs.getString("custom_bg_hex", null)
        val customAccent = prefs.getString("custom_accent_hex", null)
        val showTopics = prefs.getBoolean("show_micro_topics", true)
        val compact = prefs.getBoolean("compact_density", false)

        return AppThemeState(
            preset = ThemePreset.fromId(presetId),
            customBackgroundHex = customBg,
            customAccentHex = customAccent,
            showMicroTopics = showTopics,
            compactDensity = compact
        )
    }

    fun setPreset(preset: ThemePreset) {
        prefs.edit()
            .putString("theme_preset", preset.id)
            .remove("custom_bg_hex")
            .apply()
        _themeState.value = _themeState.value.copy(
            preset = preset,
            customBackgroundHex = null
        )
    }

    fun setCustomBackgroundColor(hex: String?) {
        if (hex == null) {
            prefs.edit().remove("custom_bg_hex").apply()
        } else {
            prefs.edit().putString("custom_bg_hex", hex).apply()
        }
        _themeState.value = _themeState.value.copy(customBackgroundHex = hex)
    }

    fun setCustomAccentColor(hex: String?) {
        if (hex == null) {
            prefs.edit().remove("custom_accent_hex").apply()
        } else {
            prefs.edit().putString("custom_accent_hex", hex).apply()
        }
        _themeState.value = _themeState.value.copy(customAccentHex = hex)
    }

    fun setShowMicroTopics(show: Boolean) {
        prefs.edit().putBoolean("show_micro_topics", show).apply()
        _themeState.value = _themeState.value.copy(showMicroTopics = show)
    }

    fun setCompactDensity(compact: Boolean) {
        prefs.edit().putBoolean("compact_density", compact).apply()
        _themeState.value = _themeState.value.copy(compactDensity = compact)
    }

    fun resetToDefault() {
        prefs.edit().clear().apply()
        _themeState.value = AppThemeState()
    }
}
