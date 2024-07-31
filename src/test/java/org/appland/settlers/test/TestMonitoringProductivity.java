package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Vegetation;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.SawmillWorker;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.GraniteMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestMonitoringProductivity {

    @Test
    public void testMonitoringWhenArmoryProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(armory.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (armory.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                armory.putCargo(new Cargo(COAL, map));
            }

            if (armory.needsMaterial(IRON_BAR)) {
                armory.putCargo(new Cargo(IRON_BAR, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(armory.getProductivity() > 0);
        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), armory);
    }

    @Test
    public void testMonitoringWhenArmoryProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Building armory = map.placeBuilding(new Armory(player0), point1);

        /* Finish construction of the armory */
        Utils.constructHouse(armory);

        /* Populate the armory */
        Worker armorer0 = Utils.occupyBuilding(new Armorer(player0, map), armory);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory);
        assertEquals(armory.getWorker(), armorer0);

        /* Connect the armory with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), armory.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(armory.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (armory.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (armory.needsMaterial(COAL)) {
                armory.putCargo(new Cargo(COAL, map));
            }

            if (armory.needsMaterial(IRON_BAR)) {
                armory.putCargo(new Cargo(IRON_BAR, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(armory.getProductivity() > 0);
        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), armory);

        /* Verify that the changed house is only reported once */
        int productivity = armory.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != armory.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(armory));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenBakeryProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker0);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Make the bakery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            if (bakery.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (bakery.needsMaterial(WATER)) {
                bakery.putCargo(new Cargo(WATER, map));
            }

            if (bakery.needsMaterial(FLOUR)) {
                bakery.putCargo(new Cargo(FLOUR, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(bakery.getProductivity() > 0);
        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), bakery);
    }

    @Test
    public void testMonitoringWhenBakeryProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point1 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point1);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery);

        /* Populate the bakery */
        Worker baker0 = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker0.isInsideBuilding());
        assertEquals(baker0.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker0);

        /* Connect the bakery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), bakery.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Make the bakery create some bread with full resources available */
        for (int i = 0; i < 1000; i++) {

            if (bakery.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (bakery.needsMaterial(WATER)) {
                bakery.putCargo(new Cargo(WATER, map));
            }

            if (bakery.needsMaterial(FLOUR)) {
                bakery.putCargo(new Cargo(FLOUR, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(bakery.getProductivity() > 0);
        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), bakery);

        /* Verify that the changed house is only reported once */
        int productivity = bakery.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != bakery.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(bakery));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenBreweryProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(brewery.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (brewery.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (brewery.needsMaterial(WATER)) {
                brewery.putCargo(new Cargo(WATER, map));
            }

            if (brewery.needsMaterial(WHEAT)) {
                brewery.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), brewery);
    }

    @Test
    public void testMonitoringWhenBreweryProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery */
        Point point1 = new Point(7, 9);
        Building brewery = map.placeBuilding(new Brewery(player0), point1);

        /* Finish construction of the brewery */
        Utils.constructHouse(brewery);

        /* Populate the brewery */
        Worker armorer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), armorer0);

        /* Connect the brewery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), brewery.getFlag());
        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(brewery.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (brewery.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (brewery.needsMaterial(WATER)) {
                brewery.putCargo(new Cargo(WATER, map));
            }

            if (brewery.needsMaterial(WHEAT)) {
                brewery.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), brewery);

        /* Verify that the changed house is only reported once */
        int productivity = brewery.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != brewery.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(brewery));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenCoalMineProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        Building coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine);
        assertEquals(coalMine.getWorker(), miner0);

        /* Connect the coal mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(coalMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (coalMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (coalMine.needsMaterial(FISH)) {
                coalMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), coalMine);
    }

    @Test
    public void testMonitoringWhenCoalMineProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putCoalAtSurroundingTiles(point1, LARGE, map);

        /* Place coal mine */
        Building coalMine = map.placeBuilding(new CoalMine(player0), point1);

        /* Finish construction of the coal mine */
        constructHouse(coalMine);

        /* Populate the coal mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), coalMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), coalMine);
        assertEquals(coalMine.getWorker(), miner0);

        /* Connect the coal mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), coalMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(coalMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (coalMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (coalMine.needsMaterial(FISH)) {
                coalMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), coalMine);

        /* Verify that the changed house is only reported once */
        int productivity = coalMine.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != coalMine.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(coalMine));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenDonkeyFarmProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(7, 9);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm);

        /* Populate the donkey farm */
        Worker donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm);
        assertEquals(donkeyFarm.getWorker(), donkeyBreeder0);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(donkeyFarm.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (donkeyFarm.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (donkeyFarm.needsMaterial(WATER) && donkeyFarm.getAmount(WATER) < 2) {
                donkeyFarm.putCargo(new Cargo(WATER, map));
            }

            if (donkeyFarm.needsMaterial(WHEAT) && donkeyFarm.getAmount(WHEAT) < 2) {
                donkeyFarm.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), donkeyFarm);
    }

    @Test
    public void testDonkeyFarmWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place donkey farm */
        Point point1 = new Point(7, 9);
        Building donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        /* Finish construction of the donkey farm */
        Utils.constructHouse(donkeyFarm);

        /* Populate the donkey farm */
        Worker donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm);
        assertEquals(donkeyFarm.getWorker(), donkeyBreeder0);

        /* Connect the donkey farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(donkeyFarm.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (donkeyFarm.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (donkeyFarm.needsMaterial(WATER) && donkeyFarm.getAmount(WATER) < 2) {
                donkeyFarm.putCargo(new Cargo(WATER, map));
            }

            if (donkeyFarm.needsMaterial(WHEAT) && donkeyFarm.getAmount(WHEAT) < 2) {
                donkeyFarm.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), donkeyFarm);

        /* Verify that the changed house is only reported once */
        int productivity = donkeyFarm.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != donkeyFarm.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(donkeyFarm));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenFarmProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer0.isInsideBuilding());
        assertEquals(farmer0.getHome(), farm);
        assertEquals(farm.getWorker(), farmer0);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(farm.getProductivity(), 0);

        for (int i = 0; i < 3000; i++) {

            if (farm.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), farm);
    }

    @Test
    public void testMonitoringWhenFarmProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point1 = new Point(10, 10);
        Building farm = map.placeBuilding(new Farm(player0), point1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Populate the farm */
        Worker farmer0 = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer0.isInsideBuilding());
        assertEquals(farmer0.getHome(), farm);
        assertEquals(farm.getWorker(), farmer0);

        /* Connect the farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(farm.getProductivity(), 0);

        for (int i = 0; i < 3000; i++) {

            if (farm.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), farm);

        /* Verify that the changed house is only reported once */
        int productivity = farm.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != farm.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(farm));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenFisheryProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(fishery.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (fishery.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), fishery);
    }

    @Test
    public void testMonitoringWhenFisheryProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a fish tile */
        Point point2 = new Point(11, 9);
        Utils.surroundPointWithVegetation(point2, Vegetation.WATER, map);

        /* Place fishery */
        Point point1 = new Point(7, 9);
        Building fishery = map.placeBuilding(new Fishery(player0), point1);

        /* Finish construction of the fishery */
        constructHouse(fishery);

        /* Populate the fishery */
        Worker fisherman0 = Utils.occupyBuilding(new Fisherman(player0, map), fishery);

        assertTrue(fisherman0.isInsideBuilding());
        assertEquals(fisherman0.getHome(), fishery);
        assertEquals(fishery.getWorker(), fisherman0);

        /* Connect the fishery with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fishery.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(fishery.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (fishery.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), fishery);

        /* Verify that the changed house is only reported once */
        int productivity = fishery.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != fishery.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(fishery));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenGoldMineProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine);
        assertEquals(goldMine.getWorker(), miner0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(goldMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (goldMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (goldMine.needsMaterial(FISH)) {
                goldMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), goldMine);
    }

    @Test
    public void testMonitoringWhenGoldMineProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putGoldAtSurroundingTiles(point1, LARGE, map);

        /* Place gold mine */
        Building goldMine = map.placeBuilding(new GoldMine(player0), point1);

        /* Finish construction of the gold mine */
        constructHouse(goldMine);

        /* Populate the gold mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), goldMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), goldMine);
        assertEquals(goldMine.getWorker(), miner0);

        /* Connect the gold mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), goldMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(goldMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (goldMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (goldMine.needsMaterial(FISH)) {
                goldMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), goldMine);

        /* Verify that the changed house is only reported once */
        int productivity = goldMine.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != goldMine.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(goldMine));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenGraniteMineProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), graniteMine);
        assertEquals(graniteMine.getWorker(), miner0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(graniteMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (graniteMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (graniteMine.needsMaterial(FISH)) {
                graniteMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), graniteMine);
    }

    @Test
    public void testMonitoringWhenGraniteMineProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putGraniteAtSurroundingTiles(point1, LARGE, map);

        /* Place granite mine */
        Building graniteMine = map.placeBuilding(new GraniteMine(player0), point1);

        /* Finish construction of the granite mine */
        constructHouse(graniteMine);

        /* Populate the granite mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), graniteMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), graniteMine);
        assertEquals(graniteMine.getWorker(), miner0);

        /* Connect the granite mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), graniteMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(graniteMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (graniteMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (graniteMine.needsMaterial(FISH)) {
                graniteMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), graniteMine);

        /* Verify that the changed house is only reported once */
        int productivity = graniteMine.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != graniteMine.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(graniteMine));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenHunterHutProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place hunter hut */
        Point point1 = new Point(5, 9);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        constructHouse(hunterHut0);

        /* Populate the hunter hut */
        Worker hunter = Utils.occupyBuilding(new Hunter(player0, map), hunterHut0);

        assertTrue(hunter.isInsideBuilding());
        assertEquals(hunter.getHome(), hunterHut0);
        assertEquals(hunterHut0.getWorker(), hunter);

        /* Connect the hunter hut with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), hunterHut0.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a wild animal on the map */
        Utils.putWildAnimalOnOnePoint(map.getPointsWithinRadius(hunterHut0.getPosition(), 4), map);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(hunterHut0.getProductivity(), 0);

        for (int i = 0; i < 2000; i++) {

            if (hunterHut0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Add a new wild animal close to the hunter hut if there is only one left */
            if (map.getWildAnimals().size() < 2) {
                Utils.putWildAnimalOnOnePoint(map.getPointsWithinRadius(hunterHut0.getPosition(), 4), map);
            }

            if (hunterHut0.getProductivity() == 100) {
                break;
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), hunterHut0);

    }

    @Test
    public void testMonitoringWhenHunterHutProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place hunter hut */
        Point point1 = new Point(5, 9);
        Building hunterHut0 = map.placeBuilding(new HunterHut(player0), point1);

        /* Finish construction of the hunter hut */
        constructHouse(hunterHut0);

        /* Populate the hunter hut */
        Worker hunter = Utils.occupyBuilding(new Hunter(player0, map), hunterHut0);

        assertTrue(hunter.isInsideBuilding());
        assertEquals(hunter.getHome(), hunterHut0);
        assertEquals(hunterHut0.getWorker(), hunter);

        /* Connect the hunter hut with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), hunterHut0.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a wild animal on the map */
        Utils.putWildAnimalOnOnePoint(map.getPointsWithinRadius(hunterHut0.getPosition(), 4), map);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(hunterHut0.getProductivity(), 0);

        for (int i = 0; i < 2000; i++) {

            if (hunterHut0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Add a new wild animal close to the hunter hut if there is only one left */
            if (map.getWildAnimals().size() < 2) {
                Utils.putWildAnimalOnOnePoint(map.getPointsWithinRadius(hunterHut0.getPosition(), 4), map);
            }

            if (hunterHut0.getProductivity() == 100) {
                break;
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), hunterHut0);

        /* Verify that the changed house is only reported once */
        int productivity = hunterHut0.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != hunterHut0.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(hunterHut0));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenIronMineProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Populate the iron mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), ironMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine);
        assertEquals(ironMine.getWorker(), miner0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(ironMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (ironMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (ironMine.needsMaterial(FISH)) {
                ironMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), ironMine);
    }

    @Test
    public void testMonitoringWhenIronMineProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Put a small mountain on the map */
        Point point1 = new Point(7, 9);
        Utils.surroundPointWithMinableMountain(point1, map);
        Utils.putIronAtSurroundingTiles(point1, LARGE, map);

        /* Place iron mine */
        Building ironMine = map.placeBuilding(new IronMine(player0), point1);

        /* Finish construction of the iron mine */
        constructHouse(ironMine);

        /* Populate the iron mine */
        Worker miner0 = Utils.occupyBuilding(new Miner(player0, map), ironMine);

        assertTrue(miner0.isInsideBuilding());
        assertEquals(miner0.getHome(), ironMine);
        assertEquals(ironMine.getWorker(), miner0);

        /* Connect the iron mine with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironMine.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(ironMine.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (ironMine.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (ironMine.needsMaterial(FISH)) {
                ironMine.putCargo(new Cargo(FISH, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), ironMine);

        /* Verify that the changed house is only reported once */
        int productivity = ironMine.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != ironMine.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(ironMine));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenIronSmelterProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);


        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(ironSmelter.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (ironSmelter.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), ironSmelter);

    }

    @Test
    public void testMonitoringWhenIronSmelterProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter */
        Point point1 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);

        /* Finish construction of the iron smelter */
        Utils.constructHouse(ironSmelter);

        /* Populate the iron smelter */
        Worker ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        /* Connect the iron smelter with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), ironSmelter.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);


        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(ironSmelter.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (ironSmelter.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (ironSmelter.needsMaterial(COAL) && ironSmelter.getAmount(COAL) < 2) {
                ironSmelter.putCargo(new Cargo(COAL, map));
            }

            if (ironSmelter.needsMaterial(IRON) && ironSmelter.getAmount(IRON) < 2) {
                ironSmelter.putCargo(new Cargo(IRON, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), ironSmelter);
    }

    @Test
    public void testMonitoringWhenMetalworksProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Populate the metalworks */
        Worker armorer0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), armorer0);

        /* Connect the metalworks with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(metalworks.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (metalworks.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (metalworks.needsMaterial(IRON_BAR)) {
                metalworks.putCargo(new Cargo(IRON_BAR, map));
            }

            if (metalworks.needsMaterial(PLANK)) {
                metalworks.putCargo(new Cargo(PLANK, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), metalworks);
    }

    @Test
    public void testMonitoringWhenMetalworksProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Populate the metalworks */
        Worker armorer0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), armorer0);

        /* Connect the metalworks with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(metalworks.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (metalworks.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (metalworks.needsMaterial(IRON_BAR)) {
                metalworks.putCargo(new Cargo(IRON_BAR, map));
            }

            if (metalworks.needsMaterial(PLANK)) {
                metalworks.putCargo(new Cargo(PLANK, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), metalworks);

        /* Verify that the changed house is only reported once */
        int productivity = metalworks.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != metalworks.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(metalworks));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenMillProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Building mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill);
        assertEquals(mill.getWorker(), miller0);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(mill.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (mill.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), mill);
    }

    @Test
    public void testMonitoringWhenMillProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mill */
        Point point1 = new Point(7, 9);
        Building mill = map.placeBuilding(new Mill(player0), point1);

        /* Finish construction of the mill */
        Utils.constructHouse(mill);

        /* Populate the mill */
        Worker miller0 = Utils.occupyBuilding(new Miller(player0, map), mill);

        assertTrue(miller0.isInsideBuilding());
        assertEquals(miller0.getHome(), mill);
        assertEquals(mill.getWorker(), miller0);

        /* Connect the mill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mill.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(mill.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (mill.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (mill.needsMaterial(WHEAT) && mill.getAmount(WHEAT) < 2) {
                mill.putCargo(new Cargo(WHEAT, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), mill);
    }

    @Test
    public void testMonitoringWhenMintProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint);
        assertEquals(mint.getWorker(), minter0);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(mint.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (mint.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }

            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), mint);
    }

    @Test
    public void testMonitoringWhenMintProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point1 = new Point(7, 9);
        Building mint = map.placeBuilding(new Mint(player0), point1);

        /* Finish construction of the mint */
        Utils.constructHouse(mint);

        /* Populate the mint */
        Worker minter0 = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter0.isInsideBuilding());
        assertEquals(minter0.getHome(), mint);
        assertEquals(mint.getWorker(), minter0);

        /* Connect the mint with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), mint.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(mint.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (mint.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (mint.needsMaterial(COAL) && mint.getAmount(COAL) < 2) {
                mint.putCargo(new Cargo(COAL, map));
            }

            if (mint.needsMaterial(GOLD) && mint.getAmount(GOLD) < 2) {
                mint.putCargo(new Cargo(GOLD, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), mint);
    }

    @Test
    public void testMonitoringWhenPigFarmProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(pigFarm.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (pigFarm.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), pigFarm);
    }

    @Test
    public void testMonitoringWhenPigFarmProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place pig farm */
        Point point1 = new Point(7, 9);
        Building pigFarm = map.placeBuilding(new PigFarm(player0), point1);

        /* Finish construction of the pig farm */
        Utils.constructHouse(pigFarm);

        /* Populate the pig farm */
        Worker pigBreeder0 = Utils.occupyBuilding(new PigBreeder(player0, map), pigFarm);

        assertTrue(pigBreeder0.isInsideBuilding());
        assertEquals(pigBreeder0.getHome(), pigFarm);
        assertEquals(pigFarm.getWorker(), pigBreeder0);

        /* Connect the pig farm with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), pigFarm.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(pigFarm.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (pigFarm.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (pigFarm.needsMaterial(WHEAT) && pigFarm.getAmount(WHEAT) < 2) {
                pigFarm.putCargo(new Cargo(WHEAT, map));
            }

            if (pigFarm.needsMaterial(WATER) && pigFarm.getAmount(WATER) < 2) {
                pigFarm.putCargo(new Cargo(WATER, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), pigFarm);
    }

    @Test
    public void testMonitoringWhenQuarryProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a stone on the map */
        Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(quarry0.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (quarry0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Put a new stone if the current one is gone */
            if (map.getStones().isEmpty()) {
                Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), quarry0);
    }

    @Test
    public void testMonitoringWhenQuarryProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Place a stone on the map */
        Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(quarry0.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (quarry0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Put a new stone if the current one is gone */
            if (map.getStones().isEmpty()) {
                Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), quarry0);
    }

    @Test
    public void testMonitoringWhenSawmillProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(sawmill.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (sawmill.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                sawmill.putCargo(new Cargo(WOOD, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), sawmill);
    }

    @Test
    public void testMonitoringWhenSawmillProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill);

        /* Populate the sawmill */
        Worker sawmillWorker0 = Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        /* Connect the sawmill with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(sawmill.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (sawmill.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                sawmill.putCargo(new Cargo(WOOD, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), sawmill);

        /* Verify that the changed house is only reported once */
        int productivity = sawmill.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != sawmill.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(sawmill));
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringWhenSlaughterHouseProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(slaughterHouse);

        /* Populate the butcher */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher0);

        /* Connect the butcher with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(slaughterHouse.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (slaughterHouse.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (slaughterHouse.needsMaterial(PIG) && slaughterHouse.getAmount(PIG) < 2) {
                slaughterHouse.putCargo(new Cargo(PIG, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), slaughterHouse);
    }

    @Test
    public void testMonitoringWhenSlaughterHouseProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place butcher */
        Point point1 = new Point(7, 9);
        Building slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        /* Finish construction of the butcher */
        Utils.constructHouse(slaughterHouse);

        /* Populate the butcher */
        Worker butcher0 = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher0);

        /* Connect the butcher with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(slaughterHouse.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (slaughterHouse.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            if (slaughterHouse.needsMaterial(PIG) && slaughterHouse.getAmount(PIG) < 2) {
                slaughterHouse.putCargo(new Cargo(PIG, map));
            }
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), slaughterHouse);
    }

    @Test
    public void testMonitoringWhenWellProductionPercentageChanges() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        /* Populate the well */
        Worker wellWorker0 = Utils.occupyBuilding(new WellWorker(player0, map), well);

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well);
        assertEquals(well.getWorker(), wellWorker0);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(well.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (well.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), well);
    }

    @Test
    public void testMonitoringWhenWellProductionPercentageChangesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well */
        Point point1 = new Point(7, 9);
        Building well = map.placeBuilding(new Well(player0), point1);

        /* Finish construction of the well */
        Utils.constructHouse(well);

        /* Populate the well */
        Worker wellWorker0 = Utils.occupyBuilding(new WellWorker(player0, map), well);

        assertTrue(wellWorker0.isInsideBuilding());
        assertEquals(wellWorker0.getHome(), well);
        assertEquals(well.getWorker(), wellWorker0);

        /* Connect the well with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), well.getFlag());

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(well.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {

            if (well.getProductivity() != 0) {
                break;
            }

            map.stepTime();
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), well);
    }

    @Test
    public void testMonitoringWhenWoodcutterProductionPercentageChanges() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        constructHouse(woodcutter0);

        /* Place a lot of trees on the map */
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);

        /* Wait for the trees to grow up */
        Utils.fastForward(300, map);

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(woodcutter0.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {
            if (woodcutter0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Fill up with more trees */
            Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(woodcutter0.getProductivity() > 0);
        assertEquals(gameChangesList.changedBuildings().size(), 1);
        assertEquals(gameChangesList.changedBuildings().iterator().next(), woodcutter0);
    }

    @Test
    public void testMonitoringWhenWoodcutterProductionPercentageChangesIsOnlySentOnce() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter hut */
        Point point1 = new Point(7, 9);
        Building woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Finish construction of the woodcutter hut */
        constructHouse(woodcutter0);

        /* Place a lot of trees on the map */
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);

        /* Wait for the trees to grow up */
        Utils.fastForward(300, map);

        /* Start monitoring the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Populate the woodcutter hut */
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker);

        /* Verify that a monitoring event is sent when the productivity increases */
        assertEquals(woodcutter0.getProductivity(), 0);

        for (int i = 0; i < 1000; i++) {
            if (woodcutter0.getProductivity() != 0) {
                break;
            }

            map.stepTime();

            /* Fill up with more trees */
            Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);
        }

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.changedBuildings().contains(woodcutter0));

        /* Verify that the changed house is only reported once */
        int productivity = woodcutter0.getProductivity();

        for (int i = 0; i < 30; i++) {
            if (productivity != woodcutter0.getProductivity()) {
                break;
            }

            for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
                assertFalse(newChanges.changedBuildings().contains(woodcutter0));
            }

            map.stepTime();
        }
    }
}
