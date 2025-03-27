package org.appland.settlers.model.statistics;

import java.util.ArrayList;
import java.util.List;

public class SnapshotDataSeries {
    private final String name;
    private final List<Measurement> measurements = new ArrayList<>();

    public SnapshotDataSeries(String name) {
        this.name = name;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void report(long time, int value) {
        if (!measurements.isEmpty() && measurements.getLast().time() == time) {
            measurements.removeLast();
        }

        measurements.add(new Measurement(time, value));
    }
}
