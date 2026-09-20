package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeState
import com.example.data.ThemePreset
import com.example.data.parseHexColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeState: AppThemeState,
    onPresetSelected: (ThemePreset) -> Unit,
    onCustomBackgroundSelected: (String?) -> Unit,
    onCustomAccentSelected: (String?) -> Unit,
    onShowMicroTopicsChanged: (Boolean) -> Unit,
    onCompactDensityChanged: (Boolean) -> Unit,
    onResetDefaults: () -> Unit,
    onBack: () -> Unit
) {
    val currentBg = themeState.backgroundColor
    val currentSurface = themeState.surfaceColor
    val currentText = themeState.onBackgroundColor
    val currentVariant = themeState.onSurfaceVariant
    val currentBorder = themeState.borderColor
    val currentPrimary = themeState.primaryColor

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        containerColor = currentBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = currentText
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = currentText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onResetDefaults,
                        modifier = Modifier.testTag("settings_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Theme",
                            tint = currentVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Presets
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FormatPaint, contentDescription = null, tint = currentPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "THEME PRESETS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentVariant,
                    letterSpacing = 1.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ThemePreset.entries.forEach { preset ->
                    val isSelected = themeState.preset == preset && themeState.customBackgroundHex == null
                    PresetOptionCard(
                        preset = preset,
                        isSelected = isSelected,
                        cardBg = currentSurface,
                        textColor = currentText,
                        variantColor = currentVariant,
                        borderColor = currentBorder,
                        onSelect = { onPresetSelected(preset) }
                    )
                }
            }

            // Section 2: Custom Background Color Swatches
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ColorLens, contentDescription = null, tint = currentPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUSTOM BACKGROUND COLOR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentVariant,
                    letterSpacing = 1.sp
                )
            }

            val backgroundSwatches = listOf(
                Pair("#FFFFFF", "Pure White"),
                Pair("#F8FAFC", "Slate Light"),
                Pair("#FDFBF7", "Warm Sepia"),
                Pair("#F1F5F9", "Cool Slate"),
                Pair("#F0FDF4", "Soft Mint"),
                Pair("#F0F7FF", "Sky Blue"),
                Pair("#FAF5FF", "Lavender"),
                Pair("#FFFBEB", "Warm Amber"),
                Pair("#1E293B", "Slate Dark"),
                Pair("#0F172A", "Charcoal"),
                Pair("#0A0E1A", "Deep Navy"),
                Pair("#000000", "Pitch Black")
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                backgroundSwatches.forEach { (hex, name) ->
                    val color = parseHexColor(hex)
                    val isChosen = themeState.customBackgroundHex.equals(hex, ignoreCase = true)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onCustomBackgroundSelected(hex) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isChosen) 3.dp else 1.dp,
                                    color = if (isChosen) currentPrimary else currentBorder,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChosen) {
                                val iconTint = if (com.example.data.isLightColor(color)) Color.Black else Color.White
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = currentVariant
                        )
                    }
                }
            }

            // Section 3: Accent Color Swatches
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = currentPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PRIMARY ACCENT COLOR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentVariant,
                    letterSpacing = 1.sp
                )
            }

            val accentSwatches = listOf(
                Pair("#0F172A", "Obsidian"),
                Pair("#4F46E5", "Indigo"),
                Pair("#0284C7", "Sky Blue"),
                Pair("#059669", "Emerald"),
                Pair("#E11D48", "Crimson"),
                Pair("#D97706", "Amber"),
                Pair("#7C3AED", "Purple"),
                Pair("#64748B", "Slate")
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                accentSwatches.forEach { (hex, name) ->
                    val color = parseHexColor(hex)
                    val isChosen = themeState.customAccentHex.equals(hex, ignoreCase = true)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onCustomAccentSelected(hex) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isChosen) 3.dp else 1.dp,
                                    color = if (isChosen) currentText else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChosen) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = currentVariant
                        )
                    }
                }
            }

            // Section 4: Display Preferences
            Text(
                text = "DISPLAY PREFERENCES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = currentVariant,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, currentBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = currentSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Micro-Topics in Chapters",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = currentText
                            )
                            Text(
                                text = "Display topic tags (e.g. projectile, friction) under each chapter",
                                style = MaterialTheme.typography.bodySmall,
                                color = currentVariant
                            )
                        }
                        Switch(
                            checked = themeState.showMicroTopics,
                            onCheckedChange = onShowMicroTopicsChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = currentPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Compact Layout Spacing",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = currentText
                            )
                            Text(
                                text = "Tighter vertical padding to view more chapters on screen",
                                style = MaterialTheme.typography.bodySmall,
                                color = currentVariant
                            )
                        }
                        Switch(
                            checked = themeState.compactDensity,
                            onCheckedChange = onCompactDensityChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = currentPrimary
                            )
                        )
                    }
                }
            }

            // Reset Button
            OutlinedButton(
                onClick = onResetDefaults,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_defaults_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All to Default Monochrome")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PresetOptionCard(
    preset: ThemePreset,
    isSelected: Boolean,
    cardBg: Color,
    textColor: Color,
    variantColor: Color,
    borderColor: Color,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) preset.primaryColor else borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("preset_card_${preset.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color palette sample dots
            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(preset.backgroundColor)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(preset.surfaceColor)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(preset.primaryColor)
                        .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = preset.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = variantColor
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(preset.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
