package com.pentadigital.calculator.ui.screens.compound

import com.pentadigital.calculator.domain.model.CompoundingFrequency
import com.pentadigital.calculator.domain.model.CompoundInterestResult
import com.pentadigital.calculator.domain.model.ContributionFrequency
import com.pentadigital.calculator.domain.model.ContributionType
import java.util.Locale

enum class CurrencyInfo(val symbol: String, val code: String, val locale: Locale) {
    INR("₹", "INR", Locale("en", "IN")),
    USD("$", "USD", Locale.US),
    EUR("€", "EUR", Locale.FRANCE),
    GBP("£", "GBP", Locale.UK),
    JPY("¥", "JPY", Locale.JAPAN),
    AUD("A$", "AUD", Locale("en", "AU")),
    CAD("C$", "CAD", Locale("en", "CA"))
}

data class CompoundInterestState(
    val selectedCurrency: CurrencyInfo = CurrencyInfo.INR,
    
    // Core inputs (kept as strings for smooth TextField editing)
    val principalInput: String = "50000",
    val interestRateInput: String = "16",
    val durationYearsInput: String = "10",
    val durationMonthsInput: String = "0",
    val compoundingFrequency: CompoundingFrequency = CompoundingFrequency.MONTHLY,
    
    // Regular contributions
    val contributionType: ContributionType = ContributionType.NONE,
    val contributionAmountInput: String = "",
    val contributionFrequency: ContributionFrequency = ContributionFrequency.MONTHLY,
    val annualContributionIncreaseRateInput: String = "",
    
    // View toggles
    val isYearlyBreakdown: Boolean = true,
    val activeTab: Int = 0, // 0 = Table, 1 = Chart (future), 2 = Summary
    
    // The resulting math calculation
    val result: CompoundInterestResult? = null
)
