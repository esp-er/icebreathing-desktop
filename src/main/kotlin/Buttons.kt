package patriker.breathing.iceman

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/*
@Composable
fun FinishBreatheButton(finishClicked: () -> Unit, mod: Modifier, size: Dp){

    OutlinedButton(onClick = {finishClicked()}, modifier= mod,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors()
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = "Finish Breathing",
            modifier = Modifier.size(size))
        Spacer(modifier = Modifier.width(4.dp))
    }
}
 */

@Composable
fun BackButton(backClicked: () -> Unit, mod: Modifier, size: Dp){
    IconButton(onClick = {backClicked()}, modifier = mod,
    ) {
        Icon(
            Icons.Outlined.ArrowBack,
            contentDescription = "Go to Start",
            modifier = Modifier.size(size),
            tint = Color.White
        )
    }
}

@Composable
fun PauseButton(pauseClicked: () -> Unit, mod: Modifier, sz: Dp) {
    IconButton(
        onClick = { pauseClicked() },
        modifier = mod
    ){
        Icon(Icons.Outlined.Pause, "", tint = Color.White,
            modifier = Modifier.size(sz,sz))
    }
}

@Composable
fun ContButton(contClicked: () -> Unit, mod: Modifier, sz: Dp) {
    IconButton(
        onClick = { contClicked() },
        modifier = mod
    ){
        Icon(Icons.Outlined.PlayArrow, "", tint = Color.White,
            modifier = Modifier.size(sz,sz))
    }
}


@Composable
@Preview
fun RateButton(isLeft: Boolean = true, clickCallback: () -> Unit,
               mod: Modifier,
               size: Dp) {
    IconButton(
        onClick = { clickCallback() },
        modifier = mod
    ){
        if(isLeft)
            Icon(Icons.Outlined.FastRewind, "Decrease Speed",
                tint = Color.White,
                modifier = Modifier.size(size, size))
        else
            Icon(Icons.Outlined.FastForward, "Increase Speed",
                tint = Color.White,
                modifier = Modifier.size(size, size))
    }
}
