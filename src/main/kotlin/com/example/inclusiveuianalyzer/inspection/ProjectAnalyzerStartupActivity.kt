package com.example.inclusiveuianalyzer.inspection

import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngine
import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngineHolder
import com.example.inclusiveuianalyzer.core.model.Profile
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile

class ProjectAnalyzerStartupActivity : StartupActivity.DumbAware {

    private val logger = Logger.getInstance(ProjectAnalyzerStartupActivity::class.java)
    private val engine = AnalyzerEngineHolder.instance

    override fun runActivity(project: Project) {
        logger.info(">>> Project-wide analysis STARTED <<<")

        ApplicationManager.getApplication().executeOnPooledThread {
            analyzeAllXmlFiles(project)
        }
    }

    private fun analyzeAllXmlFiles(project: Project) {
        val psiManager = PsiManager.getInstance(project)
        val scope = GlobalSearchScope.projectScope(project)

        val xmlFiles: Collection<VirtualFile> = FileTypeIndex.getFiles(XmlFileType.INSTANCE, scope)

        xmlFiles.forEach { virtualFile ->
            val psiFile = psiManager.findFile(virtualFile) as? XmlFile ?: return@forEach
            val issues = engine.analyze(psiFile, Profile.COGNITIVE)

            issues.forEach { issue ->
                logger.warn("File: ${psiFile.name}, Element: ${issue.element}, Issue: ${issue.message}")
            }
        }

        logger.info(">>> Project-wide analysis FINISHED <<<")
    }
}
