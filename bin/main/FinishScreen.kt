package io.github.esp_er.icebreathing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.draw.clip
import kotlin.system.exitProcess

@Composable
fun FinishScreen(thisSession: SessionData, clickedBack: () -> Unit, breathsFinished: Int = 0) {
    val totalHold by remember {
        derivedStateOf {
            thisSession.breathHoldTime.values.take(thisSession.numRounds).fold(0) { sum, v ->
                sum + v
            }
        }
    }

    val holdTime by remember{ mutableStateOf(totalHold.secondsAsStr())}
    val mins by remember{
        derivedStateOf {  if(totalHold < 600) holdTime.split(":").first().takeLast(1) else holdTime.split(":").first() }
    }
    val secs by remember {
        derivedStateOf {  if(totalHold % 60 < 10) holdTime.split(":").last().takeLast(1) else holdTime.split(":").last() }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val textOpacity = 0.95f
            val size = 24.sp
            val digitSize = 28.sp
            Text(
                StrRes.awesome,
                fontSize = 28.sp,
                color = Color.White.copy(alpha = textOpacity)
            )

            Text(
                StrRes.ufinished,
                style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = textOpacity))
            )

            Spacer(Modifier.height(12.dp))

            Row {
                Text(
                    "$breathsFinished",
                    color = MaterialTheme.colors.secondary,
                    fontSize = digitSize,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    "Breaths in",
                    fontSize = size,
                    color = Color.White.copy(alpha = textOpacity),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    "${thisSession.numRounds}",
                    fontSize = digitSize,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    "Rounds",
                    fontSize = size,
                    color = Color.White.copy(alpha = textOpacity),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                StrRes.heldbreath,
                fontSize = size,
                color = Color.White.copy(alpha = textOpacity)
            )
            Row {
                Text(
                    mins,
                    color = MaterialTheme.colors.secondary,
                    fontSize = digitSize,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    "minutes",
                    color = Color.White.copy(alpha = textOpacity),
                    fontSize = size,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    secs,
                    color = MaterialTheme.colors.secondary,
                    fontSize = digitSize,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    "seconds",
                    color = Color.White.copy(alpha = textOpacity),
                    fontSize = size,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)){
                (1..thisSession.breathHoldTime.keys.size).forEach{roundNumber ->
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colors.primary)
                        .padding(8.dp))
                    {
                        Text(
                            "Round $roundNumber   :   ${thisSession.breathHoldTime.getOrDefault(roundNumber, 0).secondsAsStr()}",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        }
        BackButton(
            backClicked = { clickedBack() },
            mod = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            size = 32.dp
        )

        //Close button
        OutlinedButton(onClick = { exitProcess(0) },
                       shape = CircleShape,
                       modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
        )
        {
            Icon(Icons.Outlined.Cancel, contentDescription = "Close Application")
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                StrRes.close,
                style = TextStyle(fontSize = 14.sp)
                )
        }
    }
}