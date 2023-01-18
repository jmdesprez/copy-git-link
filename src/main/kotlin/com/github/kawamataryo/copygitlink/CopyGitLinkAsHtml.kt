package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import copyToClipboard
import showNotification
import truncateText

class CopyPermalinkAsHtml : CopyPermalinkAbstractAction() {

    override fun doCopy(gitLink: GitLink, selected: String, project: Project, event: AnActionEvent) {
        val permalink = gitLink.toGitLink(selected)
        val linkText = gitLink.relativePath + gitLink.linePath
        // Copy to clipboard
        copyToClipboard("<a href='$permalink'>$linkText</a>")

        showNotification(
            gitLink.project,
            NotificationType.INFORMATION,
            "Copied permalink.",
            "<a href='$permalink'>${truncateText(permalink, 45)}</a>."
        )
    }

}
