package com.pentadigital.calculator.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pentadigital.calculator.ui.screens.AgeScreen
import com.pentadigital.calculator.ui.screens.BmiScreen
import com.pentadigital.calculator.ui.screens.CalculatorScreen
import com.pentadigital.calculator.ui.screens.CurrencyScreen
import com.pentadigital.calculator.ui.screens.EmiScreen
import com.pentadigital.calculator.ui.screens.FavoritesScreen
import com.pentadigital.calculator.ui.screens.GeometryScreen
import com.pentadigital.calculator.ui.screens.HomeScreen
import com.pentadigital.calculator.ui.screens.SettingsScreen
import com.pentadigital.calculator.ui.screens.SipScreen
import com.pentadigital.calculator.ui.screens.SimpleInterestScreen
import com.pentadigital.calculator.ui.screens.CompoundInterestScreen
import com.pentadigital.calculator.ui.screens.DiscountScreen
import com.pentadigital.calculator.ui.screens.LoanPrepaymentScreen
import com.pentadigital.calculator.ui.screens.GoalPlannerScreen
import com.pentadigital.calculator.ui.screens.TipScreen
import com.pentadigital.calculator.ui.screens.FuelCostScreen
import com.pentadigital.calculator.ui.screens.UnitPriceScreen
import com.pentadigital.calculator.ui.screens.DateDifferenceScreen
import com.pentadigital.calculator.ui.screens.TimeCalculatorScreen
import com.pentadigital.calculator.ui.screens.TDEEScreen
import com.pentadigital.calculator.ui.screens.UnitConverterScreen
import com.pentadigital.calculator.ui.screens.BodyFatScreen
import com.pentadigital.calculator.ui.screens.WaterIntakeScreen
import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.viewmodels.TDEEViewModel
import com.pentadigital.calculator.viewmodels.BodyFatViewModel
import com.pentadigital.calculator.viewmodels.WaterIntakeViewModel

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Favorites : Screen("favorites", "Favorites")
    object Basic : Screen("basic", "Calculator")
    object SIP : Screen("sip", "SIP Calculator")
    object EMI : Screen("emi", "EMI Calculator")
    object BMI : Screen("bmi", "BMI Calculator")
    object Age : Screen("age", "Age Calculator")
    object Currency : Screen("currency", "Currency Converter")
    object UnitConverter : Screen("unit_converter", "Unit Converter")
    object Geometry : Screen("geometry", "Geometry Calculator")
    object SimpleInterest : Screen("simple_interest", "Simple Interest")
    object CompoundInterest : Screen("compound_interest", "Compound Interest")
    object LoanPrepayment : Screen("loan_prepayment", "Loan Prepayment")
    object GoalPlanner : Screen("goal_planner", "Goal Planner")
    object Discount : Screen("discount", "Discount")
    object Tip : Screen("tip", "Tip")
    object FuelCost : Screen("fuel_cost", "Fuel Cost")
    object UnitPrice : Screen("unit_price", "Unit Price")
    object DateDifference : Screen("date_difference", "Date Difference")
    object TimeCalculator : Screen("time_calculator", "Time Calculator")
    object TDEE : Screen("tdee", "TDEE Calculator")
    object BodyFat : Screen("body_fat", "Body Fat Calculator")
    object WaterIntake : Screen("water_intake", "Water Intake Calculator")
    object Settings : Screen("settings", "Settings")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    calculatorViewModel: CalculatorViewModel,
    sipViewModel: SipViewModel,
    emiViewModel: EmiViewModel,
    bmiViewModel: BmiViewModel,
    ageViewModel: AgeViewModel,
    currencyViewModel: CurrencyViewModel,
    unitConverterViewModel: UnitConverterViewModel,
    geometryViewModel: GeometryViewModel,
    simpleInterestViewModel: SimpleInterestViewModel,
    compoundInterestViewModel: CompoundInterestViewModel,
    loanPrepaymentViewModel: LoanPrepaymentViewModel,
    goalPlannerViewModel: GoalPlannerViewModel,
    discountViewModel: DiscountViewModel,
    tipViewModel: TipViewModel,
    fuelCostViewModel: FuelCostViewModel,
    unitPriceViewModel: UnitPriceViewModel,
    dateDifferenceViewModel: DateDifferenceViewModel,
    timeCalculatorViewModel: TimeCalculatorViewModel,
    tdeeViewModel: TDEEViewModel,
    bodyFatViewModel: BodyFatViewModel,
    waterIntakeViewModel: WaterIntakeViewModel,
    themeViewModel: ThemeViewModel,
    onOpenDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCalculator = { route: String ->
                    navController.navigate(route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateToCalculator = { route: String ->
                    navController.navigate(route)
                },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Basic.route) {
            CalculatorScreen(
                state = calculatorViewModel.state,
                onAction = calculatorViewModel::onAction,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.SIP.route) {
            SipScreen(
                state = sipViewModel.state,
                onAction = sipViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.EMI.route) {
            EmiScreen(
                state = emiViewModel.state,
                onAction = emiViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.BMI.route) {
            BmiScreen(
                state = bmiViewModel.state,
                onAction = bmiViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Age.route) {
            AgeScreen(
                state = ageViewModel.state,
                onAction = ageViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Currency.route) {
            CurrencyScreen(
                state = currencyViewModel.state,
                onAction = currencyViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.UnitConverter.route) {
            UnitConverterScreen(
                state = unitConverterViewModel.state,
                onAction = unitConverterViewModel::onEvent,
                availableUnits = unitConverterViewModel.getUnitsForCategory(unitConverterViewModel.state.category),
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Geometry.route) {
            GeometryScreen(
                state = geometryViewModel.state,
                onAction = geometryViewModel::onEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SimpleInterest.route) {
            SimpleInterestScreen(
                state = simpleInterestViewModel.state,
                onAction = simpleInterestViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.CompoundInterest.route) {
            CompoundInterestScreen(
                state = compoundInterestViewModel.state,
                onAction = compoundInterestViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.LoanPrepayment.route) {
            LoanPrepaymentScreen(
                state = loanPrepaymentViewModel.state,
                onAction = loanPrepaymentViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.GoalPlanner.route) {
            GoalPlannerScreen(
                state = goalPlannerViewModel.state,
                onAction = goalPlannerViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Discount.route) {
            DiscountScreen(
                state = discountViewModel.state,
                onAction = discountViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Tip.route) {
            TipScreen(
                state = tipViewModel.state,
                onAction = tipViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.FuelCost.route) {
            FuelCostScreen(
                state = fuelCostViewModel.state,
                onAction = fuelCostViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.UnitPrice.route) {
            UnitPriceScreen(
                state = unitPriceViewModel.state,
                onAction = unitPriceViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.DateDifference.route) {
            DateDifferenceScreen(
                state = dateDifferenceViewModel.state,
                onAction = dateDifferenceViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.TimeCalculator.route) {
            TimeCalculatorScreen(
                state = timeCalculatorViewModel.state,
                onAction = timeCalculatorViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.TDEE.route) {
            TDEEScreen(
                state = tdeeViewModel.state,
                onAction = tdeeViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.BodyFat.route) {
            BodyFatScreen(
                state = bodyFatViewModel.state,
                onAction = bodyFatViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.WaterIntake.route) {
            WaterIntakeScreen(
                state = waterIntakeViewModel.state,
                onAction = waterIntakeViewModel::onEvent,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                state = themeViewModel.state,
                onAction = themeViewModel::onEvent,
                onClearHistory = { calculatorViewModel.onAction(com.pentadigital.calculator.viewmodels.CalculatorAction.ClearHistory) },
                onOpenDrawer = onOpenDrawer
            )
        }
    }
}
