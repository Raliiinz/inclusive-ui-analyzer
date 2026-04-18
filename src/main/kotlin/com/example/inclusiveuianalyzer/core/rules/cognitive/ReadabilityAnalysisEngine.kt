package com.example.inclusiveuianalyzer.core.rules.cognitive

object ReadabilityAnalysisEngine {

    fun analyzeText(text: String): ReadabilityState {
        val state = ReadabilityState()

        state.avgSentenceLength = SentenceAnalyzer.analyze(text)
        state.avgWordLength = WordAnalyzer.averageLength(text)
        state.hasComplexWords = ComplexWordDetector.hasComplexWords(text)

        if (state.avgSentenceLength > 12) {
            state.reasons.add("Long sentences")
        }

        if (state.avgWordLength > 7) {
            state.reasons.add("Long words")
        }

        if (state.hasComplexWords) {
            state.reasons.add("Complex terminology")
        }

        return state
    }
}