package com.example.inclusiveuianalyzer.core.rules.audio

object AudioApiDetector {

    private val audioApis = setOf(
        "MediaPlayer",
        "SoundPool",
        "AudioManager"
    )

    fun detect(text: String?): String? {
        if (text == null) return null
        return audioApis.find { text.contains(it) }
    }
}