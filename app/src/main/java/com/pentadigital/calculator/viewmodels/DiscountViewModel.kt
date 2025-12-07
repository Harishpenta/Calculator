package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class DiscountState(
    val originalPrice: Double = 0.0,
    val discountPercentage: Double = 0.0,
    val taxPercentage: Double = 0.0,
    val finalPrice: Double = 0.0,
    val amountSaved: Double = 0.0,
    val taxAmount: Double = 0.0
)

sealed class DiscountEvent {
    data class UpdateOriginalPrice(val price: Double) : DiscountEvent()
    data class UpdateDiscount(val discount: Double) : DiscountEvent()
    data class UpdateTax(val tax: Double) : DiscountEvent()
    object Reset : DiscountEvent()
}

class DiscountViewModel : ViewModel() {
    var state by mutableStateOf(DiscountState())
        private set

    fun onEvent(event: DiscountEvent) {
        when (event) {
            is DiscountEvent.UpdateOriginalPrice -> {
                state = state.copy(originalPrice = event.price)
                calculateDiscount()
            }
            is DiscountEvent.UpdateDiscount -> {
                state = state.copy(discountPercentage = event.discount)
                calculateDiscount()
            }
            is DiscountEvent.UpdateTax -> {
                state = state.copy(taxPercentage = event.tax)
                calculateDiscount()
            }
            is DiscountEvent.Reset -> {
                state = DiscountState()
            }
        }
    }

    private fun calculateDiscount() {
        val price = state.originalPrice
        val discount = state.discountPercentage
        val tax = state.taxPercentage

        if (price > 0) {
            val discountAmount = price * (discount / 100)
            val priceAfterDiscount = price - discountAmount
            val taxAmount = priceAfterDiscount * (tax / 100)
            val finalPrice = priceAfterDiscount + taxAmount

            state = state.copy(
                finalPrice = finalPrice,
                amountSaved = discountAmount,
                taxAmount = taxAmount
            )
        } else {
            state = state.copy(
                finalPrice = 0.0,
                amountSaved = 0.0,
                taxAmount = 0.0
            )
        }
    }
}
