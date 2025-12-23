package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.pow

// Enum for 2D Shapes
enum class Shape2D(val displayName: String) {
    CIRCLE("Circle"),
    SQUARE("Square"),
    RECTANGLE("Rectangle"),
    TRIANGLE("Triangle"),
    TRAPEZOID("Trapezoid"),
    PARALLELOGRAM("Parallelogram"),
    ELLIPSE("Ellipse"),
    RHOMBUS("Rhombus")
}

// Enum for 3D Shapes
enum class Shape3D(val displayName: String) {
    CUBE("Cube"),
    SPHERE("Sphere"),
    CYLINDER("Cylinder"),
    CONE("Cone"),
    PYRAMID("Pyramid"),
    RECTANGULAR_PRISM("Rectangular Prism"),
    TRIANGULAR_PRISM("Triangular Prism"),
    TORUS("Torus"),
    HEMISPHERE("Hemisphere")
}

// Tab selection
enum class GeometryTab {
    SHAPES_2D,
    SHAPES_3D
}

// State for Geometry Calculator
data class GeometryState(
    val selectedTab: GeometryTab = GeometryTab.SHAPES_2D,
    
    // 2D Shape
    val selected2DShape: Shape2D = Shape2D.CIRCLE,
    val radius: String = "5.0",
    val side: String = "5.0",
    val length: String = "8.0",
    val width: String = "5.0",
    val base: String = "6.0",
    val height: String = "4.0",
    val sideA: String = "3.0",
    val sideB: String = "4.0",
    val sideC: String = "5.0",
    val topBase: String = "4.0",
    val bottomBase: String = "6.0",
    val diagonal1: String = "6.0",
    val diagonal2: String = "4.0",
    val semiAxisA: String = "5.0",
    val semiAxisB: String = "3.0",
    
    // 2D Results
    val area2D: Double = 0.0,
    val perimeter2D: Double = 0.0,
    
    // 3D Shape
    val selected3DShape: Shape3D = Shape3D.CUBE,
    val radius3D: String = "3.0",
    val height3D: String = "5.0",
    val side3D: String = "4.0",
    val length3D: String = "6.0",
    val width3D: String = "4.0",
    val depth3D: String = "3.0",
    val baseRadius: String = "3.0",
    val majorRadius: String = "4.0",
    val minorRadius: String = "1.5",
    val slantHeight: String = "5.0",
    
    // 3D Results
    val volume: Double = 0.0,
    val surfaceArea: Double = 0.0,
    
    // 3D Visualization
    val rotationX: Float = 25f,
    val rotationY: Float = 45f,
    val rotationZ: Float = 0f,
    val isAutoRotating: Boolean = true
)

// Events for Geometry
sealed class GeometryEvent {
    data class SelectTab(val tab: GeometryTab) : GeometryEvent()
    
    // 2D Shape events
    data class Select2DShape(val shape: Shape2D) : GeometryEvent()
    data class UpdateRadius(val value: String) : GeometryEvent()
    data class UpdateSide(val value: String) : GeometryEvent()
    data class UpdateLength(val value: String) : GeometryEvent()
    data class UpdateWidth(val value: String) : GeometryEvent()
    data class UpdateBase(val value: String) : GeometryEvent()
    data class UpdateHeight(val value: String) : GeometryEvent()
    data class UpdateSideA(val value: String) : GeometryEvent()
    data class UpdateSideB(val value: String) : GeometryEvent()
    data class UpdateSideC(val value: String) : GeometryEvent()
    data class UpdateTopBase(val value: String) : GeometryEvent()
    data class UpdateBottomBase(val value: String) : GeometryEvent()
    data class UpdateDiagonal1(val value: String) : GeometryEvent()
    data class UpdateDiagonal2(val value: String) : GeometryEvent()
    data class UpdateSemiAxisA(val value: String) : GeometryEvent()
    data class UpdateSemiAxisB(val value: String) : GeometryEvent()
    
    // 3D Shape events
    data class Select3DShape(val shape: Shape3D) : GeometryEvent()
    data class UpdateRadius3D(val value: String) : GeometryEvent()
    data class UpdateHeight3D(val value: String) : GeometryEvent()
    data class UpdateSide3D(val value: String) : GeometryEvent()
    data class UpdateLength3D(val value: String) : GeometryEvent()
    data class UpdateWidth3D(val value: String) : GeometryEvent()
    data class UpdateDepth3D(val value: String) : GeometryEvent()
    data class UpdateBaseRadius(val value: String) : GeometryEvent()
    data class UpdateMajorRadius(val value: String) : GeometryEvent()
    data class UpdateMinorRadius(val value: String) : GeometryEvent()
    data class UpdateSlantHeight(val value: String) : GeometryEvent()
    
    // Rotation events
    data class UpdateRotationX(val value: Float) : GeometryEvent()
    data class UpdateRotationY(val value: Float) : GeometryEvent()
    data class UpdateRotationZ(val value: Float) : GeometryEvent()
    object ToggleAutoRotate : GeometryEvent()
    object ResetRotation : GeometryEvent()
}

class GeometryViewModel : ViewModel() {
    var state by mutableStateOf(GeometryState())
        private set
    
    init {
        calculate()
    }
    
    fun onEvent(event: GeometryEvent) {
        when (event) {
            is GeometryEvent.SelectTab -> {
                state = state.copy(selectedTab = event.tab)
            }
            // 2D Shape events
            is GeometryEvent.Select2DShape -> {
                state = state.copy(selected2DShape = event.shape)
                calculate()
            }
            is GeometryEvent.UpdateRadius -> {
                state = state.copy(radius = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSide -> {
                state = state.copy(side = event.value)
                calculate()
            }
            is GeometryEvent.UpdateLength -> {
                state = state.copy(length = event.value)
                calculate()
            }
            is GeometryEvent.UpdateWidth -> {
                state = state.copy(width = event.value)
                calculate()
            }
            is GeometryEvent.UpdateBase -> {
                state = state.copy(base = event.value)
                calculate()
            }
            is GeometryEvent.UpdateHeight -> {
                state = state.copy(height = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSideA -> {
                state = state.copy(sideA = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSideB -> {
                state = state.copy(sideB = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSideC -> {
                state = state.copy(sideC = event.value)
                calculate()
            }
            is GeometryEvent.UpdateTopBase -> {
                state = state.copy(topBase = event.value)
                calculate()
            }
            is GeometryEvent.UpdateBottomBase -> {
                state = state.copy(bottomBase = event.value)
                calculate()
            }
            is GeometryEvent.UpdateDiagonal1 -> {
                state = state.copy(diagonal1 = event.value)
                calculate()
            }
            is GeometryEvent.UpdateDiagonal2 -> {
                state = state.copy(diagonal2 = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSemiAxisA -> {
                state = state.copy(semiAxisA = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSemiAxisB -> {
                state = state.copy(semiAxisB = event.value)
                calculate()
            }
            // 3D Shape events
            is GeometryEvent.Select3DShape -> {
                state = state.copy(selected3DShape = event.shape)
                calculate()
            }
            is GeometryEvent.UpdateRadius3D -> {
                state = state.copy(radius3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateHeight3D -> {
                state = state.copy(height3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSide3D -> {
                state = state.copy(side3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateLength3D -> {
                state = state.copy(length3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateWidth3D -> {
                state = state.copy(width3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateDepth3D -> {
                state = state.copy(depth3D = event.value)
                calculate()
            }
            is GeometryEvent.UpdateBaseRadius -> {
                state = state.copy(baseRadius = event.value)
                calculate()
            }
            is GeometryEvent.UpdateMajorRadius -> {
                state = state.copy(majorRadius = event.value)
                calculate()
            }
            is GeometryEvent.UpdateMinorRadius -> {
                state = state.copy(minorRadius = event.value)
                calculate()
            }
            is GeometryEvent.UpdateSlantHeight -> {
                state = state.copy(slantHeight = event.value)
                calculate()
            }
            // Rotation events
            is GeometryEvent.UpdateRotationX -> {
                state = state.copy(rotationX = event.value)
            }
            is GeometryEvent.UpdateRotationY -> {
                state = state.copy(rotationY = event.value)
            }
            is GeometryEvent.UpdateRotationZ -> {
                state = state.copy(rotationZ = event.value)
            }
            is GeometryEvent.ToggleAutoRotate -> {
                state = state.copy(isAutoRotating = !state.isAutoRotating)
            }
            is GeometryEvent.ResetRotation -> {
                state = state.copy(rotationX = 25f, rotationY = 45f, rotationZ = 0f)
            }
        }
    }
    
    private fun calculate() {
        calculate2D()
        calculate3D()
    }
    
    private fun calculate2D() {
        // Parse inputs
        val r = state.radius.toDoubleOrNull() ?: 0.0
        val side = state.side.toDoubleOrNull() ?: 0.0
        val len = state.length.toDoubleOrNull() ?: 0.0
        val wid = state.width.toDoubleOrNull() ?: 0.0
        val bs = state.base.toDoubleOrNull() ?: 0.0
        val h = state.height.toDoubleOrNull() ?: 0.0
        val sA = state.sideA.toDoubleOrNull() ?: 0.0
        val sB = state.sideB.toDoubleOrNull() ?: 0.0
        val sC = state.sideC.toDoubleOrNull() ?: 0.0
        val tb = state.topBase.toDoubleOrNull() ?: 0.0
        val bb = state.bottomBase.toDoubleOrNull() ?: 0.0
        val d1 = state.diagonal1.toDoubleOrNull() ?: 0.0
        val d2 = state.diagonal2.toDoubleOrNull() ?: 0.0
        val saA = state.semiAxisA.toDoubleOrNull() ?: 0.0
        val saB = state.semiAxisB.toDoubleOrNull() ?: 0.0
    
        val (area, perimeter) = when (state.selected2DShape) {
            Shape2D.CIRCLE -> {
                val a = PI * r.pow(2)
                val p = 2 * PI * r
                Pair(a, p)
            }
            Shape2D.SQUARE -> {
                val a = side.pow(2)
                val p = 4 * side
                Pair(a, p)
            }
            Shape2D.RECTANGLE -> {
                val a = len * wid
                val p = 2 * (len + wid)
                Pair(a, p)
            }
            Shape2D.TRIANGLE -> {
                // Using Heron's formula for area
                val s = (sA + sB + sC) / 2
                val a = if (s > sA && s > sB && s > sC) sqrt(s * (s - sA) * (s - sB) * (s - sC)) else 0.0
                val p = sA + sB + sC
                Pair(a, p)
            }
            Shape2D.TRAPEZOID -> {
                val a = ((tb + bb) / 2) * h
                // Assuming isosceles trapezoid for perimeter estimation
                val leg = sqrt(h.pow(2) + ((bb - tb) / 2).pow(2))
                val p = tb + bb + 2 * leg
                Pair(a, p)
            }
            Shape2D.PARALLELOGRAM -> {
                val a = bs * h
                val p = 2 * (bs + side) // Using side as side length
                Pair(a, p)
            }
            Shape2D.ELLIPSE -> {
                val a = PI * saA * saB
                // Ramanujan's approximation for perimeter
                val h_val = ((saA - saB) / (saA + saB)).pow(2)
                val p = PI * (saA + saB) * (1 + (3 * h_val) / (10 + sqrt(4 - 3 * h_val)))
                Pair(a, p)
            }
            Shape2D.RHOMBUS -> {
                val a = (d1 * d2) / 2
                val s_val = sqrt((d1 / 2).pow(2) + (d2 / 2).pow(2))
                val p = 4 * s_val
                Pair(a, p)
            }
        }
        state = state.copy(area2D = area, perimeter2D = perimeter)
    }
    
    private fun calculate3D() {
        // Parse inputs
        val r3d = state.radius3D.toDoubleOrNull() ?: 0.0
        val h3d = state.height3D.toDoubleOrNull() ?: 0.0
        val s3d = state.side3D.toDoubleOrNull() ?: 0.0
        val l3d = state.length3D.toDoubleOrNull() ?: 0.0
        val w3d = state.width3D.toDoubleOrNull() ?: 0.0
        val d3d = state.depth3D.toDoubleOrNull() ?: 0.0
        val br = state.baseRadius.toDoubleOrNull() ?: 0.0
        val majR = state.majorRadius.toDoubleOrNull() ?: 0.0
        val minR = state.minorRadius.toDoubleOrNull() ?: 0.0
        val sh = state.slantHeight.toDoubleOrNull() ?: 0.0
    
        val (volume, surfaceArea) = when (state.selected3DShape) {
            Shape3D.CUBE -> {
                val v = s3d.pow(3)
                val sa = 6 * s3d.pow(2)
                Pair(v, sa)
            }
            Shape3D.SPHERE -> {
                val v = (4.0 / 3.0) * PI * r3d.pow(3)
                val sa = 4 * PI * r3d.pow(2)
                Pair(v, sa)
            }
            Shape3D.CYLINDER -> {
                val v = PI * r3d.pow(2) * h3d
                val sa = 2 * PI * r3d * (r3d + h3d)
                Pair(v, sa)
            }
            Shape3D.CONE -> {
                val v = (1.0 / 3.0) * PI * br.pow(2) * h3d
                val slant = sqrt(br.pow(2) + h3d.pow(2))
                val sa = PI * br * (br + slant)
                Pair(v, sa)
            }
            Shape3D.PYRAMID -> {
                // Square base pyramid
                val v = (1.0 / 3.0) * s3d.pow(2) * h3d
                val slant = sqrt((s3d / 2).pow(2) + h3d.pow(2))
                val sa = s3d.pow(2) + 2 * s3d * slant
                Pair(v, sa)
            }
            Shape3D.RECTANGULAR_PRISM -> {
                val v = l3d * w3d * d3d
                val sa = 2 * (l3d * w3d + w3d * d3d + d3d * l3d)
                Pair(v, sa)
            }
            Shape3D.TRIANGULAR_PRISM -> {
                // Equilateral triangle base
                val baseArea = (sqrt(3.0) / 4) * s3d.pow(2)
                val v = baseArea * h3d
                val sa = 2 * baseArea + 3 * s3d * h3d
                Pair(v, sa)
            }
            Shape3D.TORUS -> {
                val v = 2 * PI.pow(2) * majR * minR.pow(2)
                val sa = 4 * PI.pow(2) * majR * minR
                Pair(v, sa)
            }
            Shape3D.HEMISPHERE -> {
                val v = (2.0 / 3.0) * PI * r3d.pow(3)
                val sa = 3 * PI * r3d.pow(2)
                Pair(v, sa)
            }
        }
        state = state.copy(volume = volume, surfaceArea = surfaceArea)
    }
}
