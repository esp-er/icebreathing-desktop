package io.github.esp_er.icebreathing

import androidx.compose.animation.animateColor
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.*
import kotlin.system.exitProcess
import io.github.esp_er.icebreathing.ui.*


val secondColorTemp = Color(143, 180, 255)

val mainColorTemp =
    Color(112, 197, 255)
val backColor = Color(29, 43, 125)
val backColorDark = Color(25, 40, 121).copy(alpha=0.7f)


//val audio = AudioPlay()

fun main() =
    application {
        //System.setProperty("skiko.renderApi", "OPENGL") //(Explicit) Not really necessary ("SOFTWARE" is too slow)
        System.setProperty("skiko.vsync.enabled", "false")
        val state =
            rememberWindowState(
                width = 450.dp, height = 580.dp, position = WindowPosition(alignment = Alignment.Center)
            )
        //var pinWindow by remember { mutableStateOf(false) }

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
            title = "Ice Breathing",
            state = state,
            transparent = false,
            undecorated = false,
        ) {

            val icon = painterResource("icebreathing_256.png")
            val density = LocalDensity.current
            SideEffect {
                window.iconImage = icon.toAwtImage(density, LayoutDirection.Ltr, Size(256f, 256f))
            }
            App(::minimize, {})
        }

    }


@Composable
fun WindowScope.App(minimizeApp: () -> Unit, pin: () -> Unit) {
    var transparency by remember{ mutableStateOf(1f) }

    var buttonVisible by remember { mutableStateOf(true)}
    val composableScope = rememberCoroutineScope()
    var thisSession by remember{ mutableStateOf( SessionData(30, 6, emptyMap(), RetentionType.CountUp, BreathRate.X1) )}

    /*fun showButtons() {
        composableScope.launch {
            if (!buttonVisible) {
                buttonVisible = true
                delay(3000)
                buttonVisible = false
            }
        }
    }*/
    fun clickedBack(){
        //loadSettings(appConfig)
        AppState.screenState(ScreenType.Start)
    }
    IceBreathingTheme {
        Surface(
            //shape = RoundedCornerShape(8.dp),
            //border = BorderStroke(1.dp, color = mainColorTemp),
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize(1f)
        ){

            fun clickedStartBreathing(s: SessionData) {
                thisSession = s
                AppState.screenState(ScreenType.Breathe)
            }
            //TitleBar(minimizeApp, pin)
            if(AppState.screenState() == ScreenType.Start) {
                StartScreen(::clickedStartBreathing)
            }
            else{
                BreatheScreen(buttonVisible, thisSession, ::clickedBack)
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



