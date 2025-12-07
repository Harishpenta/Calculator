# 🎯 Geometry Calculator Feature - Complete Explanation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture Flow](#architecture-flow)
3. [Component Breakdown](#component-breakdown)
4. [How It Works Step-by-Step](#how-it-works-step-by-step)
5. [3D Rendering Explained](#3d-rendering-explained)
6. [User Interaction Flow](#user-interaction-flow)

---

## 🌟 Overview

The Geometry Calculator is a **3-feature calculator** that:
- **Calculates** area, perimeter, volume, and surface area for 2D & 3D shapes
- **Visualizes** shapes in real-time with interactive 3D graphics
- **Responds** to user input dynamically (change dimensions → instant recalculation)

---

## 🏗️ Architecture Flow

```
User Taps "Geometry" on Home Screen
         ↓
MainActivity creates GeometryViewModel
         ↓
AppNavigation routes to GeometryScreen
         ↓
GeometryScreen displays:
  ├─ Shape Tabs (2D/3D)
  ├─ 3D Viewer (Shape3DViewer component)
  ├─ Input Controls (sliders/text fields)
  └─ Results Card (calculated values)
         ↓
User Changes Dimension
         ↓
GeometryEvent sent to ViewModel
         ↓
ViewModel updates State
         ↓
Recalculates values
         ↓
UI automatically updates (Compose recomposition)
```

---

## 🧩 Component Breakdown

### 1. **GeometryViewModel.kt** (The Brain 🧠)

**Purpose:** Manages all data and calculations

**Key Parts:**

#### A. **Enums** (Shape Definitions)
```kotlin
enum class Shape2D {
    CIRCLE, SQUARE, RECTANGLE, TRIANGLE, 
    TRAPEZOID, PARALLELOGRAM, ELLIPSE, RHOMBUS
}

enum class Shape3D {
    CUBE, SPHERE, CYLINDER, CONE, PYRAMID,
    RECTANGULAR_PRISM, TRIANGULAR_PRISM, TORUS, HEMISPHERE
}
```
**Why?** Ensures type safety - you can't select an invalid shape.

#### B. **GeometryState** (Data Container)
```kotlin
data class GeometryState(
    val selectedTab: GeometryTab = SHAPES_3D,  // Which tab is active?
    val selected3DShape: Shape3D = CUBE,       // Which 3D shape?
    val radius3D: Double = 3.0,                // Dimension inputs
    val volume: Double = 0.0,                  // Calculated result
    val rotationX: Float = 25f,                // 3D rotation angle
    val isAutoRotating: Boolean = true         // Auto-spin enabled?
)
```
**Why?** Single source of truth. All UI reads from this state.

#### C. **GeometryEvent** (User Actions)
```kotlin
sealed class GeometryEvent {
    data class Select3DShape(val shape: Shape3D) : GeometryEvent()
    data class UpdateRadius3D(val value: Double) : GeometryEvent()
    data class ToggleAutoRotate : GeometryEvent()
}
```
**Why?** Type-safe commands. User actions become event objects.

#### D. **onEvent() Function** (Event Handler)
```kotlin
fun onEvent(event: GeometryEvent) {
    when (event) {
        is GeometryEvent.UpdateRadius3D -> {
            state = state.copy(radius3D = event.value)  // Update state
            calculate3D()  // Recalculate volume & surface area
        }
    }
}
```
**Flow:** User changes radius → Event sent → State updated → calculate3D() runs → Results update

#### E. **Calculation Functions**
```kotlin
private fun calculate3D() {
    val (volume, surfaceArea) = when (state.selected3DShape) {
        Shape3D.CUBE -> {
            val side = state.side3D
            val vol = side.pow(3)           // V = s³
            val sa = 6 * side.pow(2)        // SA = 6s²
            vol to sa
        }
        Shape3D.SPHERE -> {
            val r = state.radius3D
            val vol = (4.0/3.0) * PI * r.pow(3)  // V = 4/3πr³
            val sa = 4 * PI * r.pow(2)           // SA = 4πr²
            vol to sa
        }
        // ... more shapes
    }
    state = state.copy(volume = volume, surfaceArea = surfaceArea)
}
```
**Why?** Pure math functions. Each shape has its own formula.

---

### 2. **Shape3DViewer.kt** (The 3D Graphics Engine 🎨)

**Purpose:** Renders 3D shapes on screen using Canvas

**How 3D Works on 2D Screen:**

#### Step 1: Define 3D Points
```kotlin
data class Point3D(val x: Float, val y: Float, val z: Float) {
    // Rotate around X-axis
    fun rotateX(angle: Float): Point3D {
        val cos = cos(angle)
        val sin = sin(angle)
        return Point3D(
            x = x,
            y = y * cos - z * sin,
            z = y * sin + z * cos
        )
    }
}
```
**Example:** A cube corner at (1, 1, 1) in 3D space.

#### Step 2: Rotate the Shape
```kotlin
val rotatedPoint = point
    .rotateX(rotationX)  // Tilt up/down
    .rotateY(rotationY)  // Spin left/right
    .rotateZ(rotationZ)  // Roll clockwise
```

#### Step 3: Project to 2D (Perspective)
```kotlin
fun project(width: Float, height: Float, fov: Float): Offset {
    val scale = fov / (fov + z)  // Objects farther away = smaller
    return Offset(
        x = width/2 + x * scale,   // Center + scaled X
        y = height/2 - y * scale   // Center + scaled Y (flip Y)
    )
}
```
**Result:** 3D coordinates → 2D screen coordinates

#### Step 4: Draw Faces (Surfaces)
```kotlin
data class Face3D(val points: List<Point3D>, val color: Color)

// Sort faces by depth (painter's algorithm)
val sortedFaces = faces.sortedBy { face ->
    face.points.map { it.z }.average()  // Farthest faces first
}

// Draw each face
sortedFaces.forEach { face ->
    val path = Path()
    face.points.forEachIndexed { i, point ->
        val projected = point.project(width, height, fov)
        if (i == 0) path.moveTo(projected.x, projected.y)
        else path.lineTo(projected.x, projected.y)
    }
    path.close()
    drawPath(path, face.color)
}
```

#### Step 5: Add Lighting
```kotlin
fun calculateLighting(normal: Point3D, lightDir: Point3D): Float {
    val dot = normal.x * lightDir.x + 
              normal.y * lightDir.y + 
              normal.z * lightDir.z
    return max(0.3f, dot)  // 0.3 = ambient light minimum
}
```
**Result:** Faces facing light = brighter, others = darker

#### Example: Drawing a Cube
```kotlin
private fun drawCube(size: Float): List<Face3D> {
    val h = size / 2
    // Define 8 corners
    val vertices = listOf(
        Point3D(-h, -h, -h), Point3D(h, -h, -h),  // Front bottom
        Point3D(h, h, -h),   Point3D(-h, h, -h),  // Front top
        Point3D(-h, -h, h),  Point3D(h, -h, h),   // Back bottom
        Point3D(h, h, h),    Point3D(-h, h, h)    // Back top
    )
    
    // Define 6 faces (each face = 4 vertices)
    return listOf(
        Face3D(listOf(v[0], v[1], v[2], v[3]), Color.Blue),   // Front
        Face3D(listOf(v[4], v[5], v[6], v[7]), Color.Green),  // Back
        Face3D(listOf(v[0], v[1], v[5], v[4]), Color.Red),    // Bottom
        // ... 3 more faces
    )
}
```

---

### 3. **GeometryScreen.kt** (The User Interface 📱)

**Purpose:** Display everything and handle user input

**Structure:**

```kotlin
@Composable
fun GeometryScreen(
    state: GeometryState,           // Current data from ViewModel
    onAction: (GeometryEvent) -> Unit,  // Send events to ViewModel
    onOpenDrawer: () -> Unit        // Navigate back
) {
    Scaffold {
        Column {
            // 1. Tab Selector (2D/3D)
            GeometryTabSelector(
                selectedTab = state.selectedTab,
                onTabSelect = { onAction(GeometryEvent.SelectTab(it)) }
            )
            
            // 2. Content (changes based on tab)
            if (state.selectedTab == SHAPES_3D) {
                Shapes3DContent(state, onAction)
            } else {
                Shapes2DContent(state, onAction)
            }
        }
    }
}
```

#### Tab 1: 3D Shapes
```kotlin
@Composable
private fun Shapes3DContent(state: GeometryState, onAction: ...) {
    Column {
        // A. 3D Viewer
        Shape3DViewer(
            shape = state.selected3DShape,  // Which shape to draw
            rotationX = state.rotationX,    // Current rotation
            side = state.side3D.toFloat(),  // Dimensions
            // ... more parameters
        )
        
        // B. Control Buttons
        Button(onClick = { onAction(ToggleAutoRotate) }) {
            Text(if (state.isAutoRotating) "Pause" else "Rotate")
        }
        
        // C. Shape Selector (Cube, Sphere, Cone, ...)
        LazyRow {
            items(Shape3D.values()) { shape ->
                ShapeChip(
                    shape = shape,
                    isSelected = state.selected3DShape == shape,
                    onClick = { onAction(Select3DShape(shape)) }
                )
            }
        }
        
        // D. Input Fields (dynamic based on shape)
        when (state.selected3DShape) {
            CUBE -> DimensionInput("Side", state.side3D, ...)
            SPHERE -> DimensionInput("Radius", state.radius3D, ...)
            CYLINDER -> {
                DimensionInput("Radius", state.radius3D, ...)
                DimensionInput("Height", state.height3D, ...)
            }
        }
        
        // E. Results Card
        Card {
            Text("Volume: ${state.volume} cubic units")
            Text("Surface Area: ${state.surfaceArea} sq units")
        }
    }
}
```

#### Tab 2: 2D Shapes
```kotlin
@Composable
private fun Shapes2DContent(state: GeometryState, onAction: ...) {
    Column {
        // A. 2D Visual (drawn with Canvas)
        Shape2DViewer(shape = state.selected2DShape)
        
        // B. Shape Selector
        ShapeChips(shapes = Shape2D.values())
        
        // C. Input Fields
        when (state.selected2DShape) {
            CIRCLE -> DimensionInput("Radius", state.radius, ...)
            RECTANGLE -> {
                DimensionInput("Length", state.length, ...)
                DimensionInput("Width", state.width, ...)
            }
        }
        
        // D. Results
        Card {
            Text("Area: ${state.area2D} sq units")
            Text("Perimeter: ${state.perimeter2D} units")
        }
    }
}
```

---

## 🔄 How It Works Step-by-Step

### **Scenario: User Opens Geometry Calculator and Changes Cube Size**

1. **User taps "Geometry" on Home**
   - `HomeScreen.kt` calls: `onNavigateToCalculator("geometry")`
   - `AppNavigation.kt` routes to: `GeometryScreen(...)`

2. **Screen Loads**
   - `MainActivity` creates: `val geometryViewModel = viewModel<GeometryViewModel>()`
   - Initial state: `GeometryState(selected3DShape = CUBE, side3D = 4.0, ...)`
   - `calculate3D()` runs → `volume = 64.0`, `surfaceArea = 96.0`

3. **UI Displays**
   - `Shape3DViewer` draws a cube with side = 4.0
   - Input field shows: "Side Length: 4.0 units"
   - Results card shows: "Volume: 64 cubic units"

4. **User Changes Side to 5.0**
   - `DimensionInput` detects change
   - Calls: `onAction(GeometryEvent.UpdateSide3D(5.0))`

5. **ViewModel Processes**
   ```kotlin
   fun onEvent(event: GeometryEvent) {
       when (event) {
           is UpdateSide3D -> {
               state = state.copy(side3D = 5.0)  // Update dimension
               calculate3D()  // Recalculate
           }
       }
   }
   ```

6. **Recalculation**
   ```kotlin
   private fun calculate3D() {
       val volume = 5.0.pow(3) = 125.0      // V = s³
       val surfaceArea = 6 * 5.0.pow(2) = 150.0  // SA = 6s²
       state = state.copy(volume = 125.0, surfaceArea = 150.0)
   }
   ```

7. **UI Auto-Updates** (Compose Recomposition)
   - `Shape3DViewer` redraws cube (bigger now)
   - Results card updates: "Volume: 125 cubic units"

---

## 🎮 3D Rendering Explained (Simple Analogy)

**Think of it like a photo:**

1. **3D World** → You have a physical cube in real space
2. **Rotation** → You rotate the cube in your hand
3. **Camera** → Your eye (or camera) looks at it from a fixed angle
4. **Perspective** → Objects farther away look smaller
5. **Photo** → The 2D image you see (the screen)

**In Code:**

```kotlin
// 1. Create cube vertices in 3D space
val corner = Point3D(x=2, y=2, z=2)

// 2. Rotate the cube
val rotated = corner.rotateY(45°)  // Spin 45 degrees

// 3. Apply perspective (convert 3D → 2D)
val screen2D = rotated.project(width, height, fov=500)
// Result: Offset(x=650, y=400) ← Draw here on screen

// 4. Draw on Canvas
drawCircle(color, radius=5, center=screen2D)
```

---

## 🖱️ User Interaction Flow

### **Touch Interaction (Dragging to Rotate)**

```kotlin
Shape3DViewer(
    modifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            onRotationChange(
                rotationY + dragAmount.x * 0.5f,  // Horizontal drag → Y rotation
                rotationX - dragAmount.y * 0.5f   // Vertical drag → X rotation
            )
        }
    }
)
```

**User drags finger right → `dragAmount.x = 50px` → `rotationY += 25°` → Cube spins right**

### **Auto-Rotate Animation**

```kotlin
if (state.isAutoRotating) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)  // 60 FPS
            onAction(UpdateRotationY(state.rotationY + 1f))  // Spin 1° per frame
        }
    }
}
```

---

## 📊 Data Flow Diagram

```
┌─────────────────────────────────────────────────────┐
│                  GeometryScreen                     │
│  ┌──────────────────────────────────────────────┐  │
│  │  User taps "Sphere" chip                     │  │
│  └───────────────────┬──────────────────────────┘  │
│                      ↓                              │
│  onClick = { onAction(Select3DShape(SPHERE)) }     │
└──────────────────────┬──────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────┐
│           GeometryViewModel.onEvent()                │
│  ┌────────────────────────────────────────────────┐ │
│  │  state = state.copy(selected3DShape = SPHERE)  │ │
│  │  calculate3D()  ← Recalculate with new shape  │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────┬───────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────┐
│              GeometryState (Updated)                 │
│  selected3DShape = SPHERE                            │
│  radius3D = 3.0                                      │
│  volume = 113.1 ← (4/3)πr³                          │
│  surfaceArea = 113.1 ← 4πr²                         │
└──────────────────────┬───────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────┐
│         UI Recomposes (Automatic)                    │
│  ┌────────────────────────────────────────────────┐ │
│  │  Shape3DViewer: Draws Sphere instead of Cube  │ │
│  │  Results Card: Shows "Volume: 113.1 cu units" │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```

---

## 🧪 Example Walkthrough: Calculating Cylinder Volume

**User wants to find volume of a cylinder with radius=3, height=5**

### Step-by-Step:

1. **Open Geometry Screen** → Default shows Cube

2. **Select Cylinder**
   ```kotlin
   onClick = { onAction(GeometryEvent.Select3DShape(Shape3D.CYLINDER)) }
   ```

3. **ViewModel Updates**
   ```kotlin
   state = state.copy(selected3DShape = CYLINDER)
   calculate3D()
   ```

4. **Calculation Runs**
   ```kotlin
   Shape3D.CYLINDER -> {
       val r = state.radius3D  // 3.0
       val h = state.height3D  // 5.0
       val volume = PI * r.pow(2) * h  // π × 9 × 5 = 141.37
       val sa = 2 * PI * r * (r + h)   // 2π × 3 × 8 = 150.80
       volume to sa
   }
   ```

5. **UI Updates**
   - 3D Viewer draws cylinder shape
   - Input fields show: "Radius: 3.0" and "Height: 5.0"
   - Results show: "Volume: 141.37 cubic units"

6. **User Changes Radius to 4**
   ```kotlin
   onValueChange = { onAction(GeometryEvent.UpdateRadius3D(4.0)) }
   ```

7. **Instant Recalculation**
   ```kotlin
   volume = PI * 4² * 5 = 251.33  // New volume
   ```

8. **Results Update in Real-Time**

---

## 🎯 Key Concepts Summary

| Concept | What It Does | Analogy |
|---------|-------------|---------|
| **ViewModel** | Holds data & logic | The calculator's brain |
| **State** | Current values | Snapshot of current situation |
| **Event** | User action | Buttons you press |
| **Recomposition** | UI auto-update | Screen refreshes itself |
| **Point3D** | Position in 3D | GPS coordinates but with 3 numbers |
| **Rotation** | Spin the shape | Rotating a rubik's cube |
| **Projection** | 3D → 2D | Taking a photo of a 3D object |
| **Face3D** | Surface of shape | One side of a box |
| **Lighting** | Brightness | Flashlight making one side brighter |

---

## 🚀 What Happens When You Run the App

```
1. User opens app
   ↓
2. MainActivity creates ALL ViewModels (including GeometryViewModel)
   ↓
3. User taps "Geometry" category on HomeScreen
   ↓
4. Navigation routes to GeometryScreen
   ↓
5. GeometryScreen reads state from GeometryViewModel
   ↓
6. Shape3DViewer draws initial shape (Cube)
   ↓
7. User interacts (change shape, rotate, adjust size)
   ↓
8. Events sent to ViewModel → State updates → UI recomposes
   ↓
9. Calculations happen instantly in background
   ↓
10. User sees smooth, real-time updates
```

---

## 💡 Why This Architecture?

✅ **Separation of Concerns**
- ViewModel = Logic (calculations)
- Screen = UI (display)
- Component = Reusable pieces (3D viewer)

✅ **Reactive Updates**
- Change state → UI automatically updates
- No manual "refresh" needed

✅ **Type Safety**
- Enums prevent invalid shapes
- Sealed classes ensure all events handled

✅ **Testable**
- ViewModel can be tested without UI
- Pure functions (calculations) easy to verify

---

## 🐛 Common Questions

**Q: How does the 3D cube look 3D on a flat screen?**
A: Perspective projection + lighting. Farther corners are drawn smaller, and faces have different brightness based on angle to light.

**Q: Why do we need events instead of directly changing state?**
A: Centralized control. All changes go through `onEvent()`, making debugging easier and ensuring calculations always run after updates.

**Q: What if I want to add a new shape (e.g., Pyramid)?**
A: 
1. Add to enum: `PYRAMID("Pyramid")`
2. Add calculation in `calculate3D()`
3. Add vertices/faces in `Shape3DViewer`
4. Done! UI automatically picks it up.

**Q: How is rotation smooth if we recalculate every frame?**
A: Canvas drawing is very fast. We're only rotating ~100 points and drawing ~20 faces. At 60 FPS, it's imperceptible.

---

## 🎓 Learning Path

**Beginner:** Understand State → Events → Recalculation flow

**Intermediate:** Follow how one dimension change triggers recalculation

**Advanced:** Understand 3D projection math and lighting calculations

---

## 📚 Files Reference

- `GeometryViewModel.kt` → Data & Math
- `Shape3DViewer.kt` → 3D Graphics
- `GeometryScreen.kt` → UI Layout
- `AppNavigation.kt` → Routing
- `HomeScreen.kt` → Entry Point

---

**Need more clarification on a specific part? Let me know! 🚀**
