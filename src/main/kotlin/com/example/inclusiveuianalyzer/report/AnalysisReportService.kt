package com.example.inclusiveuianalyzer.report

import com.example.inclusiveuianalyzer.core.engine.AnalyzerEngineHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiManager
import com.intellij.psi.SmartPointerManager
import java.util.concurrent.CopyOnWriteArrayList

@Service(Service.Level.PROJECT)
class AnalysisReportService(private val project: Project) {

    private val logger = Logger.getInstance(AnalysisReportService::class.java)

    private val engine = AnalyzerEngineHolder.instance
    private val listeners = CopyOnWriteArrayList<(AnalysisReport) -> Unit>()

    @Volatile
    private var currentReport: AnalysisReport = AnalysisReport()

    fun getReport(): AnalysisReport = currentReport

    fun addListener(listener: (AnalysisReport) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (AnalysisReport) -> Unit) {
        listeners.remove(listener)
    }

    fun refreshReport() {
        logger.info("Starting report refresh for project: ${project.name}")
        ApplicationManager.getApplication().executeOnPooledThread {
            val report = buildReport()
            currentReport = report
            logger.info("Report refresh finished. Total issues: ${report.issues.size}")
            ApplicationManager.getApplication().invokeLater {
                logger.info("Publishing report to ${listeners.size} listeners")
                listeners.forEach { listener -> listener(report) }
                val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Accessibility Report")
                if (toolWindow == null) {
                    logger.warn("Accessibility Report tool window is not registered or not created yet")
                } else {
                    logger.info("Accessibility Report tool window found. Showing it now.")
                    toolWindow.show()
                }
            }
        }
    }

    private fun buildReport(): AnalysisReport {
        logger.info("Collecting project files for accessibility analysis")

        val psiManager = PsiManager.getInstance(project)

        val files = mutableListOf<VirtualFile>()

        VfsUtilCore.iterateChildrenRecursively(project.baseDir, null) { file ->
            if (!file.isDirectory) {
                val ext = file.extension
                if (ext == "kt" || ext == "xml") {
                    files.add(file)
                }
            }
            true
        }

        logger.info("Collected ${files.size} files")

        val issues = ApplicationManager.getApplication().runReadAction<List<ReportedIssue>> {
            files.flatMap { vf ->
                val psiFile = psiManager.findFile(vf) ?: return@flatMap emptyList()
                engine.analyze(psiFile).map { it.toReportedIssue(project) }
            }
        }

        return AnalysisReport(issues)
    }

    private fun com.example.inclusiveuianalyzer.core.model.Issue.toReportedIssue(project: Project): ReportedIssue {
        val element = element
        val virtualFile = element.containingFile?.virtualFile
        val lineNumber = virtualFile?.let { file ->
            FileDocumentManager.getInstance()
                .getDocument(file)
                ?.getLineNumber(element.textOffset)
                ?.plus(1)
        }

        return ReportedIssue(
            code = code,
            message = message,
            profile = profile,
            severity = severity,
            fileName = virtualFile?.name ?: element.containingFile?.name ?: "Unknown file",
            lineNumber = lineNumber,
            pointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(element)
        )
    }
}
