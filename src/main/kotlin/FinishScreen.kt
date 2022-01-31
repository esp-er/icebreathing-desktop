package patriker.breathing.iceman

import androidx.compose.animation.core.RepeatMode
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.system.exitProcess

@Composable
@Preview
fun FinishScreen(){
    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Breathing Session Finished.", style = TextStyle(fontSize = 20.sp, color = Color.White))
            Text("Great Job!", style = TextStyle(fontSize = 20.sp, color = Color.White))
            Row {
                OutlinedButton(
                    onClick = { },
                    shape = CircleShape,
                    modifier = Modifier.offset(y = 20.dp)
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Start Over")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "Back",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = { exitProcess(0) },
                    shape = CircleShape,
                    modifier = Modifier.offset(y = 20.dp)
                ) {
                    Icon(Icons.Outlined.Cancel, contentDescription = "Close Application")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "Close",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }

        }
    }
}

@Composable
@Preview
fun FinishPreview(){
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(1f), color = backColor) {
            Box(modifier = Modifier.width(400.dp).height(800.dp), contentAlignment = Alignment.Center) {
                FinishScreen()
            }
        }
    }
}