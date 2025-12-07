package com.pentadigital.calculator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pentadigital.calculator.viewmodels.Shape3D
import com.pentadigital.calculator.ui.theme.*
import kotlin.math.*

// 3D Point class
data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun rotateX(angle: Float): Point3D {
        val rad = Math.toRadians(angle.toDouble())
        val cos = cos(rad).toFloat()
        val sin = sin(rad).toFloat()
        return Point3D(x, y * cos - z * sin, y * sin + z * cos)
    }
    
    fun rotateY(angle: Float): Point3D {
        val rad = Math.toRadians(angle.toDouble())
        val cos = cos(rad).toFloat()
        val sin = sin(rad).toFloat()
        return Point3D(x * cos + z * sin, y, -x * sin + z * cos)
    }
    
    fun rotateZ(angle: Float): Point3D {
        val rad = Math.toRadians(angle.toDouble())
        val cos = cos(rad).toFloat()
        val sin = sin(rad).toFloat()
        return Point3D(x * cos - y * sin, x * sin + y * cos, z)
    }
    
    fun project(fov: Float, viewerDistance: Float): Offset {
        val factor = fov / (viewerDistance + z + 0.001f)
        return Offset(x * factor, -y * factor)
    }
}

// Face of a 3D shape
data class Face3D(
    val vertices: List<Point3D>,
    val baseColor: Color,
    val normalZ: Float = 0f
)

