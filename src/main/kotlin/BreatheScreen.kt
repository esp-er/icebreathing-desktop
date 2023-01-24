package patriker.breathing.iceman

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


const val ANIM_MS = 1540
const val BREATH_DELAY = 1200L

enum class SessionState{
    Prepare, Breathe, BreatheHold, BreatheInHold, Done;
}

@Composable
fun BreatheScreen(buttonVisible: Boolean, thisSession: SessionData, clickedBack: () -> Unit, audio: AudioPlay, setTransparent: (Boolean) -> Unit){
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
    var finishClicked by remember{ mutableStateOf(false)}


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

        fun goToStart(){
            transitionBreathing(SessionState.Done)
            clickedBack()
        }
        fun finishBreathing() {
            finishClicked = true
            //numBreaths = 1
            //transitionBreathing(SessionState.Breathe)
        }


        fun incrementBreath() {
            if(numBreaths < breathGoal && !finishClicked){
                numBreaths += 1
            }
            else{
                numBreaths = 1
                finishClicked = false
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
                GlobalScope.launch {
                    audio.play(x, BREATH_DELAY)
                }
            }
            /*else if(x == SoundType.Triangle){
                audio.stopSounds()
                GlobalScope.launch {
                    audio.play(x)
                    when (roundNum) { //TODO: fix stopping at new round
                        1 -> audio.playMusic(1000L, "ambient")
                        2 -> audio.playMusic(1000L, "fluid")
                        3 -> audio.playMusic(1000L, "ambient")
                        else -> audio.playMusic(1000L, "fluid")
                        //4 -> audio.playMusic(500L, "namaste") //TODO:namaste file seems broken
                    }
                }
            }*/
            else { // Play when breathing
                GlobalScope.launch {
                    audio.play(x)
                }
            }
        }
        fun stopSound() {
            audio.stopSounds()
        }

        if(sessState == SessionState.Prepare){
            setTransparent(false)
            TransitionScreen(winsize, roundNum, breathGoal, ::transitionBreathing)
        }
        else if(sessState == SessionState.BreatheHold) {
            setTransparent(true)
            BreathHoldScreen(winsize, thisSession.breathHoldTime.getOrElse(roundNum, { 1 }),
                ::transitionBreathing, ::playSound, ::goToStart)
        }
        else if(sessState == SessionState.BreatheInHold)
            BreathInScreen(winsize, finishedHold = ::incrementRound,
                           playSound = ::playSound,
                           stopSound = ::stopSound,
                           transitionScreen = ::transitionBreathing,
                            clickedBack = clickedBack)
        else if(sessState == SessionState.Done) {
            setTransparent(false)
            FinishScreen(thisSession, clickedBack)
        }
        else{

            breatheCanvas(winsize, currBreaths = numBreaths, totalBreaths = breathGoal,
                onFinishBreath = ::incrementBreath,
                breathPaused = breathPaused,
                finishClicked = finishClicked,
                animSpeed = animationSpeed,
                playSound = ::playSound)

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

            if(buttonVisible) {
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
            }
            BackButton(backClicked = ::goToStart,
                mod = backModifier,
                size = 24.dp )

            FinishBreatheButton(::finishBreathing, Modifier.align(Alignment.BottomEnd).padding(8.dp), 24.dp)
        }
    }
}


