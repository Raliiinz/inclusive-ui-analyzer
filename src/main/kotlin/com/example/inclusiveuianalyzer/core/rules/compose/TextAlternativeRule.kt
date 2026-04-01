package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils

class TextAlternativeRule : ComposeRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.JETPACK_COMPOSE

    private val imageComponents = setOf("Icon", "Image")

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitComposableCalls(context) { call ->

            val name = ComposeUtils.getCallName(call) ?: return@visitComposableCalls

            if (name in imageComponents) {

                val modifier = ComposeUtils.findModifierChain(call)

                val hasContentDescInArgs = call.valueArguments.any {
                    it.getArgumentName()?.asName?.identifier == "contentDescription"
                }

                val hasInModifier = ComposeUtils.containsContentDescription(modifier)

                if (!hasContentDescInArgs && !hasInModifier) {
                    issues.add(
                        Issue(
                            "Missing contentDescription for $name",
                            call,
                            Severity.WARNING,
                            profile
                        )
                    )
                }
            }
        }

        return issues
    }
}