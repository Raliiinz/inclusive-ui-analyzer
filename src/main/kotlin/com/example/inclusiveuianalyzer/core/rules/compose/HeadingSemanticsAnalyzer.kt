package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils
import org.jetbrains.kotlin.psi.KtCallExpression

object HeadingSemanticsAnalyzer {

    private val textComponents = setOf("Text", "BasicText")

    fun shouldReport(call: KtCallExpression): Boolean {
        val componentName = call.calleeExpression?.text ?: return false
        if (componentName !in textComponents) return false

        val style = call.valueArguments
            .firstOrNull { it.getArgumentName()?.asName?.identifier == "style" }
            ?.getArgumentExpression()
            ?.text
            ?.lowercase()
            ?: return false

        val looksLikeHeading = listOf("display", "headline", "title")
            .any(style::contains)
        if (!looksLikeHeading) return false

        val modifier = ComposeUtils.findModifierChain(call)
        return !ComposeUtils.containsHeadingSemantics(modifier)
    }
}
