package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils

class ClearAndSetSemanticsRule : ComposeRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.JETPACK_COMPOSE

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitComposableCalls(context) { call ->

            val modifier = ComposeUtils.findModifierChain(call)

            if (ComposeUtils.containsClearAndSetSemantics(modifier)) {
                issues.add(
                    Issue(
                        "clearAndSetSemantics used — may remove accessibility info",
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