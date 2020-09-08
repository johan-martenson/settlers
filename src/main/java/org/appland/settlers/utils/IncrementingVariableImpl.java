package org.appland.settlers.utils;

public class IncrementingVariableImpl implements Variable {

    private final VariableImpl variable;
    private long value;

    IncrementingVariableImpl(String name) {
        this.variable = new VariableImpl(name);
        this.value = 0;
    }

    @Override
    public void reportValue(long value) {
        this.value = this.value + value;

        this.variable.reportValue(this.value);
    }

    @Override
    public void setUpperThreshold(long upperThresholdValue) {
        this.variable.setUpperThreshold(upperThresholdValue);
    }

    @Override
    public boolean isUpperThresholdExceeded() {
        return this.variable.isUpperThresholdExceeded();
    }

    @Override
    public boolean isLatestValueHighest() {
        return this.variable.isLatestValueHighest();
    }

    @Override
    public long getHighestValue() {
        return this.variable.getHighestValue();
    }

    @Override
    public long getUpperThreshold() {
        return this.variable.getUpperThreshold();
    }

    @Override
    public double getAverage() {
        return this.variable.getAverage();
    }

    @Override
    public long getLatestValue() {
        return this.variable.getLatestValue();
    }

    @Override
    public String getName() {
        return this.variable.getName();
    }

    @Override
    public boolean isLatestValueLowest() {
        return this.variable.isLatestValueLowest();
    }

    @Override
    public long getLowestValue() {
        return this.variable.getLowestValue();
    }
}
