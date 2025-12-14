package com.pentadigital.calculator

import com.pentadigital.calculator.data.HistoryDao
import com.pentadigital.calculator.data.HistoryEntity
import com.pentadigital.calculator.data.HistoryRepository
import com.pentadigital.calculator.viewmodels.CalculatorAction
import com.pentadigital.calculator.viewmodels.CalculatorOperation
import com.pentadigital.calculator.viewmodels.CalculatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.Rule

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
    fun `calculation sets lastExpression`() = runTest(testDispatcher) {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Calculate)
        
        testDispatcher.scheduler.advanceUntilIdle() // Wait for decoding animation
        
        assertEquals("3", viewModel.state.number1)
        assertEquals("1 + 2", viewModel.state.lastExpression)
    }

    @Test
    fun `new input clears lastExpression`() = runTest(testDispatcher) {
        // Setup initial calculation
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Calculate)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("1 + 2", viewModel.state.lastExpression)
        
        // Start new input
        viewModel.onAction(CalculatorAction.Number(5))
        
        assertEquals("5", viewModel.state.number1)
        assertEquals("", viewModel.state.lastExpression)
    }
}
