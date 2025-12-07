# 🚀 Geometry Feature - Quick Reference Guide

## 📁 File Structure
```
app/src/main/java/com/antigravity/calculator/
│
├── viewmodels/
│   └── GeometryViewModel.kt        ← State & calculations
│
├── ui/
│   ├── components/
│   │   └── Shape3DViewer.kt        ← 3D rendering engine
│   │
│   ├── screens/
│   │   └── GeometryScreen.kt       ← Main UI screen
│   │
│   └── navigation/
│       └── AppNavigation.kt        ← Routing (updated)
│
└── MainActivity.kt                 ← ViewModel creation (updated)
```

---

## ⚡ Quick Answers

### "How do I test the feature?"
1. Run the app
2. Tap "Geometry" category on home screen
3. You'll see a 3D cube spinning
4. Try:
   - Tap different shape chips (Sphere, Cone, etc.)
   - Drag on the 3D viewer to rotate
   - Change dimension values
   - Tap "Pause" to stop auto-rotation

### "How do I add a new 3D shape?"
```kotlin
// 1. Add to enum (GeometryViewModel.kt)
enum class Shape3D {
    OCTAHEDRON("Octahedron"),  // ← New shape
}

// 2. Add calculation (GeometryViewModel.kt)
private fun calculate3D() {
    when (state.selected3DShape) {
        Shape3D.OCTAHEDRON -> {
            val vol = (sqrt(2.0) / 3.0) * side.pow(3)
            val sa = 2 * sqrt(3.0) * side.pow(2)
            vol to sa
        }
    }
}

// 3. Add renderer (Shape3DViewer.kt)
private fun drawOctahedron(size: Float): List<Face3D> {
    val vertices = listOf(...)  // 6 vertices
    val faces = listOf(...)     // 8 triangular faces
    return faces
}
```

### "How do I change the initial shape?"
```kotlin
// In GeometryViewModel.kt, line ~44
data class GeometryState(
    val selected3DShape: Shape3D = Shape3D.SPHERE,  // ← Change this
    val radius3D: Double = 3.0,
    // ...
)
```

### "How do I disable auto-rotation by default?"
```kotlin
// In GeometryViewModel.kt
data class GeometryState(
    val isAutoRotating: Boolean = false,  // ← Change to false
)
```

### "How do I change colors?"
```kotlin
// In Shape3DViewer.kt, find the shape function
private fun drawCube(size: Float): List<Face3D> {
    return listOf(
        Face3D(..., Color.Red),     // ← Change colors here
        Face3D(..., Color.Green),
        Face3D(..., Color.Blue),
    )
}

// Or change base colors in Color.kt
val GeometryYellow = Color(0xFFFFC107)  // ← Change this
```

---

## 🔧 Common Modifications

### Adjust Rotation Speed
```kotlin
// In GeometryScreen.kt, search for "LaunchedEffect"
LaunchedEffect(Unit) {
    while (true) {
        delay(16)
        onAction(UpdateRotationY(state.rotationY + 2f))  // ← Change 1f to 2f for faster
    }
}
```

### Change Initial Dimensions
```kotlin
// In GeometryViewModel.kt
data class GeometryState(
    val radius3D: Double = 5.0,   // ← Default radius
    val height3D: Double = 8.0,   // ← Default height
    val side3D: Double = 6.0,     // ← Default side
)
```

### Adjust 3D Viewer Size
```kotlin
// In GeometryScreen.kt, find Shape3DViewer
Shape3DViewer(
    modifier = Modifier
        .fillMaxWidth()
        .height(400.dp),  // ← Change height here
)
```

### Change Decimal Precision
```kotlin
// In GeometryScreen.kt, line ~52
val decimalFormat = remember { 
    DecimalFormat("#,##0.##")  // ← 2 decimals
    // or
    DecimalFormat("#,##0.####")  // ← 4 decimals
}
```

---

## 🐛 Debugging Tips

### Shape Not Displaying?
```kotlin
// Check these in order:
1. Is the shape in the enum? (GeometryViewModel.kt)
2. Is calculate3D() handling it? (switch case)
3. Is drawShape() defined? (Shape3DViewer.kt)
4. Are vertices correct? (print them)
```

### Calculation Wrong?
```kotlin
// Add logging in GeometryViewModel.kt
private fun calculate3D() {
    val result = when (state.selected3DShape) {
        Shape3D.SPHERE -> {
            val r = state.radius3D
            val vol = (4.0/3.0) * PI * r.pow(3)
            println("Sphere: r=$r, vol=$vol")  // ← Debug print
            vol to surfaceArea
        }
    }
}
```

### UI Not Updating?
```kotlin
// Ensure you're calling state.copy()
state = state.copy(radius3D = newValue)  // ✅ Correct
state.radius3D = newValue  // ❌ Won't trigger recomposition
```

### 3D Rendering Issues?
```kotlin
// In Shape3DViewer.kt, check:
1. Are faces being sorted? (painter's algorithm)
2. Is projection math correct? (fov / (fov + z))
3. Are rotations applied in correct order? (X → Y → Z)
```

