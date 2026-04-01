package com.example.inclusiveuianalyzer.core.utils.compose

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgument

object ComposeUtils {

    fun getCallName(call: KtCallExpression): String? {
        return call.calleeExpression?.text
    }

    fun hasModifier(call: KtCallExpression): Boolean {
        return call.valueArguments.any {
            it.getArgumentExpression()?.text?.contains("Modifier") == true
        }
    }

    fun findModifierChain(call: KtCallExpression): String? {
        return call.valueArguments
            .mapNotNull(KtValueArgument::getArgumentExpression)
            .map { it.text }
            .find { it.contains("Modifier") }
    }

    fun containsSemantics(modifier: String?): Boolean {
        return modifier?.contains("semantics") == true
    }

    fun containsClearAndSetSemantics(modifier: String?): Boolean {
        return modifier?.contains("clearAndSetSemantics") == true
    }

    fun containsContentDescription(modifier: String?): Boolean {
        return modifier?.contains("contentDescription") == true
    }
}