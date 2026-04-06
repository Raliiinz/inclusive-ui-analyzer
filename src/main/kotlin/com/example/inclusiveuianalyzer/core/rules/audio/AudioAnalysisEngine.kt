package com.example.inclusiveuianalyzer.core.rules.audio

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

object AudioAnalysisEngine {

    fun analyzeFunction(
        function: KtNamedFunction,
        functionMap: Map<String, KtNamedFunction>,
        visited: MutableSet<String> = mutableSetOf()
    ): AudioAnalysisState {

        val state = AudioAnalysisState()

        val functionName = function.name ?: return state
        if (functionName in visited) return state
        visited.add(functionName)

        val calls = PsiTreeUtil.findChildrenOfType(
            function,
            KtCallExpression::class.java
        )

        for (call in calls) {

            AudioApiDetector.detect(call)?.let {
                state.audioCalls.add(it)
            }

            if (FeedbackAnalyzer.isHaptic(call)) {
                state.hasHaptic = true
            }

            if (FeedbackAnalyzer.isVisual(call)) {
                state.hasVisual = true
            }

            val calledName = call.calleeExpression?.text

            val calledFunction = functionMap[calledName]

            if (calledFunction != null) {
                val nestedState = analyzeFunction(
                    calledFunction,
                    functionMap,
                    visited
                )

                state.audioCalls.addAll(nestedState.audioCalls)

                if (nestedState.hasHaptic) state.hasHaptic = true
                if (nestedState.hasVisual) state.hasVisual = true
            }
        }

        return state
    }
}