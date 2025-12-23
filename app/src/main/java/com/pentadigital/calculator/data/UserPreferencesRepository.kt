package com.pentadigital.calculator.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore by preferencesDataStore(name = "user_life_goals")

data class UserPreferences(
    val dobMillis: Long?,
    val retirementAgeGoal: Int,
    val financialGoalAmount: Double,
    val currentSavings: Double
)

class UserPreferencesRepository(private val context: Context) {
    private val dataStore = context.userPreferencesDataStore

    companion object {
        val DOB_MILLIS = longPreferencesKey("dob_millis")
        val RETIREMENT_AGE_GOAL = intPreferencesKey("retirement_age_goal")
        val FINANCIAL_GOAL_AMOUNT = doublePreferencesKey("financial_goal_amount")
        val CURRENT_SAVINGS = doublePreferencesKey("current_savings_amount")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            dobMillis = preferences[DOB_MILLIS],
            retirementAgeGoal = preferences[RETIREMENT_AGE_GOAL] ?: 60,
            financialGoalAmount = preferences[FINANCIAL_GOAL_AMOUNT] ?: 10000000.0,
            currentSavings = preferences[CURRENT_SAVINGS] ?: 0.0
        )
    }

    suspend fun updateDob(dateMillis: Long) {
        dataStore.edit { it[DOB_MILLIS] = dateMillis }
    }

    suspend fun updateRetirementAge(age: Int) {
        dataStore.edit { it[RETIREMENT_AGE_GOAL] = age }
    }

    suspend fun updateFinancialGoal(amount: Double) {
        dataStore.edit { it[FINANCIAL_GOAL_AMOUNT] = amount }
    }

    suspend fun updateCurrentSavings(amount: Double) {
        dataStore.edit { it[CURRENT_SAVINGS] = amount }
    }
}
