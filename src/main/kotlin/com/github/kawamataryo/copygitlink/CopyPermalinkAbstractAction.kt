package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.text.StringUtil.first
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.speedSearch.SpeedSearchUtil.applySpeedSearchHighlighting
import com.intellij.util.Consumer
import com.intellij.vcs.commit.message.CommitMessageInspectionProfile.getSubjectRightMargin
import git4idea.i18n.GitBundle
import org.jetbrains.annotations.Nls
import showNotification
import javax.swing.JList
import javax.swing.ListSelectionModel.SINGLE_SELECTION

abstract class CopyPermalinkAbstractAction : AnAction() {
    abstract fun doCopy(gitLink: GitLink, selected: String, project: Project)

    override fun actionPerformed(e: AnActionEvent) {
        val gitLink = GitLink(e)
        val project = gitLink.project
        val caret: Caret = gitLink.caret
        val repositoriesPath = gitLink.repositoriesPath

        when (repositoriesPath.size) {
            0 -> showNotification(
                gitLink.project,
                NotificationType.ERROR,
                "Error",
                "Copy failed.\n You will need to set up a GitHub remote repository to run it."
            )

            1 -> doCopy(gitLink, repositoriesPath.first(), project)
            else -> createPopup(project, caret, repositoriesPath) { permalink ->
                    // Copy to clipboard
                    doCopy(gitLink, permalink, project)
                }
                .showInBestPositionFor(caret.editor)
        }
    }

    private fun createPopup(project: Project, caret: Caret, messages: List<String>, callback: Consumer<in String>): JBPopup {
        val rightMargin = getSubjectRightMargin(project)
        return JBPopupFactory.getInstance().createPopupChooserBuilder(messages)
            .setFont(caret.editor.colorsScheme.getFont(EditorFontType.PLAIN))
            .setVisibleRowCount(5)
            .setTitle(GitBundle.message("remotes.dialog.title"))
            .setSelectionMode(SINGLE_SELECTION)
            .setItemChosenCallback(callback)
            .setRenderer(object : ColoredListCellRenderer<String>() {
                override fun customizeCellRenderer(
                    list: JList<out String>,
                    value: @Nls String,
                    index: Int,
                    selected: Boolean,
                    hasFocus: Boolean
                ) {
                    append(first(value, rightMargin, false))

                    applySpeedSearchHighlighting(list, this, true, selected)
                }
            })
            .setNamerForFiltering { it }
            .setAutoPackHeightOnFiltering(false)
            .createPopup()
    }
}