package patriker.breathing.iceman

import androidx.compose.animation.animateColor
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow


//val secondColorTemp = Color(50, 220, 211)
//val secondColorTemp = Color(125, 175,156)

val secondColorTemp = Color(143, 180, 255)

//rgb(150, 128, 255)

val mainColorTemp =
    Color(112, 197, 255)
//val backColor = Color(43, 48, 59)
val backColor = Color(29, 43, 125)


fun main() = application {
    val audio = AudioPlay()
    System.setProperty("skiko.renderApi", "OPENGL") //(Explicit) Not really necessary ("SOFTWARE" is too slow)
    val state = rememberWindowState(width = 450.dp, height = 580.dp, position = WindowPosition(1400.dp, 200.dp))
    Window(onCloseRequest = ::exitApplication,
        state,
        transparent = true,
        undecorated = true,
        alwaysOnTop = true
    ) {
        App(audio)
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App(audio: AudioPlay) {
    //val backColor = Color(43, 48, 59)
    //val backColor = Color.White

    var transparency by remember{ mutableStateOf(1f) }

    var buttonVisible by remember { mutableStateOf(false)}
    val composableScope = rememberCoroutineScope()
    var thisSession = SessionData(30, 6, emptyMap())
    fun showButtons() {
        composableScope.launch {
            if (!buttonVisible) {
                buttonVisible = true
                delay(3000)
                buttonVisible = false
            }
        }
    }
    fun setTransparent(t: Boolean = false) {
        if(t) transparency = 0.2f
        else transparency = 1f
    }
    MaterialTheme {
        //TODO have the surface in BreatheScreen instead???
        Surface(color = backColor.copy(alpha=transparency),
            modifier = Modifier.fillMaxSize(1f)
                .pointerMoveFilter (onMove = { showButtons(); false })){

            //todo: at finished we transition to a breath hold screen // different animation
            fun clickedStartBreathing(s: SessionData) {
                thisSession = s
                AppState.screenState(ScreenType.Breathe)
            }

            if(AppState.screenState() == ScreenType.Start) {
                StartScreen(::clickedStartBreathing)
            }
            else{
                BreatheScreen(buttonVisible, thisSession, audio, ::setTransparent)
            }
        }
    }
}



enum class BreatheState{
    Full, Empty, Paused
}
enum class LeafPos{
    TopLeft, TopRight, BottomLeft, BottomRight, Top, Center
}

@Composable
fun breatheCanvas(
    changeRadius: IntSize,
    color: Color = MaterialTheme.colors.background,
    currBreaths: Int,
    totalBreaths: Int,
    onFinishBreath: () -> Unit,
    breathPaused: Boolean,
    animSpeed: Int,
    playSound: (SoundType) -> Unit
) {
    val edgeLeaves = LeafPos.values().filterNot{ it == LeafPos.Center } //preallocate the values for performance
    var currRad by remember { mutableStateOf(0f)}
    var currRadDelayed by remember { mutableStateOf(0f)}
    var radiusEnd by remember { mutableStateOf(minOf(changeRadius.height, changeRadius.width).toFloat())}
    var minRadius by remember { mutableStateOf( minOf(changeRadius.height, changeRadius.width) / 100f) }
    var breatheState by remember { mutableStateOf(BreatheState.Empty) }

    /*val animSpeed by remember { derivedStateOf {
        when(currBreaths){
            totalBreaths -> (animationSpeed * 1.1).toInt()
            else -> animationSpeed
        } }}*/

    var finalBreath by remember { mutableStateOf(false) }

    //TODO: Try to sync each animation to breathRad instead of having
    //to replicate each one manually!!


    fun recalcRadius() {
        val r = minOf(changeRadius.width, changeRadius.height)
        radiusEnd = r / 5.6f
        minRadius = r / 100f
    }
    //val remainingBreaths by remember { mutableStateOf(totalBreaths) }

    var prevState by remember { mutableStateOf(BreatheState.Paused) }

    LaunchedEffect(currBreaths) {
        recalcRadius()
        if(totalBreaths == currBreaths) finalBreath = true
        breatheState = BreatheState.Full
        playSound(SoundType.BreatheIn)
    }
    LaunchedEffect(breathPaused){
        if(breathPaused){
            prevState = breatheState
            breatheState = BreatheState.Paused
        }
        else if(prevState != BreatheState.Paused) {
            recalcRadius()
            breatheState = prevState
        }
    }


    val transit = updateTransition(targetState = breatheState)

    val animColor by transit.animateColor({
           when(breatheState) {
               BreatheState.Paused -> tween(100)
               else -> if(finalBreath && breatheState == BreatheState.Empty)
                            tween(animSpeed, 1000)
                        else tween(animSpeed)
           }
        } ) { state ->
        when (state) {
            BreatheState.Full -> mainColorTemp
            BreatheState.Empty -> secondColorTemp
            BreatheState.Paused -> Color.DarkGray
        }
    }

    val fontColor by transit.animateColor({ tween(animSpeed) }) { state ->
        when (state) {
            BreatheState.Full -> Color.White
            BreatheState.Empty -> Color.LightGray
            BreatheState.Paused -> Color.LightGray
        }
    }


    val outlineColor by transit.animateColor({
            if(breatheState == BreatheState.Empty && finalBreath)
                tween(animSpeed + 200,1200)
            else tween(animSpeed)
        }) { state ->

        when (state) {
            BreatheState.Full -> mainColorTemp
            BreatheState.Empty -> secondColorTemp
            BreatheState.Paused -> Color.DarkGray
        }
    }
    /*
    val newState = { state: BreatheState ->
        when (state) {
            BreatheState.Full -> BreatheState.Empty
            else -> BreatheState.Full
        }
    }*/

    val breathRad by animateFloatAsState(
        targetValue = when (breatheState) {
            BreatheState.Full -> radiusEnd
            BreatheState.Empty -> minRadius
            BreatheState.Paused -> currRad
        },
        animationSpec =
            if(breatheState == BreatheState.Paused)
                tween(1)
            else if(breatheState == BreatheState.Empty && finalBreath)
                tween(animSpeed + 200, 1200)
            else
                tween(animSpeed),
        visibilityThreshold = 0.5f,
        finishedListener = {
            if(breatheState == BreatheState.Full) {
                breatheState = BreatheState.Empty
                playSound(SoundType.BreatheOut)
            }
            else if(breatheState == BreatheState.Paused)
                breatheState = BreatheState.Paused
            else {
                onFinishBreath()
            }
            //breatheState = newState(breatheState)
            //if(breatheState == BreatheState.Full) remainingBreaths -= 1 //TODO: hoist state to caller
        }
    )

    val currRadUpdate by remember { derivedStateOf { breathRad } }
    LaunchedEffect(currRadUpdate){
        currRad = currRadUpdate
    }

    val breathRadDelayed by animateFloatAsState(
        targetValue = when (breatheState) {
            BreatheState.Full -> radiusEnd * .8f
            BreatheState.Empty -> minRadius - 1f
            BreatheState.Paused -> currRadDelayed
        },
        animationSpec = when (breatheState) {
            BreatheState.Full -> tween(animSpeed, delayMillis = animSpeed / 8)
            BreatheState.Empty -> { if(finalBreath)
                                        tween(animSpeed + 200 - (animSpeed / 6), 1300)
                                    else
                                        tween(animSpeed - (animSpeed / 6))
                                    }
            BreatheState.Paused -> tween(1)
        },
        visibilityThreshold = 0.5f,
        finishedListener = {}
    )

    val currRadDelayedUpd by remember { derivedStateOf { breathRadDelayed } }
    LaunchedEffect(currRadUpdate){ currRadDelayed = currRadDelayedUpd }


    fun DrawScope.drawLeaf(radius: Float, color: Color, pos: LeafPos, centerOffset: Offset, drawOutlineOnly: Boolean = false): Unit {

        val center = when (pos) {
            LeafPos.Top -> Offset(centerOffset.x, centerOffset.y - (breathRad / 1.2f) - breathRad.pow(1.28f) / 6f)
            LeafPos.TopLeft -> Offset(
                centerOffset.x - (breathRad / 2) - breathRad.pow(1.28f) / 6f,
                centerOffset.y - breathRad.pow(1.28f) / 6f
            )
            LeafPos.TopRight -> Offset(
                centerOffset.x + (breathRad / 2) + breathRad.pow(1.28f) / 6f,
                centerOffset.y - breathRad.pow(1.28f) / 6f
            )
            LeafPos.BottomLeft -> Offset(
                centerOffset.x - breathRad.pow(1.28f) / 6f,
                centerOffset.y + breathRad.pow(1.28f) / 6f
            )
            LeafPos.BottomRight -> Offset(
                centerOffset.x + breathRad.pow(1.28f) / 6f,
                centerOffset.y + breathRad.pow(1.28f) / 6f
            )
            LeafPos.Center -> Offset(centerOffset.x, centerOffset.y - (breathRad / 3))
        }

        if(!drawOutlineOnly){
            drawCircle(
                blendMode = BlendMode.Overlay,
                color = color.copy(alpha=0.8f),
                radius = radius,
                center = center
            )
        }
        if(pos == LeafPos.Center){
            drawCircle(
                color = color,
                radius = radius,
                center = center
            )
        }
        else{
            //Background Color line
            drawCircle(
                blendMode = BlendMode.Plus,
                color = outlineColor.copy(alpha=0.9f),
                style = Stroke(width = (6f * breathRad / radiusEnd)),
                radius = radius,
                center = center
            )
        }

    }



    Canvas(modifier = Modifier.offset(0.dp, 10.dp)) {
        val centerOffset = Offset(0f, 0f)
        //Inner leaf
        /*
        drawCircle(
            blendMode = BlendMode.Softlight,
            color = animColor,
            radius = breathRadDelayed,
            center = Offset(
                centerOffset.x - (breathRad / 2) - breathRad.pow(1.28f) / 6f,
                centerOffset.y - breathRad.pow(1.28f) / 6f
            )
        )*/
        for (p in edgeLeaves) {
            drawLeaf(
                radius = breathRad,
                color = animColor,
                pos = p,
                centerOffset = centerOffset
            )
        }
        for (p in edgeLeaves) {
            drawLeaf(
                radius = breathRad,
                color = animColor,
                pos = p,
                centerOffset = centerOffset,
                drawOutlineOnly = true
            )
        }
        //Center circle
        drawLeaf(
            radius = if(finalBreath) breathRadDelayed else breathRadDelayed * 0.8f,
            color = animColor,
            pos = LeafPos.Center,
            centerOffset = centerOffset
        )
    }

    fun breatheText() = when(breatheState){
        BreatheState.Paused -> "Paused"
        BreatheState.Empty -> if(finalBreath){ if(breathRad/radiusEnd < 0.99) "And\nOut" else "Fully\n  In!"}
                              else currBreaths.toString()
        else -> if(finalBreath) "Fully\n  In!" else currBreaths.toString()
    }
    fun breatheTextColor() = when(breatheState){
        BreatheState.Paused -> Color.White
        else -> if(breathRadDelayed/radiusEnd < 0.25) fontColor.copy(alpha=0f)
                else fontColor.copy(alpha=breathRadDelayed/radiusEnd)
    }

    //TODO: copy text style into shadow text style and enable the shadow
    Text(
        breatheText(),
        fontSize = if(breathPaused) radiusEnd.sp else (radiusEnd  / 2.7).sp,
        color = breatheTextColor(),
        modifier = Modifier.offset(0.dp, (10f - (breathRad / 3)).dp),
        fontWeight = FontWeight.Bold,
        //style = TextStyle(shadow = Shadow(color = Color.Black, offset = Offset(4f,4f)))
    )


}
/*
    val breathRadius by transit.animateFloat(transitionSpec ={ tween(1000)}) { state ->
        when (state) {
            BreatheState.Empty -> 10f
            BreatheState.Full -> radiusEnd
        }
    }

 */

/*
    val floatAnim by transition.animateFloat(
        initialValue = 5f,
        targetValue = radiusEnd,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
        /*Button(modifier = Modifier.offset(y = 200.dp ),
            onClick = {animationSpeed /= 2}){
            Text("faster")
        }*/

    /*
    LaunchedEffect(breathRadius){
            if (remainingBreaths > 0){
                breatheState = when (breatheState) {
                    BreatheState.Full -> BreatheState.Empty
                    BreatheState.Empty -> BreatheState.Full
                }
                remainingBreaths -= 1
                println("entered")
            }
        }
    }*/

    /*
    LaunchedEffect(key1 = remainingBreaths){
        if (remainingBreaths > 0){
            delay(2500)
            remainingBreaths -= 1
            println(remainingBreaths)
        }
    }
    val floatAnim by transition.animateFloat(
        initialValue = 5f,
        targetValue = radiusEnd,
    )
    Canvas(modifier = Modifier.padding(16.dp)) {
        val centerOffset = Offset(10f, 10f)
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = floatAnim,
            center = centerOffset,
        )
        drawCircle(
            //color = color.copy(alpha = 0.5f),
            color = Color.Black,
            radius = floatAnim / 2,
            center = centerOffset,
        )
        drawCircle(
            color = Color.Red,
            radius = floatAnim / 4,
            center = centerOffset,
        )
    }*/


*/