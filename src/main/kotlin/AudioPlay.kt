package patriker.breathing.iceman

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import javax.xml.transform.Source
import kotlin.concurrent.thread

enum class SoundType {
    BreatheIn, BreatheOut, Triangle;
}

class AudioPlay(){
    private var breathOut: Clip
    private var breathIn: Clip
    private var triangle: Clip

    init{

        val audioRes = this.javaClass.classLoader.getResource("breatheOut_44k.wav")
        val audioRes2 = this.javaClass.classLoader.getResource("breatheIn_44k.wav")
        val resTriang = this.javaClass.classLoader.getResource("triangle.wav")

        val streamIn = AudioSystem.getAudioInputStream(audioRes)
        breathOut = AudioSystem.getClip()
        breathOut.open(streamIn)

        val streamIn2 = AudioSystem.getAudioInputStream(audioRes2)
        breathIn = AudioSystem.getClip()
        breathIn.open(streamIn2)

        val streamIn3 = AudioSystem.getAudioInputStream(resTriang)
        triangle = AudioSystem.getClip()
        triangle.open(streamIn3)
    }

    fun play(whichSound: SoundType, initDelay: Long = 1){
        thread(start=true) {
            when(whichSound) {
                SoundType.BreatheOut -> playBreatheOut(initDelay)
                SoundType.BreatheIn -> playBreatheIn(initDelay)
                else -> playTriangle(initDelay)
            }
        }
    }

    private fun playTriangle(initDelay: Long){
        val sleepTime = breathIn.microsecondLength / 1000L
        Thread.sleep(initDelay);
        triangle.microsecondPosition = 0
        breathIn.loop(Clip.LOOP_CONTINUOUSLY);
        Thread.sleep(sleepTime)
        triangle.stop()
    }

    private fun playBreatheIn(initDelay: Long){
        val sleepTime = breathIn.microsecondLength / 1000L
        Thread.sleep(initDelay)
        breathIn.microsecondPosition = 0
        breathIn.loop(Clip.LOOP_CONTINUOUSLY);
        Thread.sleep(sleepTime)
        breathIn.stop()
    }
    private fun playBreatheOut( initDelay: Long){
        val sleepTime = breathOut.microsecondLength / 1000L
        Thread.sleep(initDelay)
        breathOut.microsecondPosition = 0
        breathOut.loop(Clip.LOOP_CONTINUOUSLY);
        Thread.sleep(sleepTime)
        breathOut.stop()
    }

    fun stop(){
        triangle.stop()
        breathIn.stop()
        breathOut.stop()
    }
    fun close(){
        breathIn.close()
        breathOut.close()
        triangle.close()
    }
}