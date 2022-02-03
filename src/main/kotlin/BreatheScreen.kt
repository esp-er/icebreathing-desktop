package patriker.breathing.iceman

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess


const val ANIM_MS = 1500
const val BREATH_DELAY = 1200L
val buttonDp = 32.dp

private enum class PrepareState{
    Start,End
}

@Composable
@Preview
fun PrepareScreen(winsize: IntSize, roundNum: Int, breathGoal: Int, finishedPrep: (SessionState) -> Unit){
    var prepState by remember { mutableStateOf(PrepareState.Start)}
    val transition = updateTransition(targetState = prepState)

    LaunchedEffect(true){
        prepState = PrepareState.End
    }
    LaunchedEffect(transition.currentState){
        if (transition.currentState == PrepareState.End)
            finishedPrep(SessionState.Prepare)
    }
    val textOpacity by transition.animateFloat(
        transitionSpec ={ tween(3000,1000, LinearEasing)}
    ) { state ->
        when(state){
            PrepareState.Start -> 1.0f
            PrepareState.End -> 0.0f
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text("Round $roundNum", style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = textOpacity)))
        Text("Get Ready!", style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = textOpacity)))
        Spacer(modifier = Modifier.height(30.dp))
        Text("$breathGoal Breaths", style = TextStyle(fontSize = 16.sp, color = Color.White.copy(alpha = textOpacity)))
    }
}

enum class HoldState{
    Stop, Inhale, Exhale;
}
@Composable
fun BreathInScreen(winsize: IntSize, timeLeft: Int = 17,  finishedHold: (SessionState) -> Unit, playSound: (SoundType) -> Unit, stopSound: () -> Unit){
    val holdTime = 16.0f //TODO: remove these magic values
    val progressDia = winsize.height / 1.5f
    var holdState by remember { mutableStateOf(HoldState.Stop)}
    val transition = updateTransition(targetState = holdState)
    var time by remember { mutableStateOf(timeLeft)}
    val timeProgress by remember { derivedStateOf {  (time - 1) / holdTime }}
    //val timeText by remember { derivedStateOf {timeLeft.secondsAsStr()}}

    LaunchedEffect(true) {
        stopSound()
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
    LaunchedEffect(time){
        if (time <= 0) {
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
            Text("Inhale!", style = TextStyle(fontSize = (transitionMultiplier * 28).sp, color = Color.White))
        }
        else{
            Box() {
                if(holdState == HoldState.Inhale) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Exhale In:", style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = transitionMultiplier)))
                        Text(
                            (time - 1).secondsAsStr(),
                            style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = transitionMultiplier))
                        )
                    }
                }
                else {
                    Text(
                        "Breath Out",
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


enum class SessionState{
    Prepare, Breathe, BreatheHold, BreatheInHold, Done;
}



@Composable
fun BreatheScreen(buttonVisible: Boolean, thisSession: SessionData, audio: AudioPlay, setTransparent: (Boolean) -> Unit){
    var winsize by remember{ mutableStateOf(IntSize(400,400))}
    var roundNum by remember{ mutableStateOf(1)}
    val roundGoal by remember{ mutableStateOf(thisSession.numRounds)}
    val breathGoal by remember{ mutableStateOf(thisSession.numBreaths)}
    var breathPaused by remember{ mutableStateOf(false)}
    var numBreaths by remember{ mutableStateOf(1)}
    var sessState by remember{ mutableStateOf(SessionState.Prepare)}
    var breathRate by remember { mutableStateOf(BreathRate.X1)}
    val animationSpeed by derivedStateOf{  breathRate.toMs(ANIM_MS)  }
    val speedText by derivedStateOf { "Speed: ${breathRate.str()}" }


    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.onGloballyPositioned { coords ->
            winsize = coords.size }
    ) {

        fun transitionBreathing(s: SessionState){
            sessState = if(roundNum > roundGoal)
                SessionState.Done
            else
                when(s){
                    SessionState.Prepare -> SessionState.Breathe
                    SessionState.Breathe -> SessionState.BreatheHold
                    SessionState.BreatheHold -> SessionState.BreatheInHold
                    SessionState.BreatheInHold -> SessionState.Prepare
                    else -> SessionState.Done
                }
        }


        fun incrementBreath() {
            if(numBreaths < breathGoal){
                numBreaths += 1
            }
            else{
                numBreaths = 1
                transitionBreathing(SessionState.Breathe)
            }
        }

        fun incrementRound(s: SessionState) {
            if(roundNum <= roundGoal) roundNum += 1
            transitionBreathing(s)
        }

        fun pauseClicked()   { breathPaused = true }
        fun continueClicked(){ breathPaused = false }
        fun increaseSpeed() { breathRate = breathRate.Increase()}
        fun decreaseSpeed() { breathRate = breathRate.Decrease()}

        fun playSound(x: SoundType) {
            if(sessState == SessionState.Breathe && x == SoundType.BreatheOut && numBreaths == breathGoal) {
                audio.play(x, BREATH_DELAY)
            }
            else if(x == SoundType.Triangle){
                audio.play(x)
                audio.playMusic()
            }
            else {
                audio.play(x)
            }
        }
        fun stopSound() {
            audio.stopSounds()
        }

        if(sessState == SessionState.Prepare){
            setTransparent(false)
            PrepareScreen(winsize, roundNum, breathGoal, ::transitionBreathing)
        }
        else if(sessState == SessionState.BreatheHold) {
            setTransparent(true)
            BreathHoldScreen(winsize, thisSession.breathHoldTime.getOrElse(roundNum, { 1 }),
                ::transitionBreathing, ::playSound)
        }
        else if(sessState == SessionState.BreatheInHold)
            BreathInScreen(winsize, finishedHold = ::incrementRound, playSound = ::playSound, stopSound = ::stopSound)
        else if(sessState == SessionState.Done) {
            setTransparent(false)
            FinishScreen()
        }
        else{

            breatheCanvas(winsize, currBreaths = numBreaths, totalBreaths = breathGoal,
                onFinishBreath = ::incrementBreath,
                breathPaused = breathPaused,
                animSpeed = animationSpeed,
                playSound = ::playSound)

            //Overlay button alignments
            val pauseAlign = Modifier.align(Alignment.BottomCenter)
            val soundAlign = Modifier.align(Alignment.BottomStart)
            val speedTextAlign = Modifier.align(Alignment.TopCenter)
            val leftAlign = Modifier.align(Alignment.CenterStart)
            val rightAlign = Modifier.align(Alignment.CenterEnd)
            if(buttonVisible) {
                RateButton(clickCallback = ::decreaseSpeed, mod = leftAlign)
                if (breathRate != BreathRate.X1)
                    Text(
                        speedText,
                        modifier = speedTextAlign,
                        style = TextStyle(fontSize = 18.sp, color = Color.LightGray)
                    )
                RateButton(isLeft = false, clickCallback = ::increaseSpeed, mod = rightAlign)
                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    if (breathPaused) {
                        ContButton(::continueClicked, pauseAlign, 24.dp)
                    } else {
                        PauseButton(::pauseClicked, pauseAlign, 24.dp)
                    }

                    FinishBreatheButton({}, pauseAlign, 24.dp)
                }
                SoundToggleButton({b -> println(b)}, soundAlign, 24.dp, true)
            }
        }
    }
}

@Composable
fun FinishBreatheButton(finishClicked: () -> Unit, mod: Modifier, size: Dp){
    IconButton(
        onClick = {finishClicked()},
        modifier= mod,
    ) {
        Icon(Icons.Outlined.CheckCircle, contentDescription = "Finish Breathing & Inhale", tint = Color.White,
        modifier = Modifier.size(size,size))
    }
}

@Composable
fun SoundToggleButton(buttonClicked: (Boolean) -> Unit, mod: Modifier, size: Dp, soundEnabled: Boolean){
    val enabled by remember {derivedStateOf { soundEnabled}}
    IconButton(
        onClick = { buttonClicked(enabled)},
        modifier= mod,
    ) {
        if(enabled)
            Icon(Icons.Outlined.VolumeUp, contentDescription = "Toggle Sound", tint = Color.White,
                modifier = Modifier.size(size,size))
        else
            Icon(Icons.Outlined.VolumeOff, contentDescription = "Toggle Sound", tint = Color.White,
                modifier = Modifier.size(size,size))
    }
}
@Composable
fun PauseButton(pauseClicked: () -> Unit, mod: Modifier, sz: Dp) {
    IconButton(
        onClick = { pauseClicked() },
        modifier = mod
    ){
        Icon(Icons.Outlined.Pause, "", tint = Color.White,
        modifier = Modifier.size(sz,sz))
    }
}

@Composable
fun ContButton(contClicked: () -> Unit, mod: Modifier, sz: Dp) {
    IconButton(
        onClick = { contClicked() },
        modifier = mod
    ){
        Icon(Icons.Outlined.PlayArrow, "", tint = Color.White,
        modifier = Modifier.size(sz,sz))
    }
}
@Composable
@Preview
fun RateButton(isLeft: Boolean = true, clickCallback: () -> Unit, mod: Modifier) {
    IconButton(
        onClick = { clickCallback() },
        modifier = mod
    ){
        if(isLeft)
            Icon(Icons.Filled.FastRewind, "Decrease Speed",
                tint = Color.White,
                modifier = Modifier.size(buttonDp, buttonDp))
        else
            Icon(Icons.Filled.FastForward, "Increase Speed",
                tint = Color.White,
                modifier = Modifier.size(buttonDp, buttonDp))
    }
}
