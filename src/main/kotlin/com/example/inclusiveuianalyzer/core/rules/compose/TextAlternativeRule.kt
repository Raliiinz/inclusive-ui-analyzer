package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.KotlinCallExpressionRule

class TextAlternativeRule : KotlinCallExpressionRule(
    profile = Profile.VISION,
    target = AnalysisTarget.JETPACK_COMPOSE
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitCallExpressions(context) { call ->
            val componentName = TextAlternativeAnalyzer.analyze(call) ?: return@visitCallExpressions
            issues.add(buildIssue(buildMessage(componentName), call))
        }

        return issues
    }

    private fun buildMessage(componentName: String): String {
        return "Missing contentDescription for $componentName"
    }
}
