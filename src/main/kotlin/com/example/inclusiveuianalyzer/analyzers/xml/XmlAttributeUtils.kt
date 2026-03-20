package com.example.inclusiveuianalyzer.analyzers.xml

import com.intellij.psi.xml.XmlTag

object XmlAttributeUtils {

    /** Проверяет, есть ли у тега атрибут с указанным именем */
    fun hasAttribute(tag: XmlTag, name: String): Boolean {
        return tag.getAttribute(name) != null
    }

    /** Получает значение атрибута или null */
    fun getAttributeValue(tag: XmlTag, name: String): String? {
        return tag.getAttributeValue(name)
    }

    /** Проверяет, что атрибут не пустой */
    fun isAttributeNonEmpty(tag: XmlTag, name: String): Boolean {
        return !getAttributeValue(tag, name).isNullOrBlank()
    }
}