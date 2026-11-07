package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.psi.xml.XmlTag

object ContentDescriptionAnalyzer {

    fun shouldReport(tag: XmlTag): Boolean {
        return isImageLike(tag) &&
                !XmlAttributeUtils.isAttributeNonEmpty(tag, "android:contentDescription")
    }

    private fun isImageLike(tag: XmlTag): Boolean {
        val simpleName = tag.name.substringAfterLast('.').lowercase()
        return simpleName.endsWith("imageview") ||
                simpleName.endsWith("imagebutton") ||
                simpleName.endsWith("videoview") ||
                simpleName.endsWith("surfaceview") ||
                simpleName.endsWith("floatingactionbutton")
    }
}
