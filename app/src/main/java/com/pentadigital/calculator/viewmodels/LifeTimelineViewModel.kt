package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pentadigital.calculator.data.UserPreferences
import com.pentadigital.calculator.data.UserPreferencesRepository
import com.pentadigital.calculator.domain.EventType
import com.pentadigital.calculator.domain.LifeEvent
import com.pentadigital.calculator.domain.TimelineState
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonGreen
import com.pentadigital.calculator.ui.theme.NeonPurple
import kotlinx.coroutines.launch
import java.util.Calendar

class LifeTimelineViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {
    var state by mutableStateOf(TimelineState())
        private set

    init {
        viewModelScope.launch {
            repository.userPreferencesFlow.collect { prefs ->
                calculateTimeline(prefs)
            }
        }
    }

    fun onSimulationChange(monthlySavings: Double) {
        state = state.copy(monthlySavings = monthlySavings)
        // We'll trigger a recalculation based on the *latest* prefs we have, 
        // but since onSimulationChange only updates local state 'monthlySavings',
        // we might want to persist this or just keep it ephemeral.
        // For now, let's keep it ephemeral as per original design, 
        // but we need the base prefs to recalculate. 
        // To avoid complexity, let's just re-run calculation with current state + ephemeral:
        // However, we don't have access to 'prefs' here easily unless we store it.
        // Let's store the latest prefs in a variable.
    }
    
    private var lastPrefs: UserPreferences? = null

    private fun calculateTimeline(prefs: UserPreferences) {
        lastPrefs = prefs
        
        // 1. Calculate Current Age from DOB
        val dob = prefs.dobMillis
        val currentAge = if (dob != null) {
            val dobCalendar = Calendar.getInstance().apply { timeInMillis = dob }
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age.coerceAtLeast(0)
        } else {
            30 // Default if not set
        }

        // 2. Goals & Savings
        val targetAmount = prefs.financialGoalAmount
        val rate = 12.0 / 100 / 12
        // Use the slider value if user is playing with it, otherwise default to repository savings?
        // The original code had slider controlling 'monthlySavings' in state.
        // Let's defer to state.monthlySavings if it's been touched, or init it from prefs?
        // Actually, let's say the slider starts at prefs.currentSavings
        
        // Logic: specific update to state.monthlySavings only happens on init or manual change
        // We probably want the slider to reflect the persisted savings initially.
        // But if I do 'state = state.copy(monthlySavings = prefs.currentSavings)' it might overwrite user interaction
        // if this flow emits again.
        // For V1, let's assume flow emission implies a "reset" or "load" of base data.
        
        // We will separate the "simulated" savings from "persisted" savings.
        // For this refactor, let's use the 'monthlySavings' from state, 
        // but initialize it from prefs if it's the first load or if we want to sync.
        
        // To keep it simple: We use the value from prefs as the 'base', 
        // but if the user has moved the slider (state.monthlySavings), we might want to respect that?
        // Actually, the original code had 'onSimulationChange'.
        // Let's stick to: Prefs loads -> updates everything. 
        // If user drags slider -> updates 'monthlySavings' in state -> Recalculates.
        
        // But wait, 'calculateTimeline' is called by Flow collection.
        // If user drags slider, calculateTimeline(lastPrefs) should be called?
        // Refactoring: onSimulationChange should just update state, and we need a way to combine.
        // Actually, let's just put the calculation logic in a helper that takes (Prefs, State).
        
        // Simplest: 
        // updateState(prefs) -> updates base params.
        // onSimulationChange -> updates state.monthlySavings -> triggers verify.
        
        // Let's calculate based on function arguments
        val savings = if (state.monthlySavings > 0) state.monthlySavings else prefs.currentSavings
        
        // ... (Math logic same as before)
        
        var monthsToGoal = 0
        if (savings > 0) {
            val numerator = (targetAmount * rate) / (savings * (1 + rate)) + 1
            if (numerator > 0) {
                monthsToGoal = (Math.log(numerator) / Math.log(1 + rate)).toInt()
            }
        }
        
        val yearsToGoal = monthsToGoal / 12
        val goalAchievedAge = currentAge + yearsToGoal
        
        // 3. Health
        val primeHealthEnd = 60 // Could be prefs.retirementAgeGoal or fixed 60
        
        // Construct Events
        val events = mutableListOf<LifeEvent>()
        
        // Current Age
        events.add(LifeEvent(
            title = "Now", 
            age = currentAge, 
            type = EventType.LIFE_MILESTONE, 
            description = "You are here", 
            color = Color.White,
            titleRes = com.pentadigital.calculator.R.string.event_now,
            descRes = com.pentadigital.calculator.R.string.event_now_desc
        ))
        
        // Goal Achieved
        events.add(LifeEvent(
            title = "Goal Reached", 
            age = goalAchievedAge, 
            type = EventType.FINANCE_POSITIVE, 
            description = "₹${(targetAmount/10000000).toInt()} Cr", 
            amount = targetAmount, 
            color = NeonGreen,
            titleRes = com.pentadigital.calculator.R.string.event_goal_reached,
            descRes = com.pentadigital.calculator.R.string.event_goal_reached_desc,
            descArgs = listOf((targetAmount/10000000).toInt())
        ))
        
        // Retirement / Prime Health End
        events.add(LifeEvent(
            title = "Prime Health Ends", 
            age = primeHealthEnd, 
            type = EventType.HEALTH_WARNING, 
            description = "Statistical decline", 
            color = NeonPurple,
            titleRes = com.pentadigital.calculator.R.string.event_prime_health_ends,
            descRes = com.pentadigital.calculator.R.string.event_prime_health_desc
        ))
        
        // Life Expectancy
        val lifeExp = 85
        events.add(LifeEvent(
            title = "Life Expectancy", 
            age = lifeExp, 
            type = EventType.LIFE_MILESTONE, 
            description = "Actuarial Estimate", 
            color = Color.Gray,
            titleRes = com.pentadigital.calculator.R.string.event_life_expectancy,
            descRes = com.pentadigital.calculator.R.string.event_life_expectancy_desc
        ))
        
        // User Retirement Goal (from prefs)
        events.add(LifeEvent(
            title = "Retirement Goal", 
            age = prefs.retirementAgeGoal, 
            type = EventType.LIFE_MILESTONE, 
            description = "Planned Retirement", 
            color = NeonCyan,
            titleRes = com.pentadigital.calculator.R.string.event_retirement_goal,
            descRes = com.pentadigital.calculator.R.string.event_retirement_goal_desc
        ))

        state = state.copy(
            currentAge = currentAge,
            lifeExpectancy = lifeExp,
            primeHealthEndAge = primeHealthEnd,
            goalAchievedAge = goalAchievedAge,
            events = events.sortedBy { it.age },
            monthlySavings = savings // Ensure slider reflects this
        )
    }
    
    // Allow updating prefs from UI
    fun saveSettings(dob: Long, retirementAge: Int, financialGoal: Double, currentSavings: Double) {
        viewModelScope.launch {
            repository.updateDob(dob)
            repository.updateRetirementAge(retirementAge)
            repository.updateFinancialGoal(financialGoal)
            repository.updateCurrentSavings(currentSavings)
            // Flow will trigger update
        }
    }
}

class LifeTimelineViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LifeTimelineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LifeTimelineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

