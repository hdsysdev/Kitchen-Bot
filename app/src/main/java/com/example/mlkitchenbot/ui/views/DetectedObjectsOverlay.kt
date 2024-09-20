package com.example.mlkitchenbot.ui.views

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.example.mlkitchenbot.detection.DetectedObject
import com.example.mlkitchenbot.utils.drawBounds

@Composable
fun DetectedObjectsOverlay(
    detectedObjects: List<DetectedObject>,
    imageWidth: Int,
    imageHeight: Int,
    intValue: Int,
    value1: Int
) {
    val density = LocalDensity.current

    Canvas(modifier = Modifier.fillMaxSize()) {
        detectedObjects.forEach { detectedObject ->
            val boundingBox = detectedObject.boundingBox.toComposeRect()
            drawBounds(boundingBox, imageWidth, imageHeight, Color.Yellow, 10f)

            // Draw label
            val label = detectedObject.labels.firstOrNull()
            Log.d("DetectedObjectsOverlay", "label: ${label?.text}")
            if (label != null) {
                drawLabel(
                    label = "${label.text} (${(label.confidence * 100).toInt()}%)",
                    x =  boundingBox.left,
                    y = boundingBox.top,
                    density = density
                )
            }
        }
    }
}

private fun DrawScope.drawLabel(label: String, x: Float, y: Float, density: Density) {
    val textSize = with(density) { 14.sp.toPx() }
    val padding = with(density) { 4.sp.toPx() }

    // Draw label background
    drawRect(
        color = Color.Black.copy(alpha = 0.7f),
        topLeft = Offset(x, y - textSize - padding * 2),
        size = Size(label.length * textSize * 0.6f + padding * 2, textSize + padding * 2)
    )

    // Draw label text
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawText(
            label,
            x + padding,
            y - padding,
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                this.textSize = textSize
            }
        )
    }
}