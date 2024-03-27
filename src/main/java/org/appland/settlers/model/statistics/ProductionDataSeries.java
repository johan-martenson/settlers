package org.appland.settlers.model.statistics;

import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ProductionDataSeries {
    private final List<ProductionDataPoint> dataPoints;

    public ProductionDataSeries() {
        dataPoints = new ArrayList<>();
    }

    public List<ProductionDataPoint> getProductionDataPoints() {
        return dataPoints;
    }

    public void addMeasurement(long time, int[] measurement) {
        dataPoints.add(new ProductionDataPoint(time, measurement));
    }

    public void setInitialZeroMeasurementForPlayers(List<Player> players) {

        int[] initialMeasurement = new int[players.size()];

        int amountPlayers = players.size();

        for (int i = 0; i < amountPlayers; i++) {
            initialMeasurement[i] = 0;
        }

        addMeasurement(0, initialMeasurement);
    }
}
