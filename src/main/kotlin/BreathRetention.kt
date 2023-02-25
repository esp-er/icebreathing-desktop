package io.github.esp_er.icebreathing
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow


fun LeafPos.next(): LeafPos{
    return when(this){
        LeafPos.Top -> LeafPos.TopRight
        LeafPos.TopRight -> LeafPos.BottomRight
        LeafPos.BottomRight -> LeafPos.BottomLeft
        LeafPos.BottomLeft -> LeafPos.TopLeft
        LeafPos.TopLeft -> LeafPos.Top
        else -> LeafPos.Top
    }
}


@Composable
fun BreathRetention(
    changeRadius: IntSize,
    onFinishRetention: (Int) -> Unit,
    finishClicked: Boolean,
    clickedBack: () -> Unit,
    animSpeed: Int
) {
    val edgeLeaves = LeafPos.values().filterNot{ it == LeafPos.Center } //preallocate the values for performance
    var currRad by remember { mutableStateOf(0f) }
    var currRadDelayed by remember { mutableStateOf(0f) }
    var radiusEnd by remember { mutableStateOf(minOf(changeRadius.height, changeRadius.width).toFloat() / 5.6f) }
    var minRadius by remember { mutableStateOf( minOf(changeRadius.height, changeRadius.width) / 100f) }
    var breatheState by remember { mutableStateOf(BreatheState.Empty) }

    var secondsHeld by remember { mutableStateOf(0) }

    LaunchedEffect(true) {
        fixedRateTimer("timer", false, 0, 1000) {
            secondsHeld++
        }
    }
    var leaf1 by remember { mutableStateOf(LeafPos.Top) }
    var leaf2 by remember { mutableStateOf(LeafPos.TopRight) }
    var leaf3 by remember { mutableStateOf(LeafPos.BottomRight) }
    var leaf4 by remember { mutableStateOf(LeafPos.BottomLeft) }
    var leaf5 by remember { mutableStateOf(LeafPos.TopLeft) }
    LaunchedEffect(true){
        leaf1 = leaf1.next()
        leaf2 = leaf2.next()
        leaf3 = leaf3.next()
        leaf4 = leaf4.next()
        leaf5 = leaf5.next()
    }

    fun recalcRadius() {
        val r = minOf(changeRadius.width, changeRadius.height)
        radiusEnd = r / 5.6f
        minRadius = r / 100f
    }

    LaunchedEffect(changeRadius){
        recalcRadius()
    }

    var prevState by remember { mutableStateOf(BreatheState.Paused) }


    val transit = updateTransition(targetState = breatheState)

    val animColor = mainColorTemp
    val outlineColor = mainColorTemp


    val breathRad = radiusEnd
    fun leafCenterPos(centerOffset: Offset,  leaf: LeafPos): Offset{
        return when(leaf) {
            LeafPos.Top -> Offset(
                centerOffset.x,
                centerOffset.y - (breathRad / 1.25f) - breathRad.pow(1.25f) / 6f
            )
            LeafPos.TopLeft -> Offset(
                centerOffset.x - (breathRad / 2) - breathRad.pow(1.25f) / 6f,
                centerOffset.y - breathRad.pow(1.25f) / 6f
            )
            LeafPos.TopRight -> Offset(
                centerOffset.x + (breathRad / 2) + breathRad.pow(1.25f) / 6f,
                centerOffset.y - breathRad.pow(1.25f) / 6f
            )
            LeafPos.BottomLeft -> Offset(
                centerOffset.x - breathRad.pow(1.25f) / 6f,
                centerOffset.y + breathRad.pow(1.25f) / 6f
            )
            LeafPos.BottomRight -> Offset(
                centerOffset.x + breathRad.pow(1.25f) / 6f,
                centerOffset.y + breathRad.pow(1.25f) / 6f
            )
            LeafPos.Center -> Offset(centerOffset.x, centerOffset.y - (breathRad / 3))
        }

    }

    fun animTargetValue(leaf: LeafPos): Offset {
        return when (leaf) {
            LeafPos.Top -> leafCenterPos(Offset(0f, 0f), LeafPos.Top.next())
            LeafPos.TopRight -> leafCenterPos(Offset(0f, 0f), LeafPos.TopRight.next())
            LeafPos.BottomRight -> leafCenterPos(Offset(0f, 0f), LeafPos.BottomRight.next())
            LeafPos.BottomLeft -> leafCenterPos(Offset(0f, 0f), LeafPos.BottomLeft.next())
            LeafPos.TopLeft -> leafCenterPos(Offset(0f, 0f), LeafPos.TopLeft.next())
            else -> leafCenterPos(Offset(0f, 0f), LeafPos.Top.next())
        }
    }

    val animateLeaf1 by animateOffsetAsState(
        targetValue = animTargetValue(leaf1),
        animationSpec = tween(2000),
        finishedListener = {
            leaf1 = leaf1.next()
        }
    )

    val animateLeaf2 by animateOffsetAsState(
        targetValue = animTargetValue(leaf2),
        animationSpec = tween(2000),
        finishedListener = {
            leaf2 = leaf2.next()
        }
    )
    val animateLeaf3 by animateOffsetAsState(
        targetValue = animTargetValue(leaf3),
        animationSpec = tween(2000),
        finishedListener = {
            leaf3 = leaf3.next()
        }
    )
    val animateLeaf4 by animateOffsetAsState(
        targetValue = animTargetValue(leaf4),
        animationSpec = tween(2000),
        finishedListener = {
            leaf4 = leaf4.next()
        }
    )
    val animateLeaf5 by animateOffsetAsState(
        targetValue = animTargetValue(leaf5),
        animationSpec = tween(2000),
        finishedListener = {
            leaf5 = leaf5.next()
        }
    )

    val currRadUpdate by remember { derivedStateOf { breathRad } }
    LaunchedEffect(currRadUpdate){
        currRad = currRadUpdate
    }


    val breathRadDelayed = breathRad

    val currRadDelayedUpd by remember { derivedStateOf { breathRadDelayed } }
    LaunchedEffect(currRadUpdate){ currRadDelayed = currRadDelayedUpd }


    fun DrawScope.drawCenterLeaf(radius: Float, color: Color, pos: LeafPos, centerOffset: Offset, drawOutlineOnly: Boolean = false): Unit {

        val center = Offset(centerOffset.x, centerOffset.y - (breathRad / 3))

        drawCircle(
            color = color,
            radius = radius,
            center = center
        )


    }

    fun DrawScope.drawAnimatedLeaf(radius: Float, color: Color, leafOffset: Offset,  drawOutlineOnly: Boolean = false): Unit {

        val center = leafOffset

        if(!drawOutlineOnly){
            drawCircle(
                blendMode = BlendMode.Overlay,
                color = color.copy(alpha=0.8f),
                radius = radius,
                center = center
            )
        }
        //Background Color line
        drawCircle(
            blendMode = BlendMode.Plus,
            color = outlineColor.copy(alpha=0.9f),
            style = Stroke(width = (6f * breathRad / radiusEnd)),
            radius = radius,
            center = center
        )
    }

    fun breatheText() = secondsHeld.secondsAsStr()

    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxSize(1f)
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { onFinishRetention(secondsHeld) }
            )
        }){

        Text("Double-click to Finish", modifier = Modifier
            .align(Alignment.Center)
            .offset(0.dp, -(changeRadius.height / 2.25 ).dp))

        Canvas(modifier = Modifier.offset(0.dp, 0.dp)
        ) {
            val centerOffset = Offset(0f, 0f)

            /* Inner Leaf
        drawCircle(
            blendMode = BlendMode.Softlight,
            color = animColor,
            radius = breathRadDelayed,
            center = Offset(
                centerOffset.x - (breathRad / 2) - breathRad.pow(1.28f) / 6f,
                centerOffset.y - breathRad.pow(1.28f) / 6f
            )
        )*/
            val leaves = listOf(animateLeaf1,animateLeaf2,animateLeaf3,animateLeaf4,animateLeaf5)
            for (offset in leaves) {
                drawAnimatedLeaf(
                    radius = breathRad,
                    color = animColor,
                    leafOffset = offset,
                )
            }
            for (offset in leaves) {
                drawAnimatedLeaf(
                    radius = breathRad,
                    color = animColor,
                    leafOffset = offset,
                    drawOutlineOnly = true
                )
            }
            //Center circle
            drawCenterLeaf(
                radius = breathRadDelayed,
                color = animColor,
                pos = LeafPos.Center,
                centerOffset = centerOffset
            )

            val numberSize =
                if (breatheState == BreatheState.Paused) 200f
                else if (breathRad / radiusEnd > 0.3f && breathRad > minRadius) (breathRad / radiusEnd) * 120f
                else 1f

            val numberAlpha =
                if (breatheState == BreatheState.Paused) 255 else ((breathRad / radiusEnd) * 255).toInt()
            /*drawContext.canvas.nativeCanvas.apply {
                drawText(
                    breatheText(),
                    size.width / 2 - 5f,
                    size.height / 2 - breathRad / 3 + numberSize / 3,
                    android.graphics.Paint().apply {
                        textSize = numberSize
                        textAlign = android.graphics.Paint.Align.CENTER
                        this.setColor(android.graphics.Color.WHITE)
                        alpha = numberAlpha
                        this.setShadowLayer(4.0f, 5.0f, 8.0f, android.graphics.Color.DKGRAY)
                    }
                )
            }*/
        }
        /*RoundText(
            round = roundNum,
            mod = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopStart)
        )*/


        fun breatheTextSize(): TextUnit {
            return (radiusEnd  / 2.0).sp
        }

        Text(
            breatheText(),
            fontSize = breatheTextSize(),
            color = Color.White,
            modifier = Modifier.offset(-2.dp, -((breathRad / 3)).dp),
            fontWeight = FontWeight.W500,
            style = TextStyle(shadow = Shadow(color = Color.DarkGray, offset = Offset(3f,2f), blurRadius = 1.5f))
        )

        BackButton(
            backClicked = { clickedBack() },
            mod = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            size = 32.dp
        )

        FinishBreatheButton(
            finishClicked = { onFinishRetention(secondsHeld) },
            mod = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            size = 32.dp
        )
    }

}
