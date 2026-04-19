package com.example.inclusiveuianalyzer.report

import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPsiElementPointer

data class AnalysisReport(
    val issues: List<ReportedIssue> = emptyList()
) {
    fun isEmpty(): Boolean = issues.isEmpty()
}

data class ReportedIssue(
    val message: String,
    val profile: Profile,
    val severity: Severity,
    val fileName: String,
    val lineNumber: Int?,
    val pointer: SmartPsiElementPointer<PsiElement>
)
