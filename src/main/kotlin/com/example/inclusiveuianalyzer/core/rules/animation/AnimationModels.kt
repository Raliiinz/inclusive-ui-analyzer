package com.example.inclusiveuianalyzer.core.rules.animation

import com.intellij.psi.PsiElement

enum class AnimationKind(val label: String) {
    TWEEN("tween"),
    SPRING("spring"),
    SNAP("snap"),
    ANIMATE_AS_STATE("animate*AsState"),
    INFINITE_REPEATABLE("infiniteRepeatable"),
    REPEATABLE("repeatable"),
    INFINITE_TRANSITION("rememberInfiniteTransition"),
    OBJECT_ANIMATOR("ObjectAnimator")
}

data class AnimationInvocation(
    val kind: AnimationKind,
    val apiName: String,
    val element: PsiElement,
    val durationMillis: Long? = null,
    val durationExpression: String? = null,
    val delayMillis: Long? = null,
    val stiffness: Double? = null,
    val dampingRatio: Double? = null,
    val easing: String? = null,
    val repeatCount: Long? = null,
    val animationSpec: String? = null,
    val isInfinite: Boolean = false
) {

    fun parameterSummary(): String {
        val parameters = buildList {
            durationMillis?.let { add("duration=${it}ms") }
            if (durationMillis == null) {
                durationExpression?.let { add("duration=$it") }
            }
            delayMillis?.let { add("delay=${it}ms") }
            stiffness?.let { add("stiffness=$it") }
            dampingRatio?.let { add("dampingRatio=$it") }
            easing?.let { add("easing=$it") }
            repeatCount?.let { add("repeatCount=$it") }
            animationSpec?.let { add("animationSpec=$it") }
            if (isInfinite) add("infinite")
        }

        return parameters.joinToString()
            .ifBlank { "parameters are not statically resolvable" }
    }
}
