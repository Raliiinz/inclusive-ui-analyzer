package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.XmlTagRule
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

class ContrastRule : XmlTagRule(
    profile = Profile.VISION,
    target = AnalysisTarget.ANDROID_LAYOUT_XML
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->
            val contrast = ContrastAnalyzer.analyze(context, tag) ?: return@visitTags
            issues.add(buildIssue(buildMessage(tag, contrast), tag))
        }

        return issues
    }

    private fun buildMessage(tag: XmlTag, contrast: Double): String {
        val description = XmlAttributeUtils.buildTagDescription(tag)
        return "Low contrast ratio: %.2f (should be >= %.1f)\nElement: %s"
            .format(contrast, ContrastAnalyzer.MinContrastRatio, description)
    }
}
