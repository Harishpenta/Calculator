package com.pentadigital.calculator.viewmodels

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val history: List<String> = emptyList(),
    val memory: String = "",
    val errorMessage: String? = null,
    val isResultDisplayed: Boolean = false
)

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("/")
    object Percent : CalculatorOperation("%")
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    object ClearHistory : CalculatorAction()
    object Negate : CalculatorAction()
    object SquareRoot : CalculatorAction()
    object Square : CalculatorAction()
    object Percent : CalculatorAction()
    object MemoryClear : CalculatorAction()
    object MemoryRecall : CalculatorAction()
    object MemoryAdd : CalculatorAction()
    object MemorySubtract : CalculatorAction()
}