---

## 📊 Performance Metrics

| Operation | Time | Notes |
|-----------|------|-------|
| State update | <1ms | Instant |
| Calculation | <1ms | Pure math |
| 3D rotation | ~1ms | 100 vertices |
| Canvas draw | ~5ms | 50 faces |
| Full frame | ~16ms | 60 FPS |

**Optimization tip:** If laggy, reduce vertices/faces count.

---

## 🎨 Customization Examples

### Example 1: Add Material Colors
```kotlin
// In Shape3DViewer.kt
private val materialColors = listOf(
    Color(0xFF2196F3),  // Blue
    Color(0xFF4CAF50),  // Green
    Color(0xFFFFC107),  // Amber
)

private fun drawCube(size: Float): List<Face3D> {
    return listOf(
        Face3D(face1, materialColors[0]),
        Face3D(face2, materialColors[1]),
        // ...
    )
}
```

### Example 2: Add Wireframe Mode
```kotlin
// In GeometryState
val isWireframe: Boolean = false

// In Shape3DViewer
Canvas {
    if (state.isWireframe) {
        // Draw edges only
        drawPath(path, Color.Black, style = Stroke(width = 2f))
    } else {
        // Draw filled faces
        drawPath(path, faceColor)
    }
}
```

### Example 3: Add Zoom Control
```kotlin
// In GeometryState
val zoomLevel: Float = 1.0f

// In Shape3DViewer projection
fun project(width: Float, height: Float, fov: Float, zoom: Float): Offset {
    val scale = (fov / (fov + z)) * zoom  // ← Apply zoom
    return Offset(...)
}
```

---

## 🧪 Testing Checklist

### Unit Tests (ViewModel)
```kotlin
@Test
fun `cube volume calculation is correct`() {
    val vm = GeometryViewModel()
    vm.onEvent(GeometryEvent.Select3DShape(Shape3D.CUBE))
    vm.onEvent(GeometryEvent.UpdateSide3D(5.0))
    
    assertEquals(125.0, vm.state.volume, 0.01)
}
```

### UI Tests (Screen)
```kotlin
@Test
fun `shape selection updates UI`() {
    composeTestRule.setContent {
        GeometryScreen(...)
    }
    
    composeTestRule.onNodeWithText("Sphere").performClick()
    composeTestRule.onNodeWithText("Radius").assertExists()
}
```

### Manual Tests
- [ ] App doesn't crash on shape change
- [ ] Calculations match calculator.net
- [ ] 3D viewer rotates smoothly
- [ ] Touch drag works
- [ ] Auto-rotate can be toggled
- [ ] Share button works
- [ ] Works in light/dark mode

---

## 📚 Formula Reference

### 2D Shapes
```
Circle:      A = πr²,  P = 2πr
Square:      A = s²,   P = 4s
Rectangle:   A = lw,   P = 2(l+w)
Triangle:    A = √[s(s-a)(s-b)(s-c)], P = a+b+c  (Heron's)
```

### 3D Shapes
```
Cube:        V = s³,           SA = 6s²
Sphere:      V = 4/3πr³,       SA = 4πr²
Cylinder:    V = πr²h,         SA = 2πr(r+h)
Cone:        V = 1/3πr²h,      SA = πr(r+√(h²+r²))
Pyramid:     V = 1/3Bh,        SA = B + 1/2pl
```

---

## 🔗 Related Files

| File | Lines | Purpose |
|------|-------|---------|
| GeometryViewModel.kt | 391 | Business logic |
| Shape3DViewer.kt | 520 | Graphics rendering |
| GeometryScreen.kt | 1042 | UI layout |
| Color.kt | - | Theme colors |
| AppNavigation.kt | 128 | Routing |

---

## 💬 FAQ

**Q: Why separate ViewModel and Screen?**
A: Testability + reusability. ViewModel can be unit tested without UI.

**Q: Why use events instead of direct function calls?**
A: Type safety + centralized logic. All state changes go through one place.

**Q: Why Canvas instead of OpenGL?**
A: Simplicity. Canvas is sufficient for ~100 vertices. OpenGL is overkill.

**Q: Can I use this for 4D shapes?**
A: Not directly. Need 4D → 3D projection first, then 3D → 2D.

**Q: Performance on old devices?**
A: Should run fine on devices >2018. Reduce vertices if needed.

---

## 🎓 Learning Resources

**To understand State management:**
- Read: GeometryState data class
- Follow: How onEvent() updates state

**To understand 3D math:**
- Read: Point3D.rotateX/Y/Z functions
- Follow: How project() converts 3D → 2D

**To understand Compose:**
- Read: GeometryScreen composable structure
- Follow: How state changes trigger recomposition

---

## 🚀 Next Steps

1. **Run the app** - See it in action
2. **Read GEOMETRY_FEATURE_EXPLANATION.md** - Deep dive
3. **Modify a color** - See changes live
4. **Add a new shape** - Follow the pattern
5. **Experiment** - Break things and learn!

---

**Need help? Check the main explanation docs! 🎉**
