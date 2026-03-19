package com.pentadigital.calculator.ui.screens.compound

import com.pentadigital.calculator.domain.model.CompoundingFrequency
import com.pentadigital.calculator.domain.model.ContributionFrequency
import com.pentadigital.calculator.domain.model.ContributionType

sealed class CompoundInterestEvent {
    data class UpdatePrincipal(val value: String) : CompoundInterestEvent()
    data class UpdateInterestRate(val value: String) : CompoundInterestEvent()
    data class UpdateDurationYears(val value: String) : CompoundInterestEvent()
    data class UpdateDurationMonths(val value: String) : CompoundInterestEvent()
    data class UpdateCompoundingFrequency(val freq: CompoundingFrequency) : CompoundInterestEvent()
    
    data class UpdateContributionType(val type: ContributionType) : CompoundInterestEvent()
    data class UpdateContributionAmount(val value: String) : CompoundInterestEvent()
    data class UpdateContributionFrequency(val freq: ContributionFrequency) : CompoundInterestEvent()
    data class UpdateAnnualIncreaseRate(val value: String) : CompoundInterestEvent()
    
    data class UpdateCurrency(val currency: CurrencyInfo) : CompoundInterestEvent()
    data class ToggleBreakdownView(val isYearly: Boolean) : CompoundInterestEvent()
    data class UpdateActiveTab(val tabIndex: Int) : CompoundInterestEvent()
    
    object Calculate : CompoundInterestEvent()
}
