package com.example.inclusiveuianalyzer.core.rules.audio

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.Rule
import com.example.inclusiveuianalyzer.core.utils.KotlinPsiUtils
import org.jetbrains.kotlin.psi.KtFile

class AudioUsageRule : Rule {

    override val profile = Profile.HEARING
    override val target = AnalysisTarget.KOTLIN_CLASS

    override fun check(context: AnalysisContext): List<Issue> {
        val file = context.file as? KtFile ?: return emptyList()
        val issues = mutableListOf<Issue>()

        val functions = KotlinPsiUtils.getFunctions(file)
        val functionMap = functions
            .mapNotNull { fn -> fn.name?.let { it to fn } }
            .toMap()

        for (function in functions) {

            val state = AudioAnalysisEngine.analyzeFunction(
                function,
                functionMap
            )

            if (state.hasAudio() && !state.hasAlternative()) {

                val usedApis = state.audioCalls.joinToString()

                issues.add(
                    Issue(
                        "Audio API used without alternative feedback.\n" +
                                "APIs: $usedApis\n" +
                                "Function: ${function.name}",
                        function,
                        Severity.WARNING,
                        profile
                    )
                )
            }
        }

        return issues
    }
}