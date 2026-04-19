package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.intellij.psi.PsiElement

abstract class BaseRule(
    final override val profile: Profile,
    final override val target: AnalysisTarget,
    protected open val severity: Severity = Severity.WARNING
) : Rule {

    protected fun buildIssue(message: String, element: PsiElement): Issue {
        return Issue(
            message = message,
            element = element,
            severity = severity,
            profile = profile
        )
    }
}
