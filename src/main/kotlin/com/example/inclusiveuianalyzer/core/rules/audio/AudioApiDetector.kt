package com.example.inclusiveuianalyzer.core.rules.audio

import org.jetbrains.kotlin.psi.KtCallExpression

object AudioApiDetector {

    private val audioApis = setOf(
        "MediaPlayer",
        "SoundPool",
        "AudioManager"
    )

    fun detect(call: KtCallExpression): String? {
        val name = call.calleeExpression?.text ?: return null

        return audioApis.find { name.contains(it) }
    }
}