package com.example.inclusiveuianalyzer.core.utils.compose

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgument

object ComposeUtils {

    fun getCallName(call: KtCallExpression): String? {
        return call.calleeExpression?.text?.substringAfterLast('.')
    }

    fun findModifierChain(call: KtCallExpression): String? {
        return call.valueArguments
            .firstOrNull { it.getArgumentName()?.asName?.identifier == "modifier" }
            ?.getArgumentExpression()
            ?.text
            ?: call.valueArguments
                .mapNotNull(KtValueArgument::getArgumentExpression)
                .map { it.text }
                .find { it.contains("Modifier") }
    }

    fun containsSemantics(modifier: String?): Boolean {
        return modifier?.contains(".semantics") == true || modifier?.contains("semantics {") == true
    }

    fun containsClearAndSetSemantics(modifier: String?): Boolean {
        return modifier?.contains(".clearAndSetSemantics") == true
    }

    fun containsContentDescription(modifier: String?): Boolean {
        return modifier?.contains("contentDescription") == true
    }

    fun containsInteractiveModifier(modifier: String?): Boolean {
        return modifier?.let {
            it.contains(".pointerInput") &&
                    (it.contains("detectTapGestures") ||
                            it.contains("detectDragGestures") ||
                            it.contains("detectTransformGestures"))
        } == true
    }

    fun containsClickableModifier(modifier: String?): Boolean {
        return modifier?.let {
            it.contains(".clickable") ||
                    it.contains(".combinedClickable") ||
                    it.contains(".toggleable") ||
                    it.contains(".selectable")
        } == true
    }

    fun containsHeadingSemantics(modifier: String?): Boolean {
        return modifier?.contains("heading()") == true ||
                modifier?.contains("heading ()") == true
    }
}
