package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.analyzers.xml.XmlAttributeUtils
import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.xml.XmlTag

class ContentDescriptionRule : Rule {

    private val logger = Logger.getInstance(ContentDescriptionRule::class.java)

    override val profile = Profile.VISION
    override val target = AnalysisTarget.ANDROID_LAYOUT_XML

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()
        val fileName = context.file.name

        context.file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)

                if (element is XmlTag) {
                    if (!XmlAttributeUtils.isAttributeNonEmpty(element, "android:contentDescription")) {

                        val tagName = element.name
                        val id = element.getAttributeValue("android:id")
                        val tagDescription = buildString {
                            append(tagName)
                            id?.let { append("#$it") }
                        }

                        val message = buildString {
                            append("Missing contentDescription\n")
                            append("File: $fileName\n")
                            append("Element: $tagDescription\n")
                        }
                        logger.warn(message)

                        issues.add(
                            Issue(
                                message,
                                element,
                                Severity.WARNING,
                                profile
                            )
                        )
                    }
                }
            }
        })

        return issues
    }
}
