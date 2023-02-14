package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.completion.CompletionProcess
import com.intellij.codeInsight.completion.CompletionService
import com.intellij.codeInsight.intention.impl.ShowIntentionActionsHandler
import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jetbrains.rdclient.editors.getPsiFile

class TestKotlinAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val caret = editor?.caretModel
        val caretOffset = caret?.offset ?: 0
        val project = event.getData(CommonDataKeys.PROJECT)
        val completionProcess = CompletionService.getCompletionService().currentCompletion
        //val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)
        val psiElement = caretOffset?.let { off -> file?.findElementAt(off - 1) }
        val node = psiElement?.node
        val nodeOffset = node?.startOffset ?: 0
        val nodeTextLength = node?.textLength ?: 1
        //val popupController = event.project?.let { AutoPopupController.getInstance(it) }
        //popupController?.scheduleAutoPopup(editor)
        //TestObject.toggleBool()
        //Messages.showErrorDialog(psiElement?.toString(), "Test")

        val directory = editor?.getPsiFile()?.containingFile?.containingDirectory
        if (directory != null) {
            val newFile = CreateFileAction.findOrCreateSubdirectory(directory, "Test").createFile("Test.cs")
            directory.add(newFile)
        }
        //val project = event.project?.solution.
        //val help = ShowIntentionActionsAction()
        //help.actionPerformed(event)

        val psiFile = editor?.getPsiFile()
        caret?.moveToOffset(nodeOffset)
        caret?.moveCaretRelatively(nodeTextLength, 0, true, true, true)
        if (editor != null && psiFile != null && project != null) {
            val actionHandler = ShowIntentionActionsHandler()
            actionHandler.invoke(project, editor, psiFile, true)

        }

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

