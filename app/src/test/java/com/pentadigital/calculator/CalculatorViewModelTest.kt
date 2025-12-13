package com.pentadigital.calculator

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.data.HistoryDao
import com.pentadigital.calculator.data.HistoryEntity
import com.pentadigital.calculator.data.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeDao = object : HistoryDao {
        override fun getAllHistory(): Flow<List<HistoryEntity>> = flowOf(emptyList())
        override suspend fun insertHistory(history: HistoryEntity) {}
        override suspend fun clearHistory() {}
    }
    
    private lateinit var viewModel: CalculatorViewModel
    private val repository = HistoryRepository(fakeDao)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CalculatorViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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
    fun `calculation works correctly`() = kotlinx.coroutines.test.runTest(testDispatcher) {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        viewModel.onAction(CalculatorAction.Number(3))
        viewModel.onAction(CalculatorAction.Calculate)
        
        // Wait for decoding animation (approx 300ms + delays)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Result is "8" because 5 + 3 = 8
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
