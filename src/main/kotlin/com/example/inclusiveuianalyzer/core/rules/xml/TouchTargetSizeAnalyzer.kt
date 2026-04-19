package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.utils.xml.DimensionParser
import com.intellij.psi.xml.XmlTag

object TouchTargetSizeAnalyzer {

    const val MinTouchTargetDp = 48

    fun shouldReport(tag: XmlTag): Boolean {
        val width = DimensionParser.parseDp(tag.getAttributeValue("android:layout_width"))
        val height = DimensionParser.parseDp(tag.getAttributeValue("android:layout_height"))

        return (width != null && width < MinTouchTargetDp) ||
                (height != null && height < MinTouchTargetDp)
    }
}
