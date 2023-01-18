package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import copyToClipboard
import showNotification
import truncateText


class CopyPermalinkAsMarkdown : CopyPermalinkAbstractAction() {

    override fun doCopy(gitLink: GitLink, selected: String, project: Project, event: AnActionEvent) {
        val linkText = gitLink.relativePath + gitLink.linePath
        val permalink = gitLink.toGitLink(selected)

        val markdownLink = "[$linkText]($permalink)"

        copyToClipboard(markdownLink)

        showNotification(
            project,
            NotificationType.INFORMATION,
            "Copied permalink as Markdown.",
            "<a href='$permalink'>${truncateText(linkText, 45)}</a>."
        )
    }
}
