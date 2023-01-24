package patriker.breathing.iceman

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.concurrent.fixedRateTimer


@Composable
fun BreathHoldScreen(winsize: IntSize, timeLeft: Int, transitionScreen: (SessionState) -> Unit, playSound: (SoundType) -> Unit,
                     clickedBack : () -> Unit){
    var time by remember { mutableStateOf(timeLeft) }
    var paused by rememberSaveable { mutableStateOf(false) }

    val timeProgress by remember { derivedStateOf {  (timeLeft - time) / timeLeft.toFloat() } }
    val countdownProgress by animateFloatAsState(
        targetValue = timeProgress,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    val progressDia = minOf(winsize.height, winsize.width)
    val progSize = DpSize(progressDia.dp * 0.8f, progressDia.dp * 0.8f)

    val lineDiam = DpSize(progressDia.dp * 0.73f, progressDia.dp * 0.73f)

    fun togglePause(){
        paused = !paused
    }


    fun finishClicked(){
        time = 0
        transitionScreen(SessionState.BreatheHold)
    }


    LaunchedEffect(true) {
        //playSound(SoundType.Triangle)
        fixedRateTimer("timer", false, 0, 1000) {
            if(!paused)
                time--
        }
    }
    LaunchedEffect(time){
        if (time <= 0) transitionScreen(SessionState.BreatheHold)
    }

    Canvas(modifier = Modifier.offset(0.dp, 0.dp)) {
        drawCircle(
            radius = minOf(lineDiam.height.toPx(), lineDiam.width.toPx()) / 2f,
            color = mainColorTemp.copy(alpha=0.9f),
            style = Stroke(width = (progressDia / 20.0f))
        )
    }

    CircularProgressIndicator(
        modifier = Modifier.size(progSize),
        progress = maxOf(countdownProgress, 0f),
        strokeWidth = (progressDia / 15.0f).dp,
        color = Color.White.copy(alpha=0.98f)
    )



    Box(contentAlignment =  Alignment.Center, modifier = Modifier.fillMaxSize()){
        /*RoundText(
            round = roundNum,
            mod = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopStart)
        )*/

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                StrRes.holdbreath,
                style = TextStyle(fontSize = 28.sp, color = Color.White)
            )
            Text(time.secondsAsStr(),
                style = TextStyle(fontSize = 32.sp, color = Color.White)
            )
            if(paused) {
                Text(
                    StrRes.paused,
                    style = TextStyle(fontSize = 32.sp, color = Color.White)
                )
            }
        }

        BackButton(
            backClicked = clickedBack,
            mod = Modifier.align(Alignment.BottomStart)
                .padding(8.dp),
            size = 24.dp
        )
        PauseButton(
            pauseClicked =  { togglePause() } ,
            mod = Modifier.align(Alignment.BottomCenter)
                .padding(8.dp),
            sz = 24.dp
        )

        FinishBreatheButton(
            finishClicked = { finishClicked() },
            mod = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            size = 32.dp
        )
    }
}
