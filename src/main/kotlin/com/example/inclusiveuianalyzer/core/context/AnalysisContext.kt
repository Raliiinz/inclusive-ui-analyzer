package com.example.inclusiveuianalyzer.core.context

import com.example.inclusiveuianalyzer.core.model.Profile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

data class AnalysisContext(
    val file: PsiFile,
    val project: Project,
    val profile: Profile
)