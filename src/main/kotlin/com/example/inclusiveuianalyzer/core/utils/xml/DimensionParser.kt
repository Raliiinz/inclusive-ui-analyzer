package com.example.inclusiveuianalyzer.core.utils.xml

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile

object DimensionParser {

    fun parseDp(
        value: String?,
        project: Project? = null,
        resolving: Set<String> = emptySet()
    ): Float? {
        val normalized = value?.trim() ?: return null
        if (normalized.isBlank()) return null
        if (normalized == "match_parent" || normalized == "wrap_content") return null

        if (normalized.startsWith("@dimen/")) {
            val name = normalized.substringAfter("@dimen/")
            if (name in resolving) return null
            return resolveResource(name, project, resolving + name)
        }

        val literal = normalized
            .removeSuffix("dip")
            .removeSuffix("dp")
        return literal.toFloatOrNull()
    }

    private fun resolveResource(name: String, project: Project?, resolving: Set<String>): Float? {
        if (project == null || name.isBlank()) return null

        val scope = GlobalSearchScope.projectScope(project)
        val files = FilenameIndex.getFilesByName(project, "dimens.xml", scope)
        for (file in files) {
            val xml = file as? XmlFile ?: continue
            val dimension = xml.rootTag
                ?.findSubTags("dimen")
                ?.firstOrNull { it.getAttributeValue("name") == name }
                ?: continue
            return parseDp(dimension.value.text, project, resolving)
        }

        return null
    }
}
