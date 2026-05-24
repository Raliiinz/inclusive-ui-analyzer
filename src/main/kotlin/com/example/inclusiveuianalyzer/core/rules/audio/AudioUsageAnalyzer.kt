package com.example.inclusiveuianalyzer.core.rules.audio

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*

object AudioUsageAnalyzer {

    fun analyze(
        function: KtNamedFunction,
        functionMap: Map<String, KtNamedFunction>,
        visited: MutableSet<String> = mutableSetOf()
    ): AudioAnalysisState {

        val state = AudioAnalysisState()
        val functionName = function.name ?: return state

        if (functionName in visited) return state
        visited.add(functionName)

        // 1. CALL expressions
        val calls = PsiTreeUtil.findChildrenOfType(function, KtCallExpression::class.java)

        for (call in calls) {

            val text = call.text

            AudioApiDetector.detect(text)?.let {
                state.audioCalls.add(it)
            }

            if (FeedbackAnalyzer.isHaptic(call)) state.hasHaptic = true
            if (FeedbackAnalyzer.isVisual(call)) state.hasVisual = true

            val calledFunction = functionMap[call.calleeExpression?.text]
            if (calledFunction != null) {
                state.merge(analyze(calledFunction, functionMap, visited))
            }
        }

        // 2. CLASS REFERENCES (ВАЖНО ДЛЯ AudioManager::class.java)
        val refs = PsiTreeUtil.findChildrenOfType(function, KtClassLiteralExpression::class.java)

        for (ref in refs) {
            val text = ref.text
            AudioApiDetector.detect(text)?.let {
                state.audioCalls.add(it)
            }
        }

        // 3. DOT qualified expressions (SoundPool.Builder)
        val dots = PsiTreeUtil.findChildrenOfType(function, KtDotQualifiedExpression::class.java)

        for (dot in dots) {
            val text = dot.text
            AudioApiDetector.detect(text)?.let {
                state.audioCalls.add(it)
            }
        }

        return state
    }
}