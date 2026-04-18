package com.example.inclusiveuianalyzer.core.rules.cognitive

object SentenceAnalyzer {

    fun analyze(text: String): Double {
        val sentences = text.split(".", "!", "?")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val words = text.split(" ", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (sentences.size <= 1) {
            return words.size.toDouble()
        }

        return words.size.toDouble() / sentences.size
    }
}
