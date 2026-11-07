package com.example.inclusiveuianalyzer.core.rules.audio

import org.jetbrains.kotlin.psi.KtCallExpression

object AudioApiDetector {

    private val outputMethods = setOf(
        "start",
        "play",
        "write",
        "playSoundEffect"
    )

    private val audioApiPattern = Regex(
        "\\b(?:MediaPlayer|AudioTrack|SoundPool|AudioManager|ExoPlayer|SimpleExoPlayer)\\b"
    )

    fun detect(text: String?): String? {
        if (text == null) return null
        return audioApiPattern.find(text)?.value
    }

    fun isOutputOperation(call: KtCallExpression): Boolean {
        val methodName = call.calleeExpression?.text
            ?.substringAfterLast('.')
            ?: return false

        return methodName in outputMethods
    }
}
