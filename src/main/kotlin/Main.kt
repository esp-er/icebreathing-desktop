package patriker.breathing.iceman

import androidx.compose.animation.animateColor
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import org.intellij.lang.annotations.JdkConstants


//val secondColorTemp = Color(50, 220, 211)
//val secondColorTemp = Color(125, 175,156)

val secondColorTemp = Color(143, 180, 255)

val mainColorTemp =
    Color(112, 197, 255)
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

        //Test sound with KorAU and KorIO
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
        transparency = if(t) 0.2f else 1f
    }
    MaterialTheme {
        Surface(shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, color = mainColorTemp),
            color = backColor.copy(alpha=transparency),
            modifier = Modifier.fillMaxSize(1f)
                .pointerMoveFilter (onMove = { showButtons(); false })){

            //todo: at finished we transition to a breath hold screen // different animation
            fun clickedStartBreathing(s: SessionData) {
                thisSession = s
                AppState.screenState(ScreenType.Breathe)
            }

            TitleBar()
            if(AppState.screenState() == ScreenType.Start) {
                StartScreen(::clickedStartBreathing)
            }
            else{
                BreatheScreen(buttonVisible, thisSession, audio, ::setTransparent)
            }
        }
    }
}

@Composable
fun TitleBar() {
    Row(horizontalArrangement = Arrangement.End) {
        IconButton(
            modifier = Modifier.then(Modifier.size(32.dp)),
            onClick = { exitProcess(0)},
        ){
            Icon(
                Icons.Outlined.Close, "", tint = secondColorTemp,
                modifier = Modifier.size(24.dp,24.dp))
        }
    }
}


