package com.pentadigital.calculator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pentadigital.calculator.ui.theme.CalculatorTheme
import com.pentadigital.calculator.viewmodels.TimeCalculatorViewModel
import com.pentadigital.calculator.viewmodels.TDEEViewModel
import com.pentadigital.calculator.viewmodels.BodyFatViewModel
import com.pentadigital.calculator.viewmodels.WaterIntakeViewModel
import com.pentadigital.calculator.viewmodels.TipViewModel
import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.ui.navigation.AppNavigation
import com.pentadigital.calculator.ui.navigation.Screen
import com.pentadigital.calculator.utils.InterstitialAdManager

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = androidx.navigation.compose.rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Home.route

            // Handle Widget Navigation
            androidx.compose.runtime.LaunchedEffect(Unit) {
                intent.getStringExtra("route")?.let { route ->
                    navController.navigate(route)
                }
            }
            
            // Initialize Ads
            androidx.compose.runtime.LaunchedEffect(Unit) {
                com.google.android.gms.ads.MobileAds.initialize(this@MainActivity) {}
            }
            val interstitialAdManager = androidx.compose.runtime.remember { InterstitialAdManager(this@MainActivity) }
            androidx.compose.runtime.LaunchedEffect(Unit) {
                interstitialAdManager.loadAd()
            }
            
            // Database & Repository
            val database = com.pentadigital.calculator.data.AppDatabase.getDatabase(applicationContext)
            val repository = com.pentadigital.calculator.data.HistoryRepository(database.historyDao())
            
            val calculatorViewModel = viewModel<CalculatorViewModel>(
                factory = CalculatorViewModelFactory(repository)
            )
            
            // Set up interstitial ad callback for calculations
            androidx.compose.runtime.LaunchedEffect(Unit) {
                calculatorViewModel.onShowInterstitialAd = {
                    interstitialAdManager.showAd(this@MainActivity) {}
                }
            }
            val sipViewModel = viewModel<SipViewModel>()
            val emiViewModel = viewModel<EmiViewModel>()
            val bmiViewModel = viewModel<BmiViewModel>()
            val ageViewModel = viewModel<AgeViewModel>()
            val currencyViewModel = viewModel<CurrencyViewModel>()
            val unitConverterViewModel = viewModel<UnitConverterViewModel>()
            val geometryViewModel = viewModel<GeometryViewModel>()
            val simpleInterestViewModel = viewModel<SimpleInterestViewModel>()
            val compoundInterestViewModel = viewModel<CompoundInterestViewModel>()
            val loanPrepaymentViewModel = viewModel<LoanPrepaymentViewModel>()
            val goalPlannerViewModel = viewModel<GoalPlannerViewModel>()
            val discountViewModel = viewModel<DiscountViewModel>()
            val tipViewModel = viewModel<TipViewModel>()
            val fuelCostViewModel = viewModel<FuelCostViewModel>()
            val unitPriceViewModel = viewModel<UnitPriceViewModel>()
            val dateDifferenceViewModel: DateDifferenceViewModel = viewModel()
        val timeCalculatorViewModel: TimeCalculatorViewModel = viewModel()
        val tdeeViewModel: TDEEViewModel = viewModel()
        val bodyFatViewModel: BodyFatViewModel = viewModel()
        val waterIntakeViewModel: WaterIntakeViewModel = viewModel()
        val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(applicationContext))

            // Determine if we should show bottom navigation
            val showBottomNav = currentRoute in listOf(
                Screen.Home.route, 
                Screen.Favorites.route
            )
            // Apply Language
            val currentLanguage = themeViewModel.state.language
            var localeKey by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(0) }

            androidx.compose.runtime.LaunchedEffect(currentLanguage) {
                val parts = currentLanguage.code.split("-")
                val locale = if (parts.size > 1) java.util.Locale(parts[0], parts[1]) else java.util.Locale(parts[0])
                val config = resources.configuration
                
                // Check if update is needed to avoid loops
                val currentLocale = config.locales[0]
                if (currentLocale.language != locale.language || currentLocale.country != locale.country) {
                    java.util.Locale.setDefault(locale)
                    config.setLocale(locale)
                    @Suppress("DEPRECATION")
                    resources.updateConfiguration(config, resources.displayMetrics)
                    localeKey++
                }
            }
            androidx.compose.runtime.key(localeKey) {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalHapticsEnabled provides themeViewModel.state.isHapticsEnabled,
                    LocalSoundEnabled provides themeViewModel.state.isSoundEnabled,
                    LocalHapticManager provides themeViewModel.hapticManager,
                    LocalSoundManager provides themeViewModel.soundManager
                ) {
                    CalculatorTheme(themeState = themeViewModel.state) {
val configuration = LocalConfiguration.current
                        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                        val onNavigateToRoute: (String) -> Unit = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                        val mainContent: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit = { paddingValues ->
                            Column(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            top = paddingValues.calculateTopPadding(),
                                            start = paddingValues.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                                            end = paddingValues.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
                                            // Bottom padding is handled by the AdMobBanner/Column or NavigationBar
                                        )
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background
                                    ) {
                                        AppNavigation(
                                            navController = navController,
                                            calculatorViewModel = calculatorViewModel,
                                            sipViewModel = sipViewModel,
                                            emiViewModel = emiViewModel,
                                            bmiViewModel = bmiViewModel,
                                            ageViewModel = ageViewModel,
                                            currencyViewModel = currencyViewModel,
                                            unitConverterViewModel = unitConverterViewModel,
                                            geometryViewModel = geometryViewModel,
                                            simpleInterestViewModel = simpleInterestViewModel,
                                            compoundInterestViewModel = compoundInterestViewModel,
                                            loanPrepaymentViewModel = loanPrepaymentViewModel,
                                            goalPlannerViewModel = goalPlannerViewModel,
                                            discountViewModel = discountViewModel,
                                            tipViewModel = tipViewModel,
                                            fuelCostViewModel = fuelCostViewModel,
                                            unitPriceViewModel = unitPriceViewModel,
                                            dateDifferenceViewModel = dateDifferenceViewModel,
                                            timeCalculatorViewModel = timeCalculatorViewModel,
                                            tdeeViewModel = tdeeViewModel,
                                            bodyFatViewModel = bodyFatViewModel,
                                            waterIntakeViewModel = waterIntakeViewModel,
                                            themeViewModel = themeViewModel,
                                            onOpenDrawer = {
                                                navController.navigate(Screen.Home.route) {
                                                    popUpTo(Screen.Home.route) { inclusive = true }
                                                }
                                            }
                                        )
                                    }
                                }
                                if (!showBottomNav) {
                                    AdMobBanner(modifier = Modifier.navigationBarsPadding())
                                }
                            }
                        }

                        if (isLandscape && showBottomNav) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                AppNavigationRail(
                                    currentRoute = currentRoute,
                                    onNavigate = onNavigateToRoute,
                                    onCalculatorClick = {
                                        navController.navigate(Screen.Basic.route)
                                    }
                                )
                                Scaffold(
                                    modifier = Modifier.weight(1f),
                                    containerColor = MaterialTheme.colorScheme.background
                                ) { paddingValues ->
                                    mainContent(paddingValues)
                                }
                            }
                        } else {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = MaterialTheme.colorScheme.background,
                                bottomBar = {
                                    if (showBottomNav) {
                                        BottomNavigationBar(
                                            currentRoute = currentRoute,
                                            onNavigate = onNavigateToRoute,
                                            onCalculatorClick = {
                                                navController.navigate(Screen.Basic.route)
                                            }
                                        )
                                    }
                                }
                            ) { paddingValues ->
                                mainContent(paddingValues)
                            }
                        }
                    }
                }
            }
    }
    }
}
