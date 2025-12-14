package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class GoalPlannerState(
    val targetAmount: String = "10000000.0", // 1 Crore
    val timePeriodYears: String = "10",
    val expectedReturnRate: String = "12.0",
    
    // Results
    val requiredMonthlyInvestment: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val totalReturns: Double = 0.0
)

class GoalPlannerViewModel : ViewModel() {
    var state by mutableStateOf(GoalPlannerState())
        private set

    init {
        calculateGoal()
    }

    fun onEvent(event: GoalPlannerEvent) {
        when (event) {
            is GoalPlannerEvent.UpdateTargetAmount -> {
                state = state.copy(targetAmount = event.amount)
                calculateGoal()
            }
            is GoalPlannerEvent.UpdateTimePeriod -> {
                state = state.copy(timePeriodYears = event.years)
                calculateGoal()
            }
            is GoalPlannerEvent.UpdateReturnRate -> {
                state = state.copy(expectedReturnRate = event.rate)
                calculateGoal()
            }
        }
    }

    private fun calculateGoal() {
        val target = state.targetAmount.toDoubleOrNull() ?: 0.0
        val rVal = state.expectedReturnRate.toDoubleOrNull() ?: 0.0
        val years = state.timePeriodYears.toIntOrNull() ?: 0
        
        val r = (rVal / 100) / 12
        val n = years * 12.0

        // Formula for Future Value of SIP:
        // FV = P * [ (1+r)^n - 1 ] * (1+r) / r
        // We need to find P (Monthly Investment)
        // P = FV * r / ( [ (1+r)^n - 1 ] * (1+r) )

        val monthlyInvestment = if (r != 0.0 && n > 0.0) {
            (target * r) / (((1 + r).pow(n) - 1) * (1 + r))
        } else if (n > 0.0) {
            target / n
        } else {
            0.0
        }

        val totalInvestment = monthlyInvestment * n
        val totalReturns = target - totalInvestment

        state = state.copy(
            requiredMonthlyInvestment = monthlyInvestment,
            totalInvestment = totalInvestment,
            totalReturns = totalReturns
        )
    }
}

sealed class GoalPlannerEvent {
    data class UpdateTargetAmount(val amount: String) : GoalPlannerEvent()
    data class UpdateTimePeriod(val years: String) : GoalPlannerEvent()
    data class UpdateReturnRate(val rate: String) : GoalPlannerEvent()
}
