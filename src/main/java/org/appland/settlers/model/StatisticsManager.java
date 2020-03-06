package org.appland.settlers.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SHIELD;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.SWORD;
import static org.appland.settlers.model.Material.WOOD;

public class StatisticsManager {

    private final List<Material> PRODUCTION_STATISTICS_MATERIALS = Arrays.asList(
        WOOD,
        STONE,
        PLANK,
        GOLD,
        SWORD,
        SHIELD
    );

    private final Map<Material, ProductionDataSeries> productionStatistics;
    private final LandStatistics landStatistics;

    public StatisticsManager() {
        productionStatistics = new HashMap<>();
        landStatistics = new LandStatistics();

        /* Create collectors for each material to collect */
        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {
            productionStatistics.put(material, new ProductionDataSeries());
        }
    }

    public ProductionDataSeries getProductionStatisticsForMaterial(Material material) {
        return productionStatistics.get(material);
    }

    public void addZeroInitialMeasurementForPlayers(List<Player> players) {

        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {
            productionStatistics.get(material).setInitialZeroMeasurementForPlayers(players);
        }
    }

    public void collectFromPlayers(long time, List<Player> players) {
        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {
            int[] measurement = new int[players.size()];

            for (int i = 0; i < players.size(); i++) {
                measurement[i] = players.get(i).getProducedMaterial(material);
            }

            productionStatistics.get(material).addMeasurement(time, measurement);
        }
    }

    public LandStatistics getLandStatistics() {
        return landStatistics;
    }

    public void collectLandStatisticsFromPlayers(long time, List<Player> players) {

        int[] measurement = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {

            measurement[i] = players.get(i).getLandInPoints().size();
        }

        landStatistics.addMeasurement(time, measurement);
    }
}
