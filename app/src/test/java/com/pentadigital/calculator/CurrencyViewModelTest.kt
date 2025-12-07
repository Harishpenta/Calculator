package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyViewModelTest {

    private val viewModel = CurrencyViewModel()

    @Test
    fun `initial conversion is correct`() {
        // 1 USD to INR (83.3)
        val state = viewModel.state
        assertEquals(83.3, state.convertedAmount, 0.1)
    }

    @Test
    fun `swap currencies works`() {
        viewModel.onEvent(CurrencyEvent.SwapCurrencies)
        // 1 INR to USD (1/83.3 = 0.012)
        assertEquals("INR", viewModel.state.fromCurrency)
        assertEquals("USD", viewModel.state.toCurrency)
        assertEquals(0.012, viewModel.state.convertedAmount, 0.001)
    }
}
