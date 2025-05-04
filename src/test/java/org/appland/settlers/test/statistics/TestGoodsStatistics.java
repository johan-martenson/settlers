package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.ResourceLevel;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Armorer;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Shipwright;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.LookoutTower;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.statistics.StatisticsManager;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestGoodsStatistics {

    @Test
    public void testInitialGoodsStatistics() throws InvalidUserActionException {

        // Start a new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters and set the initial resources
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarter0.setInitialResources(ResourceLevel.MEDIUM);

        // Verify that the initial goods statistics are correct
        var statisticsManager = map.getStatisticsManager();

        assertEquals(
                statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getFirst().value(),
                headquarter0.getAmount(Material.WOOD) +
                        headquarter0.getAmount(Material.PLANK) +
                        headquarter0.getAmount(Material.STONE) +
                        headquarter0.getAmount(Material.PIG) +
                        headquarter0.getAmount(Material.WHEAT) +
                        headquarter0.getAmount(Material.FLOUR) +
                        headquarter0.getAmount(Material.FISH) +
                        headquarter0.getAmount(Material.MEAT) +
                        headquarter0.getAmount(Material.BREAD) +
                        headquarter0.getAmount(Material.WATER) +
                        headquarter0.getAmount(Material.BEER) +
                        headquarter0.getAmount(Material.COAL) +
                        headquarter0.getAmount(Material.IRON) +
                        headquarter0.getAmount(Material.GOLD) +
                        headquarter0.getAmount(Material.IRON_BAR) +
                        headquarter0.getAmount(Material.COIN) +
                        headquarter0.getAmount(Material.TONGS) +
                        headquarter0.getAmount(Material.AXE) +
                        headquarter0.getAmount(Material.SAW) +
                        headquarter0.getAmount(Material.PICK_AXE) +
                        headquarter0.getAmount(Material.HAMMER) +
                        headquarter0.getAmount(Material.SHOVEL) +
                        headquarter0.getAmount(Material.CRUCIBLE) +
                        headquarter0.getAmount(Material.FISHING_ROD) +
                        headquarter0.getAmount(Material.SCYTHE) +
                        headquarter0.getAmount(Material.CLEAVER) +
                        headquarter0.getAmount(Material.ROLLING_PIN) +
                        headquarter0.getAmount(Material.BOW) +
                        headquarter0.getAmount(Material.SWORD) +
                        headquarter0.getAmount(Material.SHIELD) +
                        headquarter0.getAmount(Material.BOAT)
        );
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenWoodIsCutDown() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarters0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarters0.setInitialResources(ResourceLevel.MEDIUM);

        // Place and grow the tree
        var point2 = new Point(12, 4);
        var tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        var point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

        // Let the woodcutter reach the tree and start cutting
        Utils.waitForWoodcutterToStartCuttingTree(wcWorker, map);

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that the good statistics is updated when the tree is cut down
        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();

        for (int i = 0; i < 2000; i++) {
            if (wcWorker.getCargo() != null) {
                break;
            }

            assertEquals(goodsStatistics.getMeasurements().size(), nrMeasurementsBefore);

            map.stepTime();
        }

        assertNotNull(wcWorker.getCargo());
        assertEquals(goodsStatistics.getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), 1);
    }

    @Test
    public void testGoodsStatisticsWhenWoodIsConsumedAndPlankIsProduced() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarters0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarters0.setInitialResources(ResourceLevel.MEDIUM);

        // Adjust the inventory to only contain one piece of wood
        Utils.adjustInventoryTo(headquarters0, Material.WOOD, 1);

        // Place sawmill, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters0.getFlag(), sawmill.getFlag());

        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        /* Verify that goods statistics remains the same when a piece of wood is consumed.
            -- one piece of wood is consumed and one plank is produced
         */
        Utils.waitForBuildingToHave(sawmill, Material.WOOD, 1);

        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();

        Utils.waitForBuildingToHave(sawmill, Material.WOOD, 0);

        assertEquals(goodsStatistics.getMeasurements().size(), nrMeasurementsBefore);
    }

    @Test
    public void testGoodsStatisticsIsUpdatedWhenFlagWithWoodCargoIsRemoved() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarters0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarters0.setInitialResources(ResourceLevel.MEDIUM);

        // Place flag and connect it to the headquarters
        var point1 = new Point(15, 9);
        var flag0 = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarters0.getFlag());

        // Place sawmill, connect it to the headquarters, and wait for it to get constructed and occupied
        var point2 = new Point(20, 10);
        var sawmill = map.placeBuilding(new Sawmill(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill.getFlag());

        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that goods statistics is updated when a piece of wood is by the flag and the flag is removed
        Utils.adjustInventoryTo(headquarters0, Material.WOOD, 1);

        assertTrue(sawmill.needsMaterial(Material.WOOD));
        assertEquals(headquarters0.getAmount(Material.WOOD), 1);

        Utils.waitForFlagToHaveCargoWaiting(map, flag0, Material.WOOD);

        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();

        map.removeFlag(flag0);

        map.stepTime();

        assertEquals(goodsStatistics.getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenStoneIsCut() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarters0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarters0.setInitialResources(ResourceLevel.MEDIUM);

        // Place stone
        var point1 = new Point(15, 15);
        var stone0 = map.placeStone(point1, Stone.StoneType.STONE_1, 7);

        // Place quarry, connect it to the headquarters, and wait for it to get constructed and occupied
        var point2 = new Point(20, 10);
        var quarry = map.placeBuilding(new Quarry(player0), point2);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters0.getFlag(), quarry.getFlag());

        Utils.waitForBuildingToBeConstructed(quarry);

        var stonemason = (Stonemason) Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        // Wait for the stonemason to start cutting a stone
        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        // Verify that the good statistics is updated when a stone is produced
        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();

        var goodsNrMeasurements = goodsStatistics.getMeasurements().size();
        var goodsAmount = goodsStatistics.getMeasurements().getLast().value();

        for (int i = 0; i < 2000; i++) {
            if (stonemason.getCargo() != null) {
                break;
            }

            assertEquals(goodsStatistics.getMeasurements().size(), goodsNrMeasurements);

            map.stepTime();
        }

        assertEquals(goodsStatistics.getMeasurements().size(), goodsNrMeasurements + 1);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), goodsAmount + 1);

        map.stepTime();

        assertEquals(goodsStatistics.getMeasurements().size(), goodsNrMeasurements + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenPlanksAndStoneIsConsumedDuringConstruction() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(10, 10);
        var headquarters0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarters0.setInitialResources(ResourceLevel.MEDIUM);

        // Place a building that requires both planks and stones to construct, and connect it to the headquarters
        var point1 = new Point(14, 10);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, headquarters0.getFlag(), sawmill.getFlag());

        // Wait for the building to be under construction
        Utils.waitForBuildingToBeUnderConstruction(sawmill);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that the goods statistics are updated when the building is constructed
        StatisticsManager statisticsManager = map.getStatisticsManager();

        var generalStatistics = statisticsManager.getPlayerStatistics(player0);
        var nrMeasurementsBefore = generalStatistics.goods().getMeasurements().size();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();
        var goodsBefore = generalStatistics.goods().getMeasurements().getLast().value();

        for (int i = 0; i < 5000; i++) {
            if (!sawmill.isUnderConstruction()) {
                break;
            }

            assertTrue(sawmill.isUnderConstruction());
            assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore);
            assertEquals(generalStatistics.goods().getMeasurements().size(), nrMeasurementsBefore);

            map.stepTime();
        }

        assertFalse(sawmill.isUnderConstruction());
        assertTrue(monitor.getStatisticsEvents().size() > nrStatisticsEventsBefore);
        assertEquals(generalStatistics.goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(generalStatistics.goods().getMeasurements().getLast().value(), goodsBefore - 4);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenStoneIsThrownByCatapult() throws InvalidUserActionException {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place headquarters
        var point0 = new Point(13, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Ensure player0 only has stones to build the catapult
        Utils.adjustInventoryTo(headquarter0, STONE, 2);

        // Place barracks, connect it to the headquarters, and wait for it to get constructed and occupied
        var point2 = new Point(33, 5);
        var barracks0 = map.placeBuilding(new Barracks(player1), point2);
        var road0 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Place catapult, connect it to the headquarters, and wait for it to get constructed and occupied
        var point3 = new Point(27, 15);
        var catapult = map.placeBuilding(new Catapult(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult.getFlag());

        Utils.waitForBuildingToBeConstructed(catapult);

        var catapultWorker0 = (CatapultWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        // Give player0 a stone and wait for it to get delivered to the catapult
        Utils.adjustInventoryTo(headquarter0, STONE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Wait for the catapult to throw a projectile
        assertTrue(catapult.isReady());

        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();

        for (int i = 0; i < 2000; i++) {
            if (!map.getProjectiles().isEmpty()) {
                break;
            }

            monitor.getStatisticsEvents().clear();

            map.stepTime();
        }

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore - 1);
        assertTrue(monitor.getStatisticsEvents().size() > 0);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenSoldierIsDrafted() throws InvalidUserActionException {

        // Create new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(13, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        headquarter0.setInitialResources(ResourceLevel.MEDIUM);

        // Adjust the inventory so the headquarters has one beer, one sword, and one shield
        Utils.adjustInventoryTo(headquarter0, BEER, 1);
        Utils.adjustInventoryTo(headquarter0, SWORD, 1);
        Utils.adjustInventoryTo(headquarter0, SHIELD, 1);

        // Verify that the goods statistics are updated when a soldier is drafted
        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();
        var privates = headquarter0.getAmount(PRIVATE);

        for (int i = 0; i < 2000; i++) {
            if (headquarter0.getAmount(PRIVATE) > privates) {
                break;
            }

            map.stepTime();
        }

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore - 3);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenFishIsCaught() throws InvalidUserActionException {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place a small lake for fishing
        var point2 = new Point(5, 5);
        map.setVegetationBelow(point2, WATER);

        // Place headquarters
        var point3 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point3);

        // Place fishery, connect it to the headquarters, and wait for it to get constructed and occupied
        var point4 = new Point(7, 5);
        var fishery = map.placeBuilding(new Fishery(player0), point4);
        var road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarters.getFlag());

        Utils.waitForBuildingToBeConstructed(fishery);

        var fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        // Wait for the fisherman to start fishing
        Utils.waitForFishermanToStartFishing(fisherman, map);

        // Verify that the goods statistics are updated when a fish is caught
        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();

        for (int i = 0; i < 2000; i++) {
            if (fisherman.getCargo() != null) {
                break;
            }

            map.stepTime();
        }

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore + 1);

        map.stepTime();

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenWheatIsHarvested() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Remove all wheat from the headquarters
        Utils.adjustInventoryTo(headquarter, WHEAT, 0);

        // Place mill
        var point4 = new Point(10, 4);
        var mill = map.placeBuilding(new Mill(player0), point4);

        // Connect the mill to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        // Wait for the mill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(mill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        // Place the farm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(14, 4);
        var farm = map.placeBuilding(new Farm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(farm);

        var farmer = (Farmer) Utils.waitForNonMilitaryBuildingToGetPopulated(farm);

        // Wait for the farmer to start harvesting
        Utils.waitForFarmerToStartHarvesting(map, farmer);

        // Verify that the goods statistics are updated when wheat is harvested
        StatisticsManager statisticsManager = map.getStatisticsManager();
        var goodsStatistics = statisticsManager.getPlayerStatistics(player0).goods();
        var nrMeasurementsBefore = goodsStatistics.getMeasurements().size();
        var valueBefore = goodsStatistics.getMeasurements().getLast().value();

        for (int i = 0; i < 2000; i++) {
            if (farmer.getCargo() != null) {
                break;
            }

            map.stepTime();
        }

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore + 1);

        map.stepTime();

        assertTrue(goodsStatistics.getMeasurements().size() > nrMeasurementsBefore);
        assertEquals(goodsStatistics.getMeasurements().getLast().value(), valueBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenHunterCatchesPrey() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place hunter hut, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var hunterHut = map.placeBuilding(new HunterHut(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), hunterHut.getFlag());

        Utils.waitForBuildingToBeConstructed(hunterHut);
        var hunter = (Hunter) Utils.waitForNonMilitaryBuildingToGetPopulated(hunterHut);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that the goods statistics is updated when the hunter catches a wild animal
        StatisticsManager statisticsManager5 = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager5.getPlayerStatistics(player0).goods().getMeasurements().size();
        StatisticsManager statisticsManager4 = map.getStatisticsManager();
        var valueBefore = statisticsManager4.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (hunter.getCargo() != null) {
                break;
            }

            StatisticsManager statisticsManager1 = map.getStatisticsManager();
            assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            StatisticsManager statisticsManager = map.getStatisticsManager();
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(hunter.getCargo());
        StatisticsManager statisticsManager3 = map.getStatisticsManager();
        assertEquals(statisticsManager3.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager2 = map.getStatisticsManager();
        assertEquals(statisticsManager2.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        StatisticsManager statisticsManager1 = map.getStatisticsManager();
        assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager = map.getStatisticsManager();
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenWellProducesWater() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place well, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var well = map.placeBuilding(new Well(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), well.getFlag());

        Utils.waitForBuildingToBeConstructed(well);
        var wellWorker = (WellWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that the goods statistics is updated when the well produces a bucket of water
        StatisticsManager statisticsManager5 = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager5.getPlayerStatistics(player0).goods().getMeasurements().size();
        StatisticsManager statisticsManager4 = map.getStatisticsManager();
        var valueBefore = statisticsManager4.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (wellWorker.getCargo() != null) {
                break;
            }

            StatisticsManager statisticsManager1 = map.getStatisticsManager();
            assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            StatisticsManager statisticsManager = map.getStatisticsManager();
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(wellWorker.getCargo());
        StatisticsManager statisticsManager3 = map.getStatisticsManager();
        assertEquals(statisticsManager3.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager2 = map.getStatisticsManager();
        assertEquals(statisticsManager2.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        StatisticsManager statisticsManager1 = map.getStatisticsManager();
        assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager = map.getStatisticsManager();
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    // Test for production of one material that consumes several materials
    // - Metalworks, Mint, Bakery, Pig farm, Donkey farm, Armory, Slaughter house, military buildings (promotions)
    @Test
    public void testGoodsStatisticsUpdatedWhenToolIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place metalworks, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var metalworks = map.placeBuilding(new Metalworks(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), metalworks.getFlag());

        Utils.waitForBuildingToBeConstructed(metalworks);
        var metalWorker = (Metalworker) Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the metalworks planks and an iron bar
        Utils.deliverCargo(metalworks, Material.PLANK);
        Utils.deliverCargo(metalworks, IRON_BAR);

        // Verify that the goods statistics is updated when the metalworks produces a tool
        StatisticsManager statisticsManager5 = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager5.getPlayerStatistics(player0).goods().getMeasurements().size();
        StatisticsManager statisticsManager4 = map.getStatisticsManager();
        var valueBefore = statisticsManager4.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (metalWorker.getCargo() != null) {
                break;
            }

            StatisticsManager statisticsManager1 = map.getStatisticsManager();
            assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            StatisticsManager statisticsManager = map.getStatisticsManager();
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(metalWorker.getCargo());
        StatisticsManager statisticsManager3 = map.getStatisticsManager();
        assertEquals(statisticsManager3.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager2 = map.getStatisticsManager();
        assertEquals(statisticsManager2.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        StatisticsManager statisticsManager1 = map.getStatisticsManager();
        assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager = map.getStatisticsManager();
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenCoinIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place mint, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var mint = map.placeBuilding(new Mint(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), mint.getFlag());

        Utils.waitForBuildingToBeConstructed(mint);
        var minter = (Minter) Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the mint coal and gold
        Utils.deliverCargo(mint, COAL);
        Utils.deliverCargo(mint, GOLD);

        // Verify that the goods statistics is updated when the mint produces a coin
        StatisticsManager statisticsManager5 = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager5.getPlayerStatistics(player0).goods().getMeasurements().size();
        StatisticsManager statisticsManager4 = map.getStatisticsManager();
        var valueBefore = statisticsManager4.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (minter.getCargo() != null) {
                break;
            }

            StatisticsManager statisticsManager1 = map.getStatisticsManager();
            assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            StatisticsManager statisticsManager = map.getStatisticsManager();
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(minter.getCargo());
        StatisticsManager statisticsManager3 = map.getStatisticsManager();
        assertEquals(statisticsManager3.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager2 = map.getStatisticsManager();
        assertEquals(statisticsManager2.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        StatisticsManager statisticsManager1 = map.getStatisticsManager();
        assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager = map.getStatisticsManager();
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenWeaponIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var armory = map.placeBuilding(new Armory(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), armory.getFlag());

        Utils.waitForBuildingToBeConstructed(armory);
        var armorer = (Armorer) Utils.waitForNonMilitaryBuildingToGetPopulated(armory);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the armory coal and gold
        Utils.deliverCargo(armory, COAL);
        Utils.deliverCargo(armory, IRON_BAR);

        // Verify that the goods statistics is updated when the armory produces a weapon
        StatisticsManager statisticsManager5 = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager5.getPlayerStatistics(player0).goods().getMeasurements().size();
        StatisticsManager statisticsManager4 = map.getStatisticsManager();
        var valueBefore = statisticsManager4.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (armorer.getCargo() != null) {
                break;
            }

            StatisticsManager statisticsManager1 = map.getStatisticsManager();
            assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            StatisticsManager statisticsManager = map.getStatisticsManager();
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(armorer.getCargo());
        StatisticsManager statisticsManager3 = map.getStatisticsManager();
        assertEquals(statisticsManager3.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager2 = map.getStatisticsManager();
        assertEquals(statisticsManager2.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        StatisticsManager statisticsManager1 = map.getStatisticsManager();
        assertEquals(statisticsManager1.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        StatisticsManager statisticsManager = map.getStatisticsManager();
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenBreadIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place bakery, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var bakery = map.placeBuilding(new Bakery(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), bakery.getFlag());

        Utils.waitForBuildingToBeConstructed(bakery);
        var baker = (Baker) Utils.waitForNonMilitaryBuildingToGetPopulated(bakery);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the bakery coal and gold
        Utils.deliverCargo(bakery, FLOUR);
        Utils.deliverCargo(bakery, Material.WATER);

        // Verify that the goods statistics is updated when the bakery produces a loaf of bread
        var statisticsManager = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var valueBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (baker.getCargo() != null) {
                break;
            }

            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(baker.getCargo());
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenPigIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place pig farm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var pigFarm = map.placeBuilding(new PigFarm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), pigFarm.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);
        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the pigFarm coal and gold
        Utils.deliverCargo(pigFarm, WHEAT);
        Utils.deliverCargo(pigFarm, Material.WATER);

        // Verify that the goods statistics is updated when the pigFarm produces a loaf of bread
        var statisticsManager = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var valueBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (pigBreeder.getCargo() != null) {
                break;
            }

            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(pigBreeder.getCargo());
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenDonkeyIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkeyFarm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), donkeyFarm.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyFarm);
        var donkeyBreeder = (DonkeyBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyFarm);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the donkeyFarm coal and gold
        Utils.deliverCargo(donkeyFarm, WHEAT);
        Utils.deliverCargo(donkeyFarm, Material.WATER);

        // Verify that the goods statistics is updated when the donkeyFarm produces a loaf of bread
        var statisticsManager = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var valueBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();
        var donkeysBefore = map.getWorkers().stream()
                .filter(worker -> worker instanceof Donkey)
                .count();

        for (int i = 0; i < 10_000; i++) {
            if (map.getWorkers().stream()
                    .filter(worker -> worker instanceof Donkey)
                    .count() != donkeysBefore) {
                break;
            }

            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 2);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 2);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenIronBarIsProduced() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Place ironSmelter, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), ironSmelter.getFlag());

        Utils.waitForBuildingToBeConstructed(ironSmelter);
        var ironFounder = (IronFounder) Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        // Start monitoring statistics
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Give the ironSmelter coal and iron
        Utils.deliverCargo(ironSmelter, COAL);
        Utils.deliverCargo(ironSmelter, IRON);

        // Verify that the goods statistics is updated when the ironSmelter produces a bar of iron
        var statisticsManager = map.getStatisticsManager();
        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var valueBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (ironFounder.getCargo() != null) {
                break;
            }

            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(ironFounder.getCargo());
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    // Test for all worker creation
    @Test
    public void testGoodsAndWorkerStatisticsWhenArmorerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, ARMORER, 0);
        Utils.adjustInventoryTo(headquarters, TONGS, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place armory, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var armory = map.placeBuilding(new Armory(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), armory.getFlag());

        Utils.waitForBuildingToBeConstructed(armory);

        // Verify that the goods statistics is updated when an armorer is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        assertEquals(headquarters.getAmount(TONGS), 1);

        map.stepTime();

        assertEquals(headquarters.getAmount(TONGS), 0);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenBakerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, BAKER, 0);
        Utils.adjustInventoryTo(headquarters, ROLLING_PIN, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place bakery, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var bakery = map.placeBuilding(new Bakery(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), bakery.getFlag());

        Utils.waitForBuildingToBeConstructed(bakery);

        // Verify that the goods statistics is updated when a baker is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        assertEquals(headquarters.getAmount(ROLLING_PIN), 1);

        map.stepTime();

        assertEquals(headquarters.getAmount(ROLLING_PIN), 0);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenBrewerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, BREWER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place brewery, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var brewery = map.placeBuilding(new Brewery(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), brewery.getFlag());

        Utils.waitForBuildingToBeConstructed(brewery);

        // Verify that the goods statistics is updated when a brewer is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        assertTrue(brewery.needsWorker());

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Brewer));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenCatapultWorkerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, CATAPULT_WORKER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place catapult, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var catapult = map.placeBuilding(new Catapult(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), catapult.getFlag());

        Utils.waitForBuildingToBeConstructed(catapult);

        // Verify that the goods statistics is updated when a catapult worker is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof CatapultWorker));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenButcherIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, BUTCHER, 0);
        Utils.adjustInventoryTo(headquarters, CLEAVER, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place slaughterHouse, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), slaughterHouse.getFlag());

        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        // Verify that the goods statistics is updated when a butcher is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(CLEAVER), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Builder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndStatisticsStatisticsWhenCarpenterIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, CARPENTER, 0);
        Utils.adjustInventoryTo(headquarters, SAW, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place sawmill, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), sawmill.getFlag());

        Utils.waitForBuildingToBeConstructed(sawmill);

        // Verify that the goods statistics is updated when a butcher is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(SAW), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Carpenter));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenDonkeyBreederIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, DONKEY_BREEDER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place donkeyFarm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), donkeyFarm.getFlag());

        Utils.waitForBuildingToBeConstructed(donkeyFarm);

        // Verify that the goods statistics is updated when a donkey breeder is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof DonkeyBreeder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenFarmerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, FARMER, 0);
        Utils.adjustInventoryTo(headquarters, SCYTHE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place farm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var farm = map.placeBuilding(new Farm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), farm.getFlag());

        Utils.waitForBuildingToBeConstructed(farm);

        // Verify that the goods statistics is updated when a farmer is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(SCYTHE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Farmer));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenFishermanIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, FISHERMAN, 0);
        Utils.adjustInventoryTo(headquarters, FISHING_ROD, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place lake
        var point2 = new Point(15, 5);
        Utils.surroundPointWithWater(point2, map);

        // Place fishery, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var fishery = map.placeBuilding(new Fishery(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), fishery.getFlag());

        Utils.waitForBuildingToBeConstructed(fishery);

        // Verify that the goods statistics is updated when a butcher is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(FISHING_ROD), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Fisherman));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenForesterIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, FORESTER, 0);
        Utils.adjustInventoryTo(headquarters, SHOVEL, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place foresterHut, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), foresterHut.getFlag());

        Utils.waitForBuildingToBeConstructed(foresterHut);

        // Verify that the goods statistics is updated when a butcher is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(SHOVEL), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Forester));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenGeologistIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        headquarters.setInitialResources(ResourceLevel.MEDIUM);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, GEOLOGIST, 0);
        Utils.adjustInventoryTo(headquarters, PICK_AXE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place mountain
        var point2 = new Point(15, 5);
        Utils.surroundPointWithMinableMountain(point2, map);

        // Place flag and connect it to the headquarters
        var point1 = new Point(10, 4);
        var flag = map.placeFlag(player0, point1);
        var road0 = map.placeAutoSelectedRoad(player0, flag, headquarters.getFlag());

        // Call geologist
        flag.callGeologist();

        // Verify that the goods statistics is updated when a butcher is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (var i = 0; i < 200; i++) {
            if (map.getWorkers().stream().anyMatch(worker -> worker instanceof Geologist)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(headquarters.getAmount(PICK_AXE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Geologist));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenHunterIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, HUNTER, 0);
        Utils.adjustInventoryTo(headquarters, BOW, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place hunterHut, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var hunterHut = map.placeBuilding(new HunterHut(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), hunterHut.getFlag());

        Utils.waitForBuildingToBeConstructed(hunterHut);

        // Verify that the goods statistics is updated when a hunter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(BOW), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Hunter));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenIronFounderIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, IRON_FOUNDER, 0);
        Utils.adjustInventoryTo(headquarters, CRUCIBLE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place ironSmelter, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var ironSmelter = map.placeBuilding(new IronSmelter(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), ironSmelter.getFlag());

        Utils.waitForBuildingToBeConstructed(ironSmelter);

        // Verify that the goods statistics is updated when an iron founder is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(CRUCIBLE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof IronFounder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenMetalworkerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, METALWORKER, 0);
        Utils.adjustInventoryTo(headquarters, HAMMER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place metalworks, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var metalworks = map.placeBuilding(new Metalworks(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), metalworks.getFlag());

        Utils.waitForBuildingToBeConstructed(metalworks);

        // Verify that the goods statistics is updated when a metalworker is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        Utils.adjustInventoryTo(headquarters, HAMMER, 1);

        assertTrue(metalworks.isReady());
        assertTrue(metalworks.needsWorker());
        assertEquals(headquarters.getAmount(HAMMER), 1);

        for (int i = 0; i < 200; i++) {
            if (map.getWorkers().stream().anyMatch(worker -> worker instanceof Metalworker)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(headquarters.getAmount(HAMMER), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Metalworker));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenMillerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, MILLER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place mill, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var mill = map.placeBuilding(new Mill(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), mill.getFlag());

        Utils.waitForBuildingToBeConstructed(mill);

        // Verify that the goods statistics is updated when a miller is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Miller));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenMinerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, MINER, 0);
        Utils.adjustInventoryTo(headquarters, PICK_AXE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place a small mountain
        var point1 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point1, map);

        // Place coalMine, connect it to the headquarters, and wait for it to get constructed and occupied
        var coalMine = map.placeBuilding(new CoalMine(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), coalMine.getFlag());

        Utils.waitForBuildingToBeConstructed(coalMine);

        // Verify that the goods statistics is updated when a miner is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(PICK_AXE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Miner));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenMinterIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, MINTER, 0);
        Utils.adjustInventoryTo(headquarters, CRUCIBLE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place mint, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var mint = map.placeBuilding(new Mint(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), mint.getFlag());

        Utils.waitForBuildingToBeConstructed(mint);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(CRUCIBLE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Minter));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkerStatisticsWhenPigBreederIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Adjust the inventory
        Utils.adjustInventoryTo(headquarters, PIG_BREEDER, 0);
        Utils.clearInventory(headquarters, Material.WATER, WHEAT);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place pigFarm, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var pigFarm = map.placeBuilding(new PigFarm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), pigFarm.getFlag());

        Utils.waitForBuildingToBeConstructed(pigFarm);

        // Verify that the goods statistics is updated when a pig breeder is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof PigBreeder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof PigBreeder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenScoutIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, SCOUT, 0);
        Utils.adjustInventoryTo(headquarters, BOW, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place lookoutTower, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var lookoutTower = map.placeBuilding(new LookoutTower(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), lookoutTower.getFlag());

        Utils.waitForBuildingToBeConstructed(lookoutTower);

        // Verify that the goods statistics is updated when a scout is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        Utils.adjustInventoryTo(headquarters, BOW, 1);

        map.stepTime();

        assertEquals(headquarters.getAmount(BOW), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Scout));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }


    @Test
    public void testGoodsAndWorkerStatisticsUpdatedWhenShipwrightIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, SHIPWRIGHT, 0);
        Utils.adjustInventoryTo(headquarters, HAMMER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place shipyard, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var shipyard = map.placeBuilding(new Shipyard(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), shipyard.getFlag());

        Utils.waitForBuildingToBeConstructed(shipyard);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        Utils.adjustInventoryTo(headquarters, HAMMER, 1);

        map.stepTime();

        assertEquals(headquarters.getAmount(HAMMER), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Shipwright));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }


    @Test
    public void testGoodsAndWorkersStatisticsUpdatedWhenStonemasonIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, STONEMASON, 0);
        Utils.adjustInventoryTo(headquarters, PICK_AXE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place quarry, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var quarry = map.placeBuilding(new Quarry(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), quarry.getFlag());

        Utils.waitForBuildingToBeConstructed(quarry);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkerMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(PICK_AXE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Stonemason));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkerMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkersStatisticsNotUpdatedWhenStorehouseWorkerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, STOREHOUSE_WORKER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place storehouse, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), storehouse.getFlag());

        Utils.waitForBuildingToBeConstructed(storehouse);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrGoodsMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkersMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof StorehouseWorker));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore);
    }


    @Test
    public void testGoodsAndWorkersStatisticsNotUpdatedWhenWellWorkerIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, WELL_WORKER, 0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place well, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var well = map.placeBuilding(new Well(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), well.getFlag());

        Utils.waitForBuildingToBeConstructed(well);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrGoodsMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkersMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof WellWorker));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkersStatisticsUpdatedWhenWoodcutterIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, WOODCUTTER_WORKER, 0);
        Utils.adjustInventoryTo(headquarters, AXE, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place woodcutter, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(10, 4);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), woodcutter.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Verify that the goods statistics is updated when a minter is created within the headquarters
        var statisticsManager = map.getStatisticsManager();

        var nrGoodsMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkersMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        map.stepTime();

        assertEquals(headquarters.getAmount(AXE), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof WoodcutterWorker));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    @Test
    public void testGoodsAndWorkersStatisticsUpdatedWhenBuilderIsCreated() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        headquarters.setInitialResources(ResourceLevel.MEDIUM);

        // Remove the worker and add a tool so it can be created
        Utils.adjustInventoryTo(headquarters, BUILDER, 0);
        Utils.adjustInventoryTo(headquarters, HAMMER, 1);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Verify that the goods statistics is updated when a builder is created within the headquarters
        var point1 = new Point(10, 4);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), woodcutter.getFlag());

        var statisticsManager = map.getStatisticsManager();

        var nrGoodsMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var nrWorkersMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();
        var workersBefore = statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value();
        var statisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 200; i++) {
            if (map.getWorkers().stream().anyMatch(worker -> worker instanceof WoodcutterWorker)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(headquarters.getAmount(HAMMER), 0);
        assertTrue(map.getWorkers().stream().anyMatch(worker -> worker instanceof Builder));
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrGoodsMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().size(), nrWorkersMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).workers().getMeasurements().getLast().value(), workersBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), statisticsEventsBefore + 1);
    }

    // Test for each type of building getting destroyed so its inventory is lost. Also building under construction.
    // And test for storehouse and headquarters getting destroyed. And test for military building - regular with coin,
    // and while being upgraded. Finally, test harbor where goods are collected for a mission
    @Test
    public void testGoodsStatisticsUpdatedWhenHouseUnderConstructionIsTornDown() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        headquarters.setInitialResources(ResourceLevel.MEDIUM);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place farm, connect it to the headquarters
        var point1 = new Point(10, 4);
        var farm = map.placeBuilding(new Farm(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), farm.getFlag());

        // Wait for the house to get some deliveries
        for (int i = 0; i < 500; i++) {
            if (farm.getAmount(PLANK) + farm.getAmount(STONE) == 3) {
                break;
            }

            map.stepTime();
        }

        assertEquals(farm.getAmount(PLANK) + farm.getAmount(STONE), 3);

        // Verify goods statistics when the farm is torn down
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();

        farm.tearDown();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 3);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 3);
    }

    @Test
    public void testGoodsStatisticsUpdatedWhenReadyProductionHouseIsTornDown() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        headquarters.setInitialResources(ResourceLevel.MEDIUM);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place sawmill, connect it to the headquarters
        var point1 = new Point(10, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), sawmill.getFlag());

        // Wait for the house to get constructed
        Utils.waitForBuildingToBeConstructed(sawmill);

        // Deliver three pieces of wood to the sawmill
        Utils.deliverCargos(sawmill, WOOD, 3);

        // Verify goods statistics when the sawmill is torn down
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();

        sawmill.tearDown();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 3);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 3);
    }


    @Test
    public void testGoodsStatisticsUpdatedWhenStorehouseIsTornDown() throws InvalidUserActionException {

        // Start new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(15, 9);
        var headquarters = map.placeBuilding(new Headquarter(player0), point0);

        headquarters.setInitialResources(ResourceLevel.MEDIUM);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        map.getStatisticsManager().addListener(monitor);

        // Place storehouse and connect it to the headquarters
        var point1 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, headquarters.getFlag(), storehouse.getFlag());

        // Wait for the house to get constructed
        Utils.waitForBuildingToBeConstructed(storehouse);

        // Deliver some materials to the storehouse
        Utils.deliverCargos(storehouse, WOOD, 3);
        Utils.deliverCargos(storehouse, PIG, 5);

        // Verify goods statistics when the storehouse is torn down
        var statisticsManager = map.getStatisticsManager();

        var nrMeasurementsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size();
        var goodsBefore = statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value();

        storehouse.tearDown();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 8);

        map.stepTime();

        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(statisticsManager.getPlayerStatistics(player0).goods().getMeasurements().getLast().value(), goodsBefore - 8);
    }


    // Test for worker carrying when land is taken over and the worker dies (in case it can't go to a storehouse)
}
