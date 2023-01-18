package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import copyToClipboard
import showNotification
import truncateText


class CopyAsMarkdownWithCode : CopyPermalinkAbstractAction() {
    override fun doCopy(gitLink: GitLink, selected: String, project: Project, event: AnActionEvent) {
        val permalink = gitLink.toGitLink(selected)
        val linkText = gitLink.relativePath + gitLink.linePath
        val markdownLink = "[$linkText]($permalink)"
        val source = gitLink.source;
        val editor: Editor = event.getRequiredData(CommonDataKeys.EDITOR)

        var vf: VirtualFile = event.getRequiredData(CommonDataKeys.VIRTUAL_FILE)

        val content = """$markdownLink
                |```${vf.name.split(".").last()}
                |$source
                |```
            """.trimMargin()

        // Copy to clipboard
        copyToClipboard(content)

        showNotification(
            gitLink.project,
            NotificationType.INFORMATION,
            "Copied permalink as Markdown.",
            "<a href='$permalink'>${truncateText(linkText, 45)}</a>."
        )
    }
}
