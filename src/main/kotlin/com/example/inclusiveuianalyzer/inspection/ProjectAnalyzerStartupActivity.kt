package com.example.inclusiveuianalyzer.inspection

import com.example.inclusiveuianalyzer.report.AnalysisReportService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ProjectAnalyzerStartupActivity : StartupActivity.DumbAware {

    private val logger = Logger.getInstance(ProjectAnalyzerStartupActivity::class.java)

    override fun runActivity(project: Project) {
        logger.info(">>> Project-wide analysis STARTED for project '${project.name}' <<<")
        logger.info("Accessibility Report tool window should appear inside the IDE where this plugin is running")
        project.getService(AnalysisReportService::class.java).refreshReport()
        logger.info(">>> Project-wide analysis refresh requested <<<")
    }
}
