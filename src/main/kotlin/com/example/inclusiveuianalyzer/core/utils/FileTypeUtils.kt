package com.example.inclusiveuianalyzer.core.utils

import com.intellij.psi.PsiFile

object FileTypeUtils {
    fun isKotlin(file: PsiFile) = file.name.endsWith(".kt")
    fun isJava(file: PsiFile) = file.name.endsWith(".java")
    fun isXml(file: PsiFile) = file.name.endsWith(".xml")
}
