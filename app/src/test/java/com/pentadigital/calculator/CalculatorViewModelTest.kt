package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorViewModelTest {

    private val viewModel = CalculatorViewModel()

    @Test
    fun `initial state is empty`() {
        val state = viewModel.state
        assertEquals("", state.number1)
        assertEquals("", state.number2)
        assertEquals(null, state.operation)
    }

    @Test
    fun `enter number updates number1`() {
        viewModel.onAction(CalculatorAction.Number(5))
        assertEquals("5", viewModel.state.number1)
    }

    @Test
    fun `enter operation updates operation`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        assertEquals(CalculatorOperation.Add, viewModel.state.operation)
    }

    @Test
    fun `calculation works correctly`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Calculate)
        
        assertEquals("8", viewModel.state.number1)
        assertEquals("", viewModel.state.number2)
        assertEquals(null, viewModel.state.operation)
    }

    @Test
    fun `enter decimal on empty state adds 0 prefix`() {
        viewModel.onAction(CalculatorAction.Decimal)
        assertEquals("0.", viewModel.state.number1)
    }
}
