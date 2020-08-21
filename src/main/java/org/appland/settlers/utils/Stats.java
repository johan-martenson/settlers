package org.appland.settlers.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Stats {

    private final Map<String, Variable> variableMap;

    static class Variable {
        public long latestValue;
        long numberOfMeasurements;
        double average;
        long currentHighestValue;
        long currentLowestValue;
        private boolean isLatestHighest;
        private boolean isLatestLowest;

        Variable() {
            numberOfMeasurements = 0;
            average = 0;
            currentHighestValue = Long.MIN_VALUE;
            currentLowestValue = Long.MAX_VALUE;
            isLatestHighest = false;
            isLatestLowest = false;
        }

        void reportValue(long value) {
            latestValue = value;

            if (numberOfMeasurements == 0) {
                average = value;
                isLatestHighest = true;
                isLatestLowest = true;
            } else {
                average = (average * numberOfMeasurements + value) / (numberOfMeasurements + 1);
            }

            isLatestHighest = value > currentHighestValue;
            isLatestLowest = value < currentLowestValue;

            if (isLatestHighest) {
                currentHighestValue = value;
            }

            if (isLatestLowest) {
                currentLowestValue = value;
            }

            numberOfMeasurements = numberOfMeasurements + 1;
        }
    }

    static class PeriodicCounterVariable extends Variable {

        private long sum;

        void reportValue(long value) {
            sum = sum + value;
        }

        void collectionPeriodDone() {
            super.reportValue(sum);

            sum = 0;
        }
    }

    public Stats() {
        variableMap = new HashMap<>();
    }

    public void addVariable(String name) {
        variableMap.put(name, new Variable());
    }

    public void addVariableIfMissing(String name) {
        if (!variableMap.containsKey(name)) {
            variableMap.put(name, new Variable());
        }
    }

    public Collection<String> getVariables() {
        return variableMap.keySet();
    }

    public void addPeriodicCounterVariable(String name) {
        variableMap.put(name, new PeriodicCounterVariable());
    }

    public void resetCollectionPeriod(String name) {
        Variable variable = variableMap.get(name);

        if (variable instanceof  PeriodicCounterVariable) {
            ((PeriodicCounterVariable) variable).collectionPeriodDone();
        } else {
            throw new RuntimeException("Can't reset collection period for this variable");
        }
    }

    public void reportVariableValue(String name, long value) {
        Variable variable = variableMap.get(name);

        variable.reportValue(value);
    }

    public boolean isVariableLatestValueHighest(String name) {
        Variable variable = variableMap.get(name);

        return variable.isLatestHighest;
    }

    public boolean isVariableLatestValueLowest(String name) {
        Variable variable = variableMap.get(name);

        return variable.isLatestLowest;
    }

    public double getAverageForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.average;
    }

    public long getHighestValueForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.currentHighestValue;
    }

    public long getLatestValueForVariable(String name) {
        Variable variable = variableMap.get(name);

        return variable.latestValue;
    }
}
