package com.example.inclusiveuianalyzer.inspection

import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngineHolder
import com.example.inclusiveuianalyzer.inspection.fixes.AccessibilityQuickFixFactory
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class InclusiveAccessibilityInspection : LocalInspectionTool() {

    private val logger = Logger.getInstance(InclusiveAccessibilityInspection::class.java)
    private val engine = AnalyzerEngineHolder.instance

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                super.visitFile(file)
                val issues = engine.analyze(file)

                issues.forEach { issue ->
                    holder.registerProblem(issue.element, issue.message)
                }
            }
        }
    }


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
                manager.createProblemDescriptor(
                    issue.element,
                    issue.message,
                    isOnTheFly,
                    fixes,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                )
            }.toTypedArray()
        } catch (e: Exception) {
            logger.error("Error during inspection for file ${file.name}", e)
            null
        }
    }
}
