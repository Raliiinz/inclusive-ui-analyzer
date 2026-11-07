package com.example.inclusiveuianalyzer.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class AddComposeTouchTargetQuickFix : LocalQuickFix {

    override fun getFamilyName(): String {
        return "Add minimum 48dp Compose touch target"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val call = descriptor.psiElement as? KtCallExpression ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            applyToCall(project, call)
        }
    }

    companion object {
        private const val SIZE_IN_IMPORT = "androidx.compose.foundation.layout.sizeIn"
        private const val DP_IMPORT = "androidx.compose.ui.unit.dp"
        private const val SIZE_IN_PREFIX =
            "Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)"

        fun applyToCall(project: Project, call: KtCallExpression): Boolean {
            val modifierArgument = call.valueArguments.firstOrNull {
                it.getArgumentName()?.asName?.identifier == "modifier"
            } ?: return false
            val modifierExpression = modifierArgument.getArgumentExpression() ?: return false
            val modifierText = modifierExpression.text.trim()
            if (!modifierText.startsWith("Modifier")) return false
            if (modifierText.contains("sizeIn(") ||
                modifierText.contains("minimumInteractiveComponentSize()")
            ) {
                return false
            }

            val file = call.containingKtFile
            if (!ensureImports(project, file)) return false

            val psiFactory = KtPsiFactory(project)
            val replacement = psiFactory.createExpression(
                SIZE_IN_PREFIX + modifierText.removePrefix("Modifier")
            )
            modifierExpression.replace(replacement)
            return true
        }

        private fun ensureImports(project: Project, file: KtFile): Boolean {
            val requiredImports = listOf(SIZE_IN_IMPORT, DP_IMPORT)
            val missingImports = requiredImports.filter { requiredImport ->
                file.importDirectives.none { it.importPath?.pathStr == requiredImport }
            }
            if (missingImports.isEmpty()) return true

            val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return false
            val importList = file.importList
            val packageDirective = file.packageDirective
            val importsText = missingImports.joinToString("\n") { "import $it" }

            if (importList != null) {
                document.insertString(importList.textRange.endOffset, "\n$importsText")
            } else if (packageDirective != null) {
                document.insertString(packageDirective.textRange.endOffset, "\n\n$importsText")
            } else {
                document.insertString(0, "$importsText\n\n")
            }

            PsiDocumentManager.getInstance(project).commitDocument(document)
            return true
        }
    }
}
