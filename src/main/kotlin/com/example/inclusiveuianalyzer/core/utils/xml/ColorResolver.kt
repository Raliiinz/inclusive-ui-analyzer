package com.example.inclusiveuianalyzer.core.utils.xml

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import java.awt.Color

object ColorResolver {

    fun resolveColor(project: Project, value: String?): Color? {
        val normalized = value?.trim() ?: return null

        if (normalized.startsWith("#")) {
            return parseHexColor(normalized)
        }

        // @color/...
        if (normalized.startsWith("@color/")) {
            val colorName = normalized.substringAfter("/")

            val files = FilenameIndex.getFilesByName(
                project,
                "colors.xml",
                GlobalSearchScope.projectScope(project)
            )

            files.forEach { file ->
                val xml = file as? XmlFile ?: return@forEach

                xml.rootTag?.findSubTags("color")?.forEach { tag ->
                    if (tag.getAttributeValue("name") == colorName) {
                        val hex = tag.value.text.trim()
                        return parseHexColor(hex)
                    }
                }
            }
        }

        return null
    }

    private fun parseHexColor(value: String): Color? {
        val hex = value.removePrefix("#")
        if (hex.any { it !in "0123456789abcdefABCDEF" }) {
            return null
        }

        val expanded = when (hex.length) {
            3 -> hex.map { "$it$it" }.joinToString("")
            4 -> hex.map { "$it$it" }.joinToString("")
            6, 8 -> hex
            else -> return null
        }

        return try {
            if (expanded.length == 8) {
                Color(
                    expanded.substring(2, 4).toInt(16),
                    expanded.substring(4, 6).toInt(16),
                    expanded.substring(6, 8).toInt(16),
                    expanded.substring(0, 2).toInt(16)
                )
            } else {
                Color(
                    expanded.substring(0, 2).toInt(16),
                    expanded.substring(2, 4).toInt(16),
                    expanded.substring(4, 6).toInt(16)
                )
            }
        } catch (_: NumberFormatException) {
            null
        }
    }
}
