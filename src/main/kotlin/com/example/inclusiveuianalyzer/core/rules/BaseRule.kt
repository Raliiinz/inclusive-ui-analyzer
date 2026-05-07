package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.IssueCode
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.intellij.psi.PsiElement

abstract class BaseRule(
    final override val profile: Profile,
    final override val target: AnalysisTarget,
    protected open val severity: Severity = Severity.WARNING
) : Rule {

    protected fun buildIssue(code: IssueCode, message: String, element: PsiElement): Issue {
        return Issue(
            code = code,
            message = message,
            element = element,
            severity = severity,
            profile = profile
        )
    }
}
