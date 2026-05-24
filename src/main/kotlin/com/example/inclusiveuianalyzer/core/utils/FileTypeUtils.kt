package com.example.inclusiveuianalyzer.core.utils

import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType

object FileTypeUtils {


    fun isKotlin(file: PsiFile): Boolean {
        return file.fileType == KotlinFileType.INSTANCE
    }
    fun isJava(file: PsiFile) = file.name.endsWith(".java")
    fun isXml(file: PsiFile) = file.name.endsWith(".xml")
}
