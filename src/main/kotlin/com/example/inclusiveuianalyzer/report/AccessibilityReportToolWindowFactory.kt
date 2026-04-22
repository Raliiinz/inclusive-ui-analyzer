package com.example.inclusiveuianalyzer.report

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class AccessibilityReportToolWindowFactory : ToolWindowFactory, DumbAware {

    private val logger = Logger.getInstance(AccessibilityReportToolWindowFactory::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        logger.info("Creating Accessibility Report tool window for project: ${project.name}")
        val panel = AccessibilityReportPanel(project)
        val content = ContentFactory.getInstance()
            .createContent(panel, "", false)

        content.setDisposer(panel)
        toolWindow.contentManager.addContent(content)
        logger.info("Accessibility Report tool window content added successfully")
    }
}
