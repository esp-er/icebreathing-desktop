package patriker.breathing.iceman

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import kotlin.concurrent.thread


class AudioPlay(){
    private var clip: Clip
    private var stream: AudioInputStream
    init{
        val audioRes = this.javaClass.classLoader.getResource("long.wav")
        stream = AudioSystem.getAudioInputStream(audioRes)
        val format  = stream.format
        val info = DataLine.Info(Clip::class.java, format)
        clip = AudioSystem.getLine(info) as Clip
        clip.open(stream)
    }
    fun play(){
        val sleepTime = clip.microsecondLength / 1000L
        thread(start=true) {
            clip.microsecondPosition = 0
            clip.start()
            Thread.sleep(sleepTime)
        }
    }
    fun stop(){
        clip.stop()
    }
    fun close(){
        clip.close()
        stream.close()
    }
}