package com.jetbrains.rider.plugins.sampleplugin

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResult
import com.intellij.codeInsight.completion.CompletionService
import com.intellij.util.Consumer

class KotlinLookupElementProvider {
    val elements = ArrayList<String>()
    private lateinit var _contributors: MutableList<CompletionContributor>
    private lateinit var _parameters: CompletionParameters

    constructor(parameters: CompletionParameters) {
        _parameters = parameters
        _contributors = CompletionContributor.forParameters(parameters)
        fillElementsList()
    }

    fun fillElementsList(){
        val elementConsumer: Consumer<in CompletionResult> =
            Consumer { cons: CompletionResult ->
                elements.add(
                    cons.toString()
                )
            }
        elements.clear()
        for (contributor in _contributors) {
            CompletionService.getCompletionService().getVariantsFromContributors(_parameters, contributor, elementConsumer)
        }
    }
}