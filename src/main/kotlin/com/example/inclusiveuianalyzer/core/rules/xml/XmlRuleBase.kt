package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.rules.Rule
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.xml.XmlTag

abstract class XmlRuleBase : Rule {

    protected fun visitTags(
        context: AnalysisContext,
        block: (XmlTag) -> Unit
    ) {
        context.file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                (element as? XmlTag)?.let(block)
            }
        })
    }
}
