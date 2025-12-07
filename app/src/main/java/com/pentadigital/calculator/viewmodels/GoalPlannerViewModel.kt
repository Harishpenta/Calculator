package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class GoalPlannerState(
    val targetAmount: Double = 10000000.0, // 1 Crore
    val timePeriodYears: Int = 10,
    val expectedReturnRate: Double = 12.0,
    
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
        val target = state.targetAmount
        val r = (state.expectedReturnRate / 100) / 12
        val n = state.timePeriodYears * 12.0

        // Formula for Future Value of SIP:
        // FV = P * [ (1+r)^n - 1 ] * (1+r) / r
        // We need to find P (Monthly Investment)
        // P = FV * r / ( [ (1+r)^n - 1 ] * (1+r) )

        val monthlyInvestment = if (r != 0.0 && n > 0) {
            (target * r) / (((1 + r).pow(n) - 1) * (1 + r))
        } else if (n > 0) {
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
    data class UpdateTargetAmount(val amount: Double) : GoalPlannerEvent()
    data class UpdateTimePeriod(val years: Int) : GoalPlannerEvent()
    data class UpdateReturnRate(val rate: Double) : GoalPlannerEvent()
}
