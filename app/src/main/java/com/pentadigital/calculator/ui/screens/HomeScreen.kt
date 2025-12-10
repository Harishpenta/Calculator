package com.pentadigital.calculator.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.data.FavoritesManager
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.utils.WindowSizeClass
import com.pentadigital.calculator.utils.rememberWindowSize

import com.pentadigital.calculator.ui.components.CyberpunkCard
import com.pentadigital.calculator.ui.components.TechText
import com.pentadigital.calculator.ui.components.GlowingDivider
import com.pentadigital.calculator.ui.theme.CyberpunkDarkBG
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonPurple
import com.pentadigital.calculator.ui.theme.NeonGreen
import com.pentadigital.calculator.ui.theme.CyberpunkTextPrimary
import com.pentadigital.calculator.ui.theme.CyberpunkTextSecondary

// Data classes for categories and calculators
data class CalculatorCategory(
    val id: String,
    val name: String,
    val subtitle: String,
    @DrawableRes val iconRes: Int,
    val backgroundColor: Color,
    val iconTint: Color
)

data class CalculatorItem(
    val id: String,
    val name: String,
    val categoryId: String,
    @DrawableRes val iconRes: Int,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCalculator: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager.getInstance(context) }
    val favorites by favoritesManager.favorites.collectAsState()
    
    // Screen size adaptation
    val windowSize = rememberWindowSize()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    
    // Adaptive padding
    val horizontalPadding = when {
        windowSize.width == WindowSizeClass.EXPANDED -> 32.dp
        windowSize.width == WindowSizeClass.MEDIUM -> 24.dp
        else -> 20.dp
    }
    
    var searchQuery by remember { mutableStateOf("") }
    // Track expanded category - default to Finance or null
    var expandedCategoryId by remember { mutableStateOf<String?>(null) }
    
    // Define categories
    // Detect theme based on background color (since MaterialTheme is already set by CalculatorTheme)
    val currentBg = MaterialTheme.colorScheme.background
    val isDarkTheme = remember(currentBg) { currentBg == CyberpunkDarkBG }

    val primaryColor = MaterialTheme.colorScheme.primary
    val categories = remember(primaryColor) {
        listOf(
            CalculatorCategory(
                id = "algebra",
                name = context.getString(R.string.cat_algebra),
                subtitle = context.getString(R.string.sub_algebra),
                iconRes = R.drawable.ic_algebra,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "geometry",
                name = context.getString(R.string.cat_geometry),
                subtitle = context.getString(R.string.sub_geometry),
                iconRes = R.drawable.ic_geometry,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "unit_converters",
                name = context.getString(R.string.cat_unit_converters),
                subtitle = context.getString(R.string.sub_unit_converters),
                iconRes = R.drawable.ic_unit_converter,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "finance",
                name = context.getString(R.string.cat_finance),
                subtitle = context.getString(R.string.sub_finance),
                iconRes = R.drawable.ic_finance,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "health",
                name = context.getString(R.string.cat_health),
                subtitle = context.getString(R.string.sub_health),
                iconRes = R.drawable.ic_health,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "datetime",
                name = context.getString(R.string.cat_datetime),
                subtitle = context.getString(R.string.sub_datetime),
                iconRes = R.drawable.ic_datetime,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            )
        )
    }

    
    // Define all calculators
    val allCalculators = remember {
        listOf(
            // Algebra
            CalculatorItem("percentage", context.getString(R.string.calc_percentage), "algebra", R.drawable.ic_percentage, "basic"),
            CalculatorItem("average", context.getString(R.string.calc_average), "algebra", R.drawable.ic_average, "basic"),
            CalculatorItem("proportion", context.getString(R.string.calc_proportion), "algebra", R.drawable.ic_proportion, "basic"),
            CalculatorItem("ratio", context.getString(R.string.calc_ratio), "algebra", R.drawable.ic_ratio, "basic"),
            // Geometry
            CalculatorItem("geometry", context.getString(R.string.geometry_title), "geometry", R.drawable.ic_geometry, "geometry"),
            // Finance
            CalculatorItem("sip", context.getString(R.string.sip_title), "finance", R.drawable.ic_investment, "sip"),
            CalculatorItem("emi", context.getString(R.string.emi_title), "finance", R.drawable.ic_loan, "emi"),
            CalculatorItem("simple_interest", context.getString(R.string.simple_interest_title), "finance", R.drawable.ic_percentage, "simple_interest"),
            CalculatorItem("compound_interest", context.getString(R.string.compound_interest_title), "finance", R.drawable.ic_investment, "compound_interest"),
            CalculatorItem("loan_prepayment", context.getString(R.string.loan_prepayment_title), "finance", R.drawable.ic_loan, "loan_prepayment"),
            CalculatorItem("goal_planner", context.getString(R.string.goal_planner_title), "finance", R.drawable.ic_investment, "goal_planner"),
            CalculatorItem("discount", context.getString(R.string.discount_calculator_title), "finance", R.drawable.ic_percentage, "discount"),
            CalculatorItem("tip", context.getString(R.string.tip_calculator_title), "finance", R.drawable.ic_currency, "tip"),
            CalculatorItem("fuel_cost", context.getString(R.string.fuel_cost_calculator_title), "finance", R.drawable.ic_currency, "fuel_cost"),
            CalculatorItem("unit_price", context.getString(R.string.unit_price_comparator_title), "finance", R.drawable.ic_currency, "unit_price"),
            CalculatorItem("currency", context.getString(R.string.currency_title), "finance", R.drawable.ic_currency, "currency"),
            // Health
            CalculatorItem("bmi", context.getString(R.string.bmi_title), "health", R.drawable.ic_bmi, "bmi"),
            CalculatorItem("tdee", context.getString(R.string.tdee_title), "health", R.drawable.ic_tdee, "tdee"),
            CalculatorItem("body_fat", context.getString(R.string.body_fat_title), "health", R.drawable.ic_body_fat, "body_fat"),
            CalculatorItem("water_intake", context.getString(R.string.water_intake_title), "health", R.drawable.ic_water_intake, "water_intake"),
            // Date & Time
            CalculatorItem("age", context.getString(R.string.age_title), "datetime", R.drawable.ic_age, "age"),
            CalculatorItem("date_difference", context.getString(R.string.date_difference_title), "datetime", R.drawable.ic_date_difference, "date_difference"),
            CalculatorItem("time_calculator", context.getString(R.string.time_calculator_title), "datetime", R.drawable.ic_time_calculator, "time_calculator"),
            // Unit Converters
            CalculatorItem("unit", context.getString(R.string.unit_converter_title), "unit_converters", R.drawable.ic_unit_converter, "unit_converter")
        )
    }
    
    // Filter calculators based on search
    val filteredCalculators = remember(searchQuery) {
        if (searchQuery.isEmpty()) allCalculators else {
            allCalculators.filter { calc ->
                calc.name.lowercase().contains(searchQuery.lowercase())
            }
        }
    }
    
    // Group calculators by category
    val groupedCalculators = remember(allCalculators) {
        allCalculators.groupBy { it.categoryId }
    }

    // Calculate columns based on window size
    val columns = when (windowSize.width) {
        WindowSizeClass.EXPANDED -> 4
        WindowSizeClass.MEDIUM -> 3
        else -> 2
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        // Center content on large screens
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = if (windowSize.width == WindowSizeClass.EXPANDED) 1200.dp else 1920.dp)
                    .fillMaxWidth()
            ) {
                // Header Section
                HeaderSection(
                    onSettingsClick = onNavigateToSettings,
                    horizontalPadding = horizontalPadding
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Bar
                CalculatorSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                )
            
                Spacer(modifier = Modifier.height(24.dp))
            
                // Content
                if (searchQuery.isNotEmpty()) {
                    // Search Results List
                    SearchResultsList(
                        calculators = filteredCalculators,
                        favorites = favorites,
                        onCalculatorClick = onNavigateToCalculator,
                        onFavoriteClick = { favoritesManager.toggleFavorite(it) },
                        horizontalPadding = horizontalPadding
                    )
                } else {
                    // Accordion List - Pass dynamic columns
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categories) { category ->
                            ExpandableCategoryCard(
                                category = category,
                                calculators = groupedCalculators[category.id] ?: emptyList(),
                                isExpanded = expandedCategoryId == category.id,
                                onToggle = {
                                    expandedCategoryId = if (expandedCategoryId == category.id) null else category.id
                                },
                                favorites = favorites,
                                onCalculatorClick = onNavigateToCalculator,
                                onFavoriteClick = { favoritesManager.toggleFavorite(it) },
                                horizontalPadding = horizontalPadding,
                                columns = columns
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    calculators: List<CalculatorItem>,
    favorites: Set<String>,
    onCalculatorClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    horizontalPadding: Dp
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_results),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${calculators.size} ${stringResource(R.string.found)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (calculators.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_calculators_found),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        } else {
            items(calculators) { calculator ->
                Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
                    CalculatorListItem(
                        calculator = calculator,
                        isFavorite = favorites.contains(calculator.id),
                        onClick = { onCalculatorClick(calculator.route) },
                        onFavoriteClick = { onFavoriteClick(calculator.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableCategoryCard(
    category: CalculatorCategory,
    calculators: List<CalculatorItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    favorites: Set<String>,
    onCalculatorClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    horizontalPadding: Dp,
    columns: Int = 2
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    CyberpunkCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .animateContentSize(),
        borderColor = category.iconTint
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                category.iconTint.copy(alpha = 0.2f)
                            )
                            .border(1.dp, category.iconTint.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.iconRes),
                            contentDescription = category.name,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(category.iconTint)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        TechText(
                            text = category.name.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TechText(
                            text = category.subtitle,
                            fontSize = 10.sp,
                            color = category.iconTint.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .graphicsLayer(rotationZ = rotationState),
                    tint = category.iconTint
                )
            }

            // Content (Calculators Grid - Dynamic Columns)
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    GlowingDivider(color = category.iconTint.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Create rows of items
                    val chunkedCalculators = calculators.chunked(columns)
                    
                    chunkedCalculators.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { calculator ->
                                Box(modifier = Modifier.weight(1f)) {
                                    CompactCalculatorGridItem(
                                        calculator = calculator,
                                        isFavorite = favorites.contains(calculator.id),
                                        onClick = { onCalculatorClick(calculator.route) },
                                        onFavoriteClick = { onFavoriteClick(calculator.id) },
                                        accentColor = category.iconTint
                                    )
                                }
                            }
                            // If row has only 1 item, add spacer to fill the gap
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCalculatorGridItem(
    calculator: CalculatorItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = calculator.iconRes),
                contentDescription = calculator.name,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(accentColor)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        TechText(
            text = calculator.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Favorite star icon (smaller)
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HeaderSection(
    onSettingsClick: () -> Unit,
    horizontalPadding: Dp = 20.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar (Cyberpunk Style)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                TechText(
                    text = stringResource(R.string.welcome).uppercase(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                TechText(
                    text = stringResource(R.string.app_title_pro).uppercase(),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Settings Icon
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CalculatorSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary),
        placeholder = {
            TechText(
                text = stringResource(R.string.search_hint),
                color = CyberpunkTextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_hint),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = true
    )
}

@Composable
private fun CategoriesSection(
    categories: List<CalculatorCategory>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit,
    horizontalPadding: Dp = 20.dp,
    columns: Int = 3
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.categories),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Categories Grid - Adaptive columns based on screen size
        val gridHeight = when {
            columns >= 6 -> 240.dp // 1 row for 6 columns
            columns >= 5 -> 260.dp // 2 rows for 5 columns (if 6 items)
            columns >= 4 -> 320.dp // 2 rows for 4 columns
            else -> 340.dp // 2 rows for 3 columns
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            contentPadding = PaddingValues(
                horizontal = (horizontalPadding.value - 4).dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    isSelected = selectedCategory == category.id,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CalculatorCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 2.dp
        ),
        border = if (isSelected) {
            BorderStroke(3.dp, category.iconTint)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) category.iconTint.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = category.name,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) category.iconTint else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = category.subtitle,
                fontSize = 10.sp,
                color = if (isSelected) category.iconTint.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CalculatorCategorySection(
    categoryName: String,
    calculators: List<CalculatorItem>,
    favorites: Set<String>,
    onCalculatorClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    horizontalPadding: Dp = 20.dp
) {
    Column(
        modifier = Modifier.padding(horizontal = horizontalPadding)
    ) {
        Text(
            text = categoryName,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        calculators.forEach { calculator ->
            CalculatorListItem(
                calculator = calculator,
                isFavorite = favorites.contains(calculator.id),
                onClick = { onCalculatorClick(calculator.route) },
                onFavoriteClick = { onFavoriteClick(calculator.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CalculatorListItem(
    calculator: CalculatorItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = calculator.iconRes),
                    contentDescription = calculator.name,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = calculator.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(getCategoryLabel(calculator.categoryId)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Favorite star icon
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isFavorite) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getCategoryLabel(categoryId: String): Int {
    return when (categoryId) {
        "algebra" -> R.string.cat_algebra
        "geometry" -> R.string.cat_geometry
        "unit_converters" -> R.string.cat_unit_converter_title
        "finance" -> R.string.cat_finance
        "health" -> R.string.cat_health
        "datetime" -> R.string.cat_date_time_title
        else -> R.string.calculator_title
    }
}
