package com.example.inclusiveuianalyzer.analyzers.xml

object DimensionParser {

    /** Преобразует строку размера (dp, sp, px) в float для сравнения */
    fun parseDimension(value: String?): Float? {
        if (value.isNullOrBlank()) return null
        return when {
            value.endsWith("dp") -> value.removeSuffix("dp").toFloat()
            value.endsWith("sp") -> value.removeSuffix("sp").toFloat()
            value.endsWith("px") -> value.removeSuffix("px").toFloat()
            else -> null
        }
    }
}