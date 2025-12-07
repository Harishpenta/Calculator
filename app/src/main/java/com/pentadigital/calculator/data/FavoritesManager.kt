package com.pentadigital.calculator.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesManager private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "calculator_favorites",
        Context.MODE_PRIVATE
    )
    
    private val _favorites = MutableStateFlow<Set<String>>(loadFavorites())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()
    
    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }
    
    fun isFavorite(calculatorId: String): Boolean {
        return _favorites.value.contains(calculatorId)
    }
    
    fun toggleFavorite(calculatorId: String) {
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.contains(calculatorId)) {
            currentFavorites.remove(calculatorId)
        } else {
            currentFavorites.add(calculatorId)
        }
        saveFavorites(currentFavorites)
        _favorites.value = currentFavorites
    }
    
    fun addFavorite(calculatorId: String) {
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.add(calculatorId)) {
            saveFavorites(currentFavorites)
            _favorites.value = currentFavorites
        }
    }
    
    fun removeFavorite(calculatorId: String) {
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.remove(calculatorId)) {
            saveFavorites(currentFavorites)
            _favorites.value = currentFavorites
        }
    }
    
    private fun saveFavorites(favorites: Set<String>) {
        prefs.edit()
            .putStringSet(FAVORITES_KEY, favorites)
            .apply()
    }
    
    companion object {
        private const val FAVORITES_KEY = "favorites"
        
        @Volatile
        private var INSTANCE: FavoritesManager? = null
        
        fun getInstance(context: Context): FavoritesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
