package patriker.breathing.iceman

import androidx.compose.animation.animateColor
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import kotlin.system.exitProcess
import patriker.breathing.iceman.ui.*


val secondColorTemp = Color(143, 180, 255)

val mainColorTemp =
    Color(112, 197, 255)
val backColor = Color(29, 43, 125)
val backColorDark = Color(25, 40, 121).copy(alpha=0.7f)


val audio = AudioPlay()

fun main() =
    application {
        //System.setProperty("skiko.renderApi", "OPENGL") //(Explicit) Not really necessary ("SOFTWARE" is too slow)
        System.setProperty("skiko.vsync.enabled", "false")
        val state =
            rememberWindowState(width = 450.dp, height = 580.dp, position = WindowPosition(1400.dp, 200.dp))
        var pinWindow by remember { mutableStateOf(false) }


        /*

        LaunchedEffect(true) {
            withContext(Dispatchers.IO) {
                audio.initMusic()
            }
        }
     */

        fun minimize() {
            state.isMinimized = true
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "Iceman Breathing",
            state = state,
            transparent = true,
            undecorated = true,
            alwaysOnTop = pinWindow
        ) {

            val icon = painterResource("icebreathing_256.png")
            val density = LocalDensity.current
            SideEffect {
                window.iconImage = icon.toAwtImage(density, LayoutDirection.Ltr, Size(256f, 256f))
            }

            fun pin() {
                pinWindow = !pinWindow
            }

            App(audio, ::minimize, ::pin)
        }

    }


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowScope.App(audio: AudioPlay, minimizeApp: () -> Unit, pin: () -> Unit) {
    //val backColor = Color(43, 48, 59)
    //val backColor = Color.White

    var transparency by remember{ mutableStateOf(1f) }

    var buttonVisible by remember { mutableStateOf(false)}
    val composableScope = rememberCoroutineScope()
    var thisSession by remember{ mutableStateOf( SessionData(30, 6, emptyMap()) )}

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
    fun clickedBack(){
        //loadSettings(appConfig)
        AppState.screenState(ScreenType.Start)
    }
    IceBreathingTheme {
        Surface(shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, color = mainColorTemp),
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize(1f)
                .pointerMoveFilter (onMove = { showButtons(); false })
        ){

            //todo: at finished we transition to a breath hold screen // different animation
            fun clickedStartBreathing(s: SessionData) {
                thisSession = s
                AppState.screenState(ScreenType.Breathe)
            }


            TitleBar(minimizeApp, pin)
            if(AppState.screenState() == ScreenType.Start) {
                StartScreen(::clickedStartBreathing)
            }
            else{
                BreatheScreen(buttonVisible, thisSession, ::clickedBack, audio, ::setTransparent)
            }
        }
    }
}

@Composable
fun WindowScope.TitleBar(minimizeApp: () -> Unit, pinApp: () -> Unit) = WindowDraggableArea {
    Box(modifier = Modifier.fillMaxWidth(1f).height(24.dp).background(MaterialTheme.colors.background)) {
            var color by remember { mutableStateOf(secondColorTemp) }

            fun togglePinColor(currColor: Color): Color{
                return if(currColor.value == secondColorTemp.value)
                    Color.White
                else
                    secondColorTemp

            }

            IconButton(
                modifier = Modifier.then(Modifier.size(32.dp))
                    .align(Alignment.TopStart),
                onClick = { pinApp(); color = togglePinColor(color) },
            ) {
                Icon(
                    Icons.Outlined.PushPin, "", tint = color,
                    modifier = Modifier.size(18.dp, 18.dp)
                )
            }

            //"_" button
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd).padding(bottom = 2.dp, end = 32.dp).then(Modifier.size(32.dp)),
                onClick = { minimizeApp() },
            ) {
                Icon(
                    Icons.Outlined.Minimize, "", tint = secondColorTemp,
                    modifier = Modifier.size(24.dp, 24.dp)
                )
            }
            //"X" button
            IconButton(
                modifier = Modifier.then(Modifier.size(32.dp)).align(Alignment.TopEnd),
                onClick = { exitProcess(0) },
            ) {
                Icon(
                    Icons.Outlined.Close, "", tint = secondColorTemp,
                    modifier = Modifier.size(24.dp, 24.dp)
                )
            }
    }
}



