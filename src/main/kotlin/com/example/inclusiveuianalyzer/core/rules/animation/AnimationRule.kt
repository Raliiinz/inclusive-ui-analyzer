package com.example.inclusiveuianalyzer.core.rules.animation

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.BaseRule
import org.jetbrains.kotlin.psi.KtFile

class AnimationRule : BaseRule(
    profile = Profile.MOTION,
    target = AnalysisTarget.KOTLIN_CLASS
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val file = context.file as? KtFile ?: return emptyList()

        return AnimationAnalyzer.analyze(file).mapNotNull { invocation ->
            when {
                invocation.isInfinite || invocation.repeatCount?.let { it >= 3 } == true -> {
                    buildIssue(
                        IssueCode.ANIMATION_INTENSE_EFFECT,
                        buildIntenseMessage(invocation),
                        invocation.element
                    )
                }

                invocation.durationMillis?.let { it > MAX_DURATION_MILLIS } == true -> {
                    buildIssue(
                        IssueCode.ANIMATION_EXCESSIVE_DURATION,
                        buildDurationMessage(invocation),
                        invocation.element
                    )
                }

                invocation.kind == AnimationKind.SNAP ||
                        invocation.durationMillis?.let { it <= MIN_SMOOTH_DURATION_MILLIS } == true ||
                        invocation.stiffness?.let { it >= HIGH_STIFFNESS } == true -> {
                    buildIssue(
                        IssueCode.ANIMATION_ABRUPT_TRANSITION,
                        buildAbruptMessage(invocation),
                        invocation.element
                    )
                }

                invocation.dampingRatio?.let { it < LOW_DAMPING_RATIO } == true -> {
                    buildIssue(
                        IssueCode.ANIMATION_INTENSE_EFFECT,
                        buildIntenseMessage(invocation),
                        invocation.element
                    )
                }

                else -> null
            }
        }
    }

    private fun buildDurationMessage(invocation: AnimationInvocation): String {
        return "${invocation.apiName} uses ${invocation.parameterSummary()}. " +
                "This may make an important state change feel delayed. " +
                "Recommendation: shorten the animation or provide a reduced-motion path."
    }

    private fun buildAbruptMessage(invocation: AnimationInvocation): String {
        if (invocation.kind == AnimationKind.SNAP) {
            return "snap immediately changes the value without a smooth transition. " +
                    "Recommendation: use tween or spring when the state change should be perceived, " +
                    "or verify that the immediate change is intentional."
        }

        if (invocation.kind == AnimationKind.SPRING) {
            return "spring uses ${invocation.parameterSummary()} and may produce very abrupt motion. " +
                    "Recommendation: reduce stiffness or verify that the rapid movement is intentional."
        }

        return "${invocation.apiName} may create an abrupt transition (${invocation.parameterSummary()}). " +
                "Recommendation: use a short non-zero transition when motion is meaningful, " +
                "or verify that the immediate change is intentional."
    }

    private fun buildIntenseMessage(invocation: AnimationInvocation): String {
        if (invocation.kind == AnimationKind.SPRING) {
            return "spring uses ${invocation.parameterSummary()} and may produce pronounced bounce. " +
                    "Recommendation: increase dampingRatio or verify that the overshoot is intentional."
        }

        if (invocation.isInfinite) {
            return "${invocation.apiName} may create continuous motion (${invocation.parameterSummary()}). " +
                    "Recommendation: avoid non-essential infinite motion and provide a pause or reduced-motion path."
        }

        invocation.repeatCount?.let { repeatCount ->
            if (repeatCount >= 3) {
                return "${invocation.apiName} repeats the animation $repeatCount times (${invocation.parameterSummary()}). " +
                        "Recommendation: reduce the number of repetitions or verify that the repeated motion is necessary."
            }
        }

        return "${invocation.apiName} may create continuous or intense motion (${invocation.parameterSummary()}). " +
                "Recommendation: avoid non-essential repeating motion and provide a pause or reduced-motion path."
    }

    private companion object {
        const val MAX_DURATION_MILLIS = 1_000L
        const val MIN_SMOOTH_DURATION_MILLIS = 100L
        const val HIGH_STIFFNESS = 5_000.0
        const val LOW_DAMPING_RATIO = 0.5
    }
}
