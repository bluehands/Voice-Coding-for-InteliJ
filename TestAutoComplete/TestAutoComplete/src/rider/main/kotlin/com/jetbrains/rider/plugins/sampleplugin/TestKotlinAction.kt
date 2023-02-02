package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.actions.OptimizeImportsAction
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProcess
import com.intellij.codeInsight.completion.CompletionService
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionActionProvider
import com.intellij.codeInsight.intention.actions.ShowIntentionActionsAction
import com.intellij.codeInsight.intention.impl.IntentionActionAsAction
import com.intellij.find.impl.ShowRecentFindUsagesAction
import com.intellij.find.impl.ShowRecentFindUsagesGroup
import com.intellij.ide.actions.ContextHelpAction
import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.actions.ShowPopupMenuAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.xdebugger.impl.actions.EditBreakpointAction.ContextAction
import com.jetbrains.rdclient.editors.getPsiFile
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.psi.CSharpFile
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.psi.impl.CSharpFileImpl
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.util.idea.getService

class TestKotlinAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val offset = editor?.caretModel?.offset
        val completionProcess: CompletionProcess? = CompletionService.getCompletionService().currentCompletion
        //val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)
        val psiElement = offset?.let { off -> file?.findElementAt(off - 1) }
        //val popupController = event.project?.let { AutoPopupController.getInstance(it) }
        //popupController?.scheduleAutoPopup(editor)
        //TestObject.toggleBool()
        Messages.showErrorDialog(psiElement?.toString(), "Test")

        val directory = editor?.getPsiFile()?.containingFile?.containingDirectory
        //if (directory != null)  CreateFileAction.findOrCreateSubdirectory(directory, "Test").createFile("Test.cs")
        //val project = event.project?.solution.
        val help = ShowIntentionActionsAction()
        help.actionPerformed(event)

        /*if ( psiElement != null && file != null && completionProcess != null) {
            val completionParams = CompletionParameters(psiElement, file, CompletionType.BASIC, offset, 0, editor, completionProcess)
            val provider = KotlinLookupElementProvider(completionParams)
            val numberOfElements = "Success! " + provider.elements.size + " Elements found"
            Messages.showErrorDialog(numberOfElements, "LookUp Element Found!")
        } else {
            var message = "Failed: "
            if (psiElement == null) message += "PSI Element null, "
            if (file == null) message += "File null, "
            if (completionProcess == null) message += "Process null, "
            if (editor == null) message += "Editor null"
            Messages.showErrorDialog(message, "Kotlin Title")
        }*/

    }
}

