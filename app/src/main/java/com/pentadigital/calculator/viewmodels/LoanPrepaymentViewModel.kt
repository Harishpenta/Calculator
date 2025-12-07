package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class LoanPrepaymentState(
    val loanAmount: Double = 5000000.0,
    val interestRate: Double = 9.0,
    val tenureYears: Int = 20,
    val monthlyPrepayment: Double = 0.0,
    val lumpsumPrepayment: Double = 0.0,
    
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
        val P = state.loanAmount
        val r = (state.interestRate / 100) / 12
        val n = state.tenureYears * 12
        
        // 1. Calculate Original EMI
        val originalEmi = if (r != 0.0) {
            (P * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        } else {
            P / n
        }

        val originalTotalPayment = originalEmi * n
        val originalTotalInterest = originalTotalPayment - P

        // 2. Calculate New Schedule with Prepayments
        var balance = P
        var totalInterestPaid = 0.0
        var monthsElapsed = 0
        
        // Apply lumpsum at the start (simplified for this version, or could be applied at specific month)
        // For this feature, let's assume lumpsum is paid at the beginning or spread? 
        // Usually lumpsum is a one-time payment. Let's assume it's paid after 1st month or immediately reducing principal.
        // Let's treat lumpsum as an immediate reduction for simplicity, or we can add a "Lumpsum Month" input later.
        // For now, let's assume lumpsum is paid along with the first EMI for maximum impact, or we can just subtract it from Principal if it's a "down payment" style, 
        // but "Prepayment" usually implies during the loan.
        // Let's apply lumpsum at month 1 for better realism.
        
        // Actually, let's stick to a standard amortization loop.
        
        while (balance > 0 && monthsElapsed < n * 2) { // Cap at 2x tenure to prevent infinite loops if logic fails
            // Interest for this month
            val interest = balance * r
            totalInterestPaid += interest
            
            // Principal component in EMI
            val principalInEmi = originalEmi - interest
            
            // Total payment this month = EMI + Monthly Prepayment
            // If it's month 1, add lumpsum too (simplification)
            var payment = originalEmi + state.monthlyPrepayment
            if (monthsElapsed == 0) {
                payment += state.lumpsumPrepayment
            }
            
            // Principal paid
            var principalPaid = payment - interest
            
            // Check if we are overpaying the balance
            if (principalPaid > balance) {
                principalPaid = balance
                // Adjust payment for last month
                // payment = balance + interest
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
    data class UpdateLoanAmount(val amount: Double) : LoanPrepaymentEvent()
    data class UpdateInterestRate(val rate: Double) : LoanPrepaymentEvent()
    data class UpdateTenure(val years: Int) : LoanPrepaymentEvent()
    data class UpdateMonthlyPrepayment(val amount: Double) : LoanPrepaymentEvent()
    data class UpdateLumpsumPrepayment(val amount: Double) : LoanPrepaymentEvent()
}
