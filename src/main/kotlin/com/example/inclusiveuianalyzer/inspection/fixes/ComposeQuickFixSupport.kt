package com.example.inclusiveuianalyzer.inspection.fixes

import org.jetbrains.kotlin.psi.KtCallExpression

object ComposeQuickFixSupport {

    fun generateContentDescription(call: KtCallExpression): String {
        val candidate = call.valueArguments
            .asSequence()
            .mapNotNull { argument -> argument.getArgumentExpression()?.text }
            .map(::extractSemanticToken)
            .firstOrNull { token -> token.isNotBlank() }

        val fallback = call.calleeExpression?.text ?: "Item"
        return normalizeWords(candidate ?: fallback)
    }

    private fun extractSemanticToken(expressionText: String): String {
        return expressionText
            .substringAfterLast('.')
            .substringAfterLast('/')
            .substringBefore('(')
            .removePrefix("ic_")
            .removePrefix("img_")
            .removePrefix("baseline_")
            .removePrefix("outline_")
            .removePrefix("outlined_")
            .removePrefix("round_")
            .removePrefix("rounded_")
            .removePrefix("filled_")
            .removePrefix("twotone_")
            .replace(Regex("\\d+$"), "")
    }

    private fun normalizeWords(value: String): String {
        return value
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("\\s+"), " ")
            .trim()
            .split(' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { char -> char.uppercase() }
            }
            .ifBlank { "Item" }
    }
}
