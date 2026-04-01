package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.intellij.openapi.diagnostic.Logger

class ContentDescriptionRule : XmlRuleBase() {

    private val logger = Logger.getInstance(ContentDescriptionRule::class.java)

    override val profile = Profile.VISION
    override val target = AnalysisTarget.ANDROID_LAYOUT_XML

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()
        val fileName = context.file.name

        visitTags(context) { tag ->
            if (!XmlAttributeUtils.isAttributeNonEmpty(tag, "android:contentDescription")) {
                val tagDescription = XmlAttributeUtils.buildTagDescription(tag)
                val message = """
                    Missing contentDescription
                    File: $fileName
                    Element: $tagDescription
                """.trimIndent()

                logger.warn(message)

                issues.add(
                    Issue(
                        message,
                        tag,
                        Severity.WARNING,
                        profile
                    )
                )
            }
        }
        return issues
    }
}
