package com.example.inclusiveuianalyzer.core.utils.xml

import com.intellij.psi.xml.XmlTag

object XmlAttributeUtils {

    fun getAttributeValue(tag: XmlTag, name: String): String? {
        return tag.getAttributeValue(name)
    }

    fun isAttributeNonEmpty(tag: XmlTag, name: String): Boolean {
        return !getAttributeValue(tag, name).isNullOrBlank()
    }

    fun getId(tag: XmlTag): String? {
        return tag.getAttributeValue("android:id")
            ?.substringAfter("/")
    }

    fun buildTagDescription(tag: XmlTag): String {
        val name = tag.name
        val id = getId(tag)

        return buildString {
            append(name)
            id?.let { append("#$it") }
        }
    }
}
