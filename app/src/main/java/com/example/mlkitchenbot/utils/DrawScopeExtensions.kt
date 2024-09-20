package com.example.mlkitchenbot.utils

import android.graphics.PointF
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawLandmark(landmark: PointF, color: Color, radius: Float) {
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(landmark.x, landmark.y),
    )
}

fun DrawScope.drawBounds(
    rect: Rect,
    imageWidth: Int,
    imageHeight: Int,
    color: Color,
    stroke: Float
) {
    val scaleX = size.width / imageWidth
    val scaleY = size.height / imageHeight

    val scaledRect = RectF(
        rect.left * scaleX,
        rect.top * scaleY,
        rect.right * scaleX,
        rect.bottom * scaleY
    )

    drawRect(
        color = color,
        topLeft = Offset(scaledRect.left, scaledRect.top),
        size = Size(scaledRect.width(), scaledRect.height()),
        style = Stroke(width = stroke)
    )
}


fun DrawScope.drawTriangle(points: List<PointF>, color: Color, stroke: Float) {
    if (points.size < 3) return
    drawPath(
        path = Path().apply {
            moveTo(points[0].x, points[0].y)
            lineTo(points[1].x, points[1].y)
            lineTo(points[2].x, points[2].y)
            close()
        },
        color = color,
        style = Stroke(width = stroke)
    )
}