package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.util.Consumer

class TestKotlinAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val completionProcess = CompletionService.getCompletionService().currentCompletion
        //val psiElement = event.getData(CommonDataKeys.PSI_ELEMENT)
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val offset = editor?.caretModel?.offset
        val psiElement = offset?.let { off -> file?.findElementAt(off - 1) }
        if (psiElement != null && file != null && completionProcess != null ) {
            val completionParams = CompletionParameters(psiElement, file, CompletionType.BASIC, offset, 0, editor, completionProcess)
            val contributor = CompletionContributor.forParameters(completionParams)
            val elements = ArrayList<String>()
            val testConsumer: Consumer<in CompletionResult> =
                Consumer { cons: CompletionResult ->
                    elements.add(
                        cons.toString()
                    )
                }
            CompletionService.getCompletionService().getVariantsFromContributors(completionParams, contributor[0], testConsumer)
            //val elementProvider = TestLookupElementProvider(contributor[0], completionParams)
            //Messages.showErrorDialog(elementProvider.getElement(0), "LookUp Element Found!")
            val numberOfElements = "Success! " + elements[0] + " Elements found"
            Messages.showErrorDialog(numberOfElements, "LookUp Element Found!")
        } else {
            var message = "Failed: "
            if (psiElement == null) message += "PSI Element null, "
            if (file == null) message += "File null, "
            if (completionProcess == null) message += "Process null, "
            if (editor == null) message += "Editor null"
            Messages.showErrorDialog(message, "Kotlin Title")
        }
    }
}

