package com.example.inclusiveuianalyzer.core.utils.audio

import org.jetbrains.kotlin.psi.KtCallExpression

object AudioUtils {

    fun isAudioApi(call: KtCallExpression): Boolean {
        val text = call.text

        return text.contains("MediaPlayer")
                || text.contains("SoundPool")
                || text.contains("AudioManager")
    }

    fun hasHapticFeedback(call: KtCallExpression): Boolean {
        val text = call.text

        return text.contains("vibrate")
                || text.contains("performHapticFeedback")
    }

    fun hasVisualFeedback(call: KtCallExpression): Boolean {
        val text = call.text

        return text.contains("Toast")
                || text.contains("Snackbar")
                || text.contains("Log")
    }
}