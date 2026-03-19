package com.pentadigital.calculator.utils.math

import com.pentadigital.calculator.domain.model.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

object CompoundInterestEngine {

    /**
     * Calculates the compound interest simulation over the specified duration.
     * Uses a month-by-month simulation to correctly apply contributions and compute accrued interest.
     */
    fun calculate(params: CompoundInterestParams): CompoundInterestResult {
        val totalMonths = params.durationYears * 12 + params.durationMonths
        if (totalMonths <= 0) {
            return emptyResult(params.principal)
        }

        val mc = MathContext.DECIMAL128
        val scale = 6 // Internal calculation scale
        val displayScale = 2

        val annualRateDec = params.interestRate.divide(BigDecimal(100), mc)

        // Calculate Effective Monthly Rate (EMR) using Double for fractional exponentiation,
        // then scale it back to BigDecimal for precise iterative multiplication.
        val nDouble = params.compoundingFrequency.periodsPerYear.toDouble()
        val emrDouble = (1.0 + annualRateDec.toDouble() / nDouble).pow(nDouble / 12.0) - 1.0
        val effectiveMonthlyRate = BigDecimal(emrDouble, mc)

        var currentBalance = params.principal
        var totalInterest = BigDecimal.ZERO
        var totalContributions = BigDecimal.ZERO

        var currentContribAmount = params.contributionAmount

        val monthlyBreakdown = mutableListOf<BreakdownRow>()
        val yearlyBreakdown = mutableListOf<BreakdownRow>()

        // Initial state at month 0 (or Year 0)
        yearlyBreakdown.add(
            BreakdownRow(
                periodNumber = 0,
                periodLabel = "Year 0",
                interestEarned = BigDecimal.ZERO,
                totalInterestAccrued = BigDecimal.ZERO,
                balance = currentBalance.setScale(displayScale, RoundingMode.HALF_UP),
                totalContributions = BigDecimal.ZERO
            )
        )

        var yearlyAccruedInterest = BigDecimal.ZERO

        for (month in 1..totalMonths) {
            // 1. Apply Interest
            val interestThisMonth = currentBalance.multiply(effectiveMonthlyRate, mc)
            currentBalance = currentBalance.add(interestThisMonth, mc)
            totalInterest = totalInterest.add(interestThisMonth, mc)
            yearlyAccruedInterest = yearlyAccruedInterest.add(interestThisMonth, mc)

            // 2. Apply Contributions
            var contributionThisMonth = BigDecimal.ZERO
            if (params.contributionType != ContributionType.NONE) {
                val shouldContribute = when (params.contributionFrequency) {
                    ContributionFrequency.MONTHLY -> true
                    ContributionFrequency.ANNUALLY -> month % 12 == 0
                }

                if (shouldContribute) {
                    contributionThisMonth = currentContribAmount
                    if (params.contributionType == ContributionType.WITHDRAWAL) {
                        contributionThisMonth = contributionThisMonth.negate()
                    }

                    currentBalance = currentBalance.add(contributionThisMonth, mc)
                    totalContributions = totalContributions.add(contributionThisMonth, mc)
                }
            }

            // Record monthly
            val roundedBalance = currentBalance.setScale(displayScale, RoundingMode.HALF_UP)
            val roundedInterest = interestThisMonth.setScale(displayScale, RoundingMode.HALF_UP)
            
            monthlyBreakdown.add(
                BreakdownRow(
                    periodNumber = month,
                    periodLabel = "Month $month",
                    interestEarned = roundedInterest,
                    totalInterestAccrued = totalInterest.setScale(displayScale, RoundingMode.HALF_UP),
                    balance = roundedBalance,
                    totalContributions = totalContributions.setScale(displayScale, RoundingMode.HALF_UP)
                )
            )

            // Record yearly
            if (month % 12 == 0) {
                val year = month / 12
                yearlyBreakdown.add(
                    BreakdownRow(
                        periodNumber = year,
                        periodLabel = "Year $year",
                        interestEarned = yearlyAccruedInterest.setScale(displayScale, RoundingMode.HALF_UP),
                        totalInterestAccrued = totalInterest.setScale(displayScale, RoundingMode.HALF_UP),
                        balance = roundedBalance,
                        totalContributions = totalContributions.setScale(displayScale, RoundingMode.HALF_UP)
                    )
                )
                yearlyAccruedInterest = BigDecimal.ZERO

                // 3. Step-up contribution for the next year
                if (params.annualContributionIncreaseRate > BigDecimal.ZERO) {
                    val stepUpRate = params.annualContributionIncreaseRate.divide(BigDecimal(100), mc)
                    val increaseMultiplier = BigDecimal.ONE.add(stepUpRate, mc)
                    currentContribAmount = currentContribAmount.multiply(increaseMultiplier, mc)
                }
            }
        }

        // Handle case where duration is not a perfect multiple of years (e.g., 5 years 6 months)
        if (totalMonths % 12 != 0) {
            val endYearFraction = totalMonths / 12.0
            yearlyBreakdown.add(
                BreakdownRow(
                    periodNumber = totalMonths,
                    periodLabel = "End ($totalMonths mo)",
                    interestEarned = yearlyAccruedInterest.setScale(displayScale, RoundingMode.HALF_UP),
                    totalInterestAccrued = totalInterest.setScale(displayScale, RoundingMode.HALF_UP),
                    balance = currentBalance.setScale(displayScale, RoundingMode.HALF_UP),
                    totalContributions = totalContributions.setScale(displayScale, RoundingMode.HALF_UP)
                )
            )
        }

        // APY computation
        // APY = (1 + r/n)^n - 1
        val apyDouble = (1.0 + annualRateDec.toDouble() / nDouble).pow(nDouble) - 1.0
        val apy = BigDecimal(apyDouble, mc).multiply(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)

        // All Time RoR
        // RoR = (Total Return / Total Invested) * 100
        val totalInvested = params.principal.add(totalContributions, mc)
        val ror = if (totalInvested > BigDecimal.ZERO) {
            totalInterest.divide(totalInvested, mc).multiply(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return CompoundInterestResult(
            futureValue = currentBalance.setScale(displayScale, RoundingMode.HALF_UP),
            totalInterestEarned = totalInterest.setScale(displayScale, RoundingMode.HALF_UP),
            allTimeRateOfReturn = ror,
            apy = apy,
            principal = params.principal.setScale(displayScale, RoundingMode.HALF_UP),
            totalContributions = totalContributions.setScale(displayScale, RoundingMode.HALF_UP),
            yearlyBreakdown = yearlyBreakdown,
            monthlyBreakdown = monthlyBreakdown
        )
    }

    private fun emptyResult(principal: BigDecimal): CompoundInterestResult {
        return CompoundInterestResult(
            futureValue = principal,
            totalInterestEarned = BigDecimal.ZERO,
            allTimeRateOfReturn = BigDecimal.ZERO,
            apy = BigDecimal.ZERO,
            principal = principal,
            totalContributions = BigDecimal.ZERO,
            yearlyBreakdown = emptyList(),
            monthlyBreakdown = emptyList()
        )
    }
}
