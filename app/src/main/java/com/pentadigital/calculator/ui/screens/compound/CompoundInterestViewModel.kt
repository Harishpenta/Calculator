package com.pentadigital.calculator.ui.screens.compound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pentadigital.calculator.domain.model.CompoundInterestParams
import com.pentadigital.calculator.utils.math.CompoundInterestEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CompoundInterestViewModel : ViewModel() {

    private val _state = MutableStateFlow(CompoundInterestState())
    val state: StateFlow<CompoundInterestState> = _state.asStateFlow()

    init {
        // Run initial calculation with default state
        calculateCompoundInterest()
    }

    fun onEvent(event: CompoundInterestEvent) {
        when (event) {
            is CompoundInterestEvent.UpdatePrincipal -> {
                _state.update { it.copy(principalInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateInterestRate -> {
                _state.update { it.copy(interestRateInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateDurationYears -> {
                _state.update { it.copy(durationYearsInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateDurationMonths -> {
                _state.update { it.copy(durationMonthsInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateCompoundingFrequency -> {
                _state.update { it.copy(compoundingFrequency = event.freq) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateContributionType -> {
                _state.update { it.copy(contributionType = event.type) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateContributionAmount -> {
                _state.update { it.copy(contributionAmountInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateContributionFrequency -> {
                _state.update { it.copy(contributionFrequency = event.freq) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateAnnualIncreaseRate -> {
                _state.update { it.copy(annualContributionIncreaseRateInput = event.value) }
                calculateCompoundInterest()
            }
            is CompoundInterestEvent.UpdateCurrency -> {
                _state.update { it.copy(selectedCurrency = event.currency) }
            }
            is CompoundInterestEvent.ToggleBreakdownView -> {
                _state.update { it.copy(isYearlyBreakdown = event.isYearly) }
            }
            is CompoundInterestEvent.UpdateActiveTab -> {
                _state.update { it.copy(activeTab = event.tabIndex) }
            }
            CompoundInterestEvent.Calculate -> {
                calculateCompoundInterest()
            }
        }
    }

    private fun calculateCompoundInterest() {
        val currentState = _state.value
        
        viewModelScope.launch(Dispatchers.Default) {
            val params = CompoundInterestParams(
                principal = currentState.principalInput.toBigDecimalSafe(),
                interestRate = currentState.interestRateInput.toBigDecimalSafe(),
                compoundingFrequency = currentState.compoundingFrequency,
                durationYears = currentState.durationYearsInput.toIntSafe(),
                durationMonths = currentState.durationMonthsInput.toIntSafe(),
                contributionType = currentState.contributionType,
                contributionAmount = currentState.contributionAmountInput.toBigDecimalSafe(),
                contributionFrequency = currentState.contributionFrequency,
                annualContributionIncreaseRate = currentState.annualContributionIncreaseRateInput.toBigDecimalSafe()
            )

            val result = CompoundInterestEngine.calculate(params)

            _state.update { it.copy(result = result) }
        }
    }

    private fun String.toBigDecimalSafe(): BigDecimal {
        return try {
            if (this.isBlank()) BigDecimal.ZERO else BigDecimal(this)
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
    }

    private fun String.toIntSafe(): Int {
        return try {
            if (this.isBlank()) 0 else this.toInt()
        } catch (e: NumberFormatException) {
            // Could be a float typed in integer field, parse as double then int, or just 0
            try {
                this.toDouble().toInt()
            } catch (ex: NumberFormatException) {
                0
            }
        }
    }
}
