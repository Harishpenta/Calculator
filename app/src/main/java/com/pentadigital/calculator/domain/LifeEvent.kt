package com.pentadigital.calculator.domain

enum class EventType {
    FINANCE_POSITIVE, // e.g., Goal Reached, Debt Free
    FINANCE_NEGATIVE, // e.g., Loan Start
    HEALTH_POSITIVE, // e.g., Prime Health
    HEALTH_WARNING, // e.g., Hitting 60s
    LIFE_MILESTONE // e.g., Retirement
}

data class LifeEvent(
    val title: String,
    val age: Int,
    val type: EventType,
    val description: String,
    val amount: Double? = null,
    val color: androidx.compose.ui.graphics.Color,
    val titleRes: Int? = null,
    val descRes: Int? = null,
    val descArgs: List<Any> = emptyList() // For formatting arguments
)

data class TimelineState(
    val currentAge: Int = 30, // Default, will come from Age Calculator
    val lifeExpectancy: Int = 85, // Default/Actuarial
    val monthlySavings: Double = 10000.0, // Simulation Slider
    val events: List<LifeEvent> = emptyList(),
    val financialFreedomAge: Int? = null,
    val debtFreeAge: Int? = null,
    val goalAchievedAge: Int? = null,
    val primeHealthEndAge: Int = 60
)
