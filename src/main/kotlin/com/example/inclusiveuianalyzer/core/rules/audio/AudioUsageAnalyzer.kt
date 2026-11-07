package com.example.inclusiveuianalyzer.core.rules.audio

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

object AudioUsageAnalyzer {

    fun analyze(
        function: KtNamedFunction,
        functionMap: Map<String, KtNamedFunction>,
        visited: MutableSet<KtNamedFunction> = mutableSetOf()
    ): AudioAnalysisState {

        val state = AudioAnalysisState()
        if (!visited.add(function)) return state

        // 1. CALL expressions
        val calls = PsiTreeUtil.findChildrenOfType(function, KtCallExpression::class.java)

        for (call in calls) {

            val text = call.text

            AudioApiDetector.detect(text)?.let {
                state.audioCalls.add(it)
            }
            if (AudioApiDetector.isOutputOperation(call)) {
                state.hasOutputOperation = true
            }

            if (FeedbackAnalyzer.isHaptic(call)) state.hasHaptic = true
            if (FeedbackAnalyzer.isVisual(call)) state.hasVisual = true

            val calledFunction = call.calleeExpression?.text
                ?.substringAfterLast('.')
                ?.let(functionMap::get)
            if (calledFunction != null) {
                state.merge(analyze(calledFunction, functionMap, visited))
            }
        }

        return state
    }
}
