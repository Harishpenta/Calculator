# 🎯 Geometry Feature - Simple Example Walkthrough

## Real Example: User Changes Sphere Radius

Let's trace exactly what happens when a user changes the sphere radius from 3 to 5.

---

## Initial State (App Just Opened Geometry Screen)

### 1. GeometryViewModel State
```kotlin
GeometryState(
    selectedTab = SHAPES_3D,
    selected3DShape = Shape3D.SPHERE,
    radius3D = 3.0,
    volume = 113.097,      // Already calculated: (4/3)π(3³)
    surfaceArea = 113.097, // Already calculated: 4π(3²)
    rotationX = 25f,
    rotationY = 45f,
    isAutoRotating = true
)
```

### 2. Screen Shows
```
┌──────────────────────────────────────┐
│  Geometry Calculator                 │
├──────────────────────────────────────┤
│                                      │
│  ┌────────────────────────────────┐ │
│  │      🔵 (rotating sphere)      │ │
│  │                                │ │
│  └────────────────────────────────┘ │
│                                      │
│  [Pause] [Reset]                    │
│                                      │
│  Shapes: [Cube] [●Sphere] [Cone]    │
│                                      │
│  Radius: 3.0 units                  │
│                                      │
│  ┌────────────────────────────────┐ │
│  │ Volume: 113.097 cubic units    │ │
│  │ Surface Area: 113.097 sq units │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘
```

---

## User Action: Changes Radius to 5

### Step 1: User Types "5" in Radius Field

**What the code sees:**
```kotlin
DimensionInput(
    label = "Radius",
    value = 3.0,  // Current value displayed
    onValueChange = { newValue ->
        // This lambda is called when user types
        onAction(GeometryEvent.UpdateRadius3D(newValue))
    }
)
```

**User types:** `5.0`

**Code executes:**
```kotlin
onAction(GeometryEvent.UpdateRadius3D(5.0))
```

---

### Step 2: Event Reaches ViewModel

**In GeometryViewModel.kt:**
```kotlin
fun onEvent(event: GeometryEvent) {
    when (event) {
        is GeometryEvent.UpdateRadius3D -> {
            println("📥 Received: UpdateRadius3D(${event.value})")
            
            // Update state with new radius
            state = state.copy(radius3D = event.value)
            println("✅ State updated: radius3D = ${state.radius3D}")
            
            // Recalculate volume and surface area
            calculate3D()
            println("🧮 Calculation complete")
        }
        // ... other events
    }
}
```

**Console output:**
```
📥 Received: UpdateRadius3D(5.0)
✅ State updated: radius3D = 5.0
🧮 Calculation complete
```

---

### Step 3: Calculation Runs

**In calculate3D():**
```kotlin
private fun calculate3D() {
    println("🔢 Calculating for ${state.selected3DShape}...")
    
    val (volume, surfaceArea) = when (state.selected3DShape) {
        Shape3D.SPHERE -> {
            val r = state.radius3D  // Now 5.0
            println("   Radius: $r")
            
            // Volume = (4/3)πr³
            val vol = (4.0 / 3.0) * PI * r.pow(3)
            println("   Volume formula: (4/3)π(${r}³)")
            println("   Volume result: $vol")
            
            // Surface Area = 4πr²
            val sa = 4 * PI * r.pow(2)
            println("   SA formula: 4π(${r}²)")
            println("   SA result: $sa")
            
            vol to sa
        }
        // ... other shapes
    }
    
    // Update state with new calculated values
    state = state.copy(
        volume = volume,
        surfaceArea = surfaceArea
    )
    println("✅ Updated state: volume=$volume, surfaceArea=$surfaceArea")
}
```

**Console output:**
```
🔢 Calculating for SPHERE...
   Radius: 5.0
   Volume formula: (4/3)π(5³)
   Volume result: 523.5987755982989
   SA formula: 4π(5²)
   SA result: 314.1592653589793
✅ Updated state: volume=523.5987755982989, surfaceArea=314.1592653589793
```

**Math breakdown:**
```
Volume = (4/3) × π × 5³
       = (4/3) × 3.14159... × 125
       = 1.33333... × 392.699...
       = 523.599 cubic units

Surface Area = 4 × π × 5²
             = 4 × 3.14159... × 25
             = 314.159 square units
```

---

### Step 4: Compose Recomposition (Automatic!)

**Compose framework detects state change:**
```
State object changed!
  Old: GeometryState(radius3D=3.0, volume=113.097, ...)
  New: GeometryState(radius3D=5.0, volume=523.599, ...)

Trigger recomposition for all composables reading this state!
```

**Affected composables:**
1. `Shape3DViewer` - radius parameter changed
2. `DimensionInput` - value parameter changed
3. `ResultsCard3D` - volume/surfaceArea changed

---

### Step 5: UI Updates

#### A. Shape3DViewer Redraws

**Old code execution (radius = 3.0):**
```kotlin
Shape3DViewer(
    shape = SPHERE,
    radius = 3.0,  // Old value
    // ... other params
)

// Inside Shape3DViewer:
private fun drawSphere(radius: Float) {
    // Generate vertices for sphere with radius 3.0
    val vertices = generateSphereVertices(radius = 3.0f)
    // Draw 50 vertices at smaller size
}
```

