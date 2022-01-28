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
    private var clip: Clip
    private var triangle: Clip

    init{

        val audioRes = this.javaClass.classLoader.getResource("breatheOut_44k.wav")
        val audioRes2 = this.javaClass.classLoader.getResource("breatheIn_44k.wav")
        val resTriang = this.javaClass.classLoader.getResource("triangle.wav")

        val streamIn = AudioSystem.getAudioInputStream(audioRes)
        breathOut = AudioSystem.getClip()
        breathOut.open(streamIn)

        val streamIn2 = AudioSystem.getAudioInputStream(audioRes2)
        clip = AudioSystem.getClip()
        clip.open(streamIn2)

        val streamIn3 = AudioSystem.getAudioInputStream(resTriang)
        triangle = AudioSystem.getClip()
        triangle.open(streamIn3)
    }

    fun play(whichSound: SoundType, initDelay: Long = 1){
        val clip = when(whichSound) {
            SoundType.Triangle -> triangle
            SoundType.BreatheOut -> breathOut
            SoundType.BreatheIn -> clip
        }

        thread(start=true) {
            playClip(clip, initDelay)
        }
    }

    private fun playClip(clip: Clip, initDelay: Long){
        val sleepTime = clip.microsecondLength / 1000L
        Thread.sleep(initDelay);
        clip.microsecondPosition = 0
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        Thread.sleep(sleepTime)
        clip.stop()
    }

    fun stop(){
        triangle.stop()
        clip.stop()
        breathOut.stop()
    }
    fun close(){
        clip.close()
        breathOut.close()
        triangle.close()
    }
}