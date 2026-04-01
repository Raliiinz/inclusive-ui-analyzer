package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.utils.xml.DimensionParser
import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils


class TouchTargetSizeRule : XmlRuleBase() {

    override val profile = Profile.VISION
    override val target = AnalysisTarget.ANDROID_LAYOUT_XML

    override fun check(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->

            val width = DimensionParser.parseDp(tag.getAttributeValue("android:layout_width"))
            val height = DimensionParser.parseDp(tag.getAttributeValue("android:layout_height"))

            if ((width != null && width < 48) || (height != null && height < 48)) {
                val desc = XmlAttributeUtils.buildTagDescription(tag)

                issues.add(
                    Issue(
                        "Touch target too small (<48dp)\nElement: $desc",
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
