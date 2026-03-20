package com.example.inclusiveuianalyzer.analyzers.xml

import java.awt.Color

object ContrastCalculator {

    /**
     * Вычисляет контраст между двумя цветами по WCAG формуле
     * @return значение контраста (1.0 — минимальный, 21.0 — максимальный)
     */
    fun calculateContrast(foreground: Color, background: Color): Double {
        fun linearize(value: Double): Double {
            return if (value <= 0.03928) value / 12.92 else Math.pow((value + 0.055) / 1.055, 2.4)
        }

        fun luminance(c: Color): Double {
            val r = linearize(c.red / 255.0)
            val g = linearize(c.green / 255.0)
            val b = linearize(c.blue / 255.0)
            return 0.2126 * r + 0.7152 * g + 0.0722 * b
        }

        val l1 = luminance(foreground)
        val l2 = luminance(background)
        return (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05)
    }
}