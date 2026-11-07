package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.KotlinCallExpressionRule

class ComposeTouchTargetSizeRule : KotlinCallExpressionRule(
    profile = Profile.VISION,
    target = AnalysisTarget.JETPACK_COMPOSE
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitCallExpressions(context) { call ->
            if (ComposeTouchTargetSizeAnalyzer.shouldReport(call)) {
                issues.add(
                    buildIssue(
                        IssueCode.SMALL_TOUCH_TARGET,
                        "Interactive Compose element has an explicit touch target smaller than 48dp",
                        call
                    )
                )
            }
        }

        return issues
    }
}
