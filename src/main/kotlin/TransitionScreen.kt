package patriker.breathing.iceman

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


enum class PrepareState{
    Start,End
}
@Composable
fun TransitionScreen(winsize: IntSize, roundNum: Int, breathGoal: Int, finishedPrep: (SessionState) -> Unit){
    var prepState by remember { mutableStateOf(PrepareState.Start) }
    val transition = updateTransition(targetState = prepState)

    LaunchedEffect(true){
        prepState = PrepareState.End
    }
    LaunchedEffect(transition.currentState){
        if (transition.currentState == PrepareState.End)
            finishedPrep(SessionState.Prepare)
    }
    val textOpacity by transition.animateFloat(
        transitionSpec ={ tween(3000,1000, LinearEasing) }
    ) { state ->
        when(state){
            PrepareState.Start -> 1.0f
            PrepareState.End -> 0.0f
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text("${StrRes.round} $roundNum", style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = textOpacity)))
        Text(StrRes.getready, style = TextStyle(fontSize = 28.sp, color = Color.White.copy(alpha = textOpacity)))
        Spacer(modifier = Modifier.height(30.dp))
        Text("$breathGoal ${StrRes.breaths}", style = TextStyle(fontSize = 16.sp, color = Color.White.copy(alpha = textOpacity)))
    }
}

/*
@Preview
@Composable
fun PreviewTransition(){ //Previewing animated content seems problematic
    TransitionScreen(IntSize(400, 400), 1, 40) { s: SessionState -> println(s) }
}
 */