**New code execution (radius = 5.0):**
```kotlin
Shape3DViewer(
    shape = SPHERE,
    radius = 5.0,  // New value! 🎉
    // ... other params
)

// Inside Shape3DViewer:
private fun drawSphere(radius: Float) {
    // Generate vertices for sphere with radius 5.0
    val vertices = generateSphereVertices(radius = 5.0f)
    // Draw 50 vertices at BIGGER size
}
```

**Visual result:**
```
Before:          After:
  🔵    →         ⚫
(small)        (bigger)
```

#### B. Input Field Updates

```kotlin
// Recomposes with new value
DimensionInput(
    label = "Radius",
    value = 5.0,  // Updated! Shows "5.0" now
    onValueChange = { ... }
)
```

**Screen shows:**
```
Radius: 5.0 units  ← Updated from "3.0 units"
```

#### C. Results Card Updates

```kotlin
ResultsCard3D(
    shapeName = "Sphere",
    volume = 523.599,    // Updated!
    surfaceArea = 314.159  // Updated!
)
```

**Screen shows:**
```
┌────────────────────────────────┐
│ Sphere                         │
│                                │
│ Volume          Surface Area   │
│ 523.599         314.159        │
│ cubic units     square units   │
└────────────────────────────────┘
```

---

## Complete Before/After

### BEFORE (radius = 3.0)

**State:**
```kotlin
radius3D = 3.0
volume = 113.097
surfaceArea = 113.097
```

**Screen:**
```
┌──────────────────────────────┐
│  Small sphere rotating       │
│  Radius: 3.0 units           │
│  Volume: 113.097 cu units    │
│  SA: 113.097 sq units        │
└──────────────────────────────┘
```

### AFTER (radius = 5.0)

**State:**
```kotlin
radius3D = 5.0
volume = 523.599
surfaceArea = 314.159
```

**Screen:**
```
┌──────────────────────────────┐
│  BIGGER sphere rotating      │
│  Radius: 5.0 units           │
│  Volume: 523.599 cu units    │
│  SA: 314.159 sq units        │
└──────────────────────────────┘
```

---

## Timeline (in milliseconds)

```
0ms    → User starts typing "5"
10ms   → Keyboard input detected
12ms   → onAction(UpdateRadius3D(5.0)) called
13ms   → ViewModel receives event
14ms   → state.copy(radius3D = 5.0) executes
15ms   → calculate3D() starts
16ms   → Math operations complete (4/3 × π × 125)
17ms   → state.copy(volume=523.599, ...) executes
18ms   → Compose detects state change
20ms   → Recomposition triggered
25ms   → Shape3DViewer redraws with new radius
30ms   → Results card updates text
35ms   → User sees bigger sphere and new numbers
```

**Total time: ~35 milliseconds** (faster than eye blink!)

---

## Code Flow Diagram (Simplified)

```
USER TYPES "5"
     ↓
┌─────────────────────────────────┐
│ DimensionInput composable       │
│ onValueChange gets called       │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ onAction(UpdateRadius3D(5.0))   │
│ Event sent to ViewModel         │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ GeometryViewModel.onEvent()     │
│ Pattern matches UpdateRadius3D  │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ state = state.copy(radius=5.0)  │
│ Immutable update                │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ calculate3D() function          │
│ Math: (4/3)πr³                  │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ state = state.copy(volume=...)  │
│ Save calculation result         │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ Compose framework               │
│ Detects state mutation          │
└─────────────────────────────────┘
     ↓
┌─────────────────────────────────┐
│ Recompose all dependent UIs     │
│ - Shape3DViewer                 │
│ - DimensionInput                │
│ - ResultsCard                   │
└─────────────────────────────────┘
     ↓
USER SEES UPDATED SCREEN
```

---

## Key Observations

### 1. **State is Immutable**
We never do: `state.radius3D = 5.0`
We always do: `state = state.copy(radius3D = 5.0)`

**Why?** Compose needs a new object to detect changes.

### 2. **Calculations are Automatic**
User doesn't click "Calculate" button. It happens automatically after every input change.

### 3. **UI Updates are Reactive**
We don't manually update the screen. Compose sees state change and updates automatically.

### 4. **Type Safety**
Can't send invalid events. `UpdateRadius3D` expects a Double, compiler enforces it.

---

## Try This Yourself!

### Experiment 1: Add Logging
```kotlin
// In GeometryViewModel.kt, calculate3D()
private fun calculate3D() {
    println("🔢 START CALCULATION")
    println("   Shape: ${state.selected3DShape}")
    println("   Radius: ${state.radius3D}")
    
    // ... calculation code ...
    
    println("   Result Volume: $volume")
    println("🔢 END CALCULATION")
}
```

Run app, change radius, watch console!

### Experiment 2: Break It
```kotlin
// Try this (wrong way):
state.radius3D = 5.0  // Won't compile! State is val, not var

// Try this (will compile but won't update UI):
state = state.apply { radius3D = 5.0 }  // Mutates same object
```

### Experiment 3: Add Validation
```kotlin
is GeometryEvent.UpdateRadius3D -> {
    if (event.value > 0 && event.value < 100) {  // Validate
        state = state.copy(radius3D = event.value)
        calculate3D()
    } else {
        println("❌ Invalid radius: ${event.value}")
    }
}
```

---

## Summary

**The entire flow is:**
1. User types → 2. Event created → 3. State updated → 4. Calculation runs → 5. UI auto-updates

**All in ~30 milliseconds!** 🚀

This is the power of **reactive UI** with **Jetpack Compose**.

---

**Want to see another example (e.g., shape switching)? Let me know! 🎉**
