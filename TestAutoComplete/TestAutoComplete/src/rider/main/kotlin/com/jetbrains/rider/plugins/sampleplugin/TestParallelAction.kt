package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.codeInsight.intention.impl.ShowIntentionActionsHandler
import com.intellij.ide.projectWizard.NewProjectWizardConstants
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.jetbrains.rdclient.editors.getPsiFile
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.psi.CSharpFile
import com.jetbrains.rider.test.scriptingApi.insertString
import com.jetbrains.rider.test.scriptingApi.moveToOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class TestParallelAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        testFun(p0)
    }
    private fun testFun(event: AnActionEvent) = runBlocking {
        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val popupController = event.project?.let { AutoPopupController.getInstance(it) }

        val actionHandler = ShowIntentionActionsHandler()
        val caret = editor?.caretModel
        val caretOffset = caret?.offset ?: 0
        val psiElement = caretOffset?.let { off -> file?.findElementAt(off - 1) }
        val node = psiElement?.node
        val nodeOffset = node?.startOffset ?: 0
        val nodeTextLength = node?.textLength ?: 1
        /*val directory = file?.containingDirectory
        val fileFactory = PsiFileFactory.getInstance(project)
        val cSharpLang = file?.language
        if (cSharpLang != null && directory != null) {
            val newFile = fileFactory.createFileFromText(cSharpLang, "class TestClass {}")
            directory.add(newFile)
        }


        launch {
            delay(500)
            popupController?.scheduleAutoPopup(editor)
        }*/
        /*var test = "u _ and _ i _"
        test = test.split(" ").joinToString(""){ it ->
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
        test = test[0].lowercase() + test.removePrefix(test[0].toString())
        */
        WaitFor.me = true
        popupController?.scheduleAutoPopup(editor)
        Thread {
            while (WaitFor.me) Thread.sleep(500)
            editor?.insertString("a")
            editor?.moveToOffset(caretOffset + 1 )
        }.start()

    }

    private fun testPopup(editor: Editor?, popupController: AutoPopupController?) = runBlocking() {
        launch {
            popupController?.scheduleAutoPopup(editor)
        }
    }
}