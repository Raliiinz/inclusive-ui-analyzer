package com.example.inclusiveuianalyzer.core.rules.audio

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

object AudioUsageAnalyzer {

    fun analyze(
        function: KtNamedFunction,
        functionMap: Map<String, KtNamedFunction>,
        visited: MutableSet<String> = mutableSetOf()
    ): AudioAnalysisState {
        val state = AudioAnalysisState()
        val functionName = function.name ?: return state

        if (functionName in visited) {
            return state
        }
        visited.add(functionName)

        val calls = PsiTreeUtil.findChildrenOfType(function, KtCallExpression::class.java)
        for (call in calls) {
            AudioApiDetector.detect(call)?.let(state.audioCalls::add)

            if (FeedbackAnalyzer.isHaptic(call)) {
                state.hasHaptic = true
            }
            if (FeedbackAnalyzer.isVisual(call)) {
                state.hasVisual = true
            }

            val calledFunction = functionMap[call.calleeExpression?.text]
            if (calledFunction != null) {
                state.merge(analyze(calledFunction, functionMap, visited))
            }
        }

        return state
    }
}
