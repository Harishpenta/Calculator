package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class EmiViewModelTest {

    private val viewModel = EmiViewModel()

    @Test
    fun `initial calculation is correct`() {
        // Default: 500,000, 8.5%, 5 years
        // E approx 10,258
        val state = viewModel.state
        assertEquals(10258.0, state.emi, 10.0)
    }

    @Test
    fun `update loan amount updates calculation`() {
        viewModel.onEvent(EmiEvent.UpdateLoanAmount(1000000.0))
        // Should be double
        assertEquals(20516.0, viewModel.state.emi, 20.0)
    }
}
