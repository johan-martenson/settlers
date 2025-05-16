package org.appland.settlers.model.statistics;

import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.Headquarter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.model.Material.COIN;

public class StatisticsManager {
    private static final long START_TIME = 1;

    private final Map<Player, Map<Class<? extends Building>, CumulativeDataSeries>> buildingStatistics = new HashMap<>();
    private final Set<StatisticsListener> listeners = new HashSet<>();
    private final Map<Player, PlayerStatistics> playerStatistics = new HashMap<>();

    public Map<Player, Map<Class<? extends Building>, CumulativeDataSeries>> getBuildingStatistics() {
        return buildingStatistics;
    }

    public <T extends Building> void houseTornDown(T building, long time) {
        var player = building.getPlayer();
        var playerStatistics = getPlayerStatistics(player);

        if (building.isUnderConstruction()) {
            playerStatistics.goods().decrease(building.getAmount(Material.PLANK), time);
            playerStatistics.goods().decrease(building.getAmount(Material.STONE), time);

            listeners.forEach(listener -> listener.generalStatisticsChanged(player));
        } else if (building.isReady()) {
            buildingStatistics.get(player).get(building.getClass()).decrease(time);
            playerStatistics.totalAmountBuildings().decrease(time);
            playerStatistics.goods().decrease(
                    building.getInventory().values().stream().mapToInt(value -> value).sum(),
                    time);

            listeners.forEach(listener -> listener.buildingStatisticsChanged(building));
        }
    }

    public <T extends Building> void houseAdded(T house, long time) {
        var player = house.getPlayer();

        var dataSeries = buildingStatistics
                .computeIfAbsent(player, k -> new HashMap<>())
                .computeIfAbsent(house.getClass(), k -> new CumulativeDataSeries(house.getClass().getSimpleName()));

        if (dataSeries.getMeasurements().isEmpty() && !(house instanceof Headquarter) && time > START_TIME) {
            dataSeries.report(START_TIME, 0);
        }

        dataSeries.increase(time);
        getPlayerStatistics(player).totalAmountBuildings().increase(time);

        listeners.forEach(listener -> listener.buildingStatisticsChanged(house));
    }

    public void coinProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.coins().increase(time);
        playerStatistics.coin().increase(time);
        playerStatistics.goods().decrease(time);

        // TODO: should consolidate and only measure coins once? Is it tracking different things? E.g. current inventory vs produced coins?

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldiersDrafted(Player player, long time, int amount) {
        var generalStatistics = getPlayerStatistics(player);
        generalStatistics.soldiers().increase(amount, time);
        generalStatistics.goods().decrease(3, time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void addListener(StatisticsListener monitor) {
        listeners.add(monitor);
    }

    public void workerCreated(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.workers().increase(time);
        playerStatistics.goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldierDied(Player player, Player enemyPlayer, long time) {
        getPlayerStatistics(player).soldiers().decrease(time);
        getPlayerStatistics(enemyPlayer).killedEnemies().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
        listeners.forEach(listener -> listener.generalStatisticsChanged(enemyPlayer));
    }

    public void landUpdated(Player player, long time, int amount) {
        getPlayerStatistics(player).land().report(time, amount);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void soldiersAtStart(Player player, long time, int amount) {
        getPlayerStatistics(player).soldiers().report(time, amount);
    }

    public PlayerStatistics getPlayerStatistics(Player player) {
        return playerStatistics.computeIfAbsent(player, k -> new PlayerStatistics(
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
                new CumulativeDataSeries("Boats", 0),
                new CumulativeDataSeries("Total houses"),
                new SnapshotDataSeries("Land"),
                new CumulativeDataSeries("Produced coins", 0),
                new CumulativeDataSeries("Soldiers"),
                new CumulativeDataSeries("Workers"),
                new CumulativeDataSeries("Killed enemies", 0),
                new CumulativeDataSeries("Goods")));
    }

    public void treeCutDown(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.wood().increase(time);
        playerStatistics.goods().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void plankProduced(Player player, long time) {
        getPlayerStatistics(player).plank().increase(time);

        // Don't update goods statistics. Producing a plank means consuming a piece of wood

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void stoneProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.stone().increase(time);
        playerStatistics.goods().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void caughtFish(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.food().increase(time);
        playerStatistics.goods().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void meatProduced(Player player, long time) {
        getPlayerStatistics(player).food().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void waterProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.water().increase(time);
        playerStatistics.goods().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void beerProduced(Player player, long time) {
        getPlayerStatistics(player).beer().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void mined(Player player, long time, Material mineral) {
        var merchandiseStatistics = getPlayerStatistics(player);

        switch (mineral) {
            case COAL -> merchandiseStatistics.coal().increase(time);
            case IRON -> merchandiseStatistics.iron().increase(time);
            case GOLD -> merchandiseStatistics.gold().increase(time);
            case STONE -> merchandiseStatistics.stone().increase(time);
        }

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void ironBarProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.ironBar().increase(time);
        playerStatistics.goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void toolProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.tools().increase(time);
        playerStatistics.goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void weaponProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.weapons().increase(time);
        playerStatistics.goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void boatProduced(Player player, long time) {
        getPlayerStatistics(player).boats().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void breadProduced(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.food().increase(time);
        playerStatistics.goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void flagRemoved(Flag flag, long time) {
        getPlayerStatistics(flag.getPlayer()).goods().decrease(flag.getStackedCargo().size(), time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(flag.getPlayer()));
    }

    public void buildingConstructed(Building building, long time) {
        var material = building.getMaterialNeededForConstruction();
        var goods = getPlayerStatistics(building.getPlayer()).goods();

        if (material.planks() > 0) {
            goods.decrease(material.planks(), time);
        }

        if (material.stones() > 0) {
            goods.decrease(material.stones(), time);
        }

        listeners.forEach(listener -> listener.buildingStatisticsChanged(building));
    }

    public void stoneThrown(Catapult catapult, long time) {
        getPlayerStatistics(catapult.getPlayer()).goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(catapult.getPlayer()));
    }

    public void wheatHarvested(Player player, long time) {
        getPlayerStatistics(player).goods().increase(time);
    }

    public void caughtWildAnimal(Player player, long time) {
        var playerStatistics = getPlayerStatistics(player);
        playerStatistics.goods().increase(time);
        playerStatistics.food().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void pigGrown(Player player, long time) {
        getPlayerStatistics(player).goods().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void donkeyGrown(Player player, long time) {
        getPlayerStatistics(player).goods().decrease(2, time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void workerCreatedWithoutTool(Player player, long time) {
        getPlayerStatistics(player).workers().increase(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(player));
    }

    public void militaryBuildingCaptured(Building building, Player previousOwner, long time) {
        var newOwnerPlayerStatistics = getPlayerStatistics(building.getPlayer());
        var previousOwnerPlayerStatistics = getPlayerStatistics(previousOwner);
        var coins = building.getAmount(COIN);

        newOwnerPlayerStatistics.goods().increase(coins, time);
        newOwnerPlayerStatistics.coin().increase(coins, time);
        newOwnerPlayerStatistics.coins().increase(coins, time);
        newOwnerPlayerStatistics.totalAmountBuildings().increase(time);

        previousOwnerPlayerStatistics.goods().decrease(coins, time);
        previousOwnerPlayerStatistics.coin().decrease(coins, time);
        previousOwnerPlayerStatistics.coins().decrease(coins, time);
        previousOwnerPlayerStatistics.totalAmountBuildings().decrease(time);

        listeners.forEach(listener -> listener.generalStatisticsChanged(building.getPlayer()));
    }
}
