package com.example.inclusiveuianalyzer.core.rules.cognitive

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.core.model.Severity
import com.example.inclusiveuianalyzer.core.rules.AnalysisTarget
import com.example.inclusiveuianalyzer.core.rules.xml.XmlRuleBase
import com.example.inclusiveuianalyzer.core.utils.xml.StringResourceUtils
import com.intellij.psi.xml.XmlFile

class ReadabilityRule : XmlRuleBase() {

    override val profile = Profile.COGNITIVE
    override val target = AnalysisTarget.ALL_XML

    override fun check(context: AnalysisContext): List<Issue> {
        println("🔥 READABILITY RULE ENTERED")
        val issues = mutableListOf<Issue>()

        val file = context.file as? XmlFile ?: return emptyList()

        if (StringResourceUtils.isStringsFile(file)) {
            val strings = StringResourceUtils.extractStrings(file)
            for ((text, tag) in strings) {
                val state = ReadabilityAnalysisEngine.analyzeText(text)

                println("TEXT: $text")
                println("STATE: $state")
                println("IS HARD: ${state.isHard()}")

                if (state.isHard()) {
                    issues.add(
                        Issue(
                            buildMessage(text, state.reasons),
                            tag,
                            Severity.WARNING,
                            profile
                        )
                    )
                }
            }

            return issues
        }

        visitTags(context) { tag ->
            val text = tag.getAttributeValue("android:text") ?: return@visitTags
            if (text.startsWith("@string/")) return@visitTags
            val state = ReadabilityAnalysisEngine.analyzeText(text)

            println("TEXT: $text")
            println("STATE: $state")
            println("IS HARD: ${state.isHard()}")

            if (state.isHard()) {
                issues.add(
                    Issue(
                        buildMessage(text, state.reasons),
                        tag,
                        Severity.WARNING,
                        profile
                    )
                )
            }
        }

        return issues
    }

    private fun buildMessage(text: String, reasons: List<String>): String {
        return buildString {
            append("Text is hard to read.\n")
            append("Text: \"$text\"\n")
            append("Problems: ${reasons.joinToString()}")
        }
    }
}