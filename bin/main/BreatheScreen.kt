package io.github.esp_er.icebreathing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.round


const val ANIM_MS = 1540
const val BREATH_DELAY = 1200L

enum class SessionState{
    Prepare, Breathe, BreatheHold, BreatheInHold, Done;
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BreatheScreen(buttonVisible: Boolean, thisSession: SessionData, clickedBack: () -> Unit){
    var winsize by remember{ mutableStateOf(IntSize(450,580))}
    var roundNum by remember{ mutableStateOf(1)}
    val roundGoal by remember{ mutableStateOf(thisSession.numRounds)}
    val breathGoal by remember{ mutableStateOf(thisSession.numBreaths)}
    var secondsHeld by rememberSaveable{ mutableStateOf(thisSession.breathHoldTime.toMutableMap()) }
    var breathsFinished by rememberSaveable{ mutableStateOf(0) }
    var breathPaused by remember{ mutableStateOf(false)}
    var numBreaths by remember{ mutableStateOf(1)}
    var sessState by remember{ mutableStateOf(SessionState.Prepare)}
    var breathRate by remember { mutableStateOf(BreathRate.X1)}
    val animationSpeed by derivedStateOf{  breathRate.toMs(ANIM_MS)  }
    val speedText by derivedStateOf { "Speed: ${breathRate.str()}" }
    var finishClicked by remember{ mutableStateOf(false)}
    fun finishBreathing() {
        finishClicked = true
        //numBreaths = 1
        //transitionBreathing(SessionState.Breathe)
    }

    fun pauseClicked() { breathPaused = true }
    fun continueClicked() { breathPaused = false }
    fun increaseSpeed() { breathRate = breathRate.Increase() }
    fun decreaseSpeed() { breathRate = breathRate.Decrease() }

    fun transitionBreathing(s: SessionState) {
        sessState = if (roundNum > roundGoal) {
            secondsHeld = secondsHeld.filter{it.key < roundNum}.toMutableMap()
            SessionState.Done
        }
        else
            when (s) {
                SessionState.Prepare -> SessionState.Breathe
                SessionState.Breathe -> SessionState.BreatheHold
                SessionState.BreatheHold -> SessionState.BreatheInHold
                SessionState.BreatheInHold -> SessionState.Prepare
                else -> SessionState.Done
            }
    }
    //Called before transitionBreathing when coming from retention
    fun transitionFromHold(secsHeld: Int){
        secondsHeld[roundNum] = secsHeld
        transitionBreathing(SessionState.BreatheHold)
    }


    fun goToStart() {
        transitionBreathing(SessionState.Done)
        clickedBack()
    }


    fun incrementBreath() {
        if (numBreaths < breathGoal && !finishClicked) {
            numBreaths += 1
            breathsFinished += 1
        } else {
            breathsFinished += 1
            numBreaths = 1
            finishClicked = false
            transitionBreathing(SessionState.Breathe)
        }
    }
    fun incrementRound(s: SessionState) {
        if(roundNum <= roundGoal) roundNum += 1
        transitionBreathing(s)
    }


    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.background(color = Color.Black) //Background rendering issue when using graalvm? this fixes it for some reason
            .offset(y=0.5.dp)

    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
            .onGloballyPositioned { winsize = it.size }
        ) {
            if(sessState != SessionState.Done)
                RoundText(roundNum, mod = Modifier.align(Alignment.TopStart).padding(horizontal = 8.dp, vertical = 4.dp))
            if (sessState == SessionState.Prepare) {
                TransitionScreen(winsize, roundNum, breathGoal, ::transitionBreathing)
            } else if (sessState == SessionState.BreatheHold) {
                if(thisSession.retentionStyle == RetentionType.Preselect) {
                    BreathHoldScreen(
                        winsize,
                        thisSession.breathHoldTime.getOrElse(roundNum, { 1 }),
                        ::transitionFromHold,
                        ::goToStart
                )
                }
                else {
                    BreathRetention(
                        changeRadius = winsize,
                        onFinishRetention = { secs -> transitionFromHold(secs) },
                        finishClicked = false,
                        clickedBack = ::goToStart,
                        animSpeed = animationSpeed
                    )
                }

            } else if (sessState == SessionState.BreatheInHold)
                BreathInScreen(
                    winsize,
                    transitionScreen = ::incrementRound,
                    clickedBack = clickedBack
                )
            else if (sessState == SessionState.Done) {
                FinishScreen(thisSession.copy(breathHoldTime = secondsHeld), clickedBack, breathsFinished = breathsFinished)
            } else {
                breatheCanvas(
                    winsize, currBreaths = numBreaths, totalBreaths = breathGoal,
                    onFinishBreath = ::incrementBreath,
                    breathPaused = breathPaused,
                    finishClicked = finishClicked,
                    animSpeed = animationSpeed
                )

                //Overlay button alignments
                val pauseAlign = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                val speedTextAlign = Modifier.align(Alignment.TopCenter)
                val leftMod = Modifier.align(Alignment.CenterStart)
                    .padding(4.dp)
                val rightMod = Modifier.align(Alignment.CenterEnd)
                    .padding(4.dp)
                val backModifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)

                RateButton(clickCallback = ::decreaseSpeed, mod = leftMod, size = 18.dp)
                if (breathRate != BreathRate.X1)
                    Text(
                        speedText,
                        modifier = speedTextAlign,
                        style = TextStyle(fontSize = 18.sp, color = Color.LightGray)
                    )
                RateButton(isLeft = false, clickCallback = ::increaseSpeed, mod = rightMod, size = 18.dp)
                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    if (breathPaused) {
                        ContButton(::continueClicked, pauseAlign, 24.dp)
                    } else {
                        PauseButton(::pauseClicked, pauseAlign, 24.dp)
                    }

                }
                BackButton(
                    backClicked = ::goToStart,
                    mod = backModifier,
                    size = 24.dp
                )

                FinishBreatheButton(::finishBreathing, Modifier.align(Alignment.BottomEnd).padding(8.dp), 24.dp)
            }
        }
    }
}


