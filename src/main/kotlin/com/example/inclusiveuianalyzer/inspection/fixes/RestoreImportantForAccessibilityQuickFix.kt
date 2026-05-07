package com.example.inclusiveuianalyzer.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag

class RestoreImportantForAccessibilityQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Set importantForAccessibility to auto"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement as? XmlTag ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            tag.setAttribute("android:importantForAccessibility", "auto")
            XmlQuickFixSupport.formatTagAttributes(project, tag)
        }
    }
}
