package com.example.inclusiveuianalyzer.report

import com.example.inclusiveuianalyzer.core.model.Profile
import com.example.inclusiveuianalyzer.inspection.fixes.AccessibilityIssueFixApplier
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BoxLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JTextArea
import javax.swing.JTree
import javax.swing.event.TreeSelectionEvent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class AccessibilityReportPanel(
    private val project: Project
) : JBPanel<AccessibilityReportPanel>(BorderLayout()), Disposable {

    private val logger = Logger.getInstance(AccessibilityReportPanel::class.java)

    private val reportService = project.getService(AnalysisReportService::class.java)
    private val treeRoot = DefaultMutableTreeNode("Accessibility Report")
    private val treeModel = DefaultTreeModel(treeRoot)
    private val tree = Tree(treeModel)
    private val detailsArea = JTextArea()
    private val summaryLabel = JBLabel("Analyzing project...")
    private val refreshButton = JButton("Refresh", AllIcons.Actions.Refresh)
    private val applyFixButton = JButton("Apply Fix", AllIcons.Actions.QuickfixBulb)
    private val applyFileFixesButton = JButton("Fix File", AllIcons.Actions.QuickfixBulb)
    private val applySelectionFixesButton = JButton("Fix Selection", AllIcons.Actions.QuickfixBulb)
    private val applyAllFixesButton = JButton("Fix All Safe", AllIcons.Actions.QuickfixBulb)
    private val listener: (AnalysisReport) -> Unit = { report -> renderReport(report) }

    init {
        logger.info("Initializing AccessibilityReportPanel for project: ${project.name}")
        border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        add(buildToolbar(), BorderLayout.NORTH)
        add(buildContent(), BorderLayout.CENTER)

        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.cellRenderer = ReportTreeCellRenderer()
        tree.selectionModel.selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION

        detailsArea.isEditable = false
        detailsArea.lineWrap = true
        detailsArea.wrapStyleWord = true
        detailsArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        detailsArea.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
        detailsArea.text = "Select a category or issue to see details."

        refreshButton.addActionListener {
            logger.info("Manual refresh triggered from Accessibility Report panel")
            summaryLabel.text = "Refreshing analysis..."
            reportService.refreshReport()
        }

        applyFixButton.isEnabled = false
        applyFixButton.addActionListener {
            val issue = getSelectedIssue() ?: return@addActionListener
            logger.info("Applying fix from report panel for issue ${issue.code} in ${issue.fileName}")
            if (AccessibilityIssueFixApplier.apply(project, issue)) {
                detailsArea.text = buildIssueDetails(issue) + "\n\nFix applied. Use Undo (Cmd/Ctrl+Z) to revert."
                reportService.refreshReport()
            }
        }

        applyFileFixesButton.isEnabled = false
        applyFileFixesButton.addActionListener {
            val issues = getIssuesForCurrentFile()
            val appliedCount = AccessibilityIssueFixApplier.applyAll(
                project,
                issues,
                "Apply Accessibility Fixes in File"
            )
            if (appliedCount > 0) {
                detailsArea.text = "Applied $appliedCount safe fixes in the current file.\n\nUse Undo (Cmd/Ctrl+Z) to revert."
                reportService.refreshReport()
            }
        }

        applySelectionFixesButton.isEnabled = false
        applySelectionFixesButton.addActionListener {
            val issues = getIssuesFromSelection()
            val appliedCount = AccessibilityIssueFixApplier.applyAll(
                project,
                issues,
                "Apply Accessibility Fixes in Selection"
            )
            if (appliedCount > 0) {
                detailsArea.text = "Applied $appliedCount safe fixes in the current selection.\n\nUse Undo (Cmd/Ctrl+Z) to revert."
                reportService.refreshReport()
            }
        }

        applyAllFixesButton.isEnabled = false
        applyAllFixesButton.addActionListener {
            val issues = reportService.getReport().issues
            val appliedCount = AccessibilityIssueFixApplier.applyAll(
                project,
                issues,
                "Apply All Safe Accessibility Fixes"
            )
            if (appliedCount > 0) {
                detailsArea.text = "Applied $appliedCount safe fixes in the whole project.\n\nUse Undo (Cmd/Ctrl+Z) to revert."
                reportService.refreshReport()
            }
        }

        tree.addTreeSelectionListener(::onSelectionChanged)
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                if (event.clickCount == 2) {
                    openSelectedIssue()
                }
            }
        })

        reportService.addListener(listener)
        logger.info("AccessibilityReportPanel subscribed to report updates")
        renderReport(reportService.getReport())
    }

    private fun buildToolbar(): JComponent {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(0, 0, 8, 0)
        panel.add(summaryLabel, BorderLayout.WEST)
        val actions = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.RIGHT, 8, 0))
        actions.add(applyFixButton)
        actions.add(applyFileFixesButton)
        actions.add(applySelectionFixesButton)
        actions.add(applyAllFixesButton)
        actions.add(refreshButton)
        panel.add(actions, BorderLayout.EAST)
        return panel
    }

    private fun buildContent(): JComponent {
        val splitter = OnePixelSplitter(false, 0.42f)
        splitter.firstComponent = JBScrollPane(tree)
        val detailsPanel = JBPanel<JBPanel<*>>()
        detailsPanel.layout = BoxLayout(detailsPanel, BoxLayout.Y_AXIS)
        detailsPanel.add(ScrollPaneFactory.createScrollPane(detailsArea))
        splitter.secondComponent = detailsPanel
        return splitter
    }

    private fun onSelectionChanged(@Suppress("UNUSED_PARAMETER") event: TreeSelectionEvent) {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return
        updateBulkActionButtons()
        when (val userObject = node.userObject) {
            is ReportedIssue -> {
                detailsArea.text = buildIssueDetails(userObject)
                applyFixButton.isEnabled = AccessibilityIssueFixApplier.canApply(userObject)
            }
            is ProfileSummary -> {
                detailsArea.text = buildProfileDetails(userObject)
                applyFixButton.isEnabled = false
            }
            is FileSummary -> {
                detailsArea.text = buildFileDetails(userObject)
                applyFixButton.isEnabled = false
            }
            else -> {
                detailsArea.text = "Select a category or issue to see details."
                applyFixButton.isEnabled = false
            }
        }
    }

    private fun renderReport(report: AnalysisReport) {
        logger.info("Rendering report in panel: ${report.issues.size} issues")
        treeRoot.removeAllChildren()

        if (report.isEmpty()) {
            logger.info("Report is empty, showing empty state")
            summaryLabel.text = "No accessibility issues found"
            applyFixButton.isEnabled = false
            applyFileFixesButton.isEnabled = false
            applySelectionFixesButton.isEnabled = false
            applyAllFixesButton.isEnabled = false
            treeRoot.add(DefaultMutableTreeNode("No issues"))
            treeModel.reload()
            expandAll()
            return
        }

        val byProfile = report.issues.groupBy { it.profile }
        var totalFiles = 0

        Profile.entries.forEach { profile ->
            val profileIssues = byProfile[profile].orEmpty()
            if (profileIssues.isEmpty()) {
                return@forEach
            }

            val profileNode = DefaultMutableTreeNode(
                ProfileSummary(profile, profileIssues.size)
            )

            val files = profileIssues.groupBy { it.fileName }
            totalFiles += files.size

            files.toSortedMap().forEach { (fileName, fileIssues) ->
                val fileNode = DefaultMutableTreeNode(FileSummary(fileName, fileIssues.size))
                fileIssues.forEach { issue ->
                    fileNode.add(DefaultMutableTreeNode(issue))
                }
                profileNode.add(fileNode)
            }

            treeRoot.add(profileNode)
        }

        summaryLabel.text = "Found ${report.issues.size} issues in $totalFiles files"
        treeModel.reload()
        expandAll()
        updateBulkActionButtons()
        logger.info("Report rendered successfully with ${report.issues.size} issues and $totalFiles files")
    }

    private fun expandAll() {
        for (row in 0 until tree.rowCount) {
            tree.expandRow(row)
        }
    }

    private fun openSelectedIssue() {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return
        val issue = node.userObject as? ReportedIssue ?: return
        val element = issue.pointer.element ?: return
        val file = element.containingFile?.virtualFile ?: return

        logger.info("Navigating to issue in file ${file.name} at offset ${element.textOffset}")
        OpenFileDescriptor(project, file, element.textOffset).navigate(true)
    }

    private fun buildIssueDetails(issue: ReportedIssue): String {
        val line = issue.lineNumber?.let { "Line: $it\n" } ?: ""
        val quickFixText = if (AccessibilityIssueFixApplier.canApply(issue)) {
            "Available actions: Apply Fix, Fix File, Fix Selection, Fix All Safe\nUndo: Cmd/Ctrl+Z\n"
        } else {
            "Available actions: no automatic fix for this issue yet\n"
        }
        return buildString {
            append("Issue Code: ${issue.code}\n")
            append("Category: ${issue.profile}\n")
            append("Severity: ${issue.severity}\n")
            append("File: ${issue.fileName}\n")
            append(line)
            append("\n")
            append(quickFixText)
            append("\n")
            append(issue.message)
            append("\n\nDouble-click this item in the tree to navigate to code.")
        }
    }

    private fun buildProfileDetails(summary: ProfileSummary): String {
        return buildString {
            append("${summary.profileLabel}\n")
            append("Issues: ${summary.issueCount}\n")
            append("\nAvailable bulk action: Fix Selection or Fix All Safe")
            append("\nUndo: Cmd/Ctrl+Z")
            append("\n\nSelect a file or issue to see more details.")
        }
    }

    private fun buildFileDetails(summary: FileSummary): String {
        return buildString {
            append("File: ${summary.fileName}\n")
            append("Issues: ${summary.issueCount}\n")
            append("\nAvailable bulk action: Fix File")
            append("\nUndo: Cmd/Ctrl+Z")
            append("\n\nSelect an issue to inspect it.")
        }
    }

    override fun dispose() {
        logger.info("Disposing AccessibilityReportPanel for project: ${project.name}")
        reportService.removeListener(listener)
    }

    private fun getSelectedIssue(): ReportedIssue? {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return null
        return node.userObject as? ReportedIssue
    }

    private fun getIssuesForCurrentFile(): List<ReportedIssue> {
        val node = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return emptyList()
        return when (node.userObject) {
            is ReportedIssue -> collectIssues((node.parent as? DefaultMutableTreeNode) ?: node)
            is FileSummary -> collectIssues(node)
            else -> emptyList()
        }
    }

    private fun getIssuesFromSelection(): List<ReportedIssue> {
        return tree.selectionPaths
            ?.flatMap { path ->
                val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return@flatMap emptyList()
                collectIssues(node)
            }
            ?.distinctBy { issueKey(it) }
            .orEmpty()
    }

    private fun collectIssues(node: DefaultMutableTreeNode): List<ReportedIssue> {
        val directIssue = node.userObject as? ReportedIssue
        if (directIssue != null) {
            return listOf(directIssue)
        }

        val result = mutableListOf<ReportedIssue>()
        val children = node.children()
        while (children.hasMoreElements()) {
            val child = children.nextElement() as? DefaultMutableTreeNode ?: continue
            result += collectIssues(child)
        }
        return result
    }

    private fun updateBulkActionButtons() {
        val currentFileIssues = getIssuesForCurrentFile()
        val selectedIssues = getIssuesFromSelection()
        val allIssues = reportService.getReport().issues

        applyFileFixesButton.isEnabled = currentFileIssues.any(AccessibilityIssueFixApplier::canApply)
        applySelectionFixesButton.isEnabled = selectedIssues.any(AccessibilityIssueFixApplier::canApply)
        applyAllFixesButton.isEnabled = allIssues.any(AccessibilityIssueFixApplier::canApply)
    }

    private fun issueKey(issue: ReportedIssue): String {
        val element = issue.pointer.element
        val filePath = element?.containingFile?.virtualFile?.path ?: issue.fileName
        val offset = element?.textOffset ?: -1
        return "${issue.code}|$filePath|$offset"
    }

    private data class ProfileSummary(
        val profile: Profile,
        val issueCount: Int
    ) {
        val profileLabel: String = when (profile) {
            Profile.VISION -> "Vision"
            Profile.HEARING -> "Hearing"
            Profile.COGNITIVE -> "Cognitive"
        }

        override fun toString(): String {
            return "$profileLabel ($issueCount)"
        }
    }

    private data class FileSummary(
        val fileName: String,
        val issueCount: Int
    ) {
        override fun toString(): String {
            return "$fileName ($issueCount)"
        }
    }

    private class ReportTreeCellRenderer : DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree?,
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ) = super.getTreeCellRendererComponent(
            tree,
            value,
            selected,
            expanded,
            leaf,
            row,
            hasFocus
        ).also {
            val node = value as? DefaultMutableTreeNode ?: return@also
            when (val userObject = node.userObject) {
                is ProfileSummary -> icon = AllIcons.Nodes.Folder
                is FileSummary -> icon = AllIcons.FileTypes.Any_type
                is ReportedIssue -> {
                    icon = when (userObject.severity) {
                        com.example.inclusiveuianalyzer.core.model.Severity.ERROR -> AllIcons.General.Error
                        com.example.inclusiveuianalyzer.core.model.Severity.WARNING -> AllIcons.General.Warning
                        com.example.inclusiveuianalyzer.core.model.Severity.INFO -> AllIcons.General.Information
                    }
                    text = buildString {
                        append(userObject.message.lineSequence().firstOrNull() ?: userObject.message)
                        userObject.lineNumber?.let { append(" (line $it)") }
                    }
                }
            }
        }
    }
}
