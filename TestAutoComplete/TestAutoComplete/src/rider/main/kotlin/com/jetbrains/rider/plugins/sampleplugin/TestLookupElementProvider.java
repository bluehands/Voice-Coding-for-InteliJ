package com.jetbrains.rider.plugins.sampleplugin;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResult;
import com.intellij.codeInsight.completion.CompletionService;

import java.util.ArrayList;
import java.util.List;
import com.intellij.util.Consumer;

public class TestLookupElementProvider {
    private final List<String> _elements = new ArrayList<String>();
    private List<CompletionContributor> _contributors = new ArrayList<CompletionContributor>();
    private CompletionParameters _parameters;
    private final Consumer<? super CompletionResult> testConsumer = cons -> _elements.add(cons.toString());
    public TestLookupElementProvider(CompletionContributor contributor, CompletionParameters parameters){
        _contributors.add(contributor);
        _parameters = parameters;
        fillElementsList();
    }
    public TestLookupElementProvider(CompletionParameters parameters){
        _parameters = parameters;
        _contributors = CompletionContributor.forParameters(parameters);
        fillElementsList();
    }
    public String getElement(int index){
        return (index < _elements.size()) ? _elements.get(index) : "";
    }

    public List<String> getAllElements(){
        return _elements;
    }

    public int getElementsCount(){
        return _elements.size();
    }

    public void fillElementsList(){
        _elements.clear();
        for (CompletionContributor contributor : _contributors)
            CompletionService.getCompletionService().getVariantsFromContributors(_parameters, contributor, testConsumer);
    }

    public void addContributor (CompletionContributor contributor){
        _contributors.add(contributor);
    }

    public CompletionContributor getContributor(int index){
        return (index < _contributors.size()) ? _contributors.get(index) : null;
    }

    public void deleteContributor (int index){
        if (index < _contributors.size()) {
            _contributors.remove(index);
        }
    }

    public void clearAllContributors(){
        _contributors.clear();
    }

    public CompletionParameters getParameters() {
        return _parameters;
    }

    public void setParameters(CompletionParameters _parameters) {
        this._parameters = _parameters;
    }
}
