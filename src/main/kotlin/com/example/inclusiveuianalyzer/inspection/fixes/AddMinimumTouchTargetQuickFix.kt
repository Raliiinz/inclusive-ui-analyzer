package com.example.inclusiveuianalyzer.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag

class AddMinimumTouchTargetQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add minimum 48dp touch target"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement as? XmlTag ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            tag.setAttribute("android:minWidth", "48dp")
            tag.setAttribute("android:minHeight", "48dp")
            XmlQuickFixSupport.formatTagAttributes(project, tag)
        }
    }
}
