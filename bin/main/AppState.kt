package io.github.esp_er.icebreathing
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class ScreenType {
    Start, Breathe, BreathHold, BreathInHold, GetReady
}

object AppState {
    private var screen: MutableState<ScreenType>
    init {
        screen = mutableStateOf(ScreenType.Start)
    }

    fun screenState() : ScreenType {
        return screen.value
    }

    fun screenState(state: ScreenType) {
        screen.value = state
    }
}