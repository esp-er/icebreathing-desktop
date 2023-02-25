package io.github.esp_er.icebreathing

enum class BreathRate{
    X0_75, X0_9, X1, X1_1, X1_25;
}

fun BreathRate.toMs(baseMs: Int): Int {
    val ms = when(this){
        BreathRate.X0_75 -> (1.25 * baseMs).toInt()
        BreathRate.X0_9 -> (1.1 * baseMs).toInt()
        BreathRate.X1 -> baseMs
        BreathRate.X1_1 -> (0.9 * baseMs).toInt()
        BreathRate.X1_25 -> (0.75 * baseMs).toInt()
    }
    return ms
}

fun BreathRate.Decrease() =
    when(this){
        BreathRate.X0_75 ->  BreathRate.X0_75
        BreathRate.X0_9 -> BreathRate.X0_75
        BreathRate.X1 -> BreathRate.X0_9
        BreathRate.X1_1 -> BreathRate.X1
        BreathRate.X1_25 -> BreathRate.X1_1
    }

fun BreathRate.Increase() =
    when(this){
        BreathRate.X0_75 ->  BreathRate.X0_9
        BreathRate.X0_9 -> BreathRate.X1
        BreathRate.X1 -> BreathRate.X1_1
        BreathRate.X1_1 -> BreathRate.X1_25
        BreathRate.X1_25 -> BreathRate.X1_25
    }

fun BreathRate.str() =
    when(this){
        BreathRate.X0_75 ->  "0.75x"
        BreathRate.X0_9 -> "0.9x"
        BreathRate.X1 -> "1x"
        BreathRate.X1_1 -> "1.1x"
        BreathRate.X1_25 -> "1.25x"
    }