package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.data.FavoritesManager
import com.pentadigital.calculator.ui.components.CyberpunkCard
import com.pentadigital.calculator.ui.components.TechText
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.utils.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToCalculator: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager.getInstance(context) }
    val favorites by favoritesManager.favorites.collectAsState()
    val isLandscape = isLandscape()

    // Map of calculator IDs to their details
    val calculatorDetails = mapOf(
        "sip" to Pair(R.string.sip_title, R.drawable.ic_investment),
        "emi" to Pair(R.string.emi_title, R.drawable.ic_loan),
        "simple_interest" to Pair(R.string.simple_interest_title, R.drawable.ic_percentage),
        "compound_interest" to Pair(R.string.compound_interest_title, R.drawable.ic_investment),
        "loan_prepayment" to Pair(R.string.loan_prepayment_title, R.drawable.ic_loan),
        "goal_planner" to Pair(R.string.goal_planner_title, R.drawable.ic_investment),
        "discount" to Pair(R.string.discount_calculator_title, R.drawable.ic_percentage),
        "tip" to Pair(R.string.tip_calculator_title, R.drawable.ic_currency),
        "fuel_cost" to Pair(R.string.fuel_cost_calculator_title, R.drawable.ic_currency),
        "unit_price" to Pair(R.string.unit_price_comparator_title, R.drawable.ic_currency),
        "currency" to Pair(R.string.currency_title, R.drawable.ic_currency),
        "bmi" to Pair(R.string.bmi_title, R.drawable.ic_bmi),
        "tdee" to Pair(R.string.tdee_title, R.drawable.ic_tdee),
        "body_fat" to Pair(R.string.body_fat_title, R.drawable.ic_body_fat),
        "water_intake" to Pair(R.string.water_intake_title, R.drawable.ic_water_intake),
        "age" to Pair(R.string.age_title, R.drawable.ic_age),
        "date_difference" to Pair(R.string.date_difference_title, R.drawable.ic_date_difference),
        "time_calculator" to Pair(R.string.time_calculator_title, R.drawable.ic_time_calculator),
        "unit" to Pair(R.string.unit_converter_title, R.drawable.ic_unit_converter),
        "percentage" to Pair(R.string.calc_percentage, R.drawable.ic_percentage),
        "average" to Pair(R.string.calc_average, R.drawable.ic_average),
        "proportion" to Pair(R.string.calc_proportion, R.drawable.ic_proportion),
        "ratio" to Pair(R.string.calc_ratio, R.drawable.ic_ratio),
        "geometry" to Pair(R.string.geometry_title, R.drawable.ic_geometry)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.favorites).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TechText(
                        text = stringResource(R.string.no_favorites),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                if (isLandscape) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favorites.toList()) { calculatorId ->
                            val details = calculatorDetails[calculatorId]
                            if (details != null) {
                                FavoriteGridItem(
                                    name = stringResource(details.first),
                                    iconRes = details.second,
                                    onClick = {
                                        val route = when(calculatorId) {
                                            "unit" -> "unit_converter"
                                            "percentage", "average", "proportion", "ratio" -> "basic"
                                            else -> calculatorId
                                        }
                                        onNavigateToCalculator(route) 
                                    }
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(favorites.toList()) { calculatorId ->
                            val details = calculatorDetails[calculatorId]
                            if (details != null) {
                                FavoriteListItem(
                                    name = stringResource(details.first),
                                    iconRes = details.second,
                                    onClick = { 
                                        val route = when(calculatorId) {
                                            "unit" -> "unit_converter"
                                            "percentage", "average", "proportion", "ratio" -> "basic"
                                            else -> calculatorId
                                        }
                                        onNavigateToCalculator(route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteListItem(
    name: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp), // Compact padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = name,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        TechText(
            text = name.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FavoriteGridItem(
    name: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    CyberpunkCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = name,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TechText(
                text = name.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
