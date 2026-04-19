package com.example.inclusiveuianalyzer.core.rules.xml

object ImportantForAccessibilityAnalyzer {

    fun analyze(value: String?): String? {
        return value?.takeIf { it == "no" || it == "noHideDescendants" }
    }
}
