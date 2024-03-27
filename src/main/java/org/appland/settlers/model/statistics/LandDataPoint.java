package org.appland.settlers.model.statistics;

public class LandDataPoint {
    private final long time;
    private final int[] measurement;

    public LandDataPoint(final long time, final int[] measurement) {
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
