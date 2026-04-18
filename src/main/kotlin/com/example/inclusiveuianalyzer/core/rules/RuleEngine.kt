package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.rules.audio.AudioUsageRule
import com.example.inclusiveuianalyzer.core.rules.cognitive.ReadabilityRule
import com.example.inclusiveuianalyzer.core.rules.compose.ClearAndSetSemanticsRule
import com.example.inclusiveuianalyzer.core.rules.compose.SemanticsRule
import com.example.inclusiveuianalyzer.core.rules.compose.TextAlternativeRule
import com.example.inclusiveuianalyzer.core.rules.xml.ContentDescriptionRule
import com.example.inclusiveuianalyzer.core.rules.xml.ContrastRule
import com.example.inclusiveuianalyzer.core.rules.xml.ImportantForAccessibilityRule
import com.example.inclusiveuianalyzer.core.rules.xml.TouchTargetSizeRule
import com.example.inclusiveuianalyzer.core.utils.FileTypeUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiFile

class RuleEngine {

    private val rules = mutableListOf<Rule>()

    init {
        // XML
        rules.add(ContentDescriptionRule())
        rules.add(ImportantForAccessibilityRule())
        rules.add(TouchTargetSizeRule())
        rules.add(ContrastRule())

        // Compose
        rules.add(SemanticsRule())
        rules.add(ClearAndSetSemanticsRule())
        rules.add(TextAlternativeRule())

        // Audio
        rules.add(AudioUsageRule())

        // Cognitive
        rules.add(ReadabilityRule())
    }

//    fun runRules(context: AnalysisContext): List<Issue> {
//        return rules
//            .filter { it.profile == context.profile }
//            .filter { isApplicable(it, context.file) }
//            .flatMap { it.check(context) }
//    }
//    fun runRules(context: AnalysisContext): List<Issue> {
//        return ApplicationManager.getApplication()
//            .runReadAction<List<Issue>> {
//
//                rules
//                    .filter { it.profile == context.profile }
//                    .filter { isApplicable(it, context.file) }
//                    .flatMap { it.check(context) }
//            }
//    }

    fun runRules(context: AnalysisContext): List<Issue> {

        println("🔥 RULEENGINE CALLED")
        println("🔥 CONTEXT PROFILE = ${context.profile}")
        println("🔥 CONTEXT FILE = ${context.file.name}")

        val filtered = rules
            .onEach {
                println("➡️ RULE BEFORE FILTER: ${it.javaClass.simpleName} / profile=${it.profile}")
            }
            .filter { it.profile == context.profile }
            .onEach {
                println("✅ RULE AFTER PROFILE FILTER: ${it.javaClass.simpleName}")
            }
            .filter { isApplicable(it, context.file) }
            .onEach {
                println("🎯 RULE AFTER APPLICABILITY: ${it.javaClass.simpleName}")
            }

        println("🔥 FINAL RULES = ${filtered.map { it.javaClass.simpleName }}")

        val result = filtered.flatMap { rule ->
            println("🚀 EXECUTING RULE = ${rule.javaClass.simpleName}")
            rule.check(context)
        }

        println("🔥 TOTAL ISSUES = ${result.size}")

        return result
    }

    private fun isApplicable(rule: Rule, file: PsiFile): Boolean {
        val vf = file.virtualFile ?: return false

        return when (rule.target) {
            AnalysisTarget.ANDROID_LAYOUT_XML -> isLayoutFile(vf)
            AnalysisTarget.KOTLIN_CLASS -> FileTypeUtils.isKotlin(file)
            AnalysisTarget.JAVA_CLASS -> FileTypeUtils.isJava(file)
            AnalysisTarget.ALL_XML -> FileTypeUtils.isXml(file)
            AnalysisTarget.ANDROID_MANIFEST -> file.name == "AndroidManifest.xml"
            AnalysisTarget.JETPACK_COMPOSE -> isComposeFile(file)
        }
    }

    private fun isLayoutFile(file: com.intellij.openapi.vfs.VirtualFile): Boolean {
        var current = file.parent
        while (current != null) {
            if (current.name.startsWith("layout") && current.parent?.name == "res") {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun isComposeFile(file: PsiFile): Boolean {
        if (!FileTypeUtils.isKotlin(file)) return false
        val text = file.text ?: return false
        return text.contains("@Composable")
    }
}
