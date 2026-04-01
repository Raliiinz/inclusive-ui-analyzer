package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.rules.compose.ClearAndSetSemanticsRule
import com.example.inclusiveuianalyzer.core.rules.compose.SemanticsRule
import com.example.inclusiveuianalyzer.core.rules.compose.TextAlternativeRule
import com.example.inclusiveuianalyzer.core.rules.xml.ContentDescriptionRule
import com.example.inclusiveuianalyzer.core.rules.xml.ContrastRule
import com.example.inclusiveuianalyzer.core.rules.xml.ImportantForAccessibilityRule
import com.example.inclusiveuianalyzer.core.rules.xml.TouchTargetSizeRule
import com.example.inclusiveuianalyzer.core.utils.FileTypeUtils
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
    }

    fun runRules(context: AnalysisContext): List<Issue> {
        return rules
            .filter { it.profile == context.profile }
            .filter { isApplicable(it, context.file) }
            .flatMap { it.check(context) }
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
