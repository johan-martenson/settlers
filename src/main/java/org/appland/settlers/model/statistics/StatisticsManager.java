package org.appland.settlers.model.statistics;

import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Headquarter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.model.Material.*;

public class StatisticsManager {
    private static final long START_TIME = 1;
    private static final Set<Material> PRODUCTION_STATISTICS_MATERIALS = EnumSet.copyOf(Arrays.asList(
        WOOD,
        STONE,
        PLANK,
        GOLD,
        SWORD,
        SHIELD,
        COIN,
        PRIVATE,
        PRIVATE_FIRST_CLASS,
        SERGEANT,
        OFFICER,
        GENERAL,
        COAL,
        GOLD,
        IRON));

    private final Map<Material, ProductionDataSeries> productionStatistics = new EnumMap<>(Material.class);
    private final LandStatistics landStatistics = new LandStatistics();
    private final Map<Player, Map<Class<? extends Building>, CumulativeDataSeries>> buildingStatistics = new HashMap<>();
    private final Set<StatisticsListener> listeners = new HashSet<>();
    private final Map<Player, GeneralStatistics> generalStatistics = new HashMap<>();

    public StatisticsManager() {
        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {
            productionStatistics.put(material, new ProductionDataSeries());
        }
    }

    public GeneralStatistics getGeneralStatistics(Player player) {
        return generalStatistics.computeIfAbsent(player, p -> new GeneralStatistics(
                new CumulativeDataSeries("Total houses"),
                new SnapshotDataSeries("Land")));
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
            int amountPlayers = players.size();

            for (int i = 0; i < amountPlayers; i++) {
                measurement[i] = players.get(i).getProducedMaterial(material);
            }

            productionStatistics.get(material).addMeasurement(time, measurement);
        }
    }

    public LandStatistics getLandStatistics() {
        return landStatistics;
    }

    public Map<Player, Map<Class<? extends Building>, CumulativeDataSeries>> getBuildingStatistics() {
        return buildingStatistics;
    }

    public void collectLandStatisticsFromPlayers(long time, List<Player> players) {
        int[] measurement = new int[players.size()];
        int amountPlayer = players.size();

        for (int i = 0; i < amountPlayer; i++) {
            measurement[i] = players.get(i).getLandInPoints().size();
        }

        landStatistics.addMeasurement(time, measurement);
    }

    public <T extends Building> void houseRemoved(T building, long time) {
        var player = building.getPlayer();

        listeners.forEach(listener -> listener.buildingStatisticsChanged(building));

        buildingStatistics.get(player).get(building.getClass()).decrease(time);

        generalStatistics.get(player).totalAmountBuildings().decrease(time);
    }

    public <T extends Building> void houseAdded(T house, long time) {
        var player = house.getPlayer();

        // Maintain detailed building statistics
        var dataSeries = buildingStatistics
                .computeIfAbsent(player, k -> new HashMap<>())
                .computeIfAbsent(house.getClass(), k -> new CumulativeDataSeries(house.getClass().getSimpleName()));

        if (dataSeries.getMeasurements().isEmpty()) {
            if (! (house instanceof Headquarter) && time > START_TIME) {
                dataSeries.report(START_TIME, 0);
            }
        }

        dataSeries.increase(time);

        // Maintain statistics on total amount of houses
        getGeneralStatistics(player).totalAmountBuildings().increase(time);

        listeners.forEach(listener -> listener.buildingStatisticsChanged(house));
    }

    public void addListener(StatisticsListener monitor) {
        listeners.add(monitor);
    }
}
