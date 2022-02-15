package patriker.breathing.iceman

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.concurrent.fixedRateTimer


@Composable
fun BreathHoldScreen(winsize: IntSize, timeLeft: Int, finishedHold: (SessionState) -> Unit, playSound: (SoundType) -> Unit){
    var time by remember { mutableStateOf(timeLeft) }
    //NOTE: val timeText by remember { derivedStateOf {timeLeft.secondsAsStr()}}
    LaunchedEffect(true) {
        playSound(SoundType.Triangle)
        fixedRateTimer("timer", false, 0, 1000) {
            time--
        }
    }
    LaunchedEffect(time){
        if (time <= 0) finishedHold(SessionState.BreatheHold)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text(StrRes.inhalein, style = TextStyle(fontSize = 28.sp, color = Color.White))
        Text(time.secondsAsStr(), style = TextStyle(fontSize = 28.sp, color = Color.White))
        OutlinedButton(onClick = {finishedHold(SessionState.BreatheHold)}, modifier= Modifier.offset(y=20.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = "Finish Holding")
            //Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            //Text("Inhale")
        }
    }

}
