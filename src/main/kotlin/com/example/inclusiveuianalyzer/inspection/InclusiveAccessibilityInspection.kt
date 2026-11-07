package com.example.inclusiveuianalyzer.inspection

import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngineHolder
import com.example.inclusiveuianalyzer.inspection.fixes.AccessibilityQuickFixFactory
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.PsiFile

class InclusiveAccessibilityInspection : LocalInspectionTool() {

    private val logger = Logger.getInstance(InclusiveAccessibilityInspection::class.java)
    private val engine = AnalyzerEngineHolder.instance

    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {
        logger.info("Starting inspection for file: ${file.name}")

        return try {
            val issues = engine.analyze(file)
            logger.info("Found ${issues.size} issues in ${file.name}")

            issues.map { issue ->
                val fixes = AccessibilityQuickFixFactory.create(issue)
                val highlightType = when (issue.severity) {
                    com.example.inclusiveuianalyzer.core.model.Severity.ERROR -> ProblemHighlightType.ERROR
                    com.example.inclusiveuianalyzer.core.model.Severity.WARNING -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                    com.example.inclusiveuianalyzer.core.model.Severity.INFO -> ProblemHighlightType.INFORMATION
                }
                manager.createProblemDescriptor(
                    issue.element,
                    issue.message,
                    isOnTheFly,
                    fixes,
                    highlightType
                )
            }.toTypedArray()
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error during inspection for file ${file.name}", e)
            null
        }
    }
}
