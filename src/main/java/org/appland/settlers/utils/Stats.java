package org.appland.settlers.utils;

import java.util.*;

public class Stats {

    private final Map<String, Variable> variableMap;
    private final Map<String, Long> upperThresholdsToBeSet;
    private final Map<String, GroupImpl> groups;

    public Stats() {
        variableMap = new HashMap<>();
        upperThresholdsToBeSet = new HashMap<>();
        groups = new HashMap<>();
    }

    public Variable addVariable(String name) {
        VariableImpl variable = new VariableImpl(name);

        variableMap.put(name, variable);

        if (upperThresholdsToBeSet.containsKey(name)) {
            System.out.println("Stored threshold exists: " + upperThresholdsToBeSet.get(name));
            variable.setUpperThreshold(upperThresholdsToBeSet.get(name));
            upperThresholdsToBeSet.remove(name);
        }

        return variable;
    }

    public Variable addIncrementingVariableIfAbsent(String name) {
        Variable variable = variableMap.get(name);

        if (variable == null) {

            System.out.println("Adding incrementing variable: " + name);

            variable = new IncrementingVariableImpl(name);

            variableMap.put(name, variable);

            if (upperThresholdsToBeSet.containsKey(name)) {
                System.out.println("Stored threshold exists: " + upperThresholdsToBeSet.get(name));

                variable.setUpperThreshold(upperThresholdsToBeSet.get(name));
                upperThresholdsToBeSet.remove(name);
            }
        }

        return variable;
    }

    public Variable addVariable(String name, String groupName) {
        Variable variable = addVariable(name);

        GroupImpl group = groups.get(groupName);

        group.addVariable(name);

        return variable;
    }

    public Variable addVariableIfMissing(String name) {
        Variable variable = variableMap.get(name);

        if (variable == null) {
            variable = addVariable(name);
        }

        return variable;
    }

    public Collection<String> getVariables() {
        return variableMap.keySet();
    }

    public Variable addPeriodicCounterVariableIfAbsent(String name) {
        Variable periodicVariable = variableMap.get(name);

        if (periodicVariable == null) {
            periodicVariable = new PeriodicCounterVariableImpl(name);

            variableMap.put(name, periodicVariable);
        }

        return periodicVariable;
    }

    public void resetCollectionPeriod(String name) {
        Variable variable = variableMap.get(name);

//        if (variable instanceof  PeriodicCounterVariable) {
            ((PeriodicCounterVariableImpl) variable).collectionPeriodDone();
//        }
    }

    public void reportVariableValue(String name, long value) {
        Variable variable = variableMap.get(name);

        variable.reportValue(value);
    }

    public boolean isVariableLatestValueHighest(String name) {
        Variable variable = variableMap.get(name);

        return variable.isLatestValueHighest();
    }

    public boolean isVariableLatestValueLowest(String name) {
        Variable variable = variableMap.get(name);

        return variable.isLatestValueLowest();
    }

    public double getAverageForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.getAverage();
    }

    public long getHighestValueForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.getHighestValue();
    }

    public long getLowestValueForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.getLowestValue();
    }

    public long getLatestValueForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.getLatestValue();
    }

    public void setUpperThreshold(String name, long upperThresholdValue) {
        System.out.println("Set upper threshold for " + name + " to " + upperThresholdValue);

        Variable variable = variableMap.get(name);

        if (variable == null) {
            System.out.println("Variable doesn't exist, storing this");

            upperThresholdsToBeSet.put(name, upperThresholdValue);
        } else {
            System.out.println("Setting upper threshold");

            variable.setUpperThreshold(upperThresholdValue);
        }
    }

    public long getUpperThreshold(String name) {
        Variable variable = variableMap.get(name);

        return variable.getUpperThreshold();
    }

    public boolean isVariableUpperThresholdExceeded(String name) {
        Variable variable = variableMap.get(name);

        return variable.isUpperThresholdExceeded();
    }

    public void printVariablesAsTable() {

        List<String> variableNames = new ArrayList<>(variableMap.keySet());

        int longestVariableName = 0;

        for (String name : variableNames) {
            if (name.length() > longestVariableName) {
                longestVariableName = name.length();
            }
        }

        int variableColumnLength = Math.max(longestVariableName, 60);

        String header = String.format("| %-" + variableColumnLength + "s | %-10s | %-10s | %-10s | %-10s |", "Variable", "Latest", "Average", "Max", "Min");

        System.out.println();
        System.out.println(header);
        System.out.println();

        java.util.Collections.sort(variableNames);

        for (String variableName : variableNames) {
            Variable variable = variableMap.get(variableName);

            String valueRow = String.format("| %-" + variableColumnLength + "s | %10d | %10f | %10d | %10d |",
                    variableName,
                    variable.getLatestValue(),
                    variable.getAverage(),
                    variable.getHighestValue(),
                    variable.getLatestValue());

            System.out.println(valueRow);
        }

        System.out.println();
    }

    public Group createVariableGroupIfAbsent(String name) {
        return groups.computeIfAbsent(name, n -> new GroupImpl(n, this));
    }

    public Collection<String> getVariablesInGroup(String groupName) {
        GroupImpl group = groups.get(groupName);

        return group.getVariableNames();
    }

    public Variable getVariable(String name) {
        return variableMap.get(name);
    }

    public void addVariableToGroup(String counterName, String groupName) {
        GroupImpl group = groups.get(groupName);

        group.addVariable(counterName);
    }

    public void addVariablesToGroup(Collection<String> variables, String groupName) {
        GroupImpl group = groups.get(groupName);

        group.addVariables(variables);
    }

    public CumulativeDuration measureCumulativeDuration(String name, String groupName) {
        Group group = groups.get(groupName);

        return measureCumulativeDuration(name, group);
    }

    public CumulativeDuration measureCumulativeDuration(String name, Group group) {
        return new CumulativeDurationImpl(name, group, this);
    }

    public Duration measureOneShotDuration(String name) {
        return new Duration(name, this);
    }

    public Group getGroup(String name) {
        return groups.get(name);
    }
}
