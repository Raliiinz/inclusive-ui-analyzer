package com.example.inclusiveuianalyzer.inspection

import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngine
import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngineHolder
import com.intellij.codeInspection.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiFile
import com.example.inclusiveuianalyzer.core.model.Profile
import com.intellij.psi.PsiElementVisitor

class InclusiveAccessibilityInspection : LocalInspectionTool() {
    private val logger = Logger.getInstance(InclusiveAccessibilityInspection::class.java)
    private val engine = AnalyzerEngineHolder.instance

    init {
        logger.info(">>> TestInspection ИНИЦИАЛИЗИРОВАН <<<")
        println(">>> TestInspection ИНИЦИАЛИЗИРОВАН <<<")
    }

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): PsiElementVisitor {
        return PsiElementVisitor.EMPTY_VISITOR
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        logger.info("Starting inspection for file: ${file.name}")
        println("DEBUG: Starting inspection for file: ${file.name}")

        try {
            val issues = engine.analyze(file, Profile.VISION)
            logger.info("Found ${issues.size} issues")
            println("DEBUG: Found ${issues.size} issues")

            return issues.map {
                manager.createProblemDescriptor(
                    it.element,
                    it.message,
                    false,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    isOnTheFly
                )
            }.toTypedArray()
        } catch (e: Exception) {
            logger.error("Error during inspection", e)
            e.printStackTrace()
            return null
        }
    }
}
