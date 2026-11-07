package com.example.inclusiveuianalyzer.inspection.fixes

import com.example.inclusiveuianalyzer.core.rules.xml.TouchTargetSizeAnalyzer
import com.example.inclusiveuianalyzer.core.utils.xml.DimensionParser
import com.intellij.psi.xml.XmlTag

object TouchTargetSizeFixSupport {

    fun apply(tag: XmlTag): Boolean {
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

        var changed = false
        if (isTooSmall(width)) {
            tag.setAttribute("android:minWidth", "${TouchTargetSizeAnalyzer.MinTouchTargetDp}dp")
            changed = true
        }
        if (isTooSmall(height)) {
            tag.setAttribute("android:minHeight", "${TouchTargetSizeAnalyzer.MinTouchTargetDp}dp")
            changed = true
        }

        return changed
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
        return value != null && value > 0f && value < TouchTargetSizeAnalyzer.MinTouchTargetDp
    }
}
