package com.pentadigital.calculator.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")

enum class AppTheme {
    System, Light, Dark
}

enum class AccentColor {
    Orange, Blue, Green, Pink
}

enum class AppLanguage(val code: String, val displayName: String) {
    English("en", "English"),
    Hindi("hi", "Hindi"),
    Gujarati("gu", "Gujarati"),
    Telugu("te", "Telugu"),
    Marathi("mr", "Marathi"),
    Tamil("ta", "Tamil"),
    Kannada("kn", "Kannada"),
    Malayalam("ml", "Malayalam"),
    Norwegian("no", "Norwegian"),
    Swedish("sv", "Swedish"),
    Danish("da", "Danish"),
    PortugueseBR("pt-rBR", "Portuguese (Brazil)"),
    French("fr", "French"),
    EnglishUK("en-rGB", "English (UK)")
}

data class ThemeState(
    val appTheme: AppTheme = AppTheme.System,
    val accentColor: AccentColor = AccentColor.Orange,
    val language: AppLanguage = AppLanguage.English
)

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    var state by mutableStateOf(ThemeState())
        private set

    private val context = application.applicationContext
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val ACCENT_KEY = stringPreferencesKey("accent_color")
    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    init {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                val themeName = preferences[THEME_KEY] ?: AppTheme.System.name
                val accentName = preferences[ACCENT_KEY] ?: AccentColor.Orange.name
                val languageCode = preferences[LANGUAGE_KEY] ?: AppLanguage.English.code
                
                ThemeState(
                    appTheme = AppTheme.valueOf(themeName),
                    accentColor = AccentColor.valueOf(accentName),
                    language = AppLanguage.values().find { it.code == languageCode } ?: AppLanguage.English
                )
            }.collect {
                state = it
            }
        }
    }

    fun onEvent(event: ThemeEvent) {
        viewModelScope.launch {
            when (event) {
                is ThemeEvent.UpdateTheme -> {
                    context.dataStore.edit { preferences ->
                        preferences[THEME_KEY] = event.theme.name
                    }
                }
                is ThemeEvent.UpdateAccent -> {
                    context.dataStore.edit { preferences ->
                        preferences[ACCENT_KEY] = event.color.name
                    }
                }
                is ThemeEvent.UpdateLanguage -> {
                    context.dataStore.edit { preferences ->
                        preferences[LANGUAGE_KEY] = event.language.code
                    }
                }
            }
        }
    }
}

sealed class ThemeEvent {
    data class UpdateTheme(val theme: AppTheme) : ThemeEvent()
    data class UpdateAccent(val color: AccentColor) : ThemeEvent()
    data class UpdateLanguage(val language: AppLanguage) : ThemeEvent()
}

class ThemeViewModelFactory(private val context: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(context.applicationContext as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
