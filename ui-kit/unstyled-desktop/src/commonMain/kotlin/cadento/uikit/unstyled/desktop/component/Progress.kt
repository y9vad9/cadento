package cadento.uikit.unstyled.desktop.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import cadento.uikit.component.ProgressStyle
import cadento.uikit.component.ProgressVariant
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors
import kotlin.math.roundToInt

private val LinearProgressHeight = 4.dp
private val CircularProgressSize = 24.dp
private val CircularProgressStrokeWidth = 2.dp

/**
 * Unstyled Desktop implementation of [ProgressStyle].
 */
public class UnstyledDesktopProgressStyle(private val colors: UnstyledDesktopColors) : ProgressStyle {
    @Composable
    override fun render(progress: Float?, modifier: Modifier, variant: ProgressVariant) {
        if (variant == ProgressVariant.Linear) {
            val progressColor = colors.accent
            var trackWidthPx by remember { mutableStateOf(0) }

            Box(
                modifier = modifier
                    .height(LinearProgressHeight)
                    .fillMaxWidth()
                    .onSizeChanged { size -> trackWidthPx = size.width }
                    .background(colors.border, CircleShape)
            ) {
                progress?.let {
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(it).background(progressColor, CircleShape))
                } ?: run {
                    if (trackWidthPx > 0) {
                        IndeterminateLinearProgress(progressColor, trackWidthPx)
                    }
                }
            }
        } else {
            progress?.let {
                CircularProgressDeterminate(progress = it, colors = colors, modifier = modifier)
            } ?: run {
                CircularProgressIndeterminate(colors = colors, modifier = modifier)
            }
        }
    }
}

@Composable
private fun IndeterminateLinearProgress(color: Color, trackWidthPx: Int) {
    val infiniteTransition = rememberInfiniteTransition("IndeterminateLinearProgress")
    val xOffsetPercent by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "xOffset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val segmentWidth = size.width * 0.4f
        val x = xOffsetPercent * size.width
        
        drawRoundRect(
            color = color,
            topLeft = Offset(x, 0f),
            size = Size(segmentWidth, size.height),
            cornerRadius = CornerRadius(size.height / 2, size.height / 2)
        )
    }
}

@Composable
private fun CircularProgressDeterminate(progress: Float, colors: UnstyledDesktopColors, modifier: Modifier) {
    Canvas(modifier = modifier.size(CircularProgressSize)) {
        val strokeWidth = CircularProgressStrokeWidth.toPx()
        drawCircle(color = colors.border, style = Stroke(width = strokeWidth))
        drawArc(
            color = colors.accent,
            startAngle = 270f, // Start from top
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun CircularProgressIndeterminate(colors: UnstyledDesktopColors, modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition("CircularProgressIndeterminate")
    val startAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "startAngle"
    )
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 270f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "sweepAngle"
    )

    Canvas(modifier = modifier.size(CircularProgressSize)) {
        val strokeWidth = CircularProgressStrokeWidth.toPx()
        drawCircle(color = colors.border, style = Stroke(width = strokeWidth))
        drawArc(
            color = colors.accent,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
