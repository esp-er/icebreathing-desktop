package patriker.breathing.iceman

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun RoundText(round: Int, mod: Modifier){
    Text(
        text = "Round $round",
        fontSize = 16.sp,
        color = Color.White,
        modifier = mod
    )

}