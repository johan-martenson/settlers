package org.appland.settlers.model.statistics;

public class ProductionDataPoint {
    private final int[] measurement;
    private final long time;

    public ProductionDataPoint(long time, int[] measurement) {
        this.time = time;
        this.measurement = measurement;
    }

    public int[] getValues() {
        return measurement;
    }

    public long getTime() {
        return time;
    }
}
