package patriker.breathing.iceman

import java.io.*;
import kotlin.concurrent.thread
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread


enum class SoundType {
    BreatheIn, BreatheOut, Triangle;
}

class AudioPlay(){
    private var breathOut: Sound
    private var breathIn: Sound
    private var triangle: Sound

    init{
        runBlocking{
            triangle = resourcesVfs["triangle_44k.wav"].readSound()
            breathIn = resourcesVfs["breatheIn_44k.wav"].readSound()
            breathOut = resourcesVfs["breatheOut_44k.wav"].readSound()
        }
    }

    fun play(whichSound: SoundType, initDelay: Long = 1) {
        val clip = when (whichSound) {
            SoundType.BreatheOut -> breathOut
            SoundType.BreatheIn -> breathIn
            else -> triangle
        }
        thread(start = true) {
            runBlocking {
                playSound(clip, initDelay)
            }
        }
    }

    private suspend fun playSound(sound: Sound, initDelay: Long){
        delay(initDelay)
        sound.play()
    }

}