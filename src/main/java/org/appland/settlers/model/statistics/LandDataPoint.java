package org.appland.settlers.model.statistics;

public record LandDataPoint (long time, int[] values) {}

/*public class LandDataPoint {
    private final long time;
    private final int[] measurement;

    public LandDataPoint(final long time, final int[] measurement) {
        this.time = time;
        this.measurement = measurement;
    }

    public int[] values() {
        return measurement;
    }

    public long time() {
        return time;
    }
}*/
