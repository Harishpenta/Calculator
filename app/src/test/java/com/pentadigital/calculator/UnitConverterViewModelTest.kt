package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterViewModelTest {

    private val viewModel = UnitConverterViewModel()

    @Test
    fun `initial conversion is correct`() {
        // 1 Meter to Feet (3.28084)
        val state = viewModel.state
        assertEquals(3.28084, state.resultValue, 0.001)
    }

    @Test
    fun `temperature conversion works`() {
        viewModel.onEvent(UnitConverterEvent.UpdateCategory(UnitCategory.Temperature))
        viewModel.onEvent(UnitConverterEvent.UpdateInput(100.0))
        // 100 Celsius to Fahrenheit = 212
        assertEquals(212.0, viewModel.state.resultValue, 0.1)
    }
}
