package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils
import org.jetbrains.kotlin.psi.KtCallExpression

object ClearAndSetSemanticsAnalyzer {

    fun shouldReport(call: KtCallExpression): Boolean {
        val modifier = ComposeUtils.findModifierChain(call)
        return ComposeUtils.containsClearAndSetSemantics(modifier)
    }
}
