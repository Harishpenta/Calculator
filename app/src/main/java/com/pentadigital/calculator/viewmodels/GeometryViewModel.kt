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
    val selectedTab: GeometryTab = GeometryTab.SHAPES_3D,
    
    // 2D Shape
    val selected2DShape: Shape2D = Shape2D.CIRCLE,
    val radius: Double = 5.0,
    val side: Double = 5.0,
    val length: Double = 8.0,
    val width: Double = 5.0,
    val base: Double = 6.0,
    val height: Double = 4.0,
    val sideA: Double = 3.0,
    val sideB: Double = 4.0,
    val sideC: Double = 5.0,
    val topBase: Double = 4.0,
    val bottomBase: Double = 6.0,
    val diagonal1: Double = 6.0,
    val diagonal2: Double = 4.0,
    val semiAxisA: Double = 5.0,
    val semiAxisB: Double = 3.0,
    
    // 2D Results
    val area2D: Double = 0.0,
    val perimeter2D: Double = 0.0,
    
    // 3D Shape
    val selected3DShape: Shape3D = Shape3D.CUBE,
    val radius3D: Double = 3.0,
    val height3D: Double = 5.0,
    val side3D: Double = 4.0,
    val length3D: Double = 6.0,
    val width3D: Double = 4.0,
    val depth3D: Double = 3.0,
    val baseRadius: Double = 3.0,
    val majorRadius: Double = 4.0,
    val minorRadius: Double = 1.5,
    val slantHeight: Double = 5.0,
    
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
    data class UpdateRadius(val value: Double) : GeometryEvent()
    data class UpdateSide(val value: Double) : GeometryEvent()
    data class UpdateLength(val value: Double) : GeometryEvent()
    data class UpdateWidth(val value: Double) : GeometryEvent()
    data class UpdateBase(val value: Double) : GeometryEvent()
    data class UpdateHeight(val value: Double) : GeometryEvent()
    data class UpdateSideA(val value: Double) : GeometryEvent()
    data class UpdateSideB(val value: Double) : GeometryEvent()
    data class UpdateSideC(val value: Double) : GeometryEvent()
    data class UpdateTopBase(val value: Double) : GeometryEvent()
    data class UpdateBottomBase(val value: Double) : GeometryEvent()
    data class UpdateDiagonal1(val value: Double) : GeometryEvent()
    data class UpdateDiagonal2(val value: Double) : GeometryEvent()
    data class UpdateSemiAxisA(val value: Double) : GeometryEvent()
    data class UpdateSemiAxisB(val value: Double) : GeometryEvent()
    
    // 3D Shape events
    data class Select3DShape(val shape: Shape3D) : GeometryEvent()
    data class UpdateRadius3D(val value: Double) : GeometryEvent()
    data class UpdateHeight3D(val value: Double) : GeometryEvent()
    data class UpdateSide3D(val value: Double) : GeometryEvent()
    data class UpdateLength3D(val value: Double) : GeometryEvent()
    data class UpdateWidth3D(val value: Double) : GeometryEvent()
    data class UpdateDepth3D(val value: Double) : GeometryEvent()
    data class UpdateBaseRadius(val value: Double) : GeometryEvent()
    data class UpdateMajorRadius(val value: Double) : GeometryEvent()
    data class UpdateMinorRadius(val value: Double) : GeometryEvent()
    data class UpdateSlantHeight(val value: Double) : GeometryEvent()
    
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
        val (area, perimeter) = when (state.selected2DShape) {
            Shape2D.CIRCLE -> {
                val a = PI * state.radius.pow(2)
                val p = 2 * PI * state.radius
                Pair(a, p)
            }
            Shape2D.SQUARE -> {
                val a = state.side.pow(2)
                val p = 4 * state.side
                Pair(a, p)
            }
            Shape2D.RECTANGLE -> {
                val a = state.length * state.width
                val p = 2 * (state.length + state.width)
                Pair(a, p)
            }
            Shape2D.TRIANGLE -> {
                // Using Heron's formula for area
                val s = (state.sideA + state.sideB + state.sideC) / 2
                val a = sqrt(s * (s - state.sideA) * (s - state.sideB) * (s - state.sideC))
                val p = state.sideA + state.sideB + state.sideC
                Pair(a, p)
            }
            Shape2D.TRAPEZOID -> {
                val a = ((state.topBase + state.bottomBase) / 2) * state.height
                // Assuming isosceles trapezoid for perimeter estimation
                val leg = sqrt(state.height.pow(2) + ((state.bottomBase - state.topBase) / 2).pow(2))
                val p = state.topBase + state.bottomBase + 2 * leg
                Pair(a, p)
            }
            Shape2D.PARALLELOGRAM -> {
                val a = state.base * state.height
                val p = 2 * (state.base + state.side)
                Pair(a, p)
            }
            Shape2D.ELLIPSE -> {
                val a = PI * state.semiAxisA * state.semiAxisB
                // Ramanujan's approximation for perimeter
                val h = ((state.semiAxisA - state.semiAxisB) / (state.semiAxisA + state.semiAxisB)).pow(2)
                val p = PI * (state.semiAxisA + state.semiAxisB) * (1 + (3 * h) / (10 + sqrt(4 - 3 * h)))
                Pair(a, p)
            }
            Shape2D.RHOMBUS -> {
                val a = (state.diagonal1 * state.diagonal2) / 2
                val side = sqrt((state.diagonal1 / 2).pow(2) + (state.diagonal2 / 2).pow(2))
                val p = 4 * side
                Pair(a, p)
            }
        }
        state = state.copy(area2D = area, perimeter2D = perimeter)
    }
    
    private fun calculate3D() {
        val (volume, surfaceArea) = when (state.selected3DShape) {
            Shape3D.CUBE -> {
                val v = state.side3D.pow(3)
                val sa = 6 * state.side3D.pow(2)
                Pair(v, sa)
            }
            Shape3D.SPHERE -> {
                val v = (4.0 / 3.0) * PI * state.radius3D.pow(3)
                val sa = 4 * PI * state.radius3D.pow(2)
                Pair(v, sa)
            }
            Shape3D.CYLINDER -> {
                val v = PI * state.radius3D.pow(2) * state.height3D
                val sa = 2 * PI * state.radius3D * (state.radius3D + state.height3D)
                Pair(v, sa)
            }
            Shape3D.CONE -> {
                val v = (1.0 / 3.0) * PI * state.baseRadius.pow(2) * state.height3D
                val slant = sqrt(state.baseRadius.pow(2) + state.height3D.pow(2))
                val sa = PI * state.baseRadius * (state.baseRadius + slant)
                Pair(v, sa)
            }
            Shape3D.PYRAMID -> {
                // Square base pyramid
                val v = (1.0 / 3.0) * state.side3D.pow(2) * state.height3D
                val slant = sqrt((state.side3D / 2).pow(2) + state.height3D.pow(2))
                val sa = state.side3D.pow(2) + 2 * state.side3D * slant
                Pair(v, sa)
            }
            Shape3D.RECTANGULAR_PRISM -> {
                val v = state.length3D * state.width3D * state.depth3D
                val sa = 2 * (state.length3D * state.width3D + state.width3D * state.depth3D + state.depth3D * state.length3D)
                Pair(v, sa)
            }
            Shape3D.TRIANGULAR_PRISM -> {
                // Equilateral triangle base
                val baseArea = (sqrt(3.0) / 4) * state.side3D.pow(2)
                val v = baseArea * state.height3D
                val sa = 2 * baseArea + 3 * state.side3D * state.height3D
                Pair(v, sa)
            }
            Shape3D.TORUS -> {
                val v = 2 * PI.pow(2) * state.majorRadius * state.minorRadius.pow(2)
                val sa = 4 * PI.pow(2) * state.majorRadius * state.minorRadius
                Pair(v, sa)
            }
            Shape3D.HEMISPHERE -> {
                val v = (2.0 / 3.0) * PI * state.radius3D.pow(3)
                val sa = 3 * PI * state.radius3D.pow(2)
                Pair(v, sa)
            }
        }
        state = state.copy(volume = volume, surfaceArea = surfaceArea)
    }
}
