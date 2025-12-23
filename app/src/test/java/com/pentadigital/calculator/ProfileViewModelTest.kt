package com.pentadigital.calculator

import com.pentadigital.calculator.data.HistoryDao
import com.pentadigital.calculator.data.HistoryEntity
import com.pentadigital.calculator.data.HistoryRepository
import com.pentadigital.calculator.viewmodels.ProfileViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val fakeDao = object : HistoryDao {
        override fun getAllHistory(): Flow<List<HistoryEntity>> = flowOf(
            listOf(
                HistoryEntity(expression = "1+1", result = "2"),
                HistoryEntity(expression = "2*2", result = "4")
            )
        )
        override suspend fun insertHistory(history: HistoryEntity) {}
        override suspend fun clearHistory() {}
    }
    
    private lateinit var viewModel: ProfileViewModel
    private val repository = HistoryRepository(fakeDao)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calculation count reflects history size`() = runTest(testDispatcher) {
        // Wait for flow collection
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertEquals(2, viewModel.calculationCount.value)
    }
}
