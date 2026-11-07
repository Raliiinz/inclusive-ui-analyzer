package com.example.inclusiveuianalyzer.core.engine

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.RuleEngine
import com.intellij.psi.PsiFile

class AnalyzerEngine {

    private val ruleEngine = RuleEngine()

    fun analyze(file: PsiFile, profile: Profile): List<Issue> {
        val context = AnalysisContext(file, file.project, profile)
        return ruleEngine.runRules(context)
    }

    fun analyze(file: PsiFile): List<Issue> {
        return Profile.entries
            .flatMap { profile -> analyze(file, profile) }
    }
}
