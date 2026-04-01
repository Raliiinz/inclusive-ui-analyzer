package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.rules.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

abstract class ComposeRuleBase : Rule {

    protected fun visitComposableCalls(
        context: AnalysisContext,
        block: (KtCallExpression) -> Unit
    ) {
        context.file.accept(object : KtTreeVisitorVoid() {

            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)
                block(expression)
            }
        })
    }
}
