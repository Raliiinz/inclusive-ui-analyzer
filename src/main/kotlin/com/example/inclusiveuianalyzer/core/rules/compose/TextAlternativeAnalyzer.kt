package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils
import org.jetbrains.kotlin.psi.KtCallExpression

object TextAlternativeAnalyzer {

    private val imageComponents = setOf("Icon", "Image")

    fun analyze(call: KtCallExpression): String? {
        val componentName = ComposeUtils.getCallName(call) ?: return null
        if (componentName !in imageComponents) {
            return null
        }

        val modifier = ComposeUtils.findModifierChain(call)
        val hasContentDescriptionArgument = call.valueArguments.any {
            it.getArgumentName()?.asName?.identifier == "contentDescription"
        }
        val hasContentDescriptionInModifier = ComposeUtils.containsContentDescription(modifier)

        return if (!hasContentDescriptionArgument && !hasContentDescriptionInModifier) {
            componentName
        } else {
            null
        }
    }
}
