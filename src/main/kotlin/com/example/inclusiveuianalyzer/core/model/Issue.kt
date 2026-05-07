package com.example.inclusiveuianalyzer.core.model

import com.intellij.psi.PsiElement

enum class Severity {
    INFO, WARNING, ERROR
}

enum class Profile {
    VISION, HEARING, COGNITIVE
}

enum class IssueCode {
    MISSING_CONTENT_DESCRIPTION,
    HIDDEN_FROM_ACCESSIBILITY,
    SMALL_TOUCH_TARGET,
    LOW_CONTRAST,
    MISSING_SEMANTICS,
    MISSING_TEXT_ALTERNATIVE,
    CLEAR_AND_SET_SEMANTICS,
    AUDIO_WITHOUT_ALTERNATIVE,
    HARD_TO_READ_TEXT
}

data class Issue(
    val code: IssueCode,
    val message: String,
    val element: PsiElement,
    val severity: Severity,
    val profile: Profile
)
