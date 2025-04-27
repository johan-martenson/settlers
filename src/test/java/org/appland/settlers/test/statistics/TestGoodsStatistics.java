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
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
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
                statisticsManager.getGeneralStatistics(player0).goods().getMeasurements().getFirst().value(),
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
        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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

        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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

        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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
        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();

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
        var generalStatistics = map.getStatisticsManager().getGeneralStatistics(player0);
        var nrMeasurementsBefore = generalStatistics.goods().getMeasurements().size();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

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
        assertEquals(generalStatistics.goods().getMeasurements().getLast().value(), generalStatistics.goods().getMeasurements().getFirst().value() - 4);
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

        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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
        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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
        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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
        var goodsStatistics = map.getStatisticsManager().getGeneralStatistics(player0).goods();
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (hunter.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(hunter.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (wellWorker.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(wellWorker.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore + 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (metalWorker.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(metalWorker.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (minter.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(minter.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (armorer.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(armorer.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (baker.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(baker.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (pigBreeder.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(pigBreeder.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
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

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 2);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 2);
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
        var nrMeasurementsBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size();
        var valueBefore = map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value();
        var nrStatisticsEventsBefore = monitor.getStatisticsEvents().size();

        for (int i = 0; i < 10_000; i++) {
            if (ironFounder.getCargo() != null) {
                break;
            }

            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore);
            assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore);

            map.stepTime();
        }

        assertNotNull(ironFounder.getCargo());
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);

        map.stepTime();

        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().size(), nrMeasurementsBefore + 1);
        assertEquals(map.getStatisticsManager().getGeneralStatistics(player0).goods().getMeasurements().getLast().value(), valueBefore - 1);
        assertEquals(monitor.getStatisticsEvents().size(), nrStatisticsEventsBefore + 1);
    }

    // Test for all worker creation

    // Test for each type of building getting destroyed

    // Test for worker carrying when land is taken over and the worker dies (in case it can't go to a storehouse)
}
