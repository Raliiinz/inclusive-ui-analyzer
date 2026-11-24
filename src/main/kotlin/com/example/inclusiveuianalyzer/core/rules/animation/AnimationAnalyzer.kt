package com.example.inclusiveuianalyzer.core.rules.animation

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtValueArgument

object AnimationAnalyzer {

    fun analyze(file: KtFile): List<AnimationInvocation> {
        val invocations = mutableListOf<AnimationInvocation>()

        file.accept(object : KtTreeVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)
                analyzeCall(expression)?.let(invocations::add)
            }

            override fun visitBinaryExpression(expression: KtBinaryExpression) {
                super.visitBinaryExpression(expression)
                analyzeAssignment(expression)?.let(invocations::add)
            }
        })

        return invocations
    }

    private fun analyzeCall(call: KtCallExpression): AnimationInvocation? {
        val name = call.calleeExpression?.text?.substringAfterLast('.') ?: return null
        val qualifiedName = qualifiedName(call)

        return when {
            name == "tween" -> analyzeTween(call)
            name == "spring" -> analyzeSpring(call)
            name == "snap" -> analyzeSnap(call)
            name.startsWith("animate") && name.endsWith("AsState") -> analyzeAnimateAsState(call, name)
            name == "infiniteRepeatable" -> analyzeInfiniteRepeatable(call)
            name == "repeatable" -> analyzeRepeatable(call)
            name == "rememberInfiniteTransition" -> AnimationInvocation(
                kind = AnimationKind.INFINITE_TRANSITION,
                apiName = name,
                element = call,
                isInfinite = true
            )
            qualifiedName.contains("ObjectAnimator.") && name.startsWith("of") ->
                analyzeObjectAnimatorFactory(call)
            name == "setDuration" && isObjectAnimatorContext(call) ->
                analyzeObjectAnimatorDuration(call)
            name == "setRepeatCount" && isObjectAnimatorContext(call) ->
                analyzeObjectAnimatorRepeatCount(call)
            else -> null
        }
    }

    private fun analyzeTween(call: KtCallExpression): AnimationInvocation {
        val duration = argument(call, "durationMillis", 0)
        val delay = argument(call, "delayMillis", 1)
        val easing = argument(call, "easing", 2)

        return AnimationInvocation(
            kind = AnimationKind.TWEEN,
            apiName = "tween",
            element = call,
            durationMillis = parseLong(duration),
            durationExpression = duration?.getArgumentExpression()?.text,
            delayMillis = parseLong(delay),
            easing = easing?.getArgumentExpression()?.text
        )
    }

    private fun analyzeSpring(call: KtCallExpression): AnimationInvocation {
        val damping = argument(call, "dampingRatio", 0)
        val stiffness = argument(call, "stiffness", 1)

        return AnimationInvocation(
            kind = AnimationKind.SPRING,
            apiName = "spring",
            element = call,
            dampingRatio = parseDampingRatio(damping),
            stiffness = parseStiffness(stiffness)
        )
    }

    private fun analyzeSnap(call: KtCallExpression): AnimationInvocation {
        val delay = argument(call, "delayMillis", 0)
        return AnimationInvocation(
            kind = AnimationKind.SNAP,
            apiName = "snap",
            element = call,
            delayMillis = parseLong(delay)
        )
    }

    private fun analyzeAnimateAsState(
        call: KtCallExpression,
        name: String
    ): AnimationInvocation {
        val animationSpec = argument(call, "animationSpec", 1)
        val specText = animationSpec?.getArgumentExpression()?.text

        return AnimationInvocation(
            kind = AnimationKind.ANIMATE_AS_STATE,
            apiName = name,
            element = call,
            animationSpec = specText
        )
    }

    private fun analyzeInfiniteRepeatable(call: KtCallExpression): AnimationInvocation {
        val animation = argument(call, "animation", 0)
        return AnimationInvocation(
            kind = AnimationKind.INFINITE_REPEATABLE,
            apiName = "infiniteRepeatable",
            element = call,
            animationSpec = animation?.getArgumentExpression()?.text,
            isInfinite = true
        )
    }

    private fun analyzeRepeatable(call: KtCallExpression): AnimationInvocation {
        val iterations = argument(call, "iterations", 0)
        val animation = argument(call, "animation", 1)
        return AnimationInvocation(
            kind = AnimationKind.REPEATABLE,
            apiName = "repeatable",
            element = call,
            repeatCount = parseLong(iterations),
            animationSpec = animation?.getArgumentExpression()?.text
        )
    }

    private fun analyzeObjectAnimatorFactory(call: KtCallExpression): AnimationInvocation {
        val property = call.valueArguments.getOrNull(1)?.getArgumentExpression()?.text
        return AnimationInvocation(
            kind = AnimationKind.OBJECT_ANIMATOR,
            apiName = qualifiedName(call),
            element = call,
            easing = property?.let { "property=$it" }
        )
    }

    private fun analyzeObjectAnimatorDuration(call: KtCallExpression): AnimationInvocation {
        val duration = argument(call, "duration", 0)
        return AnimationInvocation(
            kind = AnimationKind.OBJECT_ANIMATOR,
            apiName = "ObjectAnimator.setDuration",
            element = call,
            durationMillis = parseLong(duration),
            durationExpression = duration?.getArgumentExpression()?.text
        )
    }

    private fun analyzeObjectAnimatorRepeatCount(call: KtCallExpression): AnimationInvocation {
        val repeatCount = argument(call, "count", 0)
        val expression = repeatCount?.getArgumentExpression()?.text.orEmpty()
        val isInfinite = expression.substringAfterLast('.') == "INFINITE" || expression == "-1"

        return AnimationInvocation(
            kind = AnimationKind.OBJECT_ANIMATOR,
            apiName = "ObjectAnimator.setRepeatCount",
            element = call,
            repeatCount = parseLong(repeatCount),
            isInfinite = isInfinite
        )
    }

    private fun analyzeAssignment(expression: KtBinaryExpression): AnimationInvocation? {
        if (expression.operationToken != KtTokens.EQ) return null

        val leftText = expression.left?.text ?: return null
        val property = leftText.substringAfterLast('.')
        if (property != "duration" && property != "repeatCount") return null
        if (!isObjectAnimatorContext(expression)) return null

        val rightText = expression.right?.text ?: return null
        val value = parseNumber(rightText)
        val isInfinite = property == "repeatCount" &&
                (rightText.substringAfterLast('.') == "INFINITE" || rightText == "-1")

        return AnimationInvocation(
            kind = AnimationKind.OBJECT_ANIMATOR,
            apiName = "ObjectAnimator.$property",
            element = expression,
            durationMillis = if (property == "duration") value?.toLong() else null,
            durationExpression = if (property == "duration") rightText else null,
            repeatCount = if (property == "repeatCount") value?.toLong() else null,
            isInfinite = isInfinite
        )
    }

    private fun isObjectAnimatorContext(element: PsiElement): Boolean {
        val function = PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java)
        if (function?.text?.contains("ObjectAnimator") == true) return true

        val property = PsiTreeUtil.getParentOfType(element, KtProperty::class.java)
        return property?.text?.contains("ObjectAnimator") == true
    }

    private fun qualifiedName(call: KtCallExpression): String {
        val receiver = (call.parent as? KtDotQualifiedExpression)
            ?.takeIf { it.selectorExpression == call }
            ?.receiverExpression
            ?.text

        return if (receiver == null) {
            call.calleeExpression?.text.orEmpty()
        } else {
            "$receiver.${call.calleeExpression?.text.orEmpty()}"
        }
    }

    private fun argument(
        call: KtCallExpression,
        name: String,
        position: Int
    ): KtValueArgument? {
        return call.valueArguments.firstOrNull {
            it.getArgumentName()?.asName?.identifier == name
        } ?: call.valueArguments.getOrNull(position)
    }

    private fun parseLong(argument: KtValueArgument?): Long? {
        return argument?.getArgumentExpression()?.text?.let(::parseNumber)?.toLong()
    }

    private fun parseDampingRatio(argument: KtValueArgument?): Double? {
        val expression = argument?.getArgumentExpression()?.text ?: return null
        return parseNumber(expression) ?: when (expression.substringAfterLast('.')) {
            "DampingRatioNoBouncy" -> 1.0
            "DampingRatioHighBouncy" -> 0.75
            "DampingRatioMediumBouncy" -> 0.5
            "DampingRatioLowBouncy" -> 0.25
            else -> null
        }
    }

    private fun parseStiffness(argument: KtValueArgument?): Double? {
        val expression = argument?.getArgumentExpression()?.text ?: return null
        return parseNumber(expression) ?: when (expression.substringAfterLast('.')) {
            "StiffnessHigh" -> 10_000.0
            "StiffnessMedium" -> 1_500.0
            "StiffnessLow" -> 200.0
            else -> null
        }
    }

    private fun parseNumber(value: String): Double? {
        return value
            .replace("_", "")
            .removeSuffix("f")
            .removeSuffix("F")
            .removeSuffix("L")
            .removeSuffix("l")
            .toDoubleOrNull()
    }
}
