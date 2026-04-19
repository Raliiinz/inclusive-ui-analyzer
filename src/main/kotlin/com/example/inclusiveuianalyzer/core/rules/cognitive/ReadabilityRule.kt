package com.example.inclusiveuianalyzer.core.rules.cognitive

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.XmlTagRule
import com.example.inclusiveuianalyzer.core.utils.xml.StringResourceUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile

class ReadabilityRule : XmlTagRule(
    profile = Profile.COGNITIVE,
    target = AnalysisTarget.ALL_XML
) {

    override fun check(context: AnalysisContext): List<Issue> {
        val file = context.file as? XmlFile ?: return emptyList()

        return if (StringResourceUtils.isStringsFile(file)) {
            analyzeStringResources(file)
        } else {
            analyzeXmlTags(context)
        }
    }

    private fun analyzeStringResources(file: XmlFile): List<Issue> {
        return StringResourceUtils.extractStrings(file)
            .mapNotNull { (text, tag) -> analyzeText(text, tag) }
    }

    private fun analyzeXmlTags(context: AnalysisContext): List<Issue> {
        val issues = mutableListOf<Issue>()

        visitTags(context) { tag ->
            val text = tag.getAttributeValue("android:text") ?: return@visitTags
            if (text.startsWith("@string/")) {
                return@visitTags
            }

            analyzeText(text, tag)?.let(issues::add)
        }

        return issues
    }

    private fun analyzeText(text: String, element: PsiElement): Issue? {
        val state = ReadabilityAnalyzer.analyze(text)
        return if (state.isHard()) {
            buildIssue(buildMessage(text, state), element)
        } else {
            null
        }
    }

    private fun buildMessage(text: String, state: ReadabilityState): String {
        return buildString {
            append("Text is hard to read.\n")
            append("Text: \"$text\"\n")
            append("Problems: ${state.reasons.joinToString()}")
        }
    }
}
