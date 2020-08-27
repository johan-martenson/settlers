package org.appland.settlers.utils;

import java.util.*;

public class Stats {

    private final Map<String, VariableImpl> variableMap;
    private final Map<String, Long> upperThresholdsToBeSet;
    private final Map<String, GroupImpl> groups;

    public Stats() {
        variableMap = new HashMap<>();
        upperThresholdsToBeSet = new HashMap<>();
        groups = new HashMap<>();
    }

    public Variable addVariable(String name) {
        System.out.println("Adding variable: " + name);

        VariableImpl variable = new VariableImpl(name);

        variableMap.put(name, variable);

        if (upperThresholdsToBeSet.containsKey(name)) {
            System.out.println("Stored threshold exists: " + upperThresholdsToBeSet.get(name));

            variable.setUpperThreshold(upperThresholdsToBeSet.get(name));
            upperThresholdsToBeSet.remove(name);
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
        VariableImpl periodicVariable = variableMap.get(name);

        if (periodicVariable == null) {
            System.out.println("Adding periodic counter variable: " + name);

            periodicVariable = new PeriodicCounterVariableImpl(name);

            variableMap.put(name, periodicVariable);
        }

        return periodicVariable;
    }

    public void resetCollectionPeriod(String name) {
        VariableImpl variable = variableMap.get(name);

//        if (variable instanceof  PeriodicCounterVariable) {
            ((PeriodicCounterVariableImpl) variable).collectionPeriodDone();
//        }
    }

    public void reportVariableValue(String name, long value) {
        VariableImpl variable = variableMap.get(name);

        variable.reportValue(value);
    }

    public boolean isVariableLatestValueHighest(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.isLatestValueHighest();
    }

    public boolean isVariableLatestValueLowest(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.isLatestValueLowest();
    }

    public double getAverageForVariable(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.average;
    }

    public long getHighestValueForVariable(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.currentHighestValue;
    }

    public long getLowestValueForVariable(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.currentLowestValue;
    }

    public long getLatestValueForVariable(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.latestValue;
    }

    public void setUpperThreshold(String name, long upperThresholdValue) {
        System.out.println("Set upper threshold for " + name + " to " + upperThresholdValue);

        VariableImpl variable = variableMap.get(name);

        if (variable == null) {
            System.out.println("Variable doesn't exist, storing this");

            upperThresholdsToBeSet.put(name, upperThresholdValue);
        } else {
            System.out.println("Setting upper threshold");

            variable.setUpperThreshold(upperThresholdValue);
        }
    }

    public long getUpperThreshold(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.getUpperThreshold();
    }

    public boolean isVariableUpperThresholdExceeded(String name) {
        VariableImpl variable = variableMap.get(name);

        return variable.isUpperThresholdExceeded();
    }

    public void printVariablesAsTable() {
        String header = String.format("| %-60s | %-10s | %-10s | %-10s | %-10s |", "Variable", "Latest", "Average", "Max", "Min");

        System.out.println();
        System.out.println(header);
        System.out.println();

        List<String> variableNames = new ArrayList<>(variableMap.keySet());

        java.util.Collections.sort(variableNames);

        for (String variableName : variableNames) {
            VariableImpl variable = variableMap.get(variableName);

            String valueRow = String.format("| %-60s | %10d | %10f | %10d | %10d |",
                    variableName,
                    variable.latestValue,
                    variable.average,
                    variable.currentHighestValue,
                    variable.currentLowestValue);

            System.out.println(valueRow);
        }

        System.out.println();
    }

    public Group createVariableGroupIfAbsent(String name) {
        GroupImpl group = new GroupImpl(name, this);

        groups.putIfAbsent(name, group);

        return group;
    }

    public Collection<String> getVariablesInGroup(String s) {
        GroupImpl group = groups.get(s);

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
