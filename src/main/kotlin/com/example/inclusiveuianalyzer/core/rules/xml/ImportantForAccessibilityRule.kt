package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils

class ImportantForAccessibilityRule : XmlRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.ANDROID_LAYOUT_XML

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->

            val value = tag.getAttributeValue("android:importantForAccessibility")

            if (value == "no" || value == "noHideDescendants") {
                val desc = XmlAttributeUtils.buildTagDescription(tag)

                issues.add(
                    Issue(
                        "Element hidden from accessibility (importantForAccessibility=$value)\nElement: $desc",
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
