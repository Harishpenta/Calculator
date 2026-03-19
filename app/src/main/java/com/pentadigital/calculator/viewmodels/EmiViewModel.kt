package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class EmiState(
    val loanAmount: Double = 500000.0,
    val interestRate: Double = 8.5,
    val tenureYears: Int = 5,
    val emi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0,
    val errorMessage: String? = null
)

class EmiViewModel : ViewModel() {
    var state by mutableStateOf(EmiState())
        private set

    init {
        calculateEmi()
    }

    fun onEvent(event: EmiEvent) {
        when (event) {
            is EmiEvent.UpdateLoanAmount -> {
                state = state.copy(loanAmount = event.amount)
                calculateEmi()
            }
            is EmiEvent.UpdateInterestRate -> {
                state = state.copy(interestRate = event.rate)
                calculateEmi()
            }
            is EmiEvent.UpdateTenure -> {
                state = state.copy(tenureYears = event.years)
                calculateEmi()
            }
        }
    }

    private fun calculateEmi() {
        val P = state.loanAmount
        val rate = state.interestRate
        val years = state.tenureYears

        when {
            P <= 0 -> {
                state = state.copy(emi = 0.0, totalInterest = 0.0, totalPayment = 0.0, errorMessage = "Loan amount must be greater than 0")
                return
            }
            rate < 0 || rate > 100 -> {
                state = state.copy(emi = 0.0, totalInterest = 0.0, totalPayment = 0.0, errorMessage = "Interest rate must be between 0 and 100")
                return
            }
            years <= 0 -> {
                state = state.copy(emi = 0.0, totalInterest = 0.0, totalPayment = 0.0, errorMessage = "Tenure must be at least 1 year")
                return
            }
        }

        val r = (rate / 100) / 12
        val n = years * 12.0

        // E = P * r * (1 + r)^n / ((1 + r)^n - 1)
        val emi = if (r != 0.0) {
            (P * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        } else {
            P / n
        }

        val totalPayment = emi * n
        val totalInterest = totalPayment - P

        state = state.copy(
            emi = emi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            errorMessage = null
        )
    }
}

sealed class EmiEvent {
    data class UpdateLoanAmount(val amount: Double) : EmiEvent()
    data class UpdateInterestRate(val rate: Double) : EmiEvent()
    data class UpdateTenure(val years: Int) : EmiEvent()
}
