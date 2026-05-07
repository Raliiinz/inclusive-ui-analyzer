package com.example.inclusiveuianalyzer.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class AddComposeContentDescriptionQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add Compose contentDescription"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val call = descriptor.psiElement as? KtCallExpression ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            applyToCall(project, call)
        }
    }

    companion object {
        fun applyToCall(project: Project, call: KtCallExpression): Boolean {
            val argumentList = call.valueArgumentList ?: return false
            val psiFactory = KtPsiFactory(project)
            val contentDescription = ComposeQuickFixSupport.generateContentDescription(call)
            val newArgument = psiFactory.createArgument("contentDescription = \"$contentDescription\"")

            argumentList.addArgument(newArgument)
            return true
        }
    }
}
