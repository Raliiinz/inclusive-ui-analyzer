package com.example.inclusiveuianalyzer.core.utils.xml

import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

object StringResourceUtils {

    fun isStringsFile(file: PsiFile): Boolean {
        return file.name == "strings.xml"
    }

    fun extractStrings(file: XmlFile): List<Pair<String, XmlTag>> {
        val result = mutableListOf<Pair<String, XmlTag>>()

        val tags = PsiTreeUtil.findChildrenOfType(file, XmlTag::class.java)

        for (tag in tags) {
            if (tag.name == "string") {
                val value = tag.value.text.trim()
                if (value.isNotEmpty()) {
                    result.add(value to tag)
                }
            }
        }

        return result
    }
}
