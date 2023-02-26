package io.github.esp_er.icebreathing


data class SessionData(val numBreaths: Int,
                       val numRounds: Int,
                       val breathHoldTime: Map<Int,Int>,
                       val retentionStyle: RetentionType = RetentionType.CountUp,
                       val breathRate: BreathRate,
                       val breathingStyle: BreathingStyle = BreathingStyle.Standard,
                       val breathSounds: BreathSound = BreathSound.Breathing,
                       val retentionSounds: RetentionMusic = RetentionMusic.None
)

