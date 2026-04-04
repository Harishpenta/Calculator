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
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.pentadigital.calculator.viewmodels.HomeViewModel

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
    onNavigateToProfile: () -> Unit,
    onNavigateToLifeTimeline: () -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager.getInstance(context) }
    val favorites by favoritesManager.favorites.collectAsState()

    val searchQuery = homeViewModel.searchQuery
    val expandedCategoryId = homeViewModel.expandedCategoryId
    val filteredCalculators = homeViewModel.filteredCalculators
    val groupedCalculators = homeViewModel.groupedCalculators

    // Screen size adaptation
    val windowSize = rememberWindowSize()

    // Adaptive padding
    val horizontalPadding = when {
        windowSize.width == WindowSizeClass.EXPANDED -> 32.dp
        windowSize.width == WindowSizeClass.MEDIUM -> 24.dp
        else -> 20.dp
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Resolve strings via stringResource (outside remember) for proper Compose recomposition
    val catAlgebra = stringResource(R.string.cat_algebra)
    val subAlgebra = stringResource(R.string.sub_algebra)
    val catGeometry = stringResource(R.string.cat_geometry)
    val subGeometry = stringResource(R.string.sub_geometry)
    val catUnitConverters = stringResource(R.string.cat_unit_converters)
    val subUnitConverters = stringResource(R.string.sub_unit_converters)
    val catFinance = stringResource(R.string.cat_finance)
    val subFinance = stringResource(R.string.sub_finance)
    val catHealth = stringResource(R.string.cat_health)
    val subHealth = stringResource(R.string.sub_health)
    val catDatetime = stringResource(R.string.cat_datetime)
    val subDatetime = stringResource(R.string.sub_datetime)

    val categories = remember(primaryColor, catAlgebra, subAlgebra, catGeometry, subGeometry, catUnitConverters, subUnitConverters, catFinance, subFinance, catHealth, subHealth, catDatetime, subDatetime) {
        listOf(
            CalculatorCategory(
                id = "algebra",
                name = catAlgebra,
                subtitle = subAlgebra,
                iconRes = R.drawable.ic_algebra,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "geometry",
                name = catGeometry,
                subtitle = subGeometry,
                iconRes = R.drawable.ic_geometry,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "unit_converters",
                name = catUnitConverters,
                subtitle = subUnitConverters,
                iconRes = R.drawable.ic_unit_converter,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "finance",
                name = catFinance,
                subtitle = subFinance,
                iconRes = R.drawable.ic_finance,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "health",
                name = catHealth,
                subtitle = subHealth,
                iconRes = R.drawable.ic_health,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            ),
            CalculatorCategory(
                id = "datetime",
                name = catDatetime,
                subtitle = subDatetime,
                iconRes = R.drawable.ic_datetime,
                backgroundColor = Color.Transparent,
                iconTint = primaryColor
            )
        )
    }

    // Calculate columns based on window size
    val columns = when (windowSize.width) {
        WindowSizeClass.EXPANDED -> 4
        WindowSizeClass.MEDIUM -> 3
        else -> 2
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp)
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
                    onProfileClick = { onNavigateToProfile() },
                    horizontalPadding = horizontalPadding
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Life Timeline Banner
                LifeTimelineBanner(
                    onClick = onNavigateToLifeTimeline,
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Search Bar
                CalculatorSearchBar(
                    query = searchQuery,
                    onQueryChange = { homeViewModel.onSearchQueryChange(it) },
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
                        items(categories, key = { it.id }) { category ->
                            ExpandableCategoryCard(
                                category = category,
                                calculators = groupedCalculators[category.id] ?: emptyList(),
                                isExpanded = expandedCategoryId == category.id,
                                onToggle = { homeViewModel.onToggleCategory(category.id) },
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
            items(calculators, key = { it.id }) { calculator ->
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
        borderColor = category.iconTint,
        contentPadding = PaddingValues(0.dp) // Reset default padding
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(vertical = 10.dp, horizontal = 12.dp) // Compact internal padding
                    .height(48.dp), // Fixed compact height
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp) // Even smaller icon container
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                category.iconTint.copy(alpha = 0.1f)
                            )
                            .border(1.dp, category.iconTint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.iconRes),
                            contentDescription = category.name,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(category.iconTint)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        TechText(
                            text = category.name.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                        // Removed Subtitle for compactness
                    }
                }
                
                // Animated Arrow
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = category.iconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(rotationZ = rotationState)
                )
            }

            // Content (Calculators Grid - Dynamic Columns)
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 12.dp) // Add internal indentation
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
    // Clean up the name to avoid truncation (e.g., "SIP Calculator" -> "SIP")
    val cleanName = calculator.name
        .replace(" Calculator", "")
        .replace(" Comparator", "")
        .replace(" Converter", "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 4.dp, bottomStart = 4.dp)) // Asymmetrical Cyberpunk cut
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 4.dp, bottomStart = 4.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Icon Hologram Box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = calculator.iconRes),
                    contentDescription = calculator.name,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(accentColor)
                )
            }
            
            // Favorite star icon
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TechText(
            text = cleanName.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun HeaderSection(
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit, 
    horizontalPadding: Dp = 20.dp
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f) 
            ) {
                TechText(
                    text = stringResource(R.string.app_title_pro).uppercase(),
                    fontSize = 24.sp, 
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black, 
                    letterSpacing = 1.sp
                )
            }
            
            // Settings Icon
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(42.dp) 
                    .clip(RoundedCornerShape(12.dp)) // Techier shape
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        GlowingDivider(modifier = Modifier.padding(horizontal = horizontalPadding))
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
                indication = ripple(bounded = true),
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
    // Determine accent color (using primary as default for consistency in search)
    val accentColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)) // Glassy feel
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp), // Compact padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = calculator.iconRes),
                contentDescription = calculator.name,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(accentColor)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            TechText(
                text = calculator.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TechText(
                text = stringResource(getCategoryLabel(calculator.categoryId)),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Favorite star icon
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (isFavorite) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
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

@Composable
private fun LifeTimelineBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp) // Slightly taller for impact
            .clip(RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 4.dp, bottomStart = 4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    colors = listOf(NeonPurple, NeonCyan.copy(alpha = 0.5f))
                ),
                RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 4.dp, bottomStart = 4.dp)
            )
            .clickable(onClick = onClick)
    ) {
        // High-tech static mesh background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Draw subtle horizontal scanlines
            for (i in 0..h.toInt() step 8) {
                drawLine(
                    color = NeonCyan.copy(alpha = 0.05f),
                    start = Offset(0f, i.toFloat()),
                    end = Offset(w, i.toFloat()),
                    strokeWidth = 1f
                )
            }
            // Draw an angled accent shape in the background
            val path = Path().apply {
                moveTo(w * 0.6f, 0f)
                lineTo(w, 0f)
                lineTo(w, h)
                lineTo(w * 0.4f, h)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, NeonPurple.copy(alpha = 0.15f))
                )
            )
        }

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Life Timeline",
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    TechText(
                        text = stringResource(R.string.life_timeline_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    TechText(
                        text = stringResource(R.string.life_timeline_subtitle).uppercase(),
                        fontSize = 10.sp,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                }
            }

            // High-tech Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
