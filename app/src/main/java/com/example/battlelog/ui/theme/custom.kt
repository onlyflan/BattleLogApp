package com.example.battlelog.ui.theme


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.topBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val strokeOffset = strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = strokeOffset),
                end = Offset(x = width, y = strokeOffset),
                strokeWidth = strokeWidthPx
            )
        }
    }
)
fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height
            val strokeOffset = height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = strokeOffset),
                end = Offset(x = width, y = strokeOffset),
                strokeWidth = strokeWidthPx
            )
        }
    }
)