package com.example.inclusiveuianalyzer.core.rules.audio

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.BaseRule
import com.example.inclusiveuianalyzer.core.utils.KotlinPsiUtils
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class AudioUsageRule : BaseRule(
    profile = Profile.HEARING,
    target = AnalysisTarget.KOTLIN_CLASS
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val file = context.file as? KtFile ?: return emptyList()
        val issues = mutableListOf<Issue>()
        val functions = KotlinPsiUtils.getFunctions(file)
        val functionMap = buildFunctionMap(functions)

        for (function in functions) {
            val state = AudioUsageAnalyzer.analyze(function, functionMap)
            if (state.hasAudio() && !state.hasAlternative()) {
                issues.add(buildIssue(buildMessage(function, state), function))
            }
        }

        return issues
    }

    private fun buildFunctionMap(functions: List<KtNamedFunction>): Map<String, KtNamedFunction> {
        return functions
            .mapNotNull { function -> function.name?.let { it to function } }
            .toMap()
    }

    private fun buildMessage(function: KtNamedFunction, state: AudioAnalysisState): String {
        val usedApis = state.audioCalls.joinToString()
        return "Audio API used without alternative feedback.\n" +
                "APIs: $usedApis\n" +
                "Function: ${function.name}"
    }
}
