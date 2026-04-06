package com.example.inclusiveuianalyzer.core.utils

import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtFile

object KotlinPsiUtils {

    fun getFunctions(file: KtFile): List<KtNamedFunction> {
        return file.declarations.filterIsInstance<KtNamedFunction>()
    }
}