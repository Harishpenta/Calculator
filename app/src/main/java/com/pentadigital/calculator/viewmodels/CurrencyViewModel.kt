package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CurrencyState(
    val amount: Double = 1.0,
    val fromCurrency: String = "USD",
    val toCurrency: String = "INR",
    val convertedAmount: Double = 0.0
)

class CurrencyViewModel : ViewModel() {
    var state by mutableStateOf(CurrencyState())
        private set

    // Fixed exchange rates relative to USD (Base)
    private val rates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "INR" to 83.3,
        "GBP" to 0.79,
        "JPY" to 148.5
    )

    init {
        calculateConversion()
    }

    fun onEvent(event: CurrencyEvent) {
        when (event) {
            is CurrencyEvent.UpdateAmount -> {
                state = state.copy(amount = event.amount)
                calculateConversion()
            }
            is CurrencyEvent.UpdateFromCurrency -> {
                state = state.copy(fromCurrency = event.currency)
                calculateConversion()
            }
            is CurrencyEvent.UpdateToCurrency -> {
                state = state.copy(toCurrency = event.currency)
                calculateConversion()
            }
            is CurrencyEvent.SwapCurrencies -> {
                state = state.copy(
                    fromCurrency = state.toCurrency,
                    toCurrency = state.fromCurrency
                )
                calculateConversion()
            }
        }
    }

    private fun calculateConversion() {
        val fromRate = rates[state.fromCurrency] ?: 1.0
        val toRate = rates[state.toCurrency] ?: 1.0
        
        // Convert to USD first, then to target currency
        val amountInUsd = state.amount / fromRate
        val finalAmount = amountInUsd * toRate
        
        state = state.copy(convertedAmount = finalAmount)
    }
}

sealed class CurrencyEvent {
    data class UpdateAmount(val amount: Double) : CurrencyEvent()
    data class UpdateFromCurrency(val currency: String) : CurrencyEvent()
    data class UpdateToCurrency(val currency: String) : CurrencyEvent()
    object SwapCurrencies : CurrencyEvent()
}
