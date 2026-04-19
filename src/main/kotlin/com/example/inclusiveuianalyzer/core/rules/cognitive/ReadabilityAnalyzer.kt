package com.example.inclusiveuianalyzer.core.rules.cognitive

object ReadabilityAnalyzer {

    fun analyze(text: String): ReadabilityState {
        val state = ReadabilityState(
            avgSentenceLength = SentenceAnalyzer.analyze(text),
            avgWordLength = WordAnalyzer.averageLength(text),
            hasComplexWords = ComplexWordDetector.hasComplexWords(text)
        )

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
