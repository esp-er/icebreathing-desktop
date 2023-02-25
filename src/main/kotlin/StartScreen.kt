package io.github.esp_er.icebreathing
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
val DEF_HOLDMAP = mapOf(1 to 90, 2 to 120, 3 to 150, 4 to 180, 5 to 180, 6 to 180, 7 to 180, 8 to 180)



enum class BreathSound{
    None, Breathing, SingingBowl;
}


enum class RetentionMusic{
    None, SingingBowlsAssorted, SingingBowlsLow;
}

enum class BreathingStyle{
    Standard, SlowInhale;
}

fun BreathingStyle.label(): String{
    return when(this) {
        BreathingStyle.SlowInhale -> "Slow In Fast Out"
        BreathingStyle.Standard -> "Default"
    }
}

fun RetentionMusic.label(): String{
    return when(this) {
        RetentionMusic.SingingBowlsAssorted -> "Assorted Singing Bowls"
        RetentionMusic.SingingBowlsLow -> "Singing Bowl Low Tones"
        RetentionMusic.None -> "None"
    }
}



fun Int.secondsAsStr(): String {
    val minutes = this / 60
    val secs = this - minutes * 60
    return String.format("%02d:%02d", minutes, secs)
}

enum class RetentionType{
    Preselect, CountUp;

    companion object {
        fun byValue(value: Int) = if (value == 0 ) Preselect else CountUp
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartScreen(finishedSelection: (SessionData) -> Unit){
    var numRoundsSelected by remember { mutableStateOf(DEFAULT_ROUNDS + 1) }
    var numBreaths by remember { mutableStateOf(30)}
    var holdTimes by remember { mutableStateOf(DEF_HOLDMAP)}
    val breathingSession by remember { derivedStateOf { SessionData(numBreaths, numRoundsSelected,  holdTimes, RetentionType.CountUp, BreathRate.X1)  } }


    var retentionStyle by remember { mutableStateOf(RetentionType.CountUp) }

    fun roundsSelected(rounds: Int){ numRoundsSelected = rounds + 1}
    fun numBreathsSelected(n: Int){ numBreaths = n}
    fun holdTimesChanged(times: Map<Int,Int>){
        holdTimes = times
    }
    //var winsize by remember { mutableStateOf(IntSize(400,400))}



    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.background(color = Color.Black) //Background rendering issue when using graalvm/jwm? this fixes it for some reason
            .offset(y=0.5.dp)

    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.onGloballyPositioned { coords ->
                //var winsize = coords.size
            }.background(MaterialTheme.colors.background).fillMaxSize()
            ){

            Box(modifier = Modifier.height(98.dp).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally)  {
                    Text(StrRes.breaths, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                        fontWeight = FontWeight.Medium,
                        style = TextStyle(fontSize = 16.sp, color = Color.LightGray))
                    //Spacer(modifier = Modifier.padding(5.dp))
                    numBreathsSelector(numBreaths, ::numBreathsSelected)
                }
            }
            Box(modifier = Modifier.height(95.dp).offset(y = 8.dp).fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        StrRes.rounds, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray
                    )
                    //Spacer(modifier = Modifier.padding(5.dp))
                    roundSelector(::roundsSelected)
                }
            }
            Box(modifier = Modifier.height(95.dp).fillMaxWidth().offset(y = 2.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        StrRes.breathretention, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray
                    )
                    //Spacer(modifier = Modifier.padding(5.dp))
                    RetentionSelector(onSelected = { retentionStyle = it },
                        selectorLabels = StrRes.preselecttime to StrRes.countup
                    )
                }
            }

            if(retentionStyle == RetentionType.Preselect) {
                Box(
                    modifier = Modifier.offset(y = 0.dp)
                        .height(180.dp).fillMaxWidth()
                ) {
                    Column {
                        //holdTimesSelectGrid(numRoundsSelected, ::holdTimesChanged)
                        HoldTimeSelector(
                            onSelected = {},
                            holdTimes.values.toTypedArray(),
                            numRoundsSelected = numRoundsSelected,
                            ::holdTimesChanged
                        )
                        //timeSliderCard()
                    }
                }
            }else {
                Box(
                    modifier = Modifier.height(90.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        //holdTimesSelectGrid(numRoundsSelected, ::holdTimesChanged)
                        Text(StrRes.doubletapfinish, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.W400)
                        //timeSliderCard()
                    }
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
            Button(onClick = { finishedSelection(breathingSession.copy(retentionStyle = retentionStyle)) },
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

}

@Preview
@Composable
fun PreviewBreathsSelect(){
    Surface(modifier = Modifier.fillMaxSize(1f), color = backColor) {
        numBreathsSelector(30, { })
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

        val items = (1..8).toList()
        items.forEachIndexed { index, item ->
            OutlinedButton(
                modifier = Modifier.width(52.dp),
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
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@ExperimentalFoundationApi
@Composable
fun RetentionSelector(onSelected: (RetentionType) -> Unit,
                      selectorLabels: Pair<String, String>,
                      defaultSelected: RetentionType = RetentionType.CountUp){
    var selectedIndex by remember{ mutableStateOf(defaultSelected.ordinal)}

    fun buttonSelected(index: Int){
        selectedIndex = index

        val retentionSelected =
            if(selectedIndex == 0) RetentionType.Preselect else RetentionType.CountUp

        onSelected(retentionSelected)
    }

    Column(modifier = Modifier.fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
        ) {

            Spacer(modifier = Modifier.weight(1f))
            listOf(selectorLabels.first, selectorLabels.second).forEachIndexed { index, it ->
                OutlinedButton(
                    modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                    onClick = { buttonSelected(index) }, //TODO set index inside onClick
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(
                        1.dp,
                        color = if (selectedIndex == index)
                            MaterialTheme.colors.secondary
                        else
                            MaterialTheme.colors.primary
                    ),
                    colors = if (selectedIndex == index) {
                        // selected colors
                        ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.LightGray
                        )
                    } else {
                        // default colors
                        ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colors.background,
                            contentColor = MaterialTheme.colors.secondary
                        )
                    },
                ) {
                    Text(
                        text = it,
                        color = if (selectedIndex == index) {
                            Color.LightGray
                        } else {
                            MaterialTheme.colors.secondary
                        },
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                        //.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TimeSliderCard(initialTime: Int, onTimeChanged: (Int) -> Unit, round: Int){
    val minValue = 30f
    val maxValue = 600f
    var sliderValue by remember {   if(initialTime <= 30) mutableStateOf(0f)
    else mutableStateOf(initialTime.toFloat()) }
    LaunchedEffect(initialTime){
        sliderValue = initialTime.toFloat()
    }
    val sliderSeconds by remember { derivedStateOf { sliderValue.toInt() }}
    val timeText by remember { derivedStateOf { sliderSeconds.secondsAsStr() }}

    Card(modifier = Modifier
        .fillMaxWidth(0.98f)
        .height(72.dp)
        .padding(horizontal = 16.dp),
        backgroundColor = MaterialTheme.colors.secondaryVariant) {
        Box(modifier = Modifier.padding(10.dp)) {
            Row {
                Text(
                    modifier = Modifier.weight(2f),
                    text = timeText,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(6f))
                Text(
                    modifier = Modifier.weight(2.5f),
                    text = "Round $round",
                    color = Color.White
                )

            }
            Slider(
                modifier = Modifier
                    .offset(y = 16.dp)
                    .align(Alignment.Center),
                value = sliderValue,
                valueRange = minValue..maxValue,
                steps = 0,
                onValueChange = { sliderValue = it; onTimeChanged(sliderSeconds)}
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HoldTimeSelector(onSelected: (Int) -> Unit,
                     buttonLabels: Array<Int>,
                     numRoundsSelected: Int,
                     onTimeChanged: (Map<Int,Int>) -> Unit){
    var selectedIndex by remember{ mutableStateOf(-1)}

    data class ButtonClicked(val clicked: Boolean = false, val i: Int = -1){
        val backingField: ButtonClicked
            get() { return if(this.clicked && this.i != -1) this else ButtonClicked() }
    }
    val buttonSecs = remember { mutableStateListOf(*buttonLabels) }
    var lastClickedIndex by remember { mutableStateOf(ButtonClicked()) }
    var lastClickedTime by remember { mutableStateOf(0)}
    var lastClickedRound by remember { mutableStateOf(1)}
    LaunchedEffect(lastClickedIndex){
        if(lastClickedIndex.clicked) {
            lastClickedTime = buttonSecs[lastClickedIndex.i]
            lastClickedRound = lastClickedIndex.i + 1
        }
    }
    LaunchedEffect(numRoundsSelected){
        if((numRoundsSelected -1 ) < lastClickedIndex.i)
            lastClickedIndex = ButtonClicked()
    }
    //TODO fix this placeholder hoisted state func
    fun timeChanged(newTime: Int) {
        if(lastClickedIndex.clicked) {
            buttonSecs[lastClickedIndex.i] = newTime
        }

        val timesMap = buttonSecs.mapIndexed{index, secs -> index + 1 to secs}.toMap()
        onTimeChanged(timesMap)
    }

    fun buttonSelected(index: Int){
        lastClickedIndex = ButtonClicked(true, index)
        onSelected(index)
    }

    Column(modifier = Modifier.fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            fun indexChanged(i: Int) {
                selectedIndex = i
                buttonSelected(selectedIndex)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(minOf(numRoundsSelected, 5)),
                horizontalArrangement = Arrangement.Center
            ) {
                itemsIndexed(Array(numRoundsSelected, { i -> i + 1 })) { index, it ->
                    OutlinedButton(
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp),
                        onClick = { indexChanged(index) }, //TODO set index inside onClick
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(
                            1.dp,
                            color = if (selectedIndex == index)
                                MaterialTheme.colors.secondary
                            else
                                MaterialTheme.colors.primary
                        ),
                        colors = if (selectedIndex == index) {
                            // selected colors
                            ButtonDefaults.outlinedButtonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.LightGray
                            )
                        } else {
                            // default colors
                            ButtonDefaults.outlinedButtonColors(
                                backgroundColor = MaterialTheme.colors.background,
                                contentColor = MaterialTheme.colors.secondary
                            )
                        },
                    ) {
                        Column {
                            Text(
                                text = it.toString(),
                                color = if (selectedIndex == index) {
                                    Color.LightGray
                                } else {
                                    MaterialTheme.colors.secondary
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = buttonSecs[index].secondsAsStr(),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontSize = if(numRoundsSelected <= 4) 12.sp else 10.sp,
                                color = if (selectedIndex == index) {
                                    Color.LightGray
                                } else {
                                    MaterialTheme.colors.secondary
                                },
                            )
                        }
                    }
                }
            }
        }

        if(lastClickedIndex.clicked) {
            Box(modifier = Modifier.fillMaxWidth(0.93f)
                .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                TimeSliderCard(
                    initialTime = lastClickedTime,
                    onTimeChanged = ::timeChanged,
                    round = lastClickedRound
                )
            }
        }

    }
}



