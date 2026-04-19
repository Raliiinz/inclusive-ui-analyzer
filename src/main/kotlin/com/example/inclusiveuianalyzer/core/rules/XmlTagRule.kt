package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Profile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.xml.XmlTag

abstract class XmlTagRule(
    profile: Profile,
    target: AnalysisTarget
) : BaseRule(profile, target) {

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
