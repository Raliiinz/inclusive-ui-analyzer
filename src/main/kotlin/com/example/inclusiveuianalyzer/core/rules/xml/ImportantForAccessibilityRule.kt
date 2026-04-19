package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.XmlTagRule
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

class ImportantForAccessibilityRule : XmlTagRule(
    profile = Profile.VISION,
    target = AnalysisTarget.ANDROID_LAYOUT_XML
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->
            val hiddenValue = ImportantForAccessibilityAnalyzer.analyze(
                tag.getAttributeValue("android:importantForAccessibility")
            ) ?: return@visitTags

            issues.add(buildIssue(buildMessage(tag, hiddenValue), tag))
        }

        return issues
    }

    private fun buildMessage(tag: XmlTag, value: String): String {
        val description = XmlAttributeUtils.buildTagDescription(tag)
        return "Element hidden from accessibility (importantForAccessibility=$value)\nElement: $description"
    }
}
