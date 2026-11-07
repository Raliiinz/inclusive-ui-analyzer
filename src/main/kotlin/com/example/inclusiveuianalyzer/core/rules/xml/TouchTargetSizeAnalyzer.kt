package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.utils.xml.DimensionParser
import com.intellij.psi.xml.XmlTag

object TouchTargetSizeAnalyzer {

    const val MinTouchTargetDp = 48

    fun shouldReport(tag: XmlTag): Boolean {
        if (!isInteractive(tag)) {
            return false
        }

        val width = effectiveSize(
            tag.getAttributeValue("android:layout_width"),
            tag.getAttributeValue("android:minWidth"),
            tag
        )
        val height = effectiveSize(
            tag.getAttributeValue("android:layout_height"),
            tag.getAttributeValue("android:minHeight"),
            tag
        )

        return isTooSmall(width) || isTooSmall(height)
    }

    private fun isInteractive(tag: XmlTag): Boolean {
        val simpleName = tag.name.substringAfterLast('.').lowercase()
        val interactiveNames = setOf(
            "button",
            "imagebutton",
            "checkbox",
            "radiobutton",
            "switch",
            "switchcompat",
            "togglebutton",
            "seekbar",
            "ratingbar",
            "spinner",
            "edittext",
            "autocompletetextview",
            "floatingactionbutton"
        )

        return simpleName in interactiveNames ||
                tag.getAttributeValue("android:clickable") == "true" ||
                !tag.getAttributeValue("android:onClick").isNullOrBlank()
    }

    private fun effectiveSize(value: String?, minimum: String?, tag: XmlTag): Float? {
        val parsedValue = DimensionParser.parseDp(value, tag.project)
        val parsedMinimum = DimensionParser.parseDp(minimum, tag.project)

        return when {
            parsedValue == null -> parsedMinimum
            parsedMinimum == null -> parsedValue
            else -> maxOf(parsedValue, parsedMinimum)
        }
    }

    private fun isTooSmall(value: Float?): Boolean {
        return value != null && value > 0f && value < MinTouchTargetDp
    }
}
