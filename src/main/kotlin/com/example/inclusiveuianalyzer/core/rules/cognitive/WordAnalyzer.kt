package com.example.inclusiveuianalyzer.core.rules.cognitive

object WordAnalyzer {

    fun averageLength(text: String): Double {
        val words = text.split(" ", "\n", ",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return if (words.isNotEmpty()) {
            words.map { it.length }.average()
        } else 0.0
    }
}