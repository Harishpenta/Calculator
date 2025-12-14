package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class LoanPrepaymentState(
    val loanAmount: String = "5000000.0",
    val interestRate: String = "9.0",
    val tenureYears: String = "20",
    val monthlyPrepayment: String = "0.0",
    val lumpsumPrepayment: String = "0.0",
    
    // Results
    val originalEmi: Double = 0.0,
    val originalTotalInterest: Double = 0.0,
    val originalTotalPayment: Double = 0.0,
    val newTotalInterest: Double = 0.0,
    val newTotalPayment: Double = 0.0,
    val interestSaved: Double = 0.0,
    val originalTenureMonths: Int = 0,
    val newTenureMonths: Int = 0,
    val timeSavedMonths: Int = 0
)

class LoanPrepaymentViewModel : ViewModel() {
    var state by mutableStateOf(LoanPrepaymentState())
        private set

    init {
        calculatePrepayment()
    }

    fun onEvent(event: LoanPrepaymentEvent) {
        when (event) {
            is LoanPrepaymentEvent.UpdateLoanAmount -> {
                state = state.copy(loanAmount = event.amount)
                calculatePrepayment()
            }
            is LoanPrepaymentEvent.UpdateInterestRate -> {
                state = state.copy(interestRate = event.rate)
                calculatePrepayment()
            }
            is LoanPrepaymentEvent.UpdateTenure -> {
                state = state.copy(tenureYears = event.years)
                calculatePrepayment()
            }
            is LoanPrepaymentEvent.UpdateMonthlyPrepayment -> {
                state = state.copy(monthlyPrepayment = event.amount)
                calculatePrepayment()
            }
            is LoanPrepaymentEvent.UpdateLumpsumPrepayment -> {
                state = state.copy(lumpsumPrepayment = event.amount)
                calculatePrepayment()
            }
        }
    }

    private fun calculatePrepayment() {
        val P = state.loanAmount.toDoubleOrNull() ?: 0.0
        val rVal = state.interestRate.toDoubleOrNull() ?: 0.0
        val years = state.tenureYears.toIntOrNull() ?: 0
        
        val r = (rVal / 100) / 12
        val n = years * 12
        
        // 1. Calculate Original EMI
        val originalEmi = if (r != 0.0 && n > 0) {
            (P * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        } else if (n > 0) {
            P / n
        } else {
            0.0
        }

        val originalTotalPayment = originalEmi * n
        val originalTotalInterest = originalTotalPayment - P

        // 2. Calculate New Schedule with Prepayments
        var balance = P
        var totalInterestPaid = 0.0
        var monthsElapsed = 0
        
        val monthlyPrepay = state.monthlyPrepayment.toDoubleOrNull() ?: 0.0
        val lumpsumPrepay = state.lumpsumPrepayment.toDoubleOrNull() ?: 0.0
        
        while (balance > 0 && monthsElapsed < n * 2 && n > 0) { // Cap at 2x tenure
            // Interest for this month
            val interest = balance * r
            totalInterestPaid += interest
            
            // Principal component in EMI
            val principalInEmi = originalEmi - interest
            
            // Total payment this month = EMI + Monthly Prepayment
            // If it's month 1, add lumpsum too (simplification)
            var payment = originalEmi + monthlyPrepay
            if (monthsElapsed == 0) {
                payment += lumpsumPrepay
            }
            
            // Principal paid
            var principalPaid = payment - interest
            
            // Check if we are overpaying the balance
            if (principalPaid > balance) {
                principalPaid = balance
            }
            
            balance -= principalPaid
            monthsElapsed++
            
            if (balance < 1.0) balance = 0.0 // Floating point tolerance
        }
        
        val newTotalInterest = totalInterestPaid
        val newTotalPayment = P + newTotalInterest
        val interestSaved = (originalTotalInterest - newTotalInterest).coerceAtLeast(0.0)
        val timeSaved = (n - monthsElapsed).coerceAtLeast(0)

        state = state.copy(
            originalEmi = originalEmi,
            originalTotalInterest = originalTotalInterest,
            originalTotalPayment = originalTotalPayment,
            newTotalInterest = newTotalInterest,
            newTotalPayment = newTotalPayment,
            interestSaved = interestSaved,
            originalTenureMonths = n,
            newTenureMonths = monthsElapsed,
            timeSavedMonths = timeSaved
        )
    }
}

sealed class LoanPrepaymentEvent {
    data class UpdateLoanAmount(val amount: String) : LoanPrepaymentEvent()
    data class UpdateInterestRate(val rate: String) : LoanPrepaymentEvent()
    data class UpdateTenure(val years: String) : LoanPrepaymentEvent()
    data class UpdateMonthlyPrepayment(val amount: String) : LoanPrepaymentEvent()
    data class UpdateLumpsumPrepayment(val amount: String) : LoanPrepaymentEvent()
}
