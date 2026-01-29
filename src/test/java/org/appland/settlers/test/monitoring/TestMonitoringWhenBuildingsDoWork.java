package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Brewer;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.IronFounder;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Miller;
import org.appland.settlers.model.actors.Minter;
import org.appland.settlers.model.actors.PigBreeder;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mill;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.PigFarm;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestMonitoringWhenBuildingsDoWork {

    @Test
    public void testMonitoringEventWhenMillWorks() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place mill
        var point1 = new Point(12, 8);
        var mill = map.placeBuilding(new Mill(player0), point1);

        // Connect the mill with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, mill.getFlag(), headquarter.getFlag());

        // Wait for the mill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(mill);

        var miller = (Miller) Utils.waitForNonMilitaryBuildingToGetPopulated(mill);

        assertTrue(miller.isInsideBuilding());

        // Deliver wheat to the mill
        Utils.deliverCargos(mill, WHEAT, 6);

        assertFalse(mill.needsMaterial(WHEAT));

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Wait for the miller to start producing flour
        for (int i = 0; i < 100; i++) {
            if (miller.isWorking()) {
                break;
            }

            assertFalse(mill.isWorking());
            assertTrue(miller.isInsideBuilding());
            assertTrue(
                    monitor.getEvents().isEmpty() ||
                            monitor.getEvents().stream().noneMatch(ev -> ev.changedBuildings().contains(mill))
            );

            map.stepTime();
        }

        // Verify that a monitoring event is sent when the miller is working to produce flour
        assertNull(miller.getCargo());
        assertTrue(mill.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mill));

        // Verify that a monitoring event is sent when the miller stops producing flour
        monitor.clearEvents();

        Utils.waitForMillerToStopProducingFlour(miller, map);

        assertNotNull(miller.getCargo());
        assertEquals(miller.getCargo().getMaterial(), FLOUR);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mill));

        // Verify that the message is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(mill.isWorking());
        assertTrue(
                monitor.getEvents().isEmpty() ||
                        monitor.getEvents().stream().noneMatch(ev -> ev.changedBuildings().contains(mill))
        );
    }


    @Test
    public void testMonitoringEventWhenSawmillWorks() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Deliver wood to the sawmill
        sawmill.putCargo(new Cargo(WOOD, map));
        sawmill.putCargo(new Cargo(WOOD, map));

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor);

        // Verify that a monitoring event is sent when the sawmill starts producing planks
        monitor.clearEvents();

        assertFalse(sawmill.isWorking());

        for (int i = 0; i < 2_000; i++) {
            if (sawmill.isWorking()) {
                break;
            }

            assertFalse(sawmill.isWorking());
            assertFalse(sawmillWorker0.isWorking());

            monitor.clearEvents();

            map.stepTime();
        }

        assertTrue(sawmill.isWorking());
        assertTrue(sawmillWorker0.isWorking());
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sawmillWorker0.getCargo());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(sawmill));

        // Verify that an event is sent when the sawmill stops cutting the tree
        monitor.clearEvents();

        Utils.waitForCarpenterToStopSawing(sawmillWorker0, map);

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(sawmill));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(sawmill.isWorking());
        assertFalse(sawmillWorker0.isWorking());
        assertEquals(monitor.getEvents().size(), 0);
    }


    @Test
    public void testOccupiedArmoryWithCoalAndIronProducesWeapon() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(7, 9);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Place road to connect the armory with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        // Wait for the armory to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(armory0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        Worker armorer0 = armory0.getWorker();

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        // Deliver material to the armory
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        assertFalse(armory0.isWorking());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Verify that an event is sent when the armory is working
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            monitor.clearEvents();

            assertFalse(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        map.stepTime();

        assertTrue(armory0.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(armory0));

        // Verify that an event is sent when the armory stops working
        monitor.clearEvents();

        for (int i = 0; i < 49; i++) {
            map.stepTime();

            monitor.clearEvents();

            assertTrue(armory0.isWorking());
            assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
        }

        assertTrue(armory0.isWorking());
        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(armory0));

        map.stepTime();

        assertFalse(armory0.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(armory0));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertFalse(armory0.isWorking());
        assertNotNull(armorer0.getCargo());
        assertEquals(armorer0.getCargo().getMaterial(), SWORD);
        assertTrue(armory0.getFlag().getStackedCargo().isEmpty());
        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(armory0));
    }


    @Test
    public void testMonitoringEventWhenMintIsWorking() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place mint
        var point3 = new Point(7, 9);
        var mint = map.placeBuilding(new Mint(player0), point3);

        // Connect the mint with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        // Finish construction of the mint
        Utils.constructHouse(mint);

        // Populate the mint
        var minter = Utils.occupyBuilding(new Minter(player0, map), mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        // Deliver wood to the mint
        Utils.deliverCargo(mint, GOLD);
        Utils.deliverCargo(mint, COAL);

        // Let the minter rest
        for (int i = 0; i < 99; i++) {
            assertTrue(mint.getFlag().getStackedCargo().isEmpty());
            assertNull(minter.getCargo());
            assertFalse(mint.isWorking());

            map.stepTime();
        }

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Verify that an event is sent when the mint starts working
        assertFalse(mint.isWorking());

        map.stepTime();

        assertTrue(mint.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mint));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(mint));

        // Verify that an event is sent when the mint stops working
        Utils.waitForBuildingToStopWorking(mint);

        assertFalse(mint.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(mint));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(mint));
    }


    @Test
    public void testOccupiedBakeryWithIngredientsProducesBread() throws Exception {

        // Create new single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place bakery
        var point3 = new Point(7, 9);
        var bakery = map.placeBuilding(new Bakery(player0), point3);

        // Connect the bakery with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        // Finish construction of the bakery
        Utils.constructHouse(bakery);

        // Populate the bakery
        var baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);

        // Deliver material to the bakery
        Utils.deliverCargo(bakery, WATER);
        Utils.deliverCargo(bakery, FLOUR);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Let the baker rest
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertFalse(baker.isBaking());
            assertFalse(bakery.isWorking());
        }

        // Verify that an event is sent when the bakery starts working
        assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
        assertNull(baker.getCargo());
        assertFalse(baker.isBaking());
        assertFalse(bakery.isWorking());

        monitor.clearEvents();

        map.stepTime();

        assertTrue(baker.isBaking());
        assertTrue(bakery.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getLastEvent().changedBuildings().contains(bakery));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getEvents().getLast().changedBuildings().contains(bakery));
    }


    @Test
    public void testMonitoringEventWhenIronSmelterIsWorking() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place iron smelter
        var point3 = new Point(7, 9);
        var ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        // Finish construction of the iron smelter
        Utils.constructHouse(ironSmelter);

        // Occupy the iron smelter
        var ironFounder0 = Utils.occupyBuilding(new IronFounder(player0, map), ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        // Deliver iron and coal to the iron smelter
        ironSmelter.putCargo(new Cargo(COAL, map));
        ironSmelter.putCargo(new Cargo(IRON, map));

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Let the iron founder rest
        for (int i = 0; i < 99; i++) {
            assertFalse(ironSmelter.isWorking());
            assertTrue(ironFounder0.isInsideBuilding());
            assertTrue(ironSmelter.getFlag().getStackedCargo().isEmpty());
            assertNull(ironFounder0.getCargo());

            map.stepTime();
        }

        // Verify that an event is sent when the iron smelter is working
        assertFalse(ironSmelter.isWorking());
        assertTrue(ironFounder0.isInsideBuilding());

        map.stepTime();

        assertTrue(ironSmelter.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(ironSmelter));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(ironSmelter));
    }


    @Test
    public void testMonitoringEventWhenBreweryIsWorking() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place brewery
        var point3 = new Point(7, 9);
        var brewery = map.placeBuilding(new Brewery(player0), point3);

        // Finish construction of the brewery
        Utils.constructHouse(brewery);

        // Occupy the brewery
        var brewer0 = Utils.occupyBuilding(new Brewer(player0, map), brewery);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);
        assertFalse(brewery.isWorking());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Deliver wheat and water to the brewery
        Utils.deliverCargo(brewery, WHEAT);
        Utils.deliverCargo(brewery, WATER);

        // Let the brewer rest
        for (int i = 0; i < 99; i++) {
            assertTrue(brewery.getFlag().getStackedCargo().isEmpty());
            assertNull(brewer0.getCargo());
            assertFalse(brewery.isWorking());

            map.stepTime();
        }

        // Verify that an event is sent when the brewery starts working
        assertFalse(brewery.isWorking());

        map.stepTime();

        assertTrue(brewery.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(brewery));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(brewery));
    }


    @Test
    public void testMonitoringEventWhenMetalworksStartsToWork() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place metalworks
        var point3 = new Point(7, 9);
        var metalworks = map.placeBuilding(new Metalworks(player0), point3);

        // Finish construction of the metalworks
        Utils.constructHouse(metalworks);

        // Occupy the metalworks
        var metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Deliver plank and iron bar to the metalworks
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        // Let the metalworker rest
        for (int i = 0; i < 99; i++) {
            assertTrue(metalworker0.isInsideBuilding());
            assertFalse(metalworker0.isHammering());
            assertFalse(metalworker0.isSawing());
            assertFalse(metalworker0.isWipingSweat());
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertFalse(metalworks.isWorking());

            map.stepTime();
        }

        // Verify that an event is sent when the metalworks starts to work
        assertFalse(metalworks.isWorking());

        map.stepTime();

        assertTrue(metalworks.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(metalworks));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(metalworks));
    }


    @Test
    public void testMonitoringEventWhenDonkeyFarmStartsToWork() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertFalse(donkeyFarm.isWorking());

        // Verify that an event is sent when the donkey breeder starts to work
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());
        assertTrue(donkeyFarm.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(donkeyFarm));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(donkeyBreeder));
    }


    @Test
    public void testMonitoringEventWhenFarmerGoesOutToPlant() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new Farm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        // Finish construction of the farm
        Utils.constructHouse(farm);

        // Assign a farmer to the farm
        var farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Wait for the farmer to rest
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());
        assertFalse(farm.isWorking());

        // Verify that an event is sent when the farmer goes out to plant (and the farm is thus working)
        monitor.clearEvents();

        map.stepTime();

        assertFalse(farmer.isInsideBuilding());
        assertTrue(farm.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(farm));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(farm));
    }

    @Test
    public void testMonitoringEventWhenFarmerGoesOutToHarvest() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new Farm(player0), point3);

        // Place crop
        var crop = map.placeCrop(point3.upRight().upRight(), Crop.CropType.TYPE_1);

        // Finish construction of the farm
        Utils.constructHouse(farm);

        // Wait for the crop to grow
        Utils.fastForwardUntilCropIsGrown(crop, map);

        // Assign a farmer to the farm
        var farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Wait for the farmer to rest
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());
        assertFalse(farm.isWorking());

        // Verify that an event is sent when the farmer goes out to harvest (and thus the farm is working)
        monitor.clearEvents();

        map.stepTime();

        assertFalse(farmer.isInsideBuilding());
        assertTrue(farm.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(farm));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(farm));
    }


    @Test
    public void testMonitoringEventsWhenPigBreederFeedsThePigs() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place pig farm
        var point3 = new Point(10, 6);
        var pigFarm = map.placeBuilding(new PigFarm(player0), point3);

        // Connect the pig farm with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, pigFarm.getFlag(), headquarter.getFlag());

        // Wait for the pig farm to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(pigFarm);
        var pigBreeder = (PigBreeder) Utils.waitForNonMilitaryBuildingToGetPopulated(pigFarm);

        assertTrue(pigBreeder.isInsideBuilding());

        // Deliver resources to the pig farm
        Utils.deliverCargo(pigFarm, WATER);
        Utils.deliverCargo(pigFarm, WHEAT);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Wait for the pig breeder to rest
        Utils.fastForward(99, map);

        assertTrue(pigBreeder.isInsideBuilding());

        // Verify that an event is sent when the pig breeder goes out to feed the pigs (because the pig farm is working)
        map.stepTime();

        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(pigFarm.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(pigFarm));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(pigBreeder));

        // Wait for the pig breeder to start feeding the pigs
        Utils.waitForPigBreederToBeFeedingPigs(pigBreeder, map);

        // Wait for the pig breeder to stop feeding the pigs
        Utils.waitForPigBreederToNotBeFeedingPigs(pigBreeder, map);

        assertTrue(pigBreeder.isTraveling());

        // Wait for the pig breeder to come back to the pig farm and prepare the pig for delivery
        Utils.fastForwardUntilWorkersReachTarget(map, pigBreeder);

        assertTrue(pigBreeder.isPreparingPigForDelivery());

        // Verify that an event is sent when the pig breeder is done preparing and delivers to the flag
        monitor.clearEvents();

        Utils.waitForWorkerToBeOutside(pigBreeder, map);

        assertFalse(pigBreeder.isInsideBuilding());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(pigFarm));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(pigBreeder));
    }


    @Test
    public void testButcherLeavesMeatAtTheFlag() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Start monitoring
        var monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        // Deliver ingredients to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);

        // Let the butcher rest
        Utils.fastForward(99, map);

        assertFalse(slaughterHouse.isWorking());
        assertTrue(butcher.isInsideBuilding());

        monitor.clearEvents();

        map.stepTime();

        assertTrue(slaughterHouse.isWorking());
        assertTrue(monitor.getEvents().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(slaughterHouse));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(slaughterHouse));

        // Verify that an event is sent when the butcher is done and carries the meat to the flag
        for (int i = 0; i < 47; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            assertTrue(slaughterHouse.isWorking());

            map.stepTime();
        }

        assertTrue(slaughterHouse.isWorking());
        assertNull(butcher.getCargo());

        monitor.clearEvents();

        map.stepTime();

        assertFalse(slaughterHouse.isWorking());
        assertNotNull(butcher.getCargo());
        assertTrue(monitor.getEvents().size() == 1);
        assertTrue(monitor.getEvents().getLast().changedBuildings().size() > 0);
        assertTrue(monitor.getEvents().getLast().changedBuildings().contains(slaughterHouse));

        // Verify that the event is only sent once
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor.getEvents().size() == 0 || !monitor.getLastEvent().changedBuildings().contains(slaughterHouse));
    }
}
