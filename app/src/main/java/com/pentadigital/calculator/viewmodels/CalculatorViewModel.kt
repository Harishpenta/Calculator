package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt
import kotlin.math.pow

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.pentadigital.calculator.data.HistoryRepository
import com.pentadigital.calculator.data.HistoryEntity

class CalculatorViewModel(private val repository: HistoryRepository) : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    private var calculationCount = 0
    var onShowInterstitialAd: (() -> Unit)? = null
    private val MAX_DIGITS = 16

    init {
        viewModelScope.launch {
            repository.allHistory.collect { historyList ->
                // Map HistoryEntity to String format expected by UI
                val formattedHistory = historyList.map { 
                    "${it.expression} = ${it.result}" 
                }
                state = state.copy(history = formattedHistory)
            }
        }
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState(memory = state.memory)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.ClearHistory -> clearHistory()
            is CalculatorAction.Negate -> negate()
            is CalculatorAction.SquareRoot -> squareRoot()
            is CalculatorAction.Square -> square()
            is CalculatorAction.Percent -> percent()
            is CalculatorAction.MemoryClear -> memoryClear()
            is CalculatorAction.MemoryRecall -> memoryRecall()
            is CalculatorAction.MemoryAdd -> memoryAdd()
            is CalculatorAction.MemorySubtract -> memorySubtract()
        }
    }

    private fun performDeletion() {
        when {
            state.number2.isNotBlank() -> state = state.copy(
                number2 = state.number2.dropLast(1)
            )
            state.operation != null -> state = state.copy(
                operation = null
            )
            state.number1.isNotBlank() -> state = state.copy(
                number1 = state.number1.dropLast(1)
            )
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toDoubleOrNull()
        val number2 = state.number2.toDoubleOrNull()
        if (number1 != null && number2 != null && state.operation != null) {
            try {
                val result = when (state.operation) {
                    is CalculatorOperation.Add -> number1 + number2
                    is CalculatorOperation.Subtract -> number1 - number2
                    is CalculatorOperation.Multiply -> number1 * number2
                    is CalculatorOperation.Divide -> {
                        if (number2 == 0.0) {
                            state = state.copy(errorMessage = "Cannot divide by zero")
                            return
                        }
                        number1 / number2
                    }
                    is CalculatorOperation.Percent -> (number1 * number2) / 100.0
                    null -> return
                }
                
                if (result.isInfinite() || result.isNaN()) {
                    state = state.copy(errorMessage = "Invalid operation")
                    return
                }
                
                val expression = "${formatDisplayNumber(state.number1)} ${state.operation?.symbol} ${formatDisplayNumber(state.number2)}"
                val resultString = formatDisplayNumber(result.toString())
                
                // Save to DB
                viewModelScope.launch {
                    repository.insert(HistoryEntity(expression = expression, result = resultString))
                }
                
                state = state.copy(
                    number1 = formatResult(result),
                    number2 = "",
                    operation = null,
                    // history update handled by flow collector
                    errorMessage = null,
                    isResultDisplayed = true
                )

                // Increment calculation count and show ad after 4-5 calculations
                calculationCount++
                if (calculationCount >= 4) {
                    calculationCount = 0
                    onShowInterstitialAd?.invoke()
                }
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Error: ${e.message}")
            }
        }
    }
    
    private fun formatResult(result: Double): String {
        return when {
            result == result.toLong().toDouble() -> result.toLong().toString()
            result.toString().length > MAX_DIGITS -> {
                val scientific = String.format("%.${MAX_DIGITS - 7}e", result)
                scientific.replace("e", "E")
            }
            else -> result.toString().take(MAX_DIGITS)
        }
    }
    
    private fun formatDisplayNumber(number: String): String {
        val double = number.toDoubleOrNull() ?: return number
        return if (double == double.toLong().toDouble()) {
            double.toLong().toString()
        } else {
            number
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (state.number1.isNotBlank()) {
            // If there's a pending operation and number2, calculate first
            if (state.operation != null && state.number2.isNotBlank()) {
                performCalculation()
            }
            state = state.copy(
                operation = operation,
                isResultDisplayed = false,
                errorMessage = null
            )
        }
    }

    private fun enterDecimal() {
        if (state.isResultDisplayed && state.operation == null) {
            state = state.copy(
                number1 = "0.",
                isResultDisplayed = false,
                errorMessage = null
            )
            return
        }
        
        if (state.operation == null && !state.number1.contains(".") && state.number1.isNotBlank()) {
            state = state.copy(
                number1 = state.number1 + ".",
                errorMessage = null
            )
            return
        }
        if (state.operation == null && state.number1.isBlank()) {
            state = state.copy(
                number1 = "0.",
                errorMessage = null
            )
            return
        }
        if (!state.number2.contains(".") && state.number2.isNotBlank()) {
            state = state.copy(
                number2 = state.number2 + ".",
                errorMessage = null
            )
        } else if (state.number2.isBlank() && state.operation != null) {
            state = state.copy(
                number2 = "0.",
                errorMessage = null
            )
        }
    }

    private fun enterNumber(number: Int) {
        // If result was just displayed, start fresh calculation
        if (state.isResultDisplayed && state.operation == null) {
            state = state.copy(
                number1 = number.toString(),
                isResultDisplayed = false,
                errorMessage = null
            )
            return
        }
        
        if (state.operation == null) {
            if (state.number1.length >= MAX_DIGITS) {
                return
            }
            state = state.copy(
                number1 = state.number1 + number,
                errorMessage = null
            )
            return
        }
        if (state.number2.length >= MAX_DIGITS) {
            return
        }
        state = state.copy(
            number2 = state.number2 + number,
            errorMessage = null
        )
    }
    
    private fun negate() {
        if (state.operation == null && state.number1.isNotBlank()) {
            val number = state.number1.toDoubleOrNull() ?: return
            state = state.copy(
                number1 = (-number).toString()
            )
        } else if (state.number2.isNotBlank()) {
            val number = state.number2.toDoubleOrNull() ?: return
            state = state.copy(
                number2 = (-number).toString()
            )
        }
    }
    
    private fun squareRoot() {
        val currentNumber = if (state.operation == null) state.number1 else state.number2
        val number = currentNumber.toDoubleOrNull() ?: return
        
        if (number < 0) {
            state = state.copy(errorMessage = "Cannot calculate square root of negative number")
            return
        }
        
        val result = sqrt(number)
        val resultString = formatResult(result)
        
        if (state.operation == null) {
            val expression = "√${formatDisplayNumber(currentNumber)}"
            val resultFormatted = formatDisplayNumber(resultString)
            
            // Save to DB
            viewModelScope.launch {
                repository.insert(HistoryEntity(expression = expression, result = resultFormatted))
            }

            state = state.copy(
                number1 = resultString,
                errorMessage = null
            )
        } else {
            state = state.copy(
                number2 = resultString,
                errorMessage = null
            )
        }
    }
    
    private fun square() {
        val currentNumber = if (state.operation == null) state.number1 else state.number2
        val number = currentNumber.toDoubleOrNull() ?: return
        
        val result = number.pow(2.0)
        val resultString = formatResult(result)
        
        if (state.operation == null) {
            val expression = "${formatDisplayNumber(currentNumber)}²"
            val resultFormatted = formatDisplayNumber(resultString)
            
            // Save to DB
            viewModelScope.launch {
                repository.insert(HistoryEntity(expression = expression, result = resultFormatted))
            }

            state = state.copy(
                number1 = resultString,
                errorMessage = null
            )
        } else {
            state = state.copy(
                number2 = resultString,
                errorMessage = null
            )
        }
    }
    
    private fun percent() {
        if (state.operation != null && state.number1.isNotBlank() && state.number2.isNotBlank()) {
            // Calculate percentage of first number
            val number1 = state.number1.toDoubleOrNull() ?: return
            val number2 = state.number2.toDoubleOrNull() ?: return
            val percentValue = (number1 * number2) / 100.0
            state = state.copy(
                number2 = formatResult(percentValue)
            )
        } else if (state.operation == null && state.number1.isNotBlank()) {
            // Convert current number to percentage (divide by 100)
            val number = state.number1.toDoubleOrNull() ?: return
            val result = number / 100.0
            state = state.copy(
                number1 = formatResult(result)
            )
        }
    }
    
    private fun memoryClear() {
        state = state.copy(memory = "")
    }
    
    private fun memoryRecall() {
        if (state.memory.isNotBlank()) {
            if (state.operation == null) {
                state = state.copy(number1 = state.memory)
            } else {
                state = state.copy(number2 = state.memory)
            }
        }
    }
    
    private fun memoryAdd() {
        val currentNumber = if (state.operation == null) state.number1 else state.number2
        val number = currentNumber.toDoubleOrNull() ?: return
        val memoryValue = state.memory.toDoubleOrNull() ?: 0.0
        state = state.copy(memory = formatResult(memoryValue + number))
    }
    
    private fun memorySubtract() {
        val currentNumber = if (state.operation == null) state.number1 else state.number2
        val number = currentNumber.toDoubleOrNull() ?: return
        val memoryValue = state.memory.toDoubleOrNull() ?: 0.0
        state = state.copy(memory = formatResult(memoryValue - number))
    }
}
