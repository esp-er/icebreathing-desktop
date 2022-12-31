package patriker.breathing.iceman

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val DEFAULT_ROUNDS = 2
val DEF_HOLDMAP = mapOf(1 to 120, 2 to 150, 3 to 165, 4 to 185, 5 to 210, 6 to 230)

data class SessionData(val numBreaths: Int, val numRounds: Int, val breathHoldTime: Map<Int,Int>)

fun Int.secondsAsStr(): String {
    val minutes = this / 60
    val secs = this - minutes * 60
    return String.format("%02d:%02d", minutes, secs)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartScreen(finishedSelection: (SessionData) -> Unit){
    var numRoundsSelected by remember { mutableStateOf(DEFAULT_ROUNDS + 1) }
    var numBreaths by remember { mutableStateOf(30)}
    var holdTimes by remember { mutableStateOf(DEF_HOLDMAP)}
    val breathingSession by remember { derivedStateOf { SessionData(numBreaths, numRoundsSelected, holdTimes)  } }

    fun roundsSelected(rounds: Int){ numRoundsSelected = rounds + 1}
    fun numBreathsSelected(n: Int){ numBreaths = n}
    fun holdTimesChanged(times: Map<Int,Int>){
        holdTimes = times
    }
    //var winsize by remember { mutableStateOf(IntSize(400,400))}

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = 10.dp).onGloballyPositioned { coords ->
            //var winsize = coords.size
        }){

        Box(modifier = Modifier.size(400.dp, 80.dp), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally)  {
                Text(StrRes.breaths, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(fontSize = 16.sp, color = Color.LightGray))
                //Spacer(modifier = Modifier.padding(5.dp))
                numBreathsSelector(numBreaths, ::numBreathsSelected)
            }
        }

        Box(modifier = Modifier.size(400.dp, 95.dp).offset(y = 5.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(StrRes.rounds, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp),
                fontWeight = FontWeight.Medium,
                color = Color.LightGray
                )
                //Spacer(modifier = Modifier.padding(5.dp))
                roundSelector(::roundsSelected)
            }
        }


        Box(modifier = Modifier.offset(y = 0.dp)
            .size(400.dp, 260.dp)) {
            Column {
                holdTimesSelectGrid(numRoundsSelected, ::holdTimesChanged)
                //timeSliderCard()
            }
        }

            /*
        Box(modifier = Modifier.offset(y = 10.dp)
                .size(400.dp, 150.dp)) {
            Column{
                Text(
                    "Select Breath Hold Times:",
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                (1..numRoundsSelected).forEach{ round ->
                    Text("Round: $round",
                    modifier = Modifier.padding(horizontal = 20.dp))
                }

            }
        }*/

        Spacer(modifier = Modifier.padding(2.dp))
        Button(onClick = { finishedSelection(breathingSession) },
            shape = CircleShape,
            modifier = Modifier.size(100.dp)){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Spa, "Start Breathing",
                modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(StrRes.start)
            }
        }
    }

}

@Preview
@Composable
fun PreviewBreathsSelect(){
    Surface(modifier = Modifier.fillMaxSize(1f), color = backColor) {
        numBreathsSelector(30, { x -> println(x) })
    }
}
@Composable
fun numBreathsSelector(initialNumBreaths: Int, onBreathsChanged: (Int) -> Unit){
    //var numBreaths by remember { mutableStateOf(initialNumBreaths)}
    var text by remember { mutableStateOf("$initialNumBreaths") }

    //TODO: change text field to highlight all on focus
    //see https://stackoverflow.com/questions/68244362/select-all-text-of-textfield-in-jetpack-compose
    var field by remember { mutableStateOf(TextFieldValue("30"))}

    LaunchedEffect(text){
        if(text.isNotEmpty())
            onBreathsChanged(text.toInt())
    }
    //var numBreaths by remember { derivedStateOf { text }}
    fun inc5(){
        if(text.isNotEmpty()) {
            val inc = text.toInt() + 5
            if (inc <= 99)
                text = inc.toString()
        }
    }
    fun dec5(){
        if(text.isNotEmpty()){
            val dec = text.toInt() - 5
            if(dec >= 1) text = dec.toString()
        }
    }

    var size by remember { mutableStateOf(IntSize(0,0))}
    val maxChar = 2


    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.onGloballyPositioned { coords -> size=coords.size  }) {
        OutlinedButton(onClick = ::dec5,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(horizontal=4.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary, contentColor = Color.LightGray)
        ) {
            Text(StrRes.minus5)
        }
        BasicTextField(
            modifier = Modifier.width(90.dp).requiredHeight(50.dp).padding(horizontal = 10.dp)
                .border(2.dp, shape = RoundedCornerShape(24.dp), color = mainColorTemp)
                .clickable(enabled = true, onClick = { }),
            value = text,
            singleLine = true,
            onValueChange = { if(it.length in 1..maxChar && it.all{c -> c.isDigit() && it.toInt() > 0 && it.first() != '0'}) {
                                text = it
                                onBreathsChanged(text.toInt())
                            }
                              else if(it.isEmpty()) { text = "5" }
                            },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                //   lineHeight = 40.sp
            ),
            decorationBox = { innerTextField -> Box(modifier = Modifier.padding(vertical = 8.dp)){
                innerTextField()
            }}
        )
        OutlinedButton(onClick = ::inc5,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(horizontal=4.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary, contentColor = Color.LightGray))
            {
                Text(StrRes.plus5)
            }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun holdTimesSelectGrid(numRoundsSelected: Int, onTimeChanged: (Map<Int,Int>) -> Unit){

    data class ButtonClicked(val clicked: Boolean = false, val i: Int = -1){
        val backingField: ButtonClicked
            get() { return if(this.clicked && this.i != -1) this else ButtonClicked() }
    }
    var buttonLabels = arrayOf(120,150,165,180,300,360)
    var cardHeight = 64.dp
    var timeButtonHeight = 32.dp
    var showTimerPicker by remember { mutableStateOf(false)}
    var buttonSecs = remember { mutableStateListOf(*buttonLabels) }
    var lastClickedIndex by remember { mutableStateOf(ButtonClicked()) }
    var lastClickedTime by remember { mutableStateOf(0)}
    LaunchedEffect(lastClickedIndex){
        if(lastClickedIndex.clicked)
            lastClickedTime = buttonSecs[lastClickedIndex.i]
    }
    LaunchedEffect(numRoundsSelected){
        if((numRoundsSelected -1 ) < lastClickedIndex.i)
            lastClickedIndex = ButtonClicked()
    }
    //TODO fix this placeholder hoisted state func
    fun timeChanged(newTime: Int) {
        if(lastClickedIndex.clicked)
            buttonSecs[lastClickedIndex.i] = newTime

        val timesMap = buttonSecs.mapIndexed{index, secs -> index + 1 to secs}.toMap()
        onTimeChanged(timesMap)
    }

    fun cellsPerRow(n: Int): Int {
        return when{
            n > 4       -> 3
            n % 2 == 0  -> 2
            else        -> n
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            StrRes.holdtimes,
            color = Color.LightGray,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(cellsPerRow(numRoundsSelected)),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally).onGloballyPositioned { coords -> coords.size}
        ) {
            items(numRoundsSelected) { item ->
                Card(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .onGloballyPositioned { coords -> coords.size },
                    elevation = if(lastClickedIndex.i == item) 10.dp else 0.dp ,
                    backgroundColor = if(lastClickedIndex.i == item) MaterialTheme.colors.secondary
                                      else MaterialTheme.colors.secondaryVariant
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text("Round ${item + 1}", fontWeight = FontWeight.Medium, color = Color.White)

                        Button(
                            onClick = { showTimerPicker = true; lastClickedIndex = ButtonClicked(true, item)},
                            modifier = Modifier.fillMaxWidth(0.95f)
                            //backgroundColor = MaterialTheme.colors.primaryVariant
                        ) {
                            Icon(Icons.Outlined.Timer, "Choose Time",
                            modifier = Modifier.size(16.dp,16.dp))
                            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                            Text(
                                text = buttonSecs[item].secondsAsStr(),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        //TODO: expand on this and create proper toggle buttons
        if(lastClickedIndex.clicked) timeSliderCard(lastClickedTime, ::timeChanged)
    }

}

@Composable
fun timeSliderCard(initialTime: Int, onTimeChanged: (Int) -> Unit){
    val minValue = 30f
    val maxValue = 510f
    var sliderValue by remember {   if(initialTime <= 30) mutableStateOf(0f)
                                    else mutableStateOf(initialTime.toFloat()) }
    LaunchedEffect(initialTime){
        sliderValue = initialTime.toFloat()
    }
    val sliderSeconds by remember { derivedStateOf { sliderValue.toInt() }}
    val timeText by remember { derivedStateOf { sliderSeconds.secondsAsStr() }}

    val selectorColors = SliderDefaults.colors(thumbColor = MaterialTheme.colors.onSurface, activeTrackColor = MaterialTheme.colors.onSurface)

    Card(modifier = Modifier.width(400.dp).height(64.dp).padding(horizontal = 16.dp),
    backgroundColor = MaterialTheme.colors.secondaryVariant) {
        Box(modifier = Modifier.padding(10.dp)) {
            Text(text = timeText,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Slider(
                modifier = Modifier.offset(y = 10.dp).align(Alignment.Center),
                value = sliderValue,
                valueRange = minValue..maxValue,
                steps = 0,
                onValueChange = { sliderValue = it; onTimeChanged(sliderSeconds)
                    //colors = SliderDefaults.colors()
                },
                colors = selectorColors
            )
        }
    }
}

@Composable
fun roundSelector(onSelected: (Int) -> Unit){
    val cornerRadius = 4.dp
    var selectedIndex by remember{ mutableStateOf(2)}

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        fun indexChanged(i: Int){
            selectedIndex = i
            onSelected(selectedIndex)
        }

        val items = (1..6).toList()
        items.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = { indexChanged(index) },
                shape = when (index) {
                    // left outer button
                    0 -> RoundedCornerShape(topStart = cornerRadius, topEnd = 0.dp, bottomStart = cornerRadius, bottomEnd = 0.dp)
                    // right outer button
                    items.size - 1 -> RoundedCornerShape(topStart = 0.dp, topEnd = cornerRadius, bottomStart = 0.dp, bottomEnd = cornerRadius)
                    // middle button
                    else -> RoundedCornerShape(0.dp)
                },
                border = BorderStroke(1.dp,
                    color = if(selectedIndex == index) MaterialTheme.colors.secondary else MaterialTheme.colors.primaryVariant.copy(alpha = .9f)),
                colors = if(selectedIndex == index) {
                    // selected colors
                    ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.1f), contentColor = MaterialTheme.colors.secondary)
                } else {
                    // not selected colors
                    ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.primary, contentColor = Color.LightGray)
                },
            ) {
                Text(
                    text = item.toString(),
                    color = if(selectedIndex == index) { MaterialTheme.colors.secondary } else { Color.LightGray },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}