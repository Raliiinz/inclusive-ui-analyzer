package com.example.inclusiveuianalyzer.core.model

import com.intellij.psi.PsiElement

enum class Severity {
    INFO, WARNING, ERROR
}

enum class Profile {
    VISION, HEARING, COGNITIVE
}

data class Issue(
    val message: String,
    val element: PsiElement,
    val severity: Severity,
    val profile: Profile
)
