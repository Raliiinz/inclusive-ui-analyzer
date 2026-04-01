package com.example.inclusiveuianalyzer.core.utils.xml

object DimensionParser {

    fun parseDp(value: String?): Float? {
        if (value.isNullOrBlank()) return null
        if (value == "match_parent" || value == "wrap_content") return null

        return value.removeSuffix("dp").toFloatOrNull()
    }
}
