package org.appland.settlers.utils;

import java.util.Collection;

public interface Group {
    void addVariable(String name);

    Variable addVariable(Variable variable);

    Collection<Variable> getVariables();

    String getName();

    void addVariables(Collection<String> variables);

    void collectionPeriodDone();

    Collection<String> getVariableNames();
}
