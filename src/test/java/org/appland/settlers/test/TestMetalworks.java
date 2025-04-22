package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

public class TestMetalworks {

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
            SCYTHE)
    );

    @Test
    public void testMetalworksCanHoldSixIronBarsAndSixPlanks() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        /* Make sure the headquarters has enough resources */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 20);
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, METALWORKER, 20);

        /* Wait for the metalworks to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(metalworks0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks0);

        /* Stop production */
        metalworks0.stopProduction();

        /* Wait for the metalworks to get six iron bars and six planks */
        Utils.waitForBuildingToGetAmountOfMaterial(metalworks0, IRON_BAR, 6);
        Utils.waitForBuildingToGetAmountOfMaterial(metalworks0, PLANK, 6);

        /* Verify that the metalworks doesn't need any more resources and doesn't get any more deliveries */
        assertFalse(metalworks0.needsMaterial(IRON_BAR));
        assertFalse(metalworks0.needsMaterial(PLANK));

        for (int i = 0; i < 2000; i++) {
            assertFalse(metalworks0.needsMaterial(IRON_BAR));
            assertFalse(metalworks0.needsMaterial(PLANK));
            assertEquals(metalworks0.getAmount(IRON_BAR), 6);
            assertEquals(metalworks0.getAmount(PLANK), 6);

            map.stepTime();
        }
    }

    @Test
    public void testMetalworksOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Deliver two plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(stoneCargo);
        metalworks0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(metalworks0);

        /* Verify that this is enough to construct the metalworks */
        for (int i = 0; i < 150; i++) {
            assertTrue(metalworks0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(metalworks0.isReady());
    }

    @Test
    public void testMetalworksCannotBeConstructedWithTooFewPlanks() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Deliver one plank and two stone */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(stoneCargo);
        metalworks0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(metalworks0);

        /* Verify that this is not enough to construct the metalworks */
        for (int i = 0; i < 500; i++) {
            assertTrue(metalworks0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(metalworks0.isReady());
    }

    @Test
    public void testMetalworksCannotBeConstructedWithTooFewStones() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Deliver two planks and one stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(metalworks0);

        /* Verify that this is not enough to construct the metalworks */
        for (int i = 0; i < 500; i++) {
            assertTrue(metalworks0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(metalworks0.isReady());
    }

    @Test
    public void testMetalworksNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Unfinished metalworks doesn't need worker */
        assertFalse(metalworks.needsWorker());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        assertTrue(metalworks.needsWorker());
    }

    @Test
    public void testHeadquarterHasOneMetalworkerAtStart() {
        Headquarter headquarter = new Headquarter(null);

        assertEquals(headquarter.getAmount(METALWORKER), 1);
    }

    @Test
    public void testMetalworksGetsAssignedWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        assertTrue(metalworks.needsWorker());

        /* Verify that a metalworks worker leaves the headquarter */
        Worker metalworker0 = Utils.waitForWorkerOutsideBuilding(Metalworker.class, player0);

        assertTrue(map.getWorkers().contains(metalworker0));

        /* Let the metalworks worker reach the metalworks */
        assertNotNull(metalworker0);
        assertEquals(metalworker0.getTarget(), metalworks.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, metalworker0);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);
    }

    @Test
    public void testMetalworksIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        assertTrue(metalworks.needsWorker());

        /* Verify that a metalworks worker leaves the headquarter */
        Worker metalworker0 = Utils.waitForWorkerOutsideBuilding(Metalworker.class, player0);

        assertTrue(map.getWorkers().contains(metalworker0));

        /* Verify that the metalworks worker is not a soldier */
        assertNotNull(metalworker0);
        assertFalse(metalworker0.isSoldier());
    }

    @Test
    public void testOccupiedMetalworksWithoutPlankAndIronBarProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Verify that the metalworks doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedMetalworksProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Verify that the metalworks doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            map.stepTime();
        }
    }

    @Test
    public void testOccupiedMetalworksWithPlanksAndIronBarsProducesTool() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Verify that the metalworks produces tool */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
        }

        map.stepTime();

        Utils.verifyWorkerCarriesTool(metalworker0);

        assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testMetalworkerLeavesToolAtTheFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Verify that the metalworks produces tool */
        for (int i = 0; i < 149; i++) {
            map.stepTime();
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
        }

        map.stepTime();

        Utils.verifyWorkerCarriesTool(metalworker0);

        assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());

        /* Verify that the metalworks worker leaves the cargo at the flag */
        assertEquals(metalworker0.getTarget(), metalworks.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks.getFlag().getPosition());

        assertFalse(metalworks.getFlag().getStackedCargo().isEmpty());
        assertNull(metalworker0.getCargo());
        assertEquals(metalworker0.getTarget(), metalworks.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, metalworker0);

        assertTrue(metalworker0.isInsideBuilding());
    }

    @Test
    public void testToolIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory */
        Utils.clearInventory(headquarter, PLANK, STONE, IRON_BAR);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the metalworks */
        Point point1 = new Point(14, 4);
        Metalworks metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), storehouse.getFlag());

        /* Deliver the needed material to construct the metalworks */
        Utils.deliverCargos(metalworks, PLANK, 2);
        Utils.deliverCargos(metalworks, STONE, 2);

        /* Wait for the metalworks to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(metalworks);

        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

        /* Wait for the courier on the road between the storehouse and the metalworks to have a tool cargo */
        Utils.deliverCargo(metalworks, IRON_BAR);
        Utils.deliverCargo(metalworks, PLANK);

        Utils.waitForFlagToGetStackedCargo(map, metalworks.getFlag(), 1);

        Material tool = metalworks.getFlag().getStackedCargo().getFirst().getMaterial();

        assertTrue(tool.isTool());

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        Utils.adjustInventoryTo(headquarter, tool, 0);

        assertEquals(headquarter.getAmount(tool), 0);
        assertEquals(metalworks.getAmount(tool), 0);
        assertFalse(storehouse.needsMaterial(tool));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(tool));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testProductionOfOneToolConsumesOnePlankAndOneIronBar() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        /* Deliver plank and iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Wait until the metalworks worker produces a tool */
        assertEquals(metalworks.getAmount(PLANK), 1);
        assertEquals(metalworks.getAmount(IRON_BAR), 1);

        Utils.fastForward(150, map);

        assertEquals(metalworks.getAmount(PLANK), 0);
        assertEquals(metalworks.getAmount(IRON_BAR), 0);
        assertTrue(metalworks.needsMaterial(PLANK));
        assertTrue(metalworks.needsMaterial(IRON_BAR));
    }

    @Test
    public void testProductionCountdownStartsWhenPlankAndIronBarAreAvailable() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        /* Fast forward so that the metalworker would produced tool if it had had plank and iron bar */
        Utils.fastForward(150, map);

        assertNull(metalworker0.getCargo());

        /* Deliver plank and iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Verify that it takes 50 steps for the metalworks worker to produce a tool */
        for (int i = 0; i < 50; i++) {
            assertNull(metalworker0.getCargo());
            map.stepTime();
        }

        assertNotNull(metalworker0.getCargo());
    }

    @Test
    public void testMetalworksCannotProduceWithOnlyPlank() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        /* Deliver plank but not iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));

        /* Verify that the plank founder doesn't produce tool since it doesn't have any iron bar */
        for (int i = 0; i < 200; i++) {
            assertNull(metalworker0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testMetalworksCannotProduceWithOnlyIronBar() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        /* Deliver plank but not iron bar to the metalworks */
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Verify that the plank founder doesn't produce tool since it doesn't have any iron bar */
        for (int i = 0; i < 200; i++) {
            assertNull(metalworker0.getCargo());
            map.stepTime();
        }
    }

    @Test
    public void testByDefaultMetalworkerProducesEqualAmountsOfAllTools() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        Map<Material, Integer> toolCount = new EnumMap<>(Material.class);

        for (int i = 0; i < TOOLS.size() * 2; i++) {
            metalworks.putCargo(new Cargo(PLANK, map));
            metalworks.putCargo(new Cargo(IRON_BAR, map));

            /* Wait until the metalworks produces tool */
            Utils.fastForwardUntilWorkerCarriesCargo(map, metalworker0);

            Utils.verifyWorkerCarriesTool(metalworker0);

            assertTrue(metalworks.getFlag().getStackedCargo().size() < 8);

            int amount = toolCount.getOrDefault(metalworker0.getCargo().getMaterial(), 0);

            toolCount.put(metalworker0.getCargo().getMaterial(), amount + 1);

            /* Wait for the metalworks worker to leave the cargo at the flag */
            assertEquals(metalworker0.getTarget(), metalworks.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks.getFlag().getPosition());

            assertFalse(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertEquals(metalworker0.getTarget(), metalworks.getPosition());

            /* Wait for the metalworks worker to go back to the house */
            Utils.fastForwardUntilWorkersReachTarget(map, metalworker0);

            assertTrue(metalworker0.isInsideBuilding());
        }

        /* Verify that the metalworker has produced the same amount of each tool */
        for (Material tool : TOOLS) {
            assertEquals((int)toolCount.get(tool), 2);
        }
    }

    @Test
    public void testSelectOnlyProducingSaws() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Select to only product saws */
        for (Material tool : TOOLS) {
            player0.setProductionQuotaForTool(tool, 0);

            assertEquals(player0.getProductionQuotaForTool(tool), 0);
        }

        player0.setProductionQuotaForTool(SAW, 10);

        assertEquals(player0.getProductionQuotaForTool(SAW), 10);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        Map<Material, Integer> toolCount = new EnumMap<>(Material.class);

        for (int i = 0; i < TOOLS.size() * 2; i++) {
            metalworks.putCargo(new Cargo(PLANK, map));
            metalworks.putCargo(new Cargo(IRON_BAR, map));

            /* Wait until the metalworks produces tool */
            Utils.fastForwardUntilWorkerCarriesCargo(map, metalworker0);

            Utils.verifyWorkerCarriesTool(metalworker0);

            assertTrue(metalworks.getFlag().getStackedCargo().size() < 8);

            int amount = toolCount.getOrDefault(metalworker0.getCargo().getMaterial(), 0);

            toolCount.put(metalworker0.getCargo().getMaterial(), amount + 1);

            /* Wait for the metalworks worker to leave the cargo at the flag */
            assertEquals(metalworker0.getTarget(), metalworks.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks.getFlag().getPosition());

            assertFalse(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertEquals(metalworker0.getTarget(), metalworks.getPosition());

            /* Wait for the metalworks worker to go back to the house */
            Utils.fastForwardUntilWorkersReachTarget(map, metalworker0);

            assertTrue(metalworker0.isInsideBuilding());
        }

        /* Verify that the metalworker has only produced saws */
        for (Material tool : TOOLS) {
            if (tool != SAW) {
                assertEquals((int) toolCount.getOrDefault(tool, 0), 0);
            }
        }

        assertEquals((int)toolCount.get(SAW), TOOLS.size() * 2);
    }

    @Test
    public void testNoProductionWhenAllSelectionsAreZero() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Select to only product saws */
        for (Material tool : TOOLS) {
            player0.setProductionQuotaForTool(tool, 0);

            assertEquals(player0.getProductionQuotaForTool(tool), 0);
        }

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        metalworks.putCargo(new Cargo(PLANK, map));
        metalworks.putCargo(new Cargo(IRON_BAR, map));

        /* Verify that no tools are produced */
        for (int i = 0; i < 500; i++) {
            assertNull(metalworker0.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testSelectOnlyProducingTwoAxesAndOneFishingRod() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Select to only product saws */
        for (Material tool : TOOLS) {
            player0.setProductionQuotaForTool(tool, 0);

            assertEquals(player0.getProductionQuotaForTool(tool), 0);
        }

        player0.setProductionQuotaForTool(AXE, 10);
        player0.setProductionQuotaForTool(FISHING_ROD, 5);

        assertEquals(player0.getProductionQuotaForTool(AXE), 10);
        assertEquals(player0.getProductionQuotaForTool(FISHING_ROD), 5);

        /* Occupy the metalworks */
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        /* Deliver plank and iron bar to the metalworks */
        Map<Material, Integer> toolCount = new EnumMap<>(Material.class);

        for (int i = 0; i < 15; i++) {
            metalworks.putCargo(new Cargo(PLANK, map));
            metalworks.putCargo(new Cargo(IRON_BAR, map));

            /* Wait until the metalworks produces tool */
            Utils.fastForwardUntilWorkerCarriesCargo(map, metalworker0);

            Utils.verifyWorkerCarriesTool(metalworker0);

            assertTrue(metalworks.getFlag().getStackedCargo().size() < 8);

            int amount = toolCount.getOrDefault(metalworker0.getCargo().getMaterial(), 0);

            toolCount.put(metalworker0.getCargo().getMaterial(), amount + 1);

            /* Wait for the metalworks worker to leave the cargo at the flag */
            assertEquals(metalworker0.getTarget(), metalworks.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks.getFlag().getPosition());

            assertFalse(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertEquals(metalworker0.getTarget(), metalworks.getPosition());

            /* Wait for the metalworks worker to go back to the house */
            Utils.fastForwardUntilWorkersReachTarget(map, metalworker0);

            assertTrue(metalworker0.isInsideBuilding());
        }

        /* Verify that the metalworker has only produced saws */
        for (Material tool : TOOLS) {
            if (tool != AXE && tool != FISHING_ROD) {
                assertEquals((int) toolCount.getOrDefault(tool, 0), 0);
            }
        }

        assertEquals((int)toolCount.get(AXE), 10);
        assertEquals((int)toolCount.get(FISHING_ROD), 5);
    }

    @Test
    public void testCannotSetTooHighProductionQuota() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Verify that it's not possible to set too high production quota for a tool */
        try {
            player0.setProductionQuotaForTool(AXE, 11);

            fail();
        } catch (InvalidUserActionException e) {

        }
    }

    @Test
    public void testCannotSetTooLowProductionQuota() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Verify that it's not possible to set too high production quota for a tool */
        try {
            player0.setProductionQuotaForTool(AXE, -1);

            fail();
        } catch (InvalidUserActionException e) {

        }
    }

    @Test
    public void testCannotSetProductionQuotaForAllTools() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Verify that it's not possible to set  production quota for anything else than a tool */
        for (Material tool : TOOLS) {
            player0.setProductionQuotaForTool(tool, 5);

            assertEquals(player0.getProductionQuotaForTool(tool), 5);
        }
    }

    @Test
    public void testCannotSetProductionQuotaForNonTools() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point3 = new Point(7, 9);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point3);

        /* Place a road between the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks);

        /* Verify that it's not possible to set  production quota for anything else than a tool */
        for (Material material : Material.values()) {
            if (TOOLS.contains(material)) {
                continue;
            }

            try {
                player0.setProductionQuotaForTool(material, 5);

                fail();
            } catch (InvalidUserActionException e) {

            }
        }
    }

    @Test
    public void testMetalworksWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(8, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Deliver material to the metalworks */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo ironBarCargo = new Cargo(IRON_BAR, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);

        metalworks0.putCargo(ironBarCargo);
        metalworks0.putCargo(ironBarCargo);

        /* Let the metalworker rest */
        Utils.fastForward(100, map);

        /* Wait for the metalworker to produce a new tool cargo */
        Utils.fastForward(50, map);

        Worker worker = metalworks0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the metalworker puts the tool cargo at the flag */
        assertEquals(worker.getTarget(), metalworks0.getFlag().getPosition());
        assertTrue(metalworks0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(metalworks0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the metalworks */
        assertEquals(worker.getTarget(), metalworks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        /* Verify that the second cargo is put at the flag */
        assertEquals(worker.getTarget(), metalworks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(metalworks0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(8, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Deliver material to the metalworks */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo ironBarCargo = new Cargo(IRON_BAR, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);

        metalworks0.putCargo(ironBarCargo);
        metalworks0.putCargo(ironBarCargo);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Let the metalworker rest */
        Utils.fastForward(100, map);

        /* Wait for the metalworker to produce a new tool cargo */
        Utils.fastForward(50, map);

        Worker worker = metalworks0.getWorker();

        assertNotNull(worker.getCargo());

        /* Verify that the metalworker puts the tool cargo at the flag */
        assertEquals(worker.getTarget(), metalworks0.getFlag().getPosition());
        assertTrue(metalworks0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(metalworks0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = metalworks0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), metalworks0.getFlag().getPosition());

        /* Remove all planks and iron bars from the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), metalworks0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), metalworks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Material toolType = courier.getCargo().getMaterial();

        int amount = headquarter0.getAmount(toolType);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(toolType), amount + 1);
    }

    @Test
    public void testMetalworkerGoesBackToStorageWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(8, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Destroy the metalworks */
        Worker worker = metalworks0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(METALWORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        /* Verify that the metalworker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(METALWORKER), amount + 1);
    }

    @Test
    public void testMetalworkerGoesBackOnToStorageOnRoadsIfPossibleWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(8, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Connect the metalworks with the headquarter */
        map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Destroy the metalworks */
        Worker worker = metalworks0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : worker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testProductionInMetalworksCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(12, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter.getFlag());

        /* Finish the metalworks */
        Utils.constructHouse(metalworks0);

        /* Deliver material to the metalworks */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo ironBarCargo = new Cargo(IRON_BAR, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);

        metalworks0.putCargo(ironBarCargo);
        metalworks0.putCargo(ironBarCargo);

        /* Assign a worker to the metalworks */
        Metalworker worker = new Metalworker(player0, map);

        Utils.occupyBuilding(worker, metalworks0);

        assertTrue(worker.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the metalworker to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        Utils.verifyWorkerCarriesTool(worker);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), metalworks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        /* Stop production and verify that no tool is produced */
        metalworks0.stopProduction();

        assertFalse(metalworks0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInMetalworksCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(12, 8);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks and the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter.getFlag());

        /* Finish the metalworks */
        Utils.constructHouse(metalworks0);

        /* Assign a worker to the metalworks */
        Metalworker worker = new Metalworker(player0, map);

        Utils.occupyBuilding(worker, metalworks0);

        assertTrue(worker.isInsideBuilding());

        /* Deliver material to the metalworks */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo ironBarCargo = new Cargo(IRON_BAR, map);

        metalworks0.putCargo(plankCargo);
        metalworks0.putCargo(plankCargo);

        metalworks0.putCargo(ironBarCargo);
        metalworks0.putCargo(ironBarCargo);

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the metalworker to produce tool */
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        Utils.verifyWorkerCarriesTool(worker);

        /* Wait for the worker to deliver the cargo */
        assertEquals(worker.getTarget(), metalworks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        /* Stop production */
        metalworks0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the metalworks produces tools again */
        metalworks0.resumeProduction();

        assertTrue(metalworks0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedMetalworkerHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(20, 14);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks0.getFlag());

        /* Wait for metalworker to get assigned and leave the headquarter */
        List<Metalworker> workers = Utils.waitForWorkersOutsideBuilding(Metalworker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Metalworker worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place metalworks close to the new border */
        Point point4 = new Point(28, 18);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point4);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Metalworker worker = Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMetalworkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place metalworker */
        Point point2 = new Point(14, 4);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, metalworks.getFlag());

        /* Wait for the metalworker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Metalworker.class, 1, player0);

        Metalworker metalworker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Metalworker) {
                metalworker = (Metalworker) worker;
            }
        }

        assertNotNull(metalworker);
        assertEquals(metalworker.getTarget(), metalworks.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the metalworker has started walking */
        assertFalse(metalworker.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the metalworker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, flag0.getPosition());

        assertEquals(metalworker.getPosition(), flag0.getPosition());

        /* Verify that the metalworker returns to the headquarter when it reaches the flag */
        assertEquals(metalworker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getPosition());
    }

    @Test
    public void testMetalworkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place metalworker */
        Point point2 = new Point(14, 4);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, metalworks0.getFlag());

        /* Wait for the metalworker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Metalworker.class, 1, player0);

        Metalworker metalworker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Metalworker) {
                metalworker = (Metalworker) worker;
            }
        }

        assertNotNull(metalworker);
        assertEquals(metalworker.getTarget(), metalworks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the metalworker has started walking */
        assertFalse(metalworker.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the metalworker continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, flag0.getPosition());

        assertEquals(metalworker.getPosition(), flag0.getPosition());

        /* Verify that the metalworker continues to the final flag */
        assertEquals(metalworker.getTarget(), metalworks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, metalworks0.getFlag().getPosition());

        /* Verify that the metalworker goes out to metalworker instead of going directly back */
        assertNotEquals(metalworker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMetalworkerReturnsToStorageIfMetalworksIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place metalworks */
        Point point2 = new Point(14, 4);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, metalworks0.getFlag());

        /* Wait for the metalworker to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Metalworker.class, 1, player0);

        Metalworker metalworker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Metalworker) {
                metalworker = (Metalworker) worker;
            }
        }

        assertNotNull(metalworker);
        assertEquals(metalworker.getTarget(), metalworks0.getPosition());

        /* Wait for the metalworker to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, flag0.getPosition());

        map.stepTime();

        /* See that the metalworker has started walking */
        assertFalse(metalworker.isExactlyAtPoint());

        /* Tear down the metalworks */
        metalworks0.tearDown();

        /* Verify that the metalworker continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, metalworks0.getFlag().getPosition());

        assertEquals(metalworker.getPosition(), metalworks0.getFlag().getPosition());

        /* Verify that the metalworker goes back to storage */
        assertEquals(metalworker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testMetalworkerGoesOffroadBackToClosestStorageWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(17, 17);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Place a second storage closer to the metalworks */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the metalworks */
        Worker metalworker = metalworks0.getWorker();

        assertTrue(metalworker.isInsideBuilding());
        assertEquals(metalworker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(metalworker.isInsideBuilding());
        assertEquals(metalworker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(METALWORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, storehouse0.getPosition());

        /* Verify that the metalworker is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(METALWORKER), amount + 1);
    }

    @Test
    public void testMetalworkerReturnsOffroadAndAvoidsBurningStorageWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(17, 17);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Place a second storage closer to the metalworks */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the metalworks */
        Worker metalworker = metalworks0.getWorker();

        assertTrue(metalworker.isInsideBuilding());
        assertEquals(metalworker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(metalworker.isInsideBuilding());
        assertEquals(metalworker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(METALWORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getPosition());

        /* Verify that the metalworker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(METALWORKER), amount + 1);
    }

    @Test
    public void testMetalworkerReturnsOffroadAndAvoidsDestroyedStorageWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(17, 17);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Place a second storage closer to the metalworks */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the metalworks */
        Worker metalworker = metalworks0.getWorker();

        assertTrue(metalworker.isInsideBuilding());
        assertEquals(metalworker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(metalworker.isInsideBuilding());
        assertEquals(metalworker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(METALWORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getPosition());

        /* Verify that the metalworker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(METALWORKER), amount + 1);
    }

    @Test
    public void testMetalworkerReturnsOffroadAndAvoidsUnfinishedStorageWhenMetalworksIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(17, 17);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Occupy the metalworks */
        Utils.occupyBuilding(new Metalworker(player0, map), metalworks0);

        /* Place a second storage closer to the metalworks */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the metalworks */
        Worker metalworker = metalworks0.getWorker();

        assertTrue(metalworker.isInsideBuilding());
        assertEquals(metalworker.getPosition(), metalworks0.getPosition());

        metalworks0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(metalworker.isInsideBuilding());
        assertEquals(metalworker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(METALWORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getPosition());

        /* Verify that the metalworker is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(METALWORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place metalworks */
        Point point26 = new Point(17, 17);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point26);

        /* Place road to connect the headquarter and the metalworks */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks0.getFlag());

        /* Finish construction of the metalworks */
        Utils.constructHouse(metalworks0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Metalworker.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        /* Tear down the building */
        metalworks0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), metalworks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testMetalworksWithoutResourcesHasZeroProductivity() throws Exception {

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

        /* Verify that the productivity is 0% when the metalworks doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(armorer0.getCargo());
            assertEquals(metalworks.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testMetalworksWithAbundantResourcesHasFullProductivity() throws Exception {

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

        /* Make the metalworks create some tool with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (metalworks.needsMaterial(IRON_BAR)) {
                metalworks.putCargo(new Cargo(IRON_BAR, map));
            }

            if (metalworks.needsMaterial(PLANK)) {
                metalworks.putCargo(new Cargo(PLANK, map));
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(metalworks.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (metalworks.needsMaterial(IRON_BAR)) {
                metalworks.putCargo(new Cargo(IRON_BAR, map));
            }

            if (metalworks.needsMaterial(PLANK)) {
                metalworks.putCargo(new Cargo(PLANK, map));
            }

            assertEquals(metalworks.getProductivity(), 100);
        }
    }

    @Test
    public void testMetalworksLosesProductivityWhenResourcesRunOut() throws Exception {

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

        /* Remove all planks and iron bars from the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0);
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 0);

        /* Connect the metalworks with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), metalworks.getFlag());

        /* Make the metalworks create some tool with full resources available */
        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            if (metalworks.needsMaterial(IRON_BAR) && metalworks.getAmount(IRON_BAR) < 2) {
                metalworks.putCargo(new Cargo(IRON_BAR, map));
            }

            if (metalworks.needsMaterial(PLANK) && metalworks.getAmount(PLANK) < 2) {
                metalworks.putCargo(new Cargo(PLANK, map));
            }
        }

        /* Verify that the productivity goes down when resources run out */
        assertEquals(metalworks.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(metalworks.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedMetalworksHasNoProductivity() throws Exception {

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

        /* Verify that the unoccupied metalworks is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(metalworks.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testMetalworksCanProduce() throws Exception {

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
        Worker metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        /* Verify that the metalworks can produce */
        assertTrue(metalworks.canProduce());
    }

    @Test
    public void testMetalworksReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Construct the metalworks */
        Utils.constructHouse(metalworks0);

        /* Verify that the reported output is correct */
        assertEquals(metalworks0.getProducedMaterial().length, TOOLS.size());

        Set<Material> producedMaterial = new HashSet<>(Arrays.asList(metalworks0.getProducedMaterial()));

        for (Material tool : TOOLS) {
            assertTrue(producedMaterial.contains(tool));
        }
    }

    @Test
    public void testMetalworksReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(metalworks0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(metalworks0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(metalworks0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(metalworks0.getCanHoldAmount(PLANK), 2);
        assertEquals(metalworks0.getCanHoldAmount(STONE), 2);

        for (Material material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(metalworks0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMetalworksReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(6, 12);
        Building metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Construct the metalworks */
        Utils.constructHouse(metalworks0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(metalworks0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(metalworks0.getTypesOfMaterialNeeded().contains(IRON_BAR));
        assertTrue(metalworks0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(metalworks0.getCanHoldAmount(IRON_BAR), 6);
        assertEquals(metalworks0.getCanHoldAmount(PLANK), 6);

        for (Material material : Material.values()) {
            if (material == IRON_BAR || material == PLANK) {
                continue;
            }

            assertEquals(metalworks0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testMetalworksWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(16, 6);
        Building metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Wait for the metalworks to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(metalworks);
        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

        /* Give material to the metalworks */
        Utils.putCargoToBuilding(metalworks, PLANK);
        Utils.putCargoToBuilding(metalworks, IRON_BAR);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, PLANK, 8, metalworks.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the metalworks waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);
            assertNull(metalworks.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the metalworks with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == PLANK) {
                break;
            }

            assertNull(metalworks.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(metalworks.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of tool and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, metalworks.getWorker());

        Utils.verifyWorkerCarriesTool(metalworks.getWorker());
    }

    @Test
    public void testMetalworksDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(16, 6);
        Metalworks metalworks = map.placeBuilding(new Metalworks(player0), point1);

        /* Connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Wait for the metalworks to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(metalworks);
        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks);

        /* Give material to the metalworks */
        Utils.putCargoToBuilding(metalworks, PLANK);
        Utils.putCargoToBuilding(metalworks, PLANK);
        Utils.putCargoToBuilding(metalworks, IRON_BAR);
        Utils.putCargoToBuilding(metalworks, IRON_BAR);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, PLANK, 8, metalworks.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The metalworks waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);
            assertNull(metalworks.getWorker().getCargo());

            map.stepTime();
        }

        /* Reconnect the metalworks with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, metalworks.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == PLANK) {
                break;
            }

            assertNull(metalworks.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(metalworks.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, metalworks.getWorker());

        Utils.verifyWorkerCarriesTool(metalworks.getWorker());

        /* Wait for the worker to put the cargo on the flag */
        assertEquals(metalworks.getWorker().getTarget(), metalworks.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworks.getWorker(), metalworks.getFlag().getPosition());

        assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);

        /* Verify that the metalworks doesn't produce anything because the flag is full */
        for (int i = 0; i < 400; i++) {
            assertEquals(metalworks.getFlag().getStackedCargo().size(), 8);
            assertNull(metalworks.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenToolDeliveryAreBlockedMetalworksFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Metalworks */
        Point point1 = new Point(7, 9);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place road to connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        /* Wait for the metalworks to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(metalworks0);

        Worker metalworker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks0);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks0);
        assertEquals(metalworks0.getWorker(), metalworker0);

        /* Add a lot of material to the headquarter for the metalworks to consume */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, PLANK, 40);

        /* Block storage of tools */
        Utils.blockDeliveryOfTools(headquarter0);

        /* Verify that the metalworks puts eight tools on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, metalworks0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(metalworks0.getFlag().getStackedCargo().size(), 8);
            assertTrue(metalworker0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                Utils.verifyWorkerDoesNotCarryTool(road0.getCourier());
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndMetalworksIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place metalworks */
        Point point2 = new Point(18, 6);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the metalworks */
        Road road1 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the metalworks and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, metalworks0);

        /* Add a lot of material to the headquarter for the metalworks to consume */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, PLANK, 40);

        /* Wait for the metalworks and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, metalworks0);

        Worker metalworker0 = metalworks0.getWorker();

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks0);
        assertEquals(metalworks0.getWorker(), metalworker0);

        /* Verify that the worker goes to the storage when the metalworks is torn down */
        headquarter0.blockDeliveryOfMaterial(METALWORKER);

        metalworks0.tearDown();

        map.stepTime();

        assertFalse(metalworker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks0.getFlag().getPosition());

        assertEquals(metalworker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, metalworker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(metalworker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndMetalworksIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place metalworks */
        Point point2 = new Point(18, 6);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the metalworks */
        Road road1 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the metalworks and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, metalworks0);

        /* Add a lot of material to the headquarter for the metalworks to consume */
        Utils.adjustInventoryTo(headquarter0, IRON_BAR, 40);
        Utils.adjustInventoryTo(headquarter0, PLANK, 40);

        /* Wait for the metalworks and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, metalworks0);

        Worker metalworker0 = metalworks0.getWorker();

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks0);
        assertEquals(metalworks0.getWorker(), metalworker0);

        /* Verify that the worker goes to the storage off-road when the metalworks is torn down */
        headquarter0.blockDeliveryOfMaterial(METALWORKER);

        metalworks0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(metalworker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, metalworks0.getFlag().getPosition());

        assertEquals(metalworker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(metalworker0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, METALWORKER, 1);

        assertEquals(headquarter0.getAmount(METALWORKER), 1);

        headquarter0.pushOutAll(METALWORKER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Metalworker.class, player0);

            assertEquals(headquarter0.getAmount(METALWORKER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, METALWORKER, 1);

        headquarter0.blockDeliveryOfMaterial(METALWORKER);
        headquarter0.pushOutAll(METALWORKER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Metalworker.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place road to connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the metalworks to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(metalworks0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(metalworks0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(METALWORKER);

        Worker worker = metalworks0.getWorker();

        metalworks0.tearDown();

        assertEquals(worker.getPosition(), metalworks0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, metalworks0.getFlag().getPosition());

        assertEquals(worker.getPosition(), metalworks0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), metalworks0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place metalworks */
        Point point1 = new Point(7, 9);
        Metalworks metalworks0 = map.placeBuilding(new Metalworks(player0), point1);

        /* Place road to connect the metalworks with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, metalworks0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the metalworks to get constructed */
        Utils.waitForBuildingToBeConstructed(metalworks0);

        /* Wait for a metalworker to start walking to the metalworks */
        Metalworker metalworker = Utils.waitForWorkerOutsideBuilding(Metalworker.class, player0);

        /* Wait for the metalworker to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the metalworker goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(metalworker.getTarget(), metalworks0.getPosition());

        headquarter0.blockDeliveryOfMaterial(METALWORKER);

        metalworks0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, metalworks0.getFlag().getPosition());

        assertEquals(metalworker.getPosition(), metalworks0.getFlag().getPosition());
        assertNotEquals(metalworker.getTarget(), headquarter0.getPosition());
        assertFalse(metalworker.isInsideBuilding());
        assertNull(metalworks0.getWorker());
        assertNotNull(metalworker.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, metalworker, metalworker.getTarget());

        Point point = metalworker.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(metalworker.isDead());
            assertEquals(metalworker.getPosition(), point);
            assertTrue(map.getWorkers().contains(metalworker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(metalworker));
    }
}
