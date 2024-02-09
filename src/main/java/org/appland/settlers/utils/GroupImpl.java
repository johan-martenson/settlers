package org.appland.settlers.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class GroupImpl implements Group {

    private final Set<String> variables;
    private final String name;
    private final Stats stats;

    GroupImpl(String name, Stats stats) {
        this.variables = new HashSet<>();
        this.name = name;
        this.stats = stats;
    }

    @Override
    public void addVariable(String name) {
        variables.add(name);
    }

    @Override
    public Variable addVariable(Variable variable) {
        variables.add(variable.getName());

        return variable;
    }

    @Override
    public Collection<Variable> getVariables() {
        Set<Variable> variableList = new HashSet<>();

        for (String variableName : variables) {
            variableList.add(stats.getVariable(variableName));
        }

        return variableList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addVariables(Collection<String> variables) {
        this.variables.addAll(variables);
    }

    @Override
    public void collectionPeriodDone() {
        for (String variableName : variables) {
            Variable variable = stats.getVariable(variableName);

            if (variable instanceof PeriodicCounterVariableImpl periodicCounterVariable) {

                periodicCounterVariable.collectionPeriodDone();
            }
        }
    }

    @Override
    public Collection<String> getVariableNames() {
        return variables;
    }
}
