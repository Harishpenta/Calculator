package com.pentadigital.calculator.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.screens.CalculatorItem

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var searchQuery by mutableStateOf("")
        private set

    var expandedCategoryId by mutableStateOf<String?>(null)
        private set

    val allCalculators: List<CalculatorItem> by lazy {
        val ctx = getApplication<Application>()
        listOf(
            // Algebra
            CalculatorItem("percentage", ctx.getString(R.string.calc_percentage), "algebra", R.drawable.ic_percentage, "basic"),
            CalculatorItem("average", ctx.getString(R.string.calc_average), "algebra", R.drawable.ic_average, "basic"),
            CalculatorItem("proportion", ctx.getString(R.string.calc_proportion), "algebra", R.drawable.ic_proportion, "basic"),
            CalculatorItem("ratio", ctx.getString(R.string.calc_ratio), "algebra", R.drawable.ic_ratio, "basic"),
            // Geometry
            CalculatorItem("geometry", ctx.getString(R.string.geometry_title), "geometry", R.drawable.ic_geometry, "geometry"),
            // Finance
            CalculatorItem("sip", ctx.getString(R.string.sip_title), "finance", R.drawable.ic_investment, "sip"),
            CalculatorItem("emi", ctx.getString(R.string.emi_title), "finance", R.drawable.ic_loan, "emi"),
            CalculatorItem("simple_interest", ctx.getString(R.string.simple_interest_title), "finance", R.drawable.ic_percentage, "simple_interest"),
            CalculatorItem("compound_interest", ctx.getString(R.string.compound_interest_title), "finance", R.drawable.ic_investment, "compound_interest"),
            CalculatorItem("loan_prepayment", ctx.getString(R.string.loan_prepayment_title), "finance", R.drawable.ic_loan, "loan_prepayment"),
            CalculatorItem("goal_planner", ctx.getString(R.string.goal_planner_title), "finance", R.drawable.ic_investment, "goal_planner"),
            CalculatorItem("discount", ctx.getString(R.string.discount_calculator_title), "finance", R.drawable.ic_percentage, "discount"),
            CalculatorItem("tip", ctx.getString(R.string.tip_calculator_title), "finance", R.drawable.ic_currency, "tip"),
            CalculatorItem("fuel_cost", ctx.getString(R.string.fuel_cost_calculator_title), "finance", R.drawable.ic_currency, "fuel_cost"),
            CalculatorItem("unit_price", ctx.getString(R.string.unit_price_comparator_title), "finance", R.drawable.ic_currency, "unit_price"),
            CalculatorItem("currency", ctx.getString(R.string.currency_title), "finance", R.drawable.ic_currency, "currency"),
            // Health
            CalculatorItem("bmi", ctx.getString(R.string.bmi_title), "health", R.drawable.ic_bmi, "bmi"),
            CalculatorItem("tdee", ctx.getString(R.string.tdee_title), "health", R.drawable.ic_tdee, "tdee"),
            CalculatorItem("body_fat", ctx.getString(R.string.body_fat_title), "health", R.drawable.ic_body_fat, "body_fat"),
            CalculatorItem("water_intake", ctx.getString(R.string.water_intake_title), "health", R.drawable.ic_water_intake, "water_intake"),
            // Date & Time
            CalculatorItem("age", ctx.getString(R.string.age_title), "datetime", R.drawable.ic_age, "age"),
            CalculatorItem("date_difference", ctx.getString(R.string.date_difference_title), "datetime", R.drawable.ic_date_difference, "date_difference"),
            CalculatorItem("time_calculator", ctx.getString(R.string.time_calculator_title), "datetime", R.drawable.ic_time_calculator, "time_calculator"),
            // Unit Converters
            CalculatorItem("unit", ctx.getString(R.string.unit_converter_title), "unit_converters", R.drawable.ic_unit_converter, "unit_converter")
        )
    }

    val groupedCalculators: Map<String, List<CalculatorItem>> by lazy {
        allCalculators.groupBy { it.categoryId }
    }

    val filteredCalculators: List<CalculatorItem>
        get() {
            if (searchQuery.isEmpty()) return allCalculators
            val query = searchQuery.lowercase()
            return allCalculators.filter { it.name.lowercase().contains(query) }
        }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun onToggleCategory(categoryId: String) {
        expandedCategoryId = if (expandedCategoryId == categoryId) null else categoryId
    }
}
