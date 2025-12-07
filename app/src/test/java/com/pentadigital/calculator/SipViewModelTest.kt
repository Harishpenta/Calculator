package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class SipViewModelTest {

    private val viewModel = SipViewModel()

    @Test
    fun `initial calculation is correct`() {
        // Default: 5000, 12%, 10 years
        // P = 5000, i = 0.01, n = 120
        // M = 5000 * ((1.01)^120 - 1) / 0.01 * 1.01
        // M approx 11,61,695
        val state = viewModel.state
        assertEquals(1161695.0, state.totalValue, 100.0) // Allow small delta
    }

    @Test
    fun `update investment updates calculation`() {
        viewModel.onEvent(SipEvent.UpdateInvestment(10000.0))
        // Should be double the default
        assertEquals(2323390.0, viewModel.state.totalValue, 200.0)
    }
}
