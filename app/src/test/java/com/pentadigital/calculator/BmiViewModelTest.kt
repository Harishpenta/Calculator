package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class BmiViewModelTest {

    private val viewModel = BmiViewModel()

    @Test
    fun `initial calculation is correct`() {
        // 70kg, 170cm -> 70 / (1.7)^2 = 24.22
        val state = viewModel.state
        assertEquals(24.22, state.bmi, 0.1)
        assertEquals("Normal", state.category)
    }

    @Test
    fun `update weight updates bmi`() {
        viewModel.onEvent(BmiEvent.UpdateWeight(90.0))
        // 90 / (1.7)^2 = 31.14 -> Obese
        assertEquals(31.14, viewModel.state.bmi, 0.1)
        assertEquals("Obese", viewModel.state.category)
    }
}
