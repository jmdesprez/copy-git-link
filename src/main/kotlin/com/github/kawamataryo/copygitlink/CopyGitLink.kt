package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import copyToClipboard
import showNotification
import truncateText

class CopyPermalink : CopyPermalinkAbstractAction() {

    override fun doCopy(gitLink: GitLink, selected: String, project: Project) {
        val permalink = gitLink.toGitLink(selected)
        copyToClipboard(permalink)

        showNotification(
            project,
            NotificationType.INFORMATION,
            "Copied permalink.",
            "<a href='$permalink'>${truncateText(permalink, 45)}</a>."
        )
    }
}
