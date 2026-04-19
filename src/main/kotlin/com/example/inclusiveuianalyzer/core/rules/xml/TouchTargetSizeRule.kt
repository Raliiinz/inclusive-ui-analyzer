package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.XmlTagRule
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

class TouchTargetSizeRule : XmlTagRule(
    profile = Profile.VISION,
    target = AnalysisTarget.ANDROID_LAYOUT_XML
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->
            if (TouchTargetSizeAnalyzer.shouldReport(tag)) {
                issues.add(buildIssue(buildMessage(tag), tag))
            }
        }

        return issues
    }

    private fun buildMessage(tag: XmlTag): String {
        val description = XmlAttributeUtils.buildTagDescription(tag)
        return "Touch target too small (<${TouchTargetSizeAnalyzer.MinTouchTargetDp}dp)\nElement: $description"
    }
}
