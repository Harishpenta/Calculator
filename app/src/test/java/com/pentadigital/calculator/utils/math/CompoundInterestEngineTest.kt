package com.pentadigital.calculator.utils.math

import com.pentadigital.calculator.domain.model.CompoundingFrequency
import com.pentadigital.calculator.domain.model.CompoundInterestParams
import com.pentadigital.calculator.domain.model.ContributionFrequency
import com.pentadigital.calculator.domain.model.ContributionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CompoundInterestEngineTest {

    @Test
    fun `test basic compounding matches screenshot values`() {
        val params = CompoundInterestParams(
            principal = BigDecimal("50000"),
            interestRate = BigDecimal("16"),
            compoundingFrequency = CompoundingFrequency.MONTHLY,
            durationYears = 10,
            durationMonths = 0,
            contributionType = ContributionType.NONE
        )

        val result = CompoundInterestEngine.calculate(params)

        // Verifying exactly against reference image screenshot
        assertEquals("245047.05", result.futureValue.toPlainString())
        assertEquals("195047.05", result.totalInterestEarned.toPlainString())
        assertEquals("17.23", result.apy.toPlainString()) // Compounded Rate
        assertEquals("390.09", result.allTimeRateOfReturn.toPlainString())
        
        // Check yearly breakdown row 10
        val row10 = result.yearlyBreakdown.find { it.periodNumber == 10 }
        requireNotNull(row10)
        assertEquals("195047.05", row10.totalInterestAccrued.toPlainString())
        assertEquals("245047.05", row10.balance.toPlainString())
    }

    @Test
    fun `test monthly contributions`() {
        val params = CompoundInterestParams(
            principal = BigDecimal("10000"),
            interestRate = BigDecimal("5"),
            compoundingFrequency = CompoundingFrequency.MONTHLY,
            durationYears = 5,
            durationMonths = 0,
            contributionType = ContributionType.DEPOSIT,
            contributionAmount = BigDecimal("100"),
            contributionFrequency = ContributionFrequency.MONTHLY
        )

        val result = CompoundInterestEngine.calculate(params)

        // Total deposits: 5 * 12 * 100 = 6000
        assertEquals("6000.00", result.totalContributions.toPlainString())
        // FV should be higher than 16,000 due to interest
        assert(result.futureValue > BigDecimal("16000.00"))
        
        // Year 5 total contributions
        val row5 = result.yearlyBreakdown.find { it.periodNumber == 5 }
        requireNotNull(row5)
        assertEquals("6000.00", row5.totalContributions.toPlainString())
    }
}
