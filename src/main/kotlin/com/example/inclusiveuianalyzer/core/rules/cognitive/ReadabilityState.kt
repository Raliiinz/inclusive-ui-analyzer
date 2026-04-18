package com.example.inclusiveuianalyzer.core.rules.cognitive

data class ReadabilityState(
    var avgSentenceLength: Double = 0.0,
    var avgWordLength: Double = 0.0,
    var hasComplexWords: Boolean = false,
    var reasons: MutableList<String> = mutableListOf()
) {
    fun isHard(): Boolean {
        var score = 0

        if (avgSentenceLength > 12) score++
        if (avgWordLength > 7) score++
        if (hasComplexWords) score++

        return score >= 2
    }
}
