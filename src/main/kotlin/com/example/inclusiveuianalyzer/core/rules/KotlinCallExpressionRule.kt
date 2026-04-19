package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Profile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

abstract class KotlinCallExpressionRule(
    profile: Profile,
    target: AnalysisTarget
) : BaseRule(profile, target) {

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
