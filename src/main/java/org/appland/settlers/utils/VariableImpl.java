package org.appland.settlers.utils;

class VariableImpl implements Variable {
    private final String name;

    public long latestValue;

    long numberOfMeasurements;
    double average;
    long currentHighestValue;
    long currentLowestValue;

    private boolean isLatestHighest;
    private boolean isLatestLowest;
    private long upperThresholdValue;
    private boolean isUpperThresholdValueSet;

    VariableImpl(String name) {
        numberOfMeasurements = 0;
        average = 0;
        currentHighestValue = Long.MIN_VALUE;
        currentLowestValue = Long.MAX_VALUE;
        isLatestHighest = false;
        isLatestLowest = false;
        isUpperThresholdValueSet = false;

        this.name = name;
    }

    @Override
    public void reportValue(long value) {
        latestValue = value;

        /* Keep track of the average */
        if (numberOfMeasurements == 0) {
            average = value;
        } else {
            average = (average * numberOfMeasurements + value) / (numberOfMeasurements + 1);
        }

        /* Check if this is the highest or lowest value reported so far */
        if (value > currentHighestValue) {
            isLatestHighest = true;

            currentHighestValue = value;
        } else {
            isLatestHighest = false;
        }

        if (value < currentLowestValue) {
            isLatestLowest = true;

            currentLowestValue = value;
        } else {
            isLatestLowest = false;
        }

        /* Count the number of reported measurements */
        numberOfMeasurements = numberOfMeasurements + 1;
    }

    @Override
    public void setUpperThreshold(final long upperThresholdValue) {
        this.upperThresholdValue = upperThresholdValue;

        this.isUpperThresholdValueSet = true;
    }

    @Override
    public boolean isUpperThresholdExceeded() {
        return isUpperThresholdValueSet && latestValue >= upperThresholdValue;
    }

    @Override
    public boolean isLatestValueHighest() {
        return isLatestHighest;
    }

    @Override
    public long getHighestValue() {
        return currentHighestValue;
    }

    @Override
    public long getUpperThreshold() {
        return upperThresholdValue;
    }

    @Override
    public double getAverage() {
        return average;
    }

    @Override
    public long getLatestValue() {
        return latestValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLatestValueLowest() {
        return isLatestLowest;
    }

    @Override
    public long getLowestValue() {
        return currentLowestValue;
    }
}
