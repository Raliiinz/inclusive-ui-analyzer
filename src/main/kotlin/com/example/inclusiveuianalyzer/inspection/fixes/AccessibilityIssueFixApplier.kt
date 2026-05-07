package com.example.inclusiveuianalyzer.inspection.fixes

import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.example.inclusiveuianalyzer.report.ReportedIssue
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtCallExpression
import com.intellij.psi.xml.XmlTag

object AccessibilityIssueFixApplier {

    fun canApply(issue: ReportedIssue): Boolean {
        val element = issue.pointer.element
        return when {
            element is XmlTag -> issue.code in setOf(
                IssueCode.MISSING_CONTENT_DESCRIPTION,
                IssueCode.HIDDEN_FROM_ACCESSIBILITY,
                IssueCode.SMALL_TOUCH_TARGET
            )
            element is KtCallExpression -> issue.code == IssueCode.MISSING_TEXT_ALTERNATIVE
            else -> false
        }
    }

    fun apply(project: Project, issue: ReportedIssue): Boolean {
        return applyAll(project, listOf(issue), "Apply Accessibility Fix") > 0
    }

    fun applyAll(project: Project, issues: List<ReportedIssue>, commandName: String): Int {
        val applicableIssues = issues
            .asSequence()
            .filter(::canApply)
            .distinctBy(::issueKey)
            .toList()

        if (applicableIssues.isEmpty()) {
            return 0
        }

        var appliedCount = 0
        WriteCommandAction.runWriteCommandAction(project, commandName, null, Runnable {
            applicableIssues.forEach { issue ->
                when (val element = issue.pointer.element) {
                    is XmlTag -> {
                        if (applyToTag(project, issue.code, element)) {
                            appliedCount++
                        }
                    }
                    is KtCallExpression -> {
                        if (applyToComposeCall(project, issue.code, element)) {
                            appliedCount++
                        }
                    }
                }
            }
        })

        return appliedCount
    }

    private fun applyToTag(project: Project, code: IssueCode, tag: XmlTag): Boolean {
        return when (code) {
            IssueCode.MISSING_CONTENT_DESCRIPTION -> {
                tag.setAttribute(
                    "android:contentDescription",
                    XmlQuickFixSupport.generateContentDescription(tag)
                )
                XmlQuickFixSupport.formatTagAttributes(project, tag)
                true
            }
            IssueCode.HIDDEN_FROM_ACCESSIBILITY -> {
                tag.setAttribute("android:importantForAccessibility", "auto")
                XmlQuickFixSupport.formatTagAttributes(project, tag)
                true
            }
            IssueCode.SMALL_TOUCH_TARGET -> {
                tag.setAttribute("android:minWidth", "48dp")
                tag.setAttribute("android:minHeight", "48dp")
                XmlQuickFixSupport.formatTagAttributes(project, tag)
                true
            }
            else -> false
        }
    }

    private fun applyToComposeCall(project: Project, code: IssueCode, call: KtCallExpression): Boolean {
        return when (code) {
            IssueCode.MISSING_TEXT_ALTERNATIVE -> AddComposeContentDescriptionQuickFix.applyToCall(project, call)
            else -> false
        }
    }

    private fun issueKey(issue: ReportedIssue): String {
        val element = issue.pointer.element
        val filePath = element?.containingFile?.virtualFile?.path ?: issue.fileName
        val offset = element?.textOffset ?: -1
        return "${issue.code}|$filePath|$offset"
    }
}
