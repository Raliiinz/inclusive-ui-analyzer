package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.KotlinRuleBase
import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils

class SemanticsRule : KotlinRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.JETPACK_COMPOSE

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitCallExpressions(context) { call ->

            val modifier = ComposeUtils.findModifierChain(call)

            if (modifier != null && !ComposeUtils.containsSemantics(modifier)) {

                issues.add(
                    Issue(
                        "Missing Modifier.semantics for accessibility",
                        call,
                        Severity.WARNING,
                        profile
                    )
                )
            }
        }

        return issues
    }
}