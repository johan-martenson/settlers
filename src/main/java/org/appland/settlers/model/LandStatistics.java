package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

public class LandStatistics {
    private final List<LandDataPoint> dataPoints;

    public LandStatistics() {
        dataPoints = new ArrayList<>();
    }

    public List<LandDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void addMeasurement(long time, int[] measurement) {
        LandDataPoint landDataPoint = new LandDataPoint(time, measurement);

        dataPoints.add(landDataPoint);
    }
}