@Composable
fun Shape3DViewer(
    shape: Shape3D,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    isAutoRotating: Boolean,
    onRotationChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
    side: Float = 1f,
    radius: Float = 1f,
    height: Float = 1.5f,
    length: Float = 1.5f,
    width: Float = 1f,
    depth: Float = 0.8f,
    majorRadius: Float = 1f,
    minorRadius: Float = 0.3f
) {
    var currentRotationX by remember { mutableStateOf(rotationX) }
    var currentRotationY by remember { mutableStateOf(rotationY) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val autoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "autoRotation"
    )
    
    LaunchedEffect(rotationX, rotationY) {
        currentRotationX = rotationX
        currentRotationY = rotationY
    }
    
    val effectiveRotationY = if (isAutoRotating) autoRotation else currentRotationY
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberpunkDarkBG,
                        Color(0xFF1A1A2E), // Slightly lighter dark
                        CyberpunkDarkBG
                    )
                )
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    if (!isAutoRotating) {
                        currentRotationY += dragAmount.x * 0.5f
                        currentRotationX += dragAmount.y * 0.5f
                        onRotationChange(currentRotationX, currentRotationY)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        LayoutGridBackground()
        
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scale = minOf(size.width, size.height) * 0.6f
            
            drawGrid(centerX, centerY, scale, currentRotationX, effectiveRotationY)
            
            when (shape) {
                Shape3D.CUBE -> drawCube(centerX, centerY, scale, side, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.SPHERE -> drawSphere(centerX, centerY, scale, radius, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.CYLINDER -> drawCylinder(centerX, centerY, scale, radius, height, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.CONE -> drawCone(centerX, centerY, scale, radius, height, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.PYRAMID -> drawPyramid(centerX, centerY, scale, side, height, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.RECTANGULAR_PRISM -> drawRectangularPrism(centerX, centerY, scale, length, width, depth, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.TRIANGULAR_PRISM -> drawTriangularPrism(centerX, centerY, scale, side, height, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.TORUS -> drawTorus(centerX, centerY, scale, majorRadius, minorRadius, currentRotationX, effectiveRotationY, rotationZ)
                Shape3D.HEMISPHERE -> drawHemisphere(centerX, centerY, scale, radius, currentRotationX, effectiveRotationY, rotationZ)
            }
        }
    }
}

@Composable
private fun LayoutGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 40.dp.toPx()
        val gridColor = NeonCyan.copy(alpha = 0.05f)
        
        // Draw vertical lines
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1f
            )
        }
        // Draw horizontal lines
        for (y in 0..size.height.toInt() step spacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}

private fun DrawScope.drawGrid(centerX: Float, centerY: Float, scale: Float, rotX: Float, rotY: Float) {
    val gridSize = 5
    val gridStep = 0.4f
    val gridColor = NeonCyan.copy(alpha = 0.3f)
    
    for (i in -gridSize..gridSize) {
        val startPoint = Point3D(i * gridStep, 0.8f, -gridSize * gridStep).rotateX(rotX).rotateY(rotY).project(1.5f, 4f)
        val endPoint = Point3D(i * gridStep, 0.8f, gridSize * gridStep).rotateX(rotX).rotateY(rotY).project(1.5f, 4f)
        
        drawLine(
            color = gridColor,
            start = Offset(centerX + startPoint.x * scale, centerY + startPoint.y * scale),
            end = Offset(centerX + endPoint.x * scale, centerY + endPoint.y * scale),
            strokeWidth = 1.5f
        )
    }
    
    for (i in -gridSize..gridSize) {
        val startPoint = Point3D(-gridSize * gridStep, 0.8f, i * gridStep).rotateX(rotX).rotateY(rotY).project(1.5f, 4f)
        val endPoint = Point3D(gridSize * gridStep, 0.8f, i * gridStep).rotateX(rotX).rotateY(rotY).project(1.5f, 4f)
        
        drawLine(
            color = gridColor,
            start = Offset(centerX + startPoint.x * scale, centerY + startPoint.y * scale),
            end = Offset(centerX + endPoint.x * scale, centerY + endPoint.y * scale),
            strokeWidth = 1.5f
        )
    }
}

private fun DrawScope.drawCube(centerX: Float, centerY: Float, scale: Float, side: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val s = side * 0.5f
    val vertices = listOf(
        Point3D(-s, -s, -s), Point3D(s, -s, -s), Point3D(s, s, -s), Point3D(-s, s, -s),
        Point3D(-s, -s, s), Point3D(s, -s, s), Point3D(s, s, s), Point3D(-s, s, s)
    )
    
    // Neon Cyberpunk Colors
    val faces = listOf(
        Face3D(listOf(vertices[0], vertices[1], vertices[2], vertices[3]), NeonCyan), // Front
        Face3D(listOf(vertices[4], vertices[5], vertices[6], vertices[7]), NeonPurple), // Back
        Face3D(listOf(vertices[0], vertices[4], vertices[7], vertices[3]), NeonGreen), // Left
        Face3D(listOf(vertices[1], vertices[5], vertices[6], vertices[2]), NeonCyan), // Right
        Face3D(listOf(vertices[0], vertices[1], vertices[5], vertices[4]), NeonPurple), // Bottom
        Face3D(listOf(vertices[3], vertices[2], vertices[6], vertices[7]), NeonGreen)  // Top
    )
    
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawSphere(centerX: Float, centerY: Float, scale: Float, radius: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val segments = 16
    val rings = 12
    val faces = mutableListOf<Face3D>()
    
    for (i in 0 until rings) {
        val theta1 = PI * i / rings
        val theta2 = PI * (i + 1) / rings
        for (j in 0 until segments) {
            val phi1 = 2 * PI * j / segments
            val phi2 = 2 * PI * (j + 1) / segments
            
            val p1 = Point3D((radius * sin(theta1) * cos(phi1)).toFloat(), (radius * cos(theta1)).toFloat(), (radius * sin(theta1) * sin(phi1)).toFloat())
            val p2 = Point3D((radius * sin(theta1) * cos(phi2)).toFloat(), (radius * cos(theta1)).toFloat(), (radius * sin(theta1) * sin(phi2)).toFloat())
            val p3 = Point3D((radius * sin(theta2) * cos(phi2)).toFloat(), (radius * cos(theta2)).toFloat(), (radius * sin(theta2) * sin(phi2)).toFloat())
            val p4 = Point3D((radius * sin(theta2) * cos(phi1)).toFloat(), (radius * cos(theta2)).toFloat(), (radius * sin(theta2) * sin(phi1)).toFloat())
            
            // Neon Gradient
            val colorValue = (i.toFloat() / rings)
            val color = if (j % 2 == 0) NeonCyan.copy(alpha = 0.8f) else NeonPurple.copy(alpha = 0.8f)
            faces.add(Face3D(listOf(p1, p2, p3, p4), color))
        }
    }
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawCylinder(centerX: Float, centerY: Float, scale: Float, radius: Float, height: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val segments = 20
    val faces = mutableListOf<Face3D>()
    val h = height * 0.5f
    val topCenter = Point3D(0f, -h, 0f)
    val bottomCenter = Point3D(0f, h, 0f)
    
    for (i in 0 until segments) {
        val angle1 = 2 * PI * i / segments
        val angle2 = 2 * PI * (i + 1) / segments
        val top1 = Point3D((radius * cos(angle1)).toFloat(), -h, (radius * sin(angle1)).toFloat())
        val top2 = Point3D((radius * cos(angle2)).toFloat(), -h, (radius * sin(angle2)).toFloat())
        val bottom1 = Point3D((radius * cos(angle1)).toFloat(), h, (radius * sin(angle1)).toFloat())
        val bottom2 = Point3D((radius * cos(angle2)).toFloat(), h, (radius * sin(angle2)).toFloat())
        
        faces.add(Face3D(listOf(top1, top2, bottom2, bottom1), if(i%2==0) NeonCyan else NeonPurple))
        faces.add(Face3D(listOf(topCenter, top1, top2), NeonGreen))
        faces.add(Face3D(listOf(bottomCenter, bottom2, bottom1), NeonGreen))
    }
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawCone(centerX: Float, centerY: Float, scale: Float, radius: Float, height: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val segments = 20
    val faces = mutableListOf<Face3D>()
    val h = height * 0.5f
    val apex = Point3D(0f, -h, 0f)
    val baseCenter = Point3D(0f, h, 0f)
    
    for (i in 0 until segments) {
        val angle1 = 2 * PI * i / segments
        val angle2 = 2 * PI * (i + 1) / segments
        val base1 = Point3D((radius * cos(angle1)).toFloat(), h, (radius * sin(angle1)).toFloat())
        val base2 = Point3D((radius * cos(angle2)).toFloat(), h, (radius * sin(angle2)).toFloat())
        
        faces.add(Face3D(listOf(apex, base1, base2), if(i%2==0) NeonCyan else NeonPurple))
        faces.add(Face3D(listOf(baseCenter, base2, base1), NeonGreen))
    }
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawPyramid(centerX: Float, centerY: Float, scale: Float, side: Float, height: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val s = side * 0.5f
    val h = height * 0.5f
    val apex = Point3D(0f, -h, 0f)
    val base = listOf(Point3D(-s, h, -s), Point3D(s, h, -s), Point3D(s, h, s), Point3D(-s, h, s))
    
    val faces = listOf(
        Face3D(listOf(apex, base[0], base[1]), NeonCyan),
        Face3D(listOf(apex, base[1], base[2]), NeonPurple),
        Face3D(listOf(apex, base[2], base[3]), NeonGreen),
        Face3D(listOf(apex, base[3], base[0]), NeonCyan),
        Face3D(base, NeonPurple)
    )
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawRectangularPrism(centerX: Float, centerY: Float, scale: Float, length: Float, width: Float, depth: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val l = length * 0.5f
    val w = width * 0.5f
    val d = depth * 0.5f
    val vertices = listOf(
        Point3D(-l, -w, -d), Point3D(l, -w, -d), Point3D(l, w, -d), Point3D(-l, w, -d),
        Point3D(-l, -w, d), Point3D(l, -w, d), Point3D(l, w, d), Point3D(-l, w, d)
    )
    val faces = listOf(
        Face3D(listOf(vertices[0], vertices[1], vertices[2], vertices[3]), NeonCyan),
        Face3D(listOf(vertices[4], vertices[5], vertices[6], vertices[7]), NeonPurple),
        Face3D(listOf(vertices[0], vertices[4], vertices[7], vertices[3]), NeonGreen),
        Face3D(listOf(vertices[1], vertices[5], vertices[6], vertices[2]), NeonCyan),
        Face3D(listOf(vertices[0], vertices[1], vertices[5], vertices[4]), NeonPurple),
        Face3D(listOf(vertices[3], vertices[2], vertices[6], vertices[7]), NeonGreen)
    )
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawTriangularPrism(centerX: Float, centerY: Float, scale: Float, side: Float, height: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val s = side * 0.5f
    val h = height * 0.5f
    val triHeight = s * sqrt(3f) / 2
    val frontTri = listOf(Point3D(0f, -triHeight * 0.5f, -h), Point3D(-s, triHeight * 0.5f, -h), Point3D(s, triHeight * 0.5f, -h))
    val backTri = listOf(Point3D(0f, -triHeight * 0.5f, h), Point3D(-s, triHeight * 0.5f, h), Point3D(s, triHeight * 0.5f, h))
    
    val faces = listOf(
        Face3D(frontTri, NeonCyan),
        Face3D(backTri.reversed(), NeonPurple),
        Face3D(listOf(frontTri[0], frontTri[1], backTri[1], backTri[0]), NeonGreen),
        Face3D(listOf(frontTri[1], frontTri[2], backTri[2], backTri[1]), NeonCyan),
        Face3D(listOf(frontTri[2], frontTri[0], backTri[0], backTri[2]), NeonPurple)
    )
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawTorus(centerX: Float, centerY: Float, scale: Float, majorRadius: Float, minorRadius: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val majorSegments = 24
    val minorSegments = 12
    val faces = mutableListOf<Face3D>()
    
    for (i in 0 until majorSegments) {
        val theta1 = 2 * PI * i / majorSegments
        val theta2 = 2 * PI * (i + 1) / majorSegments
        for (j in 0 until minorSegments) {
            val phi1 = 2 * PI * j / minorSegments
            val phi2 = 2 * PI * (j + 1) / minorSegments
            fun torusPoint(theta: Double, phi: Double): Point3D {
                val x = (majorRadius + minorRadius * cos(phi)) * cos(theta)
                val y = minorRadius * sin(phi)
                val z = (majorRadius + minorRadius * cos(phi)) * sin(theta)
                return Point3D(x.toFloat(), y.toFloat(), z.toFloat())
            }
            val p1 = torusPoint(theta1, phi1)
            val p2 = torusPoint(theta2, phi1)
            val p3 = torusPoint(theta2, phi2)
            val p4 = torusPoint(theta1, phi2)
            
            val color = if ((i+j)%2==0) NeonCyan else NeonPurple
            faces.add(Face3D(listOf(p1, p2, p3, p4), color))
        }
    }
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawHemisphere(centerX: Float, centerY: Float, scale: Float, radius: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val segments = 16
    val rings = 8
    val faces = mutableListOf<Face3D>()
    
    for (i in 0 until rings) {
        val theta1 = PI * i / (rings * 2)
        val theta2 = PI * (i + 1) / (rings * 2)
        for (j in 0 until segments) {
            val phi1 = 2 * PI * j / segments
            val phi2 = 2 * PI * (j + 1) / segments
            
            val p1 = Point3D((radius * sin(theta1) * cos(phi1)).toFloat(), -(radius * cos(theta1)).toFloat(), (radius * sin(theta1) * sin(phi1)).toFloat())
            val p2 = Point3D((radius * sin(theta1) * cos(phi2)).toFloat(), -(radius * cos(theta1)).toFloat(), (radius * sin(theta1) * sin(phi2)).toFloat())
            val p3 = Point3D((radius * sin(theta2) * cos(phi2)).toFloat(), -(radius * cos(theta2)).toFloat(), (radius * sin(theta2) * sin(phi2)).toFloat())
            val p4 = Point3D((radius * sin(theta2) * cos(phi1)).toFloat(), -(radius * cos(theta2)).toFloat(), (radius * sin(theta2) * sin(phi1)).toFloat())
            
            val color = if (j%2==0) NeonCyan else NeonGreen
            faces.add(Face3D(listOf(p1, p2, p3, p4), color))
        }
    }
    val baseCenter = Point3D(0f, 0f, 0f)
    for (j in 0 until segments) {
        val phi1 = 2 * PI * j / segments
        val phi2 = 2 * PI * (j + 1) / segments
        val p1 = Point3D((radius * cos(phi1)).toFloat(), 0f, (radius * sin(phi1)).toFloat())
        val p2 = Point3D((radius * cos(phi2)).toFloat(), 0f, (radius * sin(phi2)).toFloat())
        faces.add(Face3D(listOf(baseCenter, p2, p1), NeonPurple))
    }
    drawFaces(faces, centerX, centerY, scale, rotX, rotY, rotZ)
}

private fun DrawScope.drawFaces(faces: List<Face3D>, centerX: Float, centerY: Float, scale: Float, rotX: Float, rotY: Float, rotZ: Float) {
    val transformedFaces = faces.map { face ->
        val transformedVertices = face.vertices.map { v -> v.rotateX(rotX).rotateY(rotY).rotateZ(rotZ) }
        val avgZ = transformedVertices.map { it.z }.average().toFloat()
        Triple(face, transformedVertices, avgZ)
    }.sortedByDescending { it.third }
    
    for ((face, transformedVertices, _) in transformedFaces) {
        val projectedPoints = transformedVertices.map { v ->
            val projected = v.project(1.5f, 4f)
            Offset(centerX + projected.x * scale, centerY + projected.y * scale)
        }
        
        if (projectedPoints.size >= 3) {
            val v1 = transformedVertices[1].let { Point3D(it.x - transformedVertices[0].x, it.y - transformedVertices[0].y, it.z - transformedVertices[0].z) }
            val v2 = transformedVertices[2].let { Point3D(it.x - transformedVertices[0].x, it.y - transformedVertices[0].y, it.z - transformedVertices[0].z) }
            val normal = Point3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x)
            val normalLength = sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z)
            val lightDir = Point3D(0.3f, -0.5f, -1f)
            val lightLength = sqrt(lightDir.x * lightDir.x + lightDir.y * lightDir.y + lightDir.z * lightDir.z)
            val dotProduct = if (normalLength > 0 && lightLength > 0) (normal.x * lightDir.x + normal.y * lightDir.y + normal.z * lightDir.z) / (normalLength * lightLength) else 0f
            val lightIntensity = (dotProduct * 0.6f + 0.4f).coerceIn(0.2f, 1f)
            val litColor = face.baseColor.copy(
                red = (face.baseColor.red * lightIntensity).coerceIn(0f, 1f),
                green = (face.baseColor.green * lightIntensity).coerceIn(0f, 1f),
                blue = (face.baseColor.blue * lightIntensity).coerceIn(0f, 1f),
                alpha = face.baseColor.alpha
            )
            val path = Path().apply {
                moveTo(projectedPoints[0].x, projectedPoints[0].y)
                for (i in 1 until projectedPoints.size) lineTo(projectedPoints[i].x, projectedPoints[i].y)
                close()
            }
            drawPath(path, litColor, style = Fill)
            drawPath(path, NeonCyan.copy(alpha = 0.5f), style = Stroke(width = 2f))
        }
    }
}
