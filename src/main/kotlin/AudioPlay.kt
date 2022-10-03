package patriker.breathing.iceman

import java.io.*;
import kotlin.concurrent.thread
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import com.soywiz.krypto.Hash
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


enum class SoundType {
    BreatheIn, BreatheOut, Triangle;
}

//TODO: fix the null-unsafe calls
class AudioPlay(){

    private var loadedSounds : HashMap<String, Sound> = HashMap<String,Sound>()
    private var channelList = mutableListOf<SoundChannel>()
    init{
        runBlocking {
            withContext(Dispatchers.IO) {
                val triangle = async { resourcesVfs["triangle_44k.wav"].readSound() }
                loadedSounds["triangle"] = triangle.await()
                val breathIn = async { resourcesVfs["breatheIn_44k.wav"].readSound() }
                loadedSounds["breathin"] = breathIn.await()
                val breathOut = async { resourcesVfs["breatheOut_44k.wav"].readSound() }
                loadedSounds["breathout"] = breathOut.await()
                val ambient = resourcesVfs["ambient.mp3"].readMusic()
                val fluid = resourcesVfs["watr-fluid.mp3"].readMusic()
                val namaste = resourcesVfs["namaste_reenc.mp3"].readMusic()

                loadedSounds["ambient"] = ambient
                loadedSounds["namaste"] = namaste
                loadedSounds["fluid"] = fluid
            }
        }
    }

    suspend fun play(whichSound: SoundType, initDelay: Long = 1) {
        val clip = when (whichSound) {
            SoundType.BreatheOut -> loadedSounds["breathout"]
            SoundType.BreatheIn -> loadedSounds["breathin"]
            else -> loadedSounds["triangle"]
        }
        playSound(clip!!, initDelay)
    }

    fun playMusic2(m: String){
        thread(start = true){
            runBlocking{
                if(m == "fluid")
                    loadedSounds["fluid"] ?: playSound(loadedSounds["fluid"]!!, 1000L)
                else
                    loadedSounds["namaste"] ?: playSound(loadedSounds["namaste"]!!, 1000L)
            }
        }
    }

    suspend fun playMusic(initDelay: Long = 1L, musicName: String){
        playSound(loadedSounds[musicName]!!, initDelay)
    }
    private suspend fun playSound(sound: Sound, initDelay: Long){
        delay(initDelay)
        val channel = sound.play()
        //channelList.add(channel)
    }

    fun stopSounds(){
        channelList.forEach{ it.stop() }
        channelList = mutableListOf<SoundChannel>()
    }

}