package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.viewmodels.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalConfiguration
import com.pentadigital.calculator.utils.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeometryScreen(
    state: GeometryState,
    onAction: (GeometryEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TechText("GEOMETRY", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GeometryTabSelector(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(GeometryEvent.SelectTab(it)) }
            )
            
            when (state.selectedTab) {
                GeometryTab.SHAPES_2D -> Shapes2DContent(state, onAction, isLandscape)
                GeometryTab.SHAPES_3D -> Shapes3DContent(state, onAction, isLandscape)
            }
        }
    }
}

@Composable
private fun GeometryTabSelector(
    selectedTab: GeometryTab,
    onTabSelected: (GeometryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GeometryTab.values().forEach { tab ->
            CyberpunkCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }, // CyberpunkCard consumes click if applied on it, or apply on content? Actually CyberpunkCard doesn't have onClick. Use modifier.
                borderColor = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                backgroundColor = if (selectedTab == tab) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TechText(
                        text = tab.name.replace("_", " "),
                        color = if (selectedTab == tab) MaterialTheme.colorScheme.primary else CyberpunkTextSecondary,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun Shapes2DContent(
    state: GeometryState,
    onAction: (GeometryEvent) -> Unit,
    isLandscape: Boolean
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            // Left Panel: Shape Selector
            LazyColumn(modifier = Modifier.weight(0.2f).fillMaxHeight().padding(start = 16.dp)) {
                items(Shape2D.values()) { shape ->
                    Shape2DItem(shape, state.selected2DShape == shape) { onAction(GeometryEvent.Select2DShape(shape)) }
                }
            }
            
            // Middle Panel: Visualizer
            Box(
                modifier = Modifier.weight(0.4f).fillMaxHeight().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ShapeVisual(state = state)
            }
            
            // Right Panel: Inputs and Results
            LazyColumn(modifier = Modifier.weight(0.4f).fillMaxHeight().padding(end = 16.dp)) {
                item { InputsSection2D(state, onAction) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { ResultsCard2D(state) }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shape Selector
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(Shape2D.values()) { shape ->
                            FilterChip(
                                selected = state.selected2DShape == shape,
                                onClick = { onAction(GeometryEvent.Select2DShape(shape)) },
                                label = { TechText(shape.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = state.selected2DShape == shape
                                )
                            )
                        }
                    }
                }
                
                // Visualizer
                item {
                    ShapeVisual(state = state)
                }
                
                // Inputs
                item { InputsSection2D(state, onAction) }
                
                // Results
                item { ResultsCard2D(state) }
            }
        }
    }
}

@Composable
private fun Shape2DItem(shape: Shape2D, isSelected: Boolean, onClick: () -> Unit) {
    CyberpunkCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    ) {
        TechText(
            text = shape.displayName,
            color = if (isSelected) MaterialTheme.colorScheme.primary else CyberpunkTextSecondary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun InputsSection2D(state: GeometryState, onAction: (GeometryEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (state.selected2DShape) {
            Shape2D.CIRCLE -> {
                CyberpunkInput(
                    value = state.radius.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateRadius(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Radius"
                )
            }
            Shape2D.SQUARE -> {
                CyberpunkInput(
                    value = state.side.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSide(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side"
                )
            }
            Shape2D.RECTANGLE -> {
                CyberpunkInput(
                    value = state.length.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateLength(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Length"
                )
                CyberpunkInput(
                    value = state.width.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateWidth(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Width"
                )
            }
            Shape2D.TRIANGLE -> {
                CyberpunkInput(
                    value = state.sideA.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSideA(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side A"
                )
                CyberpunkInput(
                    value = state.sideB.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSideB(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side B"
                )
                CyberpunkInput(
                    value = state.sideC.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSideC(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side C"
                )
            }
            Shape2D.TRAPEZOID -> {
                CyberpunkInput(
                    value = state.topBase.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateTopBase(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Top Base"
                )
                CyberpunkInput(
                    value = state.bottomBase.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateBottomBase(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Bottom Base"
                )
                CyberpunkInput(
                    value = state.height.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Height"
                )
            }
            Shape2D.PARALLELOGRAM -> {
                CyberpunkInput(
                    value = state.base.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateBase(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Base"
                )
                CyberpunkInput(
                    value = state.height.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Height"
                )
                CyberpunkInput(
                    value = state.side.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSide(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side (for Perimeter)"
                )
            }
            Shape2D.ELLIPSE -> {
                CyberpunkInput(
                    value = state.semiAxisA.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSemiAxisA(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Semi-Axis A"
                )
                CyberpunkInput(
                    value = state.semiAxisB.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSemiAxisB(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Semi-Axis B"
                )
            }
            Shape2D.RHOMBUS -> {
                CyberpunkInput(
                    value = state.diagonal1.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateDiagonal1(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Diagonal 1"
                )
                CyberpunkInput(
                    value = state.diagonal2.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateDiagonal2(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Diagonal 2"
                )
            }
        }
    }
}

@Composable
private fun ResultsCard2D(state: GeometryState) {
    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.secondary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TechText(
                text = "RESULTS",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TechText("Area:", color = CyberpunkTextSecondary)
                TechText(
                    text = String.format("%.2f", state.area2D),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TechText("Perimeter:", color = CyberpunkTextSecondary)
                TechText(
                    text = String.format("%.2f", state.perimeter2D),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun Shapes3DContent(
    state: GeometryState,
    onAction: (GeometryEvent) -> Unit,
    isLandscape: Boolean
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            // Left Panel: Shape Selector
            LazyColumn(modifier = Modifier.weight(0.2f).fillMaxHeight().padding(start = 16.dp)) {
                items(Shape3D.values()) { shape ->
                    Shape3DItem(shape, state.selected3DShape == shape) { onAction(GeometryEvent.Select3DShape(shape)) }
                }
            }
            
            // Middle Panel: Visualizer
            Box(
                modifier = Modifier.weight(0.4f).fillMaxHeight().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Shape3DViewer(
                        shape = state.selected3DShape,
                        rotationX = state.rotationX,
                        rotationY = state.rotationY,
                        rotationZ = state.rotationZ,
                        isAutoRotating = state.isAutoRotating,
                        onRotationChange = { x, y -> 
                            onAction(GeometryEvent.UpdateRotationX(x))
                            onAction(GeometryEvent.UpdateRotationY(y))
                        },
                        side = state.side3D.toFloat(),
                        radius = state.radius3D.toFloat(),
                        height = state.height3D.toFloat(),
                        length = state.length3D.toFloat(),
                        width = state.width3D.toFloat(),
                        depth = state.depth3D.toFloat(),
                        majorRadius = state.majorRadius.toFloat(),
                        minorRadius = state.minorRadius.toFloat()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CyberpunkButton(
                            text = if (state.isAutoRotating) "PAUSE" else "ROTATE",
                            onClick = { onAction(GeometryEvent.ToggleAutoRotate) },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary
                        )
                        CyberpunkButton(
                            text = "RESET",
                            onClick = { onAction(GeometryEvent.ResetRotation) },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            
            // Right Panel: Inputs and Results
            LazyColumn(modifier = Modifier.weight(0.4f).fillMaxHeight().padding(end = 16.dp)) {
                item { InputsSection3D(state, onAction) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { ResultsCard3D(state) }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shape Selector
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(Shape3D.values()) { shape ->
                            FilterChip(
                                selected = state.selected3DShape == shape,
                                onClick = { onAction(GeometryEvent.Select3DShape(shape)) },
                                label = { TechText(shape.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = state.selected3DShape == shape
                                )
                            )
                        }
                    }
                }
                
                // Visualizer
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Shape3DViewer(
                            shape = state.selected3DShape,
                            rotationX = state.rotationX,
                            rotationY = state.rotationY,
                            rotationZ = state.rotationZ,
                            isAutoRotating = state.isAutoRotating,
                            onRotationChange = { x, y -> 
                                onAction(GeometryEvent.UpdateRotationX(x))
                                onAction(GeometryEvent.UpdateRotationY(y))
                            },
                             side = state.side3D.toFloat(),
                            radius = state.radius3D.toFloat(),
                            height = state.height3D.toFloat(),
                            length = state.length3D.toFloat(),
                            width = state.width3D.toFloat(),
                            depth = state.depth3D.toFloat(),
                            majorRadius = state.majorRadius.toFloat(),
                            minorRadius = state.minorRadius.toFloat()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CyberpunkButton(
                                text = if (state.isAutoRotating) "PAUSE" else "ROTATE",
                                onClick = { onAction(GeometryEvent.ToggleAutoRotate) },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary
                            )
                            CyberpunkButton(
                                text = "RESET",
                                onClick = { onAction(GeometryEvent.ResetRotation) },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
                
                // Inputs
                item { InputsSection3D(state, onAction) }
                
                // Results
                item { ResultsCard3D(state) }
            }
        }
    }
}

@Composable
private fun Shape3DItem(shape: Shape3D, isSelected: Boolean, onClick: () -> Unit) {
    CyberpunkCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    ) {
        TechText(
            text = shape.displayName,
            color = if (isSelected) MaterialTheme.colorScheme.primary else CyberpunkTextSecondary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun InputsSection3D(state: GeometryState, onAction: (GeometryEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (state.selected3DShape) {
            Shape3D.CUBE -> {
                CyberpunkInput(
                    value = state.side3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSide3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Side"
                )
            }
            Shape3D.SPHERE -> {
                CyberpunkInput(
                    value = state.radius3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateRadius3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Radius"
                )
            }
            Shape3D.CYLINDER -> {
                CyberpunkInput(
                    value = state.radius3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateRadius3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Radius"
                )
                CyberpunkInput(
                    value = state.height3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Height"
                )
            }
            Shape3D.CONE -> {
                CyberpunkInput(
                    value = state.baseRadius.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateBaseRadius(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Base Radius"
                )
                CyberpunkInput(
                    value = state.height3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Height"
                )
            }
            Shape3D.PYRAMID -> {
                CyberpunkInput(
                    value = state.side3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSide3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Base Side"
                )
                CyberpunkInput(
                    value = state.height3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Height"
                )
            }
            Shape3D.RECTANGULAR_PRISM -> {
                CyberpunkInput(
                    value = state.length3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateLength3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Length"
                )
                CyberpunkInput(
                    value = state.width3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateWidth3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Width"
                )
                CyberpunkInput(
                    value = state.depth3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateDepth3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Depth"
                )
            }
            Shape3D.TRIANGULAR_PRISM -> {
                CyberpunkInput(
                    value = state.side3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateSide3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Base Triangle Side"
                )
                CyberpunkInput(
                    value = state.height3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateHeight3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Prism Height"
                )
            }
            Shape3D.TORUS -> {
                CyberpunkInput(
                    value = state.majorRadius.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateMajorRadius(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Major Radius (R)"
                )
                CyberpunkInput(
                    value = state.minorRadius.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateMinorRadius(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Minor Radius (r)"
                )
            }
            Shape3D.HEMISPHERE -> {
                CyberpunkInput(
                    value = state.radius3D.toString(),
                    onValueChange = { onAction(GeometryEvent.UpdateRadius3D(it.toDoubleOrNull() ?: 0.0)) },
                    label = "Radius"
                )
            }
        }
    }
}

@Composable
private fun ResultsCard3D(state: GeometryState) {
    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.secondary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TechText(
                text = "RESULTS",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TechText("Volume:", color = CyberpunkTextSecondary)
                TechText(
                    text = String.format("%.2f", state.volume),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TechText("Surface Area:", color = CyberpunkTextSecondary)
                TechText(
                    text = String.format("%.2f", state.surfaceArea),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
