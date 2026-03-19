package com.pentadigital.calculator.domain.model

import java.math.BigDecimal

/** Frequency of interest compounding */
enum class CompoundingFrequency(val periodsPerYear: Int) {
    ANNUALLY(1),
    SEMI_ANNUALLY(2),
    QUARTERLY(4),
    MONTHLY(12),
    DAILY(365)
}

/** Frequency of regular contributions */
enum class ContributionFrequency(val periodsPerYear: Int) {
    MONTHLY(12),
    ANNUALLY(1)
}

enum class ContributionType {
    DEPOSIT,
    WITHDRAWAL,
    NONE
}

data class CompoundInterestParams(
    val principal: BigDecimal = BigDecimal.ZERO,
    val interestRate: BigDecimal = BigDecimal.ZERO, // Annual rate in percentage (e.g., 5.0 for 5%)
    val compoundingFrequency: CompoundingFrequency = CompoundingFrequency.MONTHLY,
    val durationYears: Int = 0,
    val durationMonths: Int = 0,
    
    // Regular contributions
    val contributionType: ContributionType = ContributionType.NONE,
    val contributionAmount: BigDecimal = BigDecimal.ZERO,
    val contributionFrequency: ContributionFrequency = ContributionFrequency.MONTHLY,
    val annualContributionIncreaseRate: BigDecimal = BigDecimal.ZERO // e.g., 2.0 for 2%
)

data class BreakdownRow(
    val periodNumber: Int,    // Month or Year number
    val periodLabel: String,  // e.g., "Year 1", "Month 12"
    val interestEarned: BigDecimal,
    val totalInterestAccrued: BigDecimal,
    val balance: BigDecimal,
    val totalContributions: BigDecimal
)

data class CompoundInterestResult(
    val futureValue: BigDecimal,
    val totalInterestEarned: BigDecimal,
    val allTimeRateOfReturn: BigDecimal,
    val apy: BigDecimal, // Annual Percentage Yield (Effective Rate)
    val principal: BigDecimal,
    val totalContributions: BigDecimal,
    val yearlyBreakdown: List<BreakdownRow>,
    val monthlyBreakdown: List<BreakdownRow>
)
