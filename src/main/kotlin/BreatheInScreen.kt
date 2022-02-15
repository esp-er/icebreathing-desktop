package patriker.breathing.iceman

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.concurrent.fixedRateTimer

enum class HoldState{
    Stop, Inhale, Exhale;
}

@Composable
fun BreathInScreen(winsize: IntSize, timeLeft: Int = 17, finishedHold: (SessionState) -> Unit, playSound: (SoundType) -> Unit, stopSound: () -> Unit){
    val holdTime = 16.0f //TODO: remove these magic values
    val progressDia = winsize.height / 1.5f
    var holdState by remember { mutableStateOf(HoldState.Stop) }
    val transition = updateTransition(targetState = holdState)
    var time by remember { mutableStateOf(timeLeft) }
    val timeProgress by remember { derivedStateOf {  (time - 1) / holdTime } }
    //val timeText by remember { derivedStateOf {timeLeft.secondsAsStr()}}

    LaunchedEffect(true) {
        playSound(SoundType.BreatheIn)
        holdState = HoldState.Inhale
        fixedRateTimer("timer", false, 2000, 1000) {
            if(time <= 2){
                holdState = HoldState.Exhale
            }
            if(time > 0) time--
        }
    }
    LaunchedEffect(holdState) {
        if(holdState == HoldState.Exhale)
            playSound(SoundType.BreatheOut)
    }
    LaunchedEffect(time) {
        if (time <= 0) {
            stopSound()
            delay(1000)
            finishedHold(SessionState.BreatheInHold)
        }
    }

    val countdownProgress by animateFloatAsState(
        targetValue = timeProgress,
        animationSpec = tween(1000, easing = LinearEasing)
    )


    val transitionMultiplier by transition.animateFloat(
        transitionSpec = {if(holdState == HoldState.Exhale) tween(3000) else tween(2000) }
    ) { state ->
        when(state){
            HoldState.Stop -> 0.0f
            HoldState.Inhale -> 1.0f
            HoldState.Exhale -> 0.0f
        }
    }

    val progSize = DpSize(transitionMultiplier * progressDia.dp,
        transitionMultiplier * progressDia.dp)

    Canvas(modifier = Modifier.offset(0.dp,0.dp)) {
        drawCircle(radius = (progressDia / 2.0f) * transitionMultiplier,
            color = backColor)
    }

    CircularProgressIndicator(modifier = Modifier.size(progSize),
        progress = maxOf(countdownProgress, 0f),
        strokeWidth = (progressDia / 6.0f).dp,
        color = secondColorTemp)

    Column(horizontalAlignment = Alignment.CenterHorizontally){
        if(transition.currentState == HoldState.Stop){
            Text(StrRes.inhale, style = TextStyle(fontSize = (transitionMultiplier * 28).sp, color = Color.White))
        }
        else{
            Box() {
                if(holdState == HoldState.Inhale) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(StrRes.exhalein, style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = transitionMultiplier)))
                        Text(
                            (time - 1).secondsAsStr(),
                            style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = transitionMultiplier))
                        )
                    }
                }
                else {
                    Text(
                        StrRes.exhale,
                        style = TextStyle(
                            fontSize = (28 * transitionMultiplier).sp,
                            color = Color.White.copy(alpha = minOf(1.0f, transitionMultiplier + 0.5f))
                        )
                    )
                }
            }
        }
    }
}
