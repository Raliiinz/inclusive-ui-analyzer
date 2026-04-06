package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

abstract class KotlinRuleBase : Rule {

    protected fun visitCallExpressions(
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