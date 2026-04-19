package com.example.inclusiveuianalyzer.core.rules.xml

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.utils.contrast.ContrastCalculator
import com.example.inclusiveuianalyzer.core.utils.xml.ColorResolver
import com.intellij.psi.xml.XmlTag

object ContrastAnalyzer {

    const val MinContrastRatio = 4.5

    fun analyze(context: AnalysisContext, tag: XmlTag): Double? {
        val textColorValue = tag.getAttributeValue("android:textColor")
        val backgroundValue = tag.getAttributeValue("android:background")
        val foregroundColor = ColorResolver.resolveColor(context.project, textColorValue)
        val backgroundColor = ColorResolver.resolveColor(context.project, backgroundValue)

        if (foregroundColor == null || backgroundColor == null) {
            return null
        }

        val contrast = ContrastCalculator.calculateContrast(foregroundColor, backgroundColor)
        return contrast.takeIf { it < MinContrastRatio }
    }
}
