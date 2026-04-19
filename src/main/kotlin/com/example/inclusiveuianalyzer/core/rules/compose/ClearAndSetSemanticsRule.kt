package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.KotlinCallExpressionRule

class ClearAndSetSemanticsRule : KotlinCallExpressionRule(
    profile = Profile.VISION,
    target = AnalysisTarget.JETPACK_COMPOSE
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitCallExpressions(context) { call ->
            if (ClearAndSetSemanticsAnalyzer.shouldReport(call)) {
                issues.add(buildIssue(buildMessage(), call))
            }
        }

        return issues
    }

    private fun buildMessage(): String {
        return "clearAndSetSemantics used - may remove accessibility info"
    }
}
