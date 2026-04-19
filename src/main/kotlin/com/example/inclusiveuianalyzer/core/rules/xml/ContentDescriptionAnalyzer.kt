package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

object ContentDescriptionAnalyzer {

    fun shouldReport(tag: XmlTag): Boolean {
        return !XmlAttributeUtils.isAttributeNonEmpty(tag, "android:contentDescription")
    }
}
