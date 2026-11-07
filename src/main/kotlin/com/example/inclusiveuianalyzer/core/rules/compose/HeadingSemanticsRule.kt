package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.KotlinCallExpressionRule

class HeadingSemanticsRule : KotlinCallExpressionRule(
    profile = Profile.VISION,
    target = AnalysisTarget.JETPACK_COMPOSE
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitCallExpressions(context) { call ->
            if (HeadingSemanticsAnalyzer.shouldReport(call)) {
                issues.add(
                    buildIssue(
                        IssueCode.MISSING_HEADING_SEMANTICS,
                        "Text looks like a heading but does not declare heading semantics",
                        call
                    )
                )
            }
        }

        return issues
    }
}
