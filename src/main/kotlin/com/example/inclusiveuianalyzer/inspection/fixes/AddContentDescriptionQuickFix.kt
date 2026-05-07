package com.example.inclusiveuianalyzer.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag

class AddContentDescriptionQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add contentDescription"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement as? XmlTag ?: return
        val contentDescription = XmlQuickFixSupport.generateContentDescription(tag)

        WriteCommandAction.runWriteCommandAction(project) {
            tag.setAttribute("android:contentDescription", contentDescription)
            XmlQuickFixSupport.formatTagAttributes(project, tag)
        }
    }
}
