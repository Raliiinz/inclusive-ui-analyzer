package com.example.inclusiveuianalyzer.inspection.fixes

import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.intellij.codeInspection.LocalQuickFix

object AccessibilityQuickFixFactory {

    fun create(issue: Issue): Array<LocalQuickFix> {
        return when (issue.code) {
            IssueCode.MISSING_CONTENT_DESCRIPTION -> arrayOf(AddContentDescriptionQuickFix())
            IssueCode.HIDDEN_FROM_ACCESSIBILITY -> arrayOf(RestoreImportantForAccessibilityQuickFix())
            IssueCode.SMALL_TOUCH_TARGET -> arrayOf(AddMinimumTouchTargetQuickFix())
            IssueCode.MISSING_TEXT_ALTERNATIVE -> arrayOf(AddComposeContentDescriptionQuickFix())
            else -> emptyArray()
        }
    }
}
