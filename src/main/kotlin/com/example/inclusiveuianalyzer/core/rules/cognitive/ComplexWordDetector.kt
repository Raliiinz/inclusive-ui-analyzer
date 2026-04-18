package com.example.inclusiveuianalyzer.core.rules.cognitive

object ComplexWordDetector {

    private val complexWords = setOf(
        "осуществление",
        "выполнение",
        "инициализация",
        "функциональность",
        "конфигурация",
        "идентификация",
        "аутентификация",
        "параметризация",
        "оптимизация"
    )

    fun hasComplexWords(text: String): Boolean {
        val words = text.lowercase().split(" ", "\n", ",")
        return words.any { it in complexWords }
    }
}
