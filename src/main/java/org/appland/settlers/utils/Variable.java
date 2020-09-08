package org.appland.settlers.utils;

public interface Variable {
    void reportValue(long value);

    void setUpperThreshold(long upperThresholdValue);

    boolean isUpperThresholdExceeded();

    boolean isLatestValueHighest();

    long getHighestValue();

    long getUpperThreshold();

    double getAverage();

    long getLatestValue();

    String getName();

    boolean isLatestValueLowest();

    long getLowestValue();
}
