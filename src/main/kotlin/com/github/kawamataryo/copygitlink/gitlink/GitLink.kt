import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.ProperTextRange
import git4idea.repo.GitRepositoryManager

class GitLink(actionEvent: AnActionEvent) {
    val project = actionEvent.getRequiredData(CommonDataKeys.PROJECT)
    val caret: Caret =
        actionEvent.getRequiredData(CommonDataKeys.EDITOR).caretModel.primaryCaret
    private val virtualFile = actionEvent.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
    private val repo =
        GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(virtualFile)
    private val editor: Editor =
        actionEvent.getRequiredData(CommonDataKeys.EDITOR)

    val hasRepository = repo != null

    val linePath: String
        get() {
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)
            val start = logicalStartPosition.line + 1
            val end = if (logicalEndPosition.column == 0 && logicalStartPosition.line != logicalEndPosition.line) logicalEndPosition.line else logicalEndPosition.line + 1
            return if (start == end) "#L$start" else "#L$start-L$end"
        }

    val repositoriesPath: List<String>
        get() {
            val regex = Regex(".*(?:@|\\/\\/)(.[^:\\/]*).([^\\.]+)\\.git")
            return repo?.remotes?.mapNotNull { it.firstUrl }?.mapNotNull { regex.matchEntire(it) }?.map { result ->
                result.groupValues[1] + "/" + result.groupValues[2]
            } ?: listOf()
        }

    val relativePath: String
        get() {
            val path = virtualFile.path
            return path.replace(repo?.root?.path ?: "", "")
        }

    fun toGitLink(path: String) = "https://$path/blob/${repo?.currentRevision}$relativePath$linePath"

    val permalinks: List<String>
        get() {
            return repositoriesPath.map { path -> "https://$path/blob/${repo?.currentRevision}$relativePath$linePath" }
        }
    val branchLink: String
        get() {
            return "https://$repositoryPath/blob/${repo?.currentBranch}$relativePath$linePath"
        }

    val source:String
        get(){
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)

            val startOffset = editor.document.getLineStartOffset(logicalStartPosition.line)
            val endOffset = editor.document.getLineEndOffset(logicalEndPosition.line)
            return editor.document.getText(ProperTextRange(startOffset, endOffset))
        }
}


