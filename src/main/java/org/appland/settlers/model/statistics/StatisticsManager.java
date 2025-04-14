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
    private final Map<Player, MerchandiseStatistics> merchandiseStatistics = new HashMap<>();

    public StatisticsManager() {
        for (Material material : PRODUCTION_STATISTICS_MATERIALS) {
            productionStatistics.put(material, new ProductionDataSeries());
        }
    }

    public GeneralStatistics getGeneralStatistics(Player player) {
        return generalStatistics.computeIfAbsent(player, p -> new GeneralStatistics(
                new CumulativeDataSeries("Total houses"),
                new SnapshotDataSeries("Land"),
                new CumulativeDataSeries("Produced coins", 0),
                new CumulativeDataSeries("Soldiers"),
                new CumulativeDataSeries("Workers"),
                new CumulativeDataSeries("Killed enemies", 0)));
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
            measurement[i] = players.get(i).getOwnedLand().size();
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

    public void coinProduced(Player player, long time) {
        getGeneralStatistics(player).coins().increase(time);
        getMerchandiseStatistics(player).coin().increase(time);

        // TODO: should consolidate and only measure coins once

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldiersDrafted(Player player, long time, int amount) {
        getGeneralStatistics(player).soldiers().increase(time, amount);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void addListener(StatisticsListener monitor) {
        listeners.add(monitor);
    }

    public void workerCreated(Player player, long time) {
        getGeneralStatistics(player).workers().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldierDied(Player player, Player enemyPlayer, long time) {
        getGeneralStatistics(player).soldiers().decrease(time);
        getGeneralStatistics(enemyPlayer).killedEnemies().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
        listeners.forEach(listener -> listener.generalStatisticsChanged(enemyPlayer));
    }

    public void landUpdated(Player player, long time, int amount) {
        getGeneralStatistics(player).land().report(time, amount);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldiersAtStart(Player player, long time, int amount) {
        getGeneralStatistics(player).soldiers().report(time, amount);
    }

    public MerchandiseStatistics getMerchandiseStatistics(Player player) {
        return merchandiseStatistics.computeIfAbsent(player, k -> new MerchandiseStatistics(
                new CumulativeDataSeries("Wood", 0),
                new CumulativeDataSeries("Plank", 0),
                new CumulativeDataSeries("Stone", 0),
                new CumulativeDataSeries("Food", 0),
                new CumulativeDataSeries("Water", 0),
                new CumulativeDataSeries("Beer", 0),
                new CumulativeDataSeries("Coal", 0),
                new CumulativeDataSeries("Iron", 0),
                new CumulativeDataSeries("Gold", 0),
                new CumulativeDataSeries("Iron bar", 0),
                new CumulativeDataSeries("Coin", 0),
                new CumulativeDataSeries("Tools", 0),
                new CumulativeDataSeries("Weapons", 0),
                new CumulativeDataSeries("Boats", 0)));
    }

    public void treeCutDown(Player player, long time) {
        getMerchandiseStatistics(player).wood().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void plankProduced(Player player, long time) {
        getMerchandiseStatistics(player).plank().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void stoneProduced(Player player, long time) {
        getMerchandiseStatistics(player).stone().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void fishProduced(Player player, long time) {
        getMerchandiseStatistics(player).food().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void meatProduced(Player player, long time) {
        getMerchandiseStatistics(player).food().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void waterProduced(Player player, long time) {
        getMerchandiseStatistics(player).water().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void beerProduced(Player player, long time) {
        getMerchandiseStatistics(player).beer().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void mined(Player player, long time, Material mineral) {
        var merchandiseStatistics = getMerchandiseStatistics(player);

        switch (mineral) {
            case COAL -> merchandiseStatistics.coal().increase(time);
            case IRON -> merchandiseStatistics.iron().increase(time);
            case GOLD -> merchandiseStatistics.gold().increase(time);
            case STONE -> merchandiseStatistics.stone().increase(time);
        }

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void ironBarProduced(Player player, long time) {
        getMerchandiseStatistics(player).ironBar().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void toolProduced(Player player, long time) {
        getMerchandiseStatistics(player).tools().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void weaponProduced(Player player, long time) {
        getMerchandiseStatistics(player).weapons().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void boatProduced(Player player, long time) {
        getMerchandiseStatistics(player).boats().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void breadProduced(Player player, long time) {
        getMerchandiseStatistics(player).food().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }
}
