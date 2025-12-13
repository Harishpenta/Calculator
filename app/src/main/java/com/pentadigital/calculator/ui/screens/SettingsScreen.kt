package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.theme.MediumGray
import com.pentadigital.calculator.ui.theme.Orange
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: ThemeState,
    onAction: (ThemeEvent) -> Unit,
    onClearHistory: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(R.string.settings).uppercase(), 
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back), 
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val isLandscape = isLandscape()
        val windowSize = rememberWindowSize()
        val isTwoPane = isLandscape || windowSize.width == WindowSizeClass.EXPANDED

        if (isTwoPane) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Pane: App Theme & Language
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Language Section
                    ThemeSettingsSection(
                        title = stringResource(R.string.language),
                        content = {
                            LanguageDropdown(
                                selectedLanguage = state.language,
                                onLanguageSelected = { onAction(ThemeEvent.UpdateLanguage(it)) }
                            )
                        }
                    )

                    // App Theme Section
                    ThemeSettingsSection(
                        title = stringResource(R.string.app_theme),
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                AppTheme.values().forEach { theme ->
                                    ThemeOptionRow(
                                        text = when(theme) {
                                            AppTheme.System -> stringResource(R.string.system_default)
                                            AppTheme.Light -> stringResource(R.string.light)
                                            AppTheme.Dark -> stringResource(R.string.dark)
                                        },
                                        isSelected = state.appTheme == theme,
                                        onClick = { onAction(ThemeEvent.UpdateTheme(theme)) }
                                    )
                                }
                            }
                        }
                    )
                }

                // Right Pane: Accent Color
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ThemeSettingsSection(
                        title = stringResource(R.string.accent_color),
                        content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AccentColor.values().forEach { accent ->
                                    val color = when (accent) {
                                        AccentColor.Orange -> Orange
                                        AccentColor.Blue -> PrimaryBrand
                                        AccentColor.Green -> FinanceGreen
                                        AccentColor.Pink -> Pink
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (state.accentColor == accent) 2.dp else 0.dp,
                                                color = if (state.accentColor == accent) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { onAction(ThemeEvent.UpdateAccent(accent)) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.accentColor == accent) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Language Section
                ThemeSettingsSection(
                    title = stringResource(R.string.language),
                    content = {
                        LanguageDropdown(
                            selectedLanguage = state.language,
                            onLanguageSelected = { onAction(ThemeEvent.UpdateLanguage(it)) }
                        )
                    }
                )

                // App Theme Section
                ThemeSettingsSection(
                    title = stringResource(R.string.app_theme),
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppTheme.values().forEach { theme ->
                                ThemeOptionRow(
                                    text = when(theme) {
                                        AppTheme.System -> stringResource(R.string.system_default)
                                        AppTheme.Light -> stringResource(R.string.light)
                                        AppTheme.Dark -> stringResource(R.string.dark)
                                    },
                                    isSelected = state.appTheme == theme,
                                    onClick = { onAction(ThemeEvent.UpdateTheme(theme)) }
                                )
                            }
                        }
                    }
                )

                // Interactions Section
                ThemeSettingsSection(
                    title = "INTERACTIONS",
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TechText(
                                text = "Haptic Feedback",
                                fontSize = 16.sp
                            )
                            androidx.compose.material3.Switch(
                                checked = state.isHapticsEnabled,
                                onCheckedChange = { onAction(ThemeEvent.ToggleHaptics(it)) },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TechText(
                                text = "Sound Effects",
                                fontSize = 16.sp
                            )
                            androidx.compose.material3.Switch(
                                checked = state.isSoundEnabled,
                                onCheckedChange = { onAction(ThemeEvent.ToggleSound(it)) },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                )
                
                // Data Management
                ThemeSettingsSection(
                    title = "DATA",
                    content = {
                        CyberpunkButton(
                            text = "CLEAR HISTORY",
                            onClick = onClearHistory,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )

                // About Section
                ThemeSettingsSection(
                    title = "ABOUT",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            TechText("Cyberpunk Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            TechText("Version 1.0.0", fontSize = 14.sp, color = CyberpunkTextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            TechText("Designed for the future.", fontSize = 12.sp, color = CyberpunkTextSecondary)
                        }
                    }
                )

                // Accent Color Section
                ThemeSettingsSection(
                    title = stringResource(R.string.accent_color),
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AccentColor.values().forEach { accent ->
                                val color = when (accent) {
                                    AccentColor.Orange -> Orange
                                    AccentColor.Blue -> PrimaryBrand
                                    AccentColor.Green -> FinanceGreen
                                    AccentColor.Pink -> Pink
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (state.accentColor == accent) 2.dp else 0.dp,
                                            color = if (state.accentColor == accent) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { onAction(ThemeEvent.UpdateAccent(accent)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (state.accentColor == accent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TechText(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        CyberpunkCard(
            borderColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        TechText(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLanguage.displayName,
            onValueChange = {},
            readOnly = true,
            label = { TechText(stringResource(R.string.language), color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
        ) {
            AppLanguage.values().forEach { language ->
                DropdownMenuItem(
                    text = {
                        TechText(
                            text = language.displayName,
                            fontWeight = if (language == selectedLanguage) FontWeight.Bold else FontWeight.Normal,
                            color = if (language == selectedLanguage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.primary,
                        trailingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
