package com.example.inclusiveuianalyzer.core.engine

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.rules.RuleEngine
import com.intellij.psi.PsiFile

class AnalyzerEngine {

    private val ruleEngine = RuleEngine()

    fun analyze(file: PsiFile, profile: com.example.inclusiveuianalyzer.core.model.Profile): List<Issue> {
        val context = AnalysisContext(file, file.project, profile)
        return ruleEngine.runRules(context)
    }
}