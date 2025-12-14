package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class SipState(
    val monthlyInvestment: String = "5000.0",
    val expectedReturnRate: String = "12.0",
    val timePeriodYears: String = "10",
    val investedAmount: Double = 0.0,
    val estimatedReturns: Double = 0.0,
    val totalValue: Double = 0.0
)

class SipViewModel : ViewModel() {
    var state by mutableStateOf(SipState())
        private set

    init {
        calculateSip()
    }

    fun onEvent(event: SipEvent) {
        when (event) {
            is SipEvent.UpdateInvestment -> {
                state = state.copy(monthlyInvestment = event.amount)
                calculateSip()
            }
            is SipEvent.UpdateReturnRate -> {
                state = state.copy(expectedReturnRate = event.rate)
                calculateSip()
            }
            is SipEvent.UpdateTimePeriod -> {
                state = state.copy(timePeriodYears = event.years)
                calculateSip()
            }
        }
    }

    private fun calculateSip() {
        val P = state.monthlyInvestment.toDoubleOrNull() ?: 0.0
        val r = state.expectedReturnRate.toDoubleOrNull() ?: 0.0
        val years = state.timePeriodYears.toIntOrNull() ?: 0
        
        val i = (r / 100) / 12
        val n = years * 12

        // M = P × ({[1 + i]^n - 1} / i) × (1 + i)
        val M = if (i != 0.0) P * ((1 + i).pow(n) - 1) / i * (1 + i) else P * n
        val invested = P * n
        val returns = M - invested

        state = state.copy(
            investedAmount = invested,
            estimatedReturns = returns,
            totalValue = M
        )
    }
}

sealed class SipEvent {
    data class UpdateInvestment(val amount: String) : SipEvent()
    data class UpdateReturnRate(val rate: String) : SipEvent()
    data class UpdateTimePeriod(val years: String) : SipEvent()
}
