package org.appland.settlers.test.statistics;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Hunter;
import org.appland.settlers.model.actors.SawmillWorker;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.HunterHut;
import org.appland.settlers.model.buildings.IronMine;
import org.appland.settlers.model.buildings.IronSmelter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Mint;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMerchandise {

    private final static Set<Material> TOOLS = new HashSet<>(Arrays.asList(
            AXE,
            SHOVEL,
            PICK_AXE,
            FISHING_ROD,
            BOW,
            SAW,
            CLEAVER,
            ROLLING_PIN,
            CRUCIBLE,
            TONGS,
            SCYTHE));

    // Initial values.

    @Test
    public void testInitialWoodMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.wood().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.wood().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.wood().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialPlankMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.plank().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.plank().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.plank().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialStoneMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.stone().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.stone().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.stone().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialFoodMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialWaterMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.water().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.water().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.water().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialBeerMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.beer().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.beer().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.beer().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialCoalMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.coal().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.coal().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.coal().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialIronMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.iron().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.iron().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.iron().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialGoldMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.gold().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.gold().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.gold().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialIronBarMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.ironBar().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.ironBar().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.ironBar().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialCoinMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.coin().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.coin().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.coin().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialToolsMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.tools().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.tools().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.tools().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialWeaponsMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.weapons().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.weapons().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.weapons().getMeasurements().getFirst().value(), 0);
    }

    @Test
    public void testInitialBoatMerchandise() throws InvalidUserActionException {

        // Start single player game.
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 15, 15);

        // Place headquarters.
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that the initial merchandise is correct
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.boats().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.boats().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.boats().getMeasurements().getFirst().value(), 0);
    }

    // Production.

    @Test
    public void testMerchandiseStatisticsWhenWoodIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter, connect it to the headquarters, and wait for it to get constructed and occupied */
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter);

        var wcWorker = (WoodcutterWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Wait for the woodcutter to start cutting down the tree
        Utils.waitForWoodcutterToStartCuttingTree(wcWorker, map);

        // Verify that the merchandise statistics for wood are updated when the tree is cut down
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.wood().getMeasurements().size(), 1);

        Utils.fastForwardUntilWorkerCarriesCargo(map, wcWorker, WOOD);

        assertEquals(merchandiseStatistics.wood().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.wood().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.wood().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenPlankIsProduced() throws InvalidUserActionException {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill, connect it to the headquarters, and wait for it to get constructed and occupied */
        Point point3 = new Point(7, 9);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point3);

        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());
        Utils.waitForBuildingToBeConstructed(sawmill);
        var sawmillWorker0 = (SawmillWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Verify that merchandise statistics for planks are updated when a plank is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.plank().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.plank().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.plank().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmillWorker0, PLANK);

        assertEquals(merchandiseStatistics.plank().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.plank().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.plank().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenStoneIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry, connect it to the headquarters, and wait for it to get constructed and occupied */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0,quarry.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(quarry);

        var stonemason = (Stonemason) Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        // Wait for the stonemason to start cutting the stone
        Utils.waitForStonemasonToStartGettingStone(map, stonemason);

        // Verify that merchandise statistics for stones are updated when the stone is cut
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.stone().getMeasurements().size(), 1);

        Utils.fastForwardUntilWorkerCarriesCargo(map, stonemason, STONE);

        assertEquals(merchandiseStatistics.stone().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.stone().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.stone().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenFishIsProduced() throws InvalidUserActionException {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);
        map.setVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman.isInsideBuilding());

        // Wait for the fisherman to be fishing
        Utils.waitForFishermanToStartFishing(fisherman, map);

        // Verify that the merchandise statistics for food are updated when a fish is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().value(), 0);

        Utils.waitForFishermanToStopFishing(fisherman, map);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.food().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenMeatIsProducedByButcher() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place slaughterhouse, connect it to the headquarters, and wait for it to get constructed and occupied */
        Point point3 = new Point(7, 9);
        SlaughterHouse slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        Road road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        var butcher = (Butcher) Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        // Verify that the merchandise statistics for food are updated when meat is produced
        Utils.deliverCargos(slaughterHouse, PIG);

        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, butcher, MEAT);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.food().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenMeatIsProducedByHunter() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 9);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place hunter hut, connect with the headquarters, and wait for it to get constructed and occupied */
        Point point1 = new Point(10, 4);
        Building hunterHut = map.placeBuilding(new HunterHut(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, hunterHut.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(hunterHut);

        var hunter = (Hunter) Utils.waitForNonMilitaryBuildingToGetPopulated(hunterHut);

        assertTrue(hunter.isInsideBuilding());

        /* Wait for a wild animal to come close to the hut */
        Utils.waitForWildAnimalCloseToPoint(hunterHut.getPosition(), map);

        // Verify that merchandise statistics for food are updated when the hunter picks up the meat
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, hunter, MEAT);

        assertEquals(merchandiseStatistics.food().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.food().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.food().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenWaterIsProduced() throws InvalidUserActionException {

        /* Create gamemap */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place well, connect to the headquarters, and wait for it to get constructed and occupied */
        Point point1 = new Point(8, 6);
        Well well = map.placeBuilding(new Well(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(well);

        var worker = (WellWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        assertTrue(worker.isInsideBuilding());

        // Verify that the merchandise statistics for water are updated when water is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.water().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.water().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.water().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, worker, Material.WATER);

        assertEquals(merchandiseStatistics.water().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.water().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.water().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenBeerIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place brewery, connect with the headquarters, and wait for the brewery to get constructed and occupied */
        Point point3 = new Point(7, 9);
        Brewery brewery = map.placeBuilding(new Brewery(player0), point3);

        var road0 = map.placeAutoSelectedRoad(player0, brewery.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(brewery);

        Worker brewer0 = Utils.waitForNonMilitaryBuildingToGetPopulated(brewery);

        assertTrue(brewer0.isInsideBuilding());
        assertEquals(brewer0.getHome(), brewery);
        assertEquals(brewery.getWorker(), brewer0);

        /* Deliver wheat and water to the brewery */
        brewery.putCargo(new Cargo(WHEAT, map));
        brewery.putCargo(new Cargo(Material.WATER, map));

        // Verify that the merchandise statistics are updated when beer is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.beer().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.beer().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.beer().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, brewer0, Material.BEER);

        assertEquals(merchandiseStatistics.beer().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.beer().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.beer().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenCoalIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        var headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine, connect it to the headquarters, and wait for it to get constructed and occupied */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        var road0 = map.placeAutoSelectedRoad(player0, mine.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(mine);

        Worker miner = Utils.waitForNonMilitaryBuildingToGetPopulated(mine);

        assertTrue(miner.isInsideBuilding());

        // Verify that merchandise statistics are updated when the miner produces coal
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.coal().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.coal().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.coal().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, miner, COAL);

        assertEquals(merchandiseStatistics.coal().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.coal().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.coal().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenIronIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putIronAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        var headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place an iron mine, connect it to the headquarters, and wait for it to get constructed and occupied */
        Building mine = map.placeBuilding(new IronMine(player0), point0);

        var road0 = map.placeAutoSelectedRoad(player0, mine.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(mine);

        Worker miner = Utils.waitForNonMilitaryBuildingToGetPopulated(mine);

        assertTrue(miner.isInsideBuilding());

        // Verify that merchandise statistics are updated when the miner produces coal
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.iron().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.iron().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.iron().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, miner, IRON);

        assertEquals(merchandiseStatistics.iron().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.iron().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.iron().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsWhenGoldIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putGoldAtSurroundingTiles(point0, LARGE, map);

        /* Place a headquarter */
        Point point1 = new Point(15, 15);
        var headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place an iron mine, connect it to the headquarters, and wait for it to get constructed and occupied */
        Building mine = map.placeBuilding(new GoldMine(player0), point0);

        var road0 = map.placeAutoSelectedRoad(player0, mine.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(mine);

        Worker miner = Utils.waitForNonMilitaryBuildingToGetPopulated(mine);

        assertTrue(miner.isInsideBuilding());

        // Verify that merchandise statistics are updated when the miner produces coal
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.gold().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.gold().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.gold().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, miner, GOLD);

        assertEquals(merchandiseStatistics.gold().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.gold().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.gold().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsUpdatedWhenIronBarIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place iron smelter, connect it to the headquarters, and wait for it to get constructed and occupied */
        Point point3 = new Point(7, 9);
        Building ironSmelter = map.placeBuilding(new IronSmelter(player0), point3);

        var road0 = map.placeAutoSelectedRoad(player0, ironSmelter.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(ironSmelter);

        var ironFounder0 = Utils.waitForNonMilitaryBuildingToGetPopulated(ironSmelter);

        assertTrue(ironFounder0.isInsideBuilding());
        assertEquals(ironFounder0.getHome(), ironSmelter);
        assertEquals(ironSmelter.getWorker(), ironFounder0);

        // Verify that merchandise statistics are updated when an iron bar is produced
        Utils.deliverCargos(ironSmelter, COAL, IRON);

        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.ironBar().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.ironBar().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.ironBar().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, ironFounder0, IRON_BAR);

        assertEquals(merchandiseStatistics.ironBar().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.ironBar().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.ironBar().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchaniseStaticsWhenCoinIsProduced() throws InvalidUserActionException {

        /* Create a single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place mint */
        Point point3 = new Point(7, 9);
        Mint mint = map.placeBuilding(new Mint(player0), point3);

        /* Connect the mint with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, mint.getFlag(), headquarter.getFlag());

        // Wait for the mint to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(mint);

        var minter = Utils.waitForNonMilitaryBuildingToGetPopulated(mint);

        assertTrue(minter.isInsideBuilding());
        assertEquals(minter.getHome(), mint);
        assertEquals(mint.getWorker(), minter);

        /* Deliver wood to the mint */
        mint.putCargo(new Cargo(GOLD, map));
        mint.putCargo(new Cargo(COAL, map));

        // Verify that merchandise statistics are updated when a coin is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.coin().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.coin().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.coin().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, minter, COIN);

        assertEquals(merchandiseStatistics.coin().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.coin().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.coin().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsUpdatedWhenToolsAreProduced() throws InvalidUserActionException {
        for (var tool : TOOLS) {

            /* Create single player game */
            Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
            List<Player> players = new ArrayList<>();
            players.add(player0);

            GameMap map = new GameMap(players, 40, 40);

            /* Place headquarter */
            Point point0 = new Point(5, 5);
            Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

            /* Place metalworks, connect it to the headquarters, and wait for it to get constructed and occupied */
            Point point3 = new Point(7, 9);
            Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

            var road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

            Utils.waitForBuildingToBeConstructed(metalworks);

            var metalworker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

            assertTrue(metalworker0.isInsideBuilding());
            assertEquals(metalworker0.getHome(), metalworks);
            assertEquals(metalworks.getWorker(), metalworker0);

            // Tell the metal works what tool to produce
            for (var toolQuota : TOOLS) {
                player0.setProductionQuotaForTool(toolQuota, 0);
            }

            player0.setProductionQuotaForTool(tool, 10);

            /* Deliver plank and iron bar to the metalworks */
            metalworks.putCargo(new Cargo(PLANK, map));
            metalworks.putCargo(new Cargo(IRON_BAR, map));

            // Verify that merchandise statistics are updated when a tool is produced
            var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

            assertEquals(merchandiseStatistics.tools().getMeasurements().size(), 1);
            assertEquals(merchandiseStatistics.tools().getMeasurements().getFirst().time(), 1);
            assertEquals(merchandiseStatistics.tools().getMeasurements().getFirst().value(), 0);

            Utils.fastForwardUntilWorkerCarriesCargo(map, metalworker0, tool);

            assertEquals(merchandiseStatistics.tools().getMeasurements().size(), 2);
            assertTrue(merchandiseStatistics.tools().getMeasurements().getLast().time() > 1);
            assertEquals(merchandiseStatistics.tools().getMeasurements().getLast().value(), 1);
        }
    }

    @Test
    public void testMerchandiseStatisticsUpdatedWhenWeaponIsProduced() throws InvalidUserActionException {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(7, 9);
        Armory armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Place road to connect the armory with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the armory to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(armory0);

        var armorer0 = Utils.waitForNonMilitaryBuildingToGetPopulated(armory0);

        assertTrue(armorer0.isInsideBuilding());
        assertEquals(armorer0.getHome(), armory0);
        assertEquals(armory0.getWorker(), armorer0);

        /* Deliver material to the armory */
        Utils.deliverCargo(armory0, IRON_BAR);
        Utils.deliverCargo(armory0, COAL);

        assertEquals(armory0.getAmount(IRON_BAR), 1);
        assertEquals(armory0.getAmount(COAL), 1);

        // Verify that merchandise statistics are updated when a weapon is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.weapons().getMeasurements().size(), 1);

        assertEquals(merchandiseStatistics.weapons().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.weapons().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerProducesCargo(map, armorer0);

        assertTrue(armorer0.getCargo().getMaterial() == SWORD || armorer0.getCargo().getMaterial() == SHIELD);
        assertEquals(merchandiseStatistics.weapons().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.weapons().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.weapons().getMeasurements().getLast().value(), 1);
    }

    @Test
    public void testMerchandiseStatisticsUpdatedWhenBoatIsProduced() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place shipyard */
        Point point0 = new Point(14, 6);
        Shipyard shipyard = map.placeBuilding(new Shipyard(player0), point0);

        /* Connect the shipyard with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());

        /* Wait for the shipyard to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(shipyard);
        var shipwright = Utils.waitForNonMilitaryBuildingToGetPopulated(shipyard);

        /* Give planks to the shipyard */
        Utils.deliverCargos(shipyard, PLANK, 2);

        // Verify that the merchandise statistics are updated when a boat is produced
        var merchandiseStatistics = map.getStatisticsManager().getMerchandiseStatistics(player0);

        assertEquals(merchandiseStatistics.boats().getMeasurements().size(), 1);
        assertEquals(merchandiseStatistics.boats().getMeasurements().getFirst().time(), 1);
        assertEquals(merchandiseStatistics.boats().getMeasurements().getFirst().value(), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, shipwright, BOAT);

        assertEquals(merchandiseStatistics.boats().getMeasurements().size(), 2);
        assertTrue(merchandiseStatistics.boats().getMeasurements().getLast().time() > 1);
        assertEquals(merchandiseStatistics.boats().getMeasurements().getLast().value(), 1);
    }

    // Consumption


    // Listening
}
