package patriker.breathing.iceman

import java.io.*;
import kotlin.concurrent.thread
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import com.soywiz.krypto.Hash
import kotlinx.coroutines.*
import kotlin.concurrent.thread


enum class SoundType {
    BreatheIn, BreatheOut, Triangle;
}

//TODO: fix the null-unsafe calls
class AudioPlay(){

    private var loadedSounds : HashMap<String, Sound> = HashMap<String,Sound>()
    private var channelList = mutableListOf<SoundChannel>()
    init{
        thread(start = true){
            runBlocking {
                val triangle = resourcesVfs["triangle_44k.wav"].readSound()
                loadedSounds["triangle"] = triangle
                val breathIn = resourcesVfs["breatheIn_44k.wav"].readSound()
                loadedSounds["breathin"] = breathIn
                val breathOut = resourcesVfs["breatheOut_44k.wav"].readSound()
                loadedSounds["breathout"] = breathOut
                val ambient = resourcesVfs["ambient.mp3"].readMusic()
                loadedSounds["ambient"] = ambient
            }
        }
    }

    fun play(whichSound: SoundType, initDelay: Long = 1) {
        val clip = when (whichSound) {
            SoundType.BreatheOut -> loadedSounds["breathout"]
            SoundType.BreatheIn -> loadedSounds["breathin"]
            else -> loadedSounds["triangle"]
        }
        thread(start = true) {
            runBlocking {
                playSound(clip!!, initDelay)
            }
        }
    }

    fun playMusic(){
        thread(start = true){
            runBlocking{
                playSound(loadedSounds["ambient"]!!, 0L)
            }
        }
    }
    private suspend fun playSound(sound: Sound, initDelay: Long){
        delay(initDelay)
        val channel = sound.play()
        channelList.add(channel)
    }

    fun stopSounds(){
        channelList.forEach{ it.stop() }
        channelList = mutableListOf<SoundChannel>()
    }

}