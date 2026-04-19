package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.XmlTagRule
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

class ContentDescriptionRule : XmlTagRule(
    profile = Profile.VISION,
    target = AnalysisTarget.ANDROID_LAYOUT_XML
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->
            if (ContentDescriptionAnalyzer.shouldReport(tag)) {
                issues.add(buildIssue(buildMessage(context.file.name, tag), tag))
            }
        }

        return issues
    }

    private fun buildMessage(fileName: String, tag: XmlTag): String {
        val tagDescription = XmlAttributeUtils.buildTagDescription(tag)
        return """
            Missing contentDescription
            File: $fileName
            Element: $tagDescription
        """.trimIndent()
    }
}
