package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils
import org.jetbrains.kotlin.psi.KtCallExpression

object SemanticsAnalyzer {

    fun shouldReport(call: KtCallExpression): Boolean {
        val modifier = ComposeUtils.findModifierChain(call)
        return modifier != null && !ComposeUtils.containsSemantics(modifier)
    }
}
