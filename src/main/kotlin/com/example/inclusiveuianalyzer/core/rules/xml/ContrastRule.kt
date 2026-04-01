package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.utils.contrast.ContrastCalculator
import com.example.inclusiveuianalyzer.core.utils.xml.ColorResolver
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils

class ContrastRule : XmlRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.ANDROID_LAYOUT_XML

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->

            val textColorValue = tag.getAttributeValue("android:textColor")
            val backgroundValue = tag.getAttributeValue("android:background")

            val fg = ColorResolver.resolveColor(context.project, textColorValue)
            val bg = ColorResolver.resolveColor(context.project, backgroundValue)

            if (fg != null && bg != null) {
                val contrast = ContrastCalculator.calculateContrast(fg, bg)

                if (contrast < 4.5) {
                    val desc = XmlAttributeUtils.buildTagDescription(tag)

                    issues.add(
                        Issue(
                            "Low contrast ratio: %.2f (should be >= 4.5)\nElement: $desc"
                                .format(contrast),
                            tag,
                            Severity.WARNING,
                            profile
                        )
                    )
                }
            }
        }

        return issues
    }
}
