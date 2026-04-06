package com.example.inclusiveuianalyzer.core.rules.audio

import org.jetbrains.kotlin.psi.KtCallExpression

object FeedbackAnalyzer {

    fun isHaptic(call: KtCallExpression): Boolean {
        val name = call.calleeExpression?.text ?: return false
        return name.contains("vibrate") || name.contains("performHapticFeedback")
    }

    fun isVisual(call: KtCallExpression): Boolean {
        val name = call.calleeExpression?.text ?: return false
        return name.contains("Toast")
                || name.contains("Snackbar")
                || name.contains("show")
    }
}