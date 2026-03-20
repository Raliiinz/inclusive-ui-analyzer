package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile

class RuleEngine {

    private val rules = mutableListOf<Rule>()

    init {
        // Добавляем правила
        rules.add(ContentDescriptionRule())
//        rules.add(ContrastRule())
//        rules.add(SizeRule())
    }

    fun runRules(context: AnalysisContext): List<Issue> {
        return rules.filter { it.profile == context.profile }
            .flatMap { it.check(context) }
    }
}