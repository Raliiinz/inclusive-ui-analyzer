package com.example.inclusiveuianalyzer.inspection.fixes

import com.example.inclusiveuianalyzer.core.utils.xml.XmlAttributeUtils
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlTag

object XmlQuickFixSupport {

    fun generateContentDescription(tag: XmlTag): String {
        val idText = XmlAttributeUtils.getId(tag)
            ?.let(::normalizeWords)
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        val sourceText = tag.getAttributeValue("android:src")
            ?.substringAfterLast('/')
            ?.removePrefix("ic_")
            ?.removePrefix("img_")
            ?.let(::normalizeWords)
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        val baseText = idText ?: sourceText ?: tag.name

        return baseText
            .split(' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { char -> char.uppercase() }
            }
    }

    private fun normalizeWords(value: String): String {
        return value
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("\\s+"), " ")
    }

    fun formatTagAttributes(project: Project, tag: XmlTag) {
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(tag.containingFile) ?: return
        val tagText = tag.text
        val openingTagEnd = tagText.indexOf('>').takeIf { it >= 0 } ?: return
        val openingTagText = tagText.substring(0, openingTagEnd + 1)
        val closingMarker = if (openingTagText.trimEnd().endsWith("/>")) "/>" else ">"
        val indent = currentLineIndent(document.charsSequence, tag.textRange.startOffset)
        val attributeIndent = "$indent    "
        val attributes = tag.attributes.map { it.text }

        if (attributes.isEmpty()) {
            return
        }

        val formattedOpeningTag = buildString {
            append("<")
            append(tag.name)
            append("\n")
            append(attributeIndent)
            append(attributes.joinToString("\n$attributeIndent"))
            append(indent)
            append(closingMarker)
        }

        document.replaceString(
            tag.textRange.startOffset,
            tag.textRange.startOffset + openingTagText.length,
            formattedOpeningTag
        )
        documentManager.commitDocument(document)
    }

    private fun currentLineIndent(text: CharSequence, offset: Int): String {
        var lineStart = offset
        while (lineStart > 0 && text[lineStart - 1] != '\n') {
            lineStart--
        }

        val indent = StringBuilder()
        var cursor = lineStart
        while (cursor < text.length && (text[cursor] == ' ' || text[cursor] == '\t')) {
            indent.append(text[cursor])
            cursor++
        }

        return indent.toString()
    }
}
