package com.example.inclusiveuianalyzer.core.utils.xml

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import java.awt.Color

object ColorResolver {

    fun resolveColor(project: Project, value: String?): Color? {
        if (value == null) return null

        // #FFFFFF
        if (value.startsWith("#")) {
            return try {
                Color.decode(value)
            } catch (e: Exception) {
                null
            }
        }

        // @color/...
        if (value.startsWith("@color/")) {
            val colorName = value.substringAfter("/")

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
                        return try {
                            Color.decode(hex)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }

        return null
    }
}