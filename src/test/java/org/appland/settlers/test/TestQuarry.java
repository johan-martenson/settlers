/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Storehouse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Stone.StoneType.STONE_1;
import static org.appland.settlers.model.Stone.StoneType.STONE_2;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestQuarry {

    @Test
    public void testQuarryOnlyNeedsTwoPlanksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place quarry */
        Point point22 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        quarry0.putCargo(cargo);
        quarry0.putCargo(cargo);

        /* Assign builder */
        Utils.assignBuilder(quarry0);

        /* Verify that this is enough to construct the quarry */
        for (int i = 0; i < 100; i++) {
            assertTrue(quarry0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(quarry0.isReady());
    }

    @Test
    public void testQuarryCannotBeConstructedWithOnePlank() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place quarry */
        Point point22 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point22);

        /* Deliver two planks */
        Cargo cargo = new Cargo(PLANK, map);

        quarry0.putCargo(cargo);

        /* Assign builder */
        Utils.assignBuilder(quarry0);

        /* Verify that this is enough to construct the quarry */
        for (int i = 0; i < 500; i++) {
            assertTrue(quarry0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(quarry0.isReady());
    }

    @Test
    public void testFinishedQuarryNeedsWorker() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Verify that a finished quarry needs a worker */
        constructHouse(quarry);

        assertTrue(quarry.isReady());
        assertTrue(quarry.needsWorker());
    }

    @Test
    public void testStonemasonIsAssignedToFinishedHouse() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Finish the woodcutter */
        constructHouse(quarry);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        /* Verify that the right amount of workers are added to the map */
        assertTrue(map.getWorkers().size() >= 3);

        /* Verify that the map contains a stonemason */
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Stonemason.class);
    }

    @Test
    public void testStonemasonIsNotASoldier() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Finish the woodcutter */
        constructHouse(quarry);

        /* Wait for a stonemason to walk out */
        Stonemason stonemason0 = Utils.waitForWorkerOutsideBuilding(Stonemason.class, player0);

        /* Verify that the stonemason is not a soldier */
        assertFalse(stonemason0.isSoldier());
    }

    @Test
    public void testStonemasonIsCreatedFromPickAxe() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all stone masons from the headquarter and add a pick axe */
        Utils.adjustInventoryTo(headquarter, STONEMASON, 0);
        Utils.adjustInventoryTo(headquarter, Material.PICK_AXE, 1);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Finish the woodcutter */
        constructHouse(quarry);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        /* Verify that the right amount of workers are added to the map */
        assertTrue(map.getWorkers().size() >= 3);

        /* Verify that the map contains a stonemason */
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Stonemason.class);
    }

    @Test
    public void testArrivedStonemasonRestsInHutAndThenLeaves() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        assertTrue(stonemason.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
            assertTrue(stonemason.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(stonemason.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());
    }

    @Test
    public void testStonemasonFindsSpotToGetStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        assertTrue(stonemason.isInsideBuilding());

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once and make sure the stone mason goes out of the hut */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();
        assertNotNull(point);

        /* Verify that the stonemason has chosen a correct spot */
        assertEquals(point, point2);
        assertTrue(stonemason.isTraveling() || point.equals(stonemason.getPosition()));
        assertFalse(map.isBuildingAtPoint(point));
    }

    @Test
    public void testStonemasonReachesPointToGetStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Construct the forester hut */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertEquals(stonemason.getTarget(), point2);
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Wait for the stonemason to arrive if it isn't already at the right spot */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertEquals(stonemason.getPosition(), point2);
        assertTrue(stonemason.isArrived());
        assertTrue(stonemason.isAt(point));
        assertFalse(stonemason.isTraveling());
    }

    @Test
    public void testStonemasonGetsStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        assertFalse(map.isDecoratedAtPoint(point2));

        map.stepTime();
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);
    }

    @Test
    public void testStoneType1DecorationWhenStonemasonGetsLastStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, STONE_1, 1);

        assertEquals(stone.getStoneType(), STONE_1);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        assertFalse(map.isDecoratedAtPoint(point2));

        map.stepTime();
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);
        assertFalse(map.isStoneAtPoint(point2));
        assertNull(map.getStoneAtPoint(point2));
        assertFalse(map.getStones().contains(stone));
        assertTrue(map.isDecoratedAtPoint(point2));
        assertEquals(map.getDecorationAtPoint(point2), DecorationType.STONE_REMAINING_STYLE_1);
    }

    @Test
    public void testStoneType2DecorationWhenStonemasonGetsLastStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, STONE_2, 1);

        assertEquals(stone.getStoneType(), STONE_2);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        assertFalse(map.isDecoratedAtPoint(point2));

        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);
        assertFalse(map.isStoneAtPoint(point2));
        assertNull(map.getStoneAtPoint(point2));
        assertFalse(map.getStones().contains(stone));
        assertTrue(map.isDecoratedAtPoint(point2));
        assertEquals(map.getDecorationAtPoint(point2), DecorationType.STONE_REMAINING_STYLE_2);
    }

    @Test
    public void testStonemasonReturnsAndStoresStoneAsCargo() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point1 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place quarry */
        Point point2 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point2);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point3 = new Point(13, 5);
        Stone stone = map.placeStone(point3, STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), point3);
        assertTrue(stonemason.isGettingStone());
        assertNull(stonemason.getCargo());

        /* Wait for the stonemason to get some stone */
        Utils.fastForward(49, map);

        assertTrue(stonemason.isGettingStone());
        assertNull(stonemason.getCargo());

        map.stepTime();

        /* Stonemason has the stone and goes back to the quarry */
        assertFalse(stonemason.isGettingStone());

        assertEquals(stonemason.getTarget(), quarry.getPosition());
        assertNotNull(stonemason.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry.getPosition());

        assertTrue(stonemason.isInsideBuilding());
        assertNotNull(stonemason.getCargo());

        /* Stonemason leaves the hut and goes to the flag to drop the cargo */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), quarry.getFlag().getPosition());
        assertTrue(quarry.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry.getFlag().getPosition());

        assertFalse(quarry.getFlag().getStackedCargo().isEmpty());
        assertNull(stonemason.getCargo());

        /* The stonemason goes back to the quarry */
        assertEquals(stonemason.getTarget(), quarry.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, stonemason);

        assertTrue(stonemason.isInsideBuilding());
    }

    @Test
    public void testStoneCargoIsDeliveredToGuardHouseUnderConstructionWhichIsCloserThanHeadquarters() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place stone */
        Point point2 = new Point(14, 8);
        Stone stone = map.placeStone(point2, STONE_1, 10);

        /* Remove all stone from the headquarters */
        Utils.adjustInventoryTo(headquarter, STONE, 0);

        /* Place guard house */
        Point point4 = new Point(10, 4);
        GuardHouse guardHouse = map.placeBuilding(new GuardHouse(player0), point4);

        /* Connect the guard house to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, guardHouse.getFlag(), headquarter.getFlag());

        /* Place the quarry */
        Point point1 = new Point(14, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the guard house */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), guardHouse.getFlag());

        /* Wait for the quarry to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(quarry);

        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Wait for the courier on the road between the guard house and the quarry hut to have a stone cargo */
        Utils.waitForFlagToGetStackedCargo(map, quarry.getFlag(), 1);

        assertEquals(quarry.getFlag().getStackedCargo().getFirst().getMaterial(), STONE);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the guard house (and not the headquarters) */
        assertEquals(quarry.getAmount(STONE), 0);
        assertTrue(guardHouse.needsMaterial(STONE));
        assertTrue(guardHouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), guardHouse.getPosition());

        assertEquals(guardHouse.getAmount(STONE), 1);
    }

    @Test
    public void testStoneIsNotDeliveredToStorehouseUnderConstructionThatDoesntNeedStone() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are stones but no planks */
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 20);

        /* Place stone */
        Point point2 = new Point(14, 6);
        Stone stone = map.placeStone(point2, STONE_1, 10);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Wait until the storehouse doesn't need stones */
        assertEquals(map, storehouse.getMap());
        Utils.waitUntilBuildingDoesntNeedMaterial(storehouse, STONE);

        /* Place the quarry */
        Point point1 = new Point(14, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), storehouse.getFlag());

        /* Deliver the needed planks to the quarry */
        Utils.deliverCargos(quarry, PLANK, 2);

        /* Wait for the quarry to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(quarry);

        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Wait for the courier on the road between the storehouse and the quarry hut to have a stone cargo */
        Utils.waitForFlagToGetStackedCargo(map, quarry.getFlag(), 1);

        assertEquals(quarry.getFlag().getStackedCargo().getFirst().getMaterial(), STONE);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters */
        assertEquals(headquarter.getAmount(STONE), 17);
        assertEquals(quarry.getAmount(STONE), 0);
        assertFalse(storehouse.needsMaterial(STONE));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(STONE));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testStoneIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Adjust the inventory so that there are stones but no planks */
        Utils.adjustInventoryTo(headquarter, STONE, 0);

        /* Place stone */
        Point point2 = new Point(14, 6);
        Stone stone = map.placeStone(point2, STONE_1, 10);

        /* Place storehouse */
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        /* Deliver stones to the storehouse so it only needs one more stone */
        Utils.deliverCargos(storehouse, PLANK, 4);
        Utils.deliverCargos(storehouse, STONE, 2);

        /* Connect the storehouse to the headquarters */
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        /* Place the quarry */
        Point point1 = new Point(14, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry with the storehouse */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), storehouse.getFlag());

        /* Deliver the needed planks to the quarry */
        Utils.deliverCargos(quarry, PLANK, 2);

        /* Wait for the quarry to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(quarry);

        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Wait for the flag on the road between the storehouse and the quarry to have a stone cargo */
        Utils.waitForFlagToGetStackedCargo(map, quarry.getFlag(), 1);

        assertEquals(quarry.getFlag().getStackedCargo().getFirst().getMaterial(), STONE);

        /* Wait for the courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        /* Verify that no stone is delivered from the headquarters */
        Utils.adjustInventoryTo(headquarter, STONE, 1);

        assertEquals(storehouse.getCanHoldAmount(STONE) - storehouse.getAmount(STONE), 1);
        assertFalse(storehouse.needsMaterial(STONE));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(STONE), 1);
    }

    @Test
    public void testQuarryWithoutStoneProducesNothing() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        assertTrue(stonemason.isInsideBuilding());
        assertNull(stonemason.getCargo());

        /* Verify that no stone is available from the quarry's flag */
        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertTrue(quarry.getFlag().getStackedCargo().isEmpty());
            assertNull(stonemason.getCargo());
        }
    }

    @Test
    public void testStonemasonStaysAtHomeWhenNoStonesAreAvailable() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        assertTrue(stonemason.isInsideBuilding());

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());

        /* Verify that the stone mason hasn't understood that there are no resources available */
        assertFalse(quarry.isOutOfNaturalResources());

        /* Step once to verify that the stonemason stays inside */
        map.stepTime();

        assertTrue(stonemason.isInsideBuilding());

        /* Verify that the quarry is out of natural resources */
        assertTrue(quarry.isOutOfNaturalResources());
    }

    @Test
    public void testStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 15, 15);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(5, 5);
        Stone stone0 = map.placeStone(point1, STONE_1, 7);

        /* Remove all but one pats of the stone */
        for (int i = 0; i < 6; i++) {
            stone0.removeOnePart();

            map.stepTime();

            assertTrue(map.isStoneAtPoint(point1));
        }

        /* Verify that the stone is gone when the final part is removed */
        stone0.removeOnePart();

        assertEquals(stone0.getAmount(), 0);

        map.stepTime();

        assertFalse(map.isStoneAtPoint(point1));
    }

    @Test
    public void testQuarryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place stone */
        Point point3 = new Point(12, 8);
        Stone stone = map.placeStone(point3, STONE_1, 7);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Let the stone mason rest */
        Utils.fastForward(100, map);

        /* Wait for the stone mason to produce a new stone cargo */
        Worker stonemason = quarry0.getWorker();

        assertEquals(stonemason.getTarget(), stone.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, stonemason.getTarget());

        /* Wait for the stone mason to get a new stone */
        Utils.fastForward(50, map);

        assertNotNull(stonemason.getCargo());

        /* Wait for the stone mason to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        /* Verify that the stone mason puts the stone cargo at the flag */
        map.stepTime();

        assertEquals(stonemason.getTarget(), quarry0.getFlag().getPosition());
        assertTrue(quarry0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        assertNull(stonemason.getCargo());
        assertFalse(quarry0.getFlag().getStackedCargo().isEmpty());

        /* Wait for the worker to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        /* Wait for the worker to rest and produce another cargo */
        Utils.fastForward(100, map);

        assertEquals(stonemason.getTarget(), stone.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, stonemason.getTarget());

        /* Wait for the stone mason to get a new stone */
        Utils.fastForward(50, map);

        assertNotNull(stonemason.getCargo());

        /* Wait for the stone mason to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        assertNotNull(stonemason.getCargo());

        /* Verify that the second cargo is put at the flag */
        map.stepTime();

        assertEquals(stonemason.getTarget(), quarry0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        assertNull(stonemason.getCargo());
        assertEquals(quarry0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place stone */
        Point point3 = new Point(12, 8);
        Stone stone = map.placeStone(point3, STONE_1, 7);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Let the stone mason rest */
        Utils.fastForward(100, map);

        /* Wait for the stone mason to go to the stone */
        Worker stonemason = quarry0.getWorker();

        assertEquals(stonemason.getTarget(), stone.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, stonemason.getTarget());

        /* Wait for the stone mason to get a new stone */
        Utils.fastForward(50, map);

        assertNotNull(stonemason.getCargo());

        /* Wait for the stone mason to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        /* Verify that the stone mason puts the stone cargo at the flag */
        map.stepTime();

        assertEquals(stonemason.getTarget(), quarry0.getFlag().getPosition());
        assertTrue(quarry0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        assertNull(stonemason.getCargo());
        assertFalse(quarry0.getFlag().getStackedCargo().isEmpty());

        /* Wait to let the cargo remain at the flag without any connection to the storage */
        Cargo cargo = quarry0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), quarry0.getFlag().getPosition());

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), quarry0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier walks to pick up the cargo */
        map.stepTime();

        assertEquals(courier.getTarget(), quarry0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        /* Verify that the courier delivers the cargo to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONE);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has delivered the cargo to the headquarter */
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(STONE), amount + 1);
    }

    @Test
    public void testStonemasonGoesBackToStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testStonemasonGoesBackOnToStorageOnRoadsIfPossibleWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());

        /* Verify that the worker plans to use the roads */
        boolean firstStep = true;
        for (Point point : stonemason.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedQuarryIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Destroy the quarry */
        quarry0.tearDown();

        assertTrue(quarry0.isBurningDown());

        /* Wait for the quarry to stop burning */
        Utils.fastForward(50, map);

        assertTrue(quarry0.isDestroyed());

        /* Wait for the quarry to disappear */
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), quarry0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(quarry0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));

        map.removeFlag(quarry0.getFlag());

        assertNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(8, 8);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));

        quarry0.tearDown();

        assertNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInQuarryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point5 = new Point(10, 8);
        Stone stone = map.placeStone(point5, STONE_1, 7);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the quarry */
        constructHouse(quarry0);

        /* Assign a worker to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry0);

        assertTrue(stonemason.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the stonemason to produce cargo */
        Utils.fastForwardUntilWorkerProducesCargo(map, stonemason);

        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Wait for the worker to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(stonemason.getTarget(), quarry0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        /* Stop production and verify that no stone is produced */
        quarry0.stopProduction();

        assertFalse(quarry0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(stonemason.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInQuarryCanBeResumed() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point5 = new Point(10, 8);
        Stone stone = map.placeStone(point5, STONE_1, 7);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the quarry */
        constructHouse(quarry0);

        /* Assign a worker to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry0);

        assertTrue(stonemason.isInsideBuilding());

        /* Let the worker rest */
        Utils.fastForward(100, map);

        /* Wait for the stonemason to produce stone */
        Utils.fastForwardUntilWorkerProducesCargo(map, stonemason);

        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Wait for the worker to go back to the quarry */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getPosition());

        /* Wait for the worker to deliver the cargo */
        map.stepTime();

        assertEquals(stonemason.getTarget(), quarry0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        /* Stop production */
        quarry0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(stonemason.getCargo());

            map.stepTime();
        }

        /* Resume production and verify that the quarry produces stone again */
        quarry0.resumeProduction();

        assertTrue(quarry0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, stonemason);

        assertNotNull(stonemason.getCargo());
    }

    @Test
    public void testAssignedStonemasonHasCorrectlySetPlayer() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(20, 14);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Wait for stonemason to get assigned and leave the headquarter */
        List<Stonemason> workers = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Stonemason worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);
        Player player2 = new Player("Player 2", PlayerColor.RED);

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
        constructHouse(fortress0);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        /* Place quarry close to the new border */
        Point point4 = new Point(28, 18);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point4);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Stonemason worker = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Verify that the worker goes back to its own storage when the fortress is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    // Add test for stones in/out of range

    @Test
    public void testStoneMasonReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place quarry */
        Point point2 = new Point(14, 4);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stone mason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0);

        Stonemason stoneMason = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Stonemason) {
                stoneMason = (Stonemason) worker;
            }
        }

        assertNotNull(stoneMason);
        assertEquals(stoneMason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the stone mason has started walking */
        assertFalse(stoneMason.isExactlyAtPoint());

        /* Remove the next road */
        map.removeRoad(road1);

        /* Verify that the stone mason continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, flag0.getPosition());

        assertEquals(stoneMason.getPosition(), flag0.getPosition());

        /* Verify that the stone mason returns to the headquarter when it reaches the flag */
        assertEquals(stoneMason.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, headquarter0.getPosition());
    }

    @Test
    public void testStoneMasonContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place quarry */
        Point point2 = new Point(14, 4);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stone mason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0);

        Stonemason stoneMason = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Stonemason) {
                stoneMason = (Stonemason) worker;
            }
        }

        assertNotNull(stoneMason);
        assertEquals(stoneMason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* See that the stone mason has started walking */
        assertFalse(stoneMason.isExactlyAtPoint());

        /* Remove the current road */
        map.removeRoad(road0);

        /* Verify that the stone mason continues walking to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, flag0.getPosition());

        assertEquals(stoneMason.getPosition(), flag0.getPosition());

        /* Verify that the stone mason continues to the final flag */
        assertEquals(stoneMason.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, quarry0.getFlag().getPosition());

        /* Verify that the stone mason goes out to quarry instead of going directly back */
        assertNotEquals(stoneMason.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStonemasonReturnsToStorageIfQuarryIsDestroyed() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place quarry */
        Point point2 = new Point(14, 4);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stonemason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0);

        Stonemason stonemason = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Stonemason) {
                stonemason = (Stonemason) worker;
            }
        }

        assertNotNull(stonemason);
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        /* Wait for the stone mason to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, flag0.getPosition());

        map.stepTime();

        /* See that the stone mason has started walking */
        assertFalse(stonemason.isExactlyAtPoint());

        /* Tear down the quarry */
        quarry0.tearDown();

        /* Verify that the stone mason continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        assertEquals(stonemason.getPosition(), quarry0.getFlag().getPosition());

        /* Verify that the stone mason goes back to storage */
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStonemasonGoesOffroadBackToClosestStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, storehouse0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(storehouse0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testStonemasonReturnsOffroadAndAvoidsBurningStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testStonemasonReturnsOffroadAndAvoidsDestroyedStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Finish construction of the storage */
        constructHouse(storehouse0);

        /* Destroy the storage */
        storehouse0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storehouse0);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testStonemasonReturnsOffroadAndAvoidsUnfinishedStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(headquarter0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Place road to connect the headquarter and the quarry */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0).getFirst();

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, quarry0.getFlag().getPosition());

        /* Tear down the building */
        quarry0.tearDown();

        /* Verify that the worker goes to the building and then returns to the headquarter instead of entering */
        assertEquals(worker.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, quarry0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoStonemasonsGettingLastStone() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(10, 12);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(8, 6);
        Stone stone = map.placeStone(point1, STONE_1, 7);

        /* Place two quarries with the same distance to the stone */
        Point point2 = new Point(5, 9);
        Point point3 = new Point(9, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2);
        Quarry quarry1 = map.placeBuilding(new Quarry(player0), point3);

        /* Finish construction of the quarries */
        constructHouse(quarry0);
        constructHouse(quarry1);

        /* Occupy the quarries */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0);
        Utils.occupyBuilding(new Stonemason(player0, map), quarry1);

        /* Remove almost all of the stone so there is only a single piece left */
        Utils.removePiecesFromStoneUntil(stone, 1);

        /* Wait for the stone masons to go out and try to get the same piece of stone */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 2, player0);

        Worker stonemason0 = quarry0.getWorker();
        Worker stonemason1 = quarry1.getWorker();

        assertEquals(stonemason0.getTarget(), stone.getPosition());
        assertEquals(stonemason0.getTarget(), stone.getPosition());

        /* Wait for the stonemasons to try to get the same stone */
        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, stonemason0.getTarget());

        /* Wait for the stone mason to get a new stone */
        for (int i = 0; i < 1000; i++) {
            if (stonemason0.getCargo() != null || stonemason1.getCargo() != null) {
                break;
            }

            map.stepTime();
        }

        assertTrue(stonemason0.getCargo() != null || stonemason1.getCargo() != null);
        assertFalse(stonemason0.getCargo() != null && stonemason1.getCargo() != null);

        /* Wait for the stone masons to go back to the quarries */
        assertEquals(stonemason0.getTarget(), quarry0.getPosition());
        assertEquals(stonemason1.getTarget(), quarry1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, quarry0.getPosition());

        assertTrue(stonemason0.isInsideBuilding());
        assertTrue(stonemason1.isInsideBuilding());
    }

    @Test
    public void testQuarryWithoutResourcesHasZeroProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Verify that the productivity is 0% when the quarry doesn't produce anything */
        for (int i = 0; i < 500; i++) {
            assertTrue(quarry0.getFlag().getStackedCargo().isEmpty());
            assertNull(stonemason.getCargo());
            assertEquals(quarry0.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testQuarryWithAbundantResourcesHasFullProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker quarry = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(quarry.isInsideBuilding());
        assertEquals(quarry.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), quarry);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Place a stone on the map */
        Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);

        /* Make the quarry produce some stones available */
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            /* Put a new stone if the current one is gone */
            if (map.getStones().isEmpty()) {
                Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
            }
        }

        /* Verify that the productivity is 100% and stays there */
        assertEquals(quarry0.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {

            map.stepTime();

            assertEquals(quarry0.getProductivity(), 100);

            /* Put a new stone if the current one is gone */
            if (map.getStones().isEmpty()) {
                Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
            }
        }
    }

    @Test
    public void testQuarryLosesProductivityWhenResourcesRunOut() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Put a new stone if the current one is gone */
        if (map.getStones().isEmpty()) {
            Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
        }

        /* Make the quarry take down stones until the stones are gone */
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getStones().isEmpty()) {
                break;
            }
        }

        assertEquals(quarry0.getProductivity(), 100);

        /* Verify that the productivity goes down when resources run out */
        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(quarry0.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedQuarryHasNoProductivity() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Verify that the unoccupied quarry is unproductive */
        for (int i = 0; i < 1000; i++) {
            assertEquals(quarry0.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testStonemasonDoesNotGoToUnreachableStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(15, 5);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Surround the quarry with water the stonemason cannot cross */
        Point point2 = new Point(15, 7);
        Point point3 = new Point(18, 6);
        Point point4 = new Point(18, 4);
        Point point5 = new Point(15, 3);
        Point point6 = new Point(12, 4);
        Point point7 = new Point(12, 6);
        Utils.surroundPointWithVegetation(point2, WATER, map);
        Utils.surroundPointWithVegetation(point3, WATER, map);
        Utils.surroundPointWithVegetation(point4, WATER, map);
        Utils.surroundPointWithVegetation(point5, WATER, map);
        Utils.surroundPointWithVegetation(point6, WATER, map);
        Utils.surroundPointWithVegetation(point7, WATER, map);

        /* Construct the quarry */
        constructHouse(quarry0);

        /* Put a stone on the other side of the water */
        Point point8 = new Point(15, 9);
        map.placeStone(point8, STONE_1, 7);

        /* Occupy the quarry */
        Stonemason stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Verify that the stonemason doesn't try to the stone */
        for (int i = 0; i < 2000; i++) {

            map.stepTime();

            assertTrue(stonemason.isInsideBuilding());
            assertEquals(stonemason.getPosition(), quarry0.getPosition());
        }
    }

    @Test
    public void testQuarryCanProduce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 10);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason0 = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        /* Verify that the quarry can produce */
        assertTrue(quarry0.canProduce());
    }

    @Test
    public void testQuarryReportsCorrectOutput() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry0);

        /* Verify that the reported output is correct */
        assertEquals(quarry0.getProducedMaterial().length, 1);
        assertEquals(quarry0.getProducedMaterial()[0], STONE);
    }

    @Test
    public void testQuarryReportsCorrectMaterialsNeededForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Verify that the reported needed construction material is correct */
        assertEquals(quarry0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(quarry0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(quarry0.getCanHoldAmount(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(quarry0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testQuarryReportsCorrectMaterialsNeededForProduction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry0);

        /* Verify that the reported needed construction material is correct */
        assertEquals(quarry0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(quarry0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testTearingDownQuarryAndThenFlag() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(6, 12);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry0);

        /* Tear down the quarry */
        quarry0.tearDown();

        /* Verify that tearing down the flag doesn't cause any problem */
        map.removeFlag(quarry0.getFlag());

        assertFalse(map.getFlags().contains(quarry0.getFlag()));
        assertFalse(map.isFlagAtPoint(quarry0.getFlag().getPosition()));
    }

    @Test
    public void testQuarryWaitsWhenFlagIsFull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(16, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(18, 6);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Wait for the quarry to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(quarry);
        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Fill the flag with flour cargos */
        Utils.placeCargos(map, FLOUR, 8, quarry.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* Verify that the quarry waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 300; i++) {
            assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the quarry with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(quarry.getFlag().getStackedCargo().size(), 7);

        /* Verify that the worker produces a cargo of flour and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, quarry.getWorker(), STONE);
    }

    @Test
    public void testQuarryDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(16, 6);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(18, 6);
        Stone stone = map.placeStone(point2, STONE_1, 7);

        /* Connect the quarry with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Wait for the quarry to get constructed and assigned a worker */
        Utils.waitForBuildingToBeConstructed(quarry);
        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Fill the flag with cargos */
        Utils.placeCargos(map, FLOUR, 8, quarry.getFlag(), headquarter);

        /* Remove the road */
        map.removeRoad(road0);

        /* The quarry waits for the flag to get empty and produces nothing */
        for (int i = 0; i < 800; i++) {
            assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        /* Reconnect the quarry with the headquarter */
        Road road1 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter.getFlag());

        /* Wait for the courier to pick up one of the cargos */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(quarry.getFlag().getStackedCargo().size(), 7);

        /* Remove the road */
        map.removeRoad(road1);

        /* The worker produces a cargo and puts it on the flag */
        Utils.fastForwardUntilWorkerCarriesCargo(map, quarry.getWorker(), STONE);

        /* Wait for the worker to put the cargo on the flag */
        Utils.waitForFlagToGetStackedCargo(map, quarry.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, quarry.getWorker(), quarry.getFlag().getPosition());

        assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

        /* Verify that the quarry doesn't produce anything because the flag is full */
        for (int i = 0; i < 800; i++) {
            assertEquals(quarry.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }
    }

    @Test
    public void testWhenStoneDeliveryAreBlockedQuarryFillsUpFlagAndThenStops() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place Quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Place stones */
        Point point2 = new Point(9, 9);
        Point point3 = new Point(10, 8);
        Stone stone0 = map.placeStone(point2, STONE_1, 7);
        Stone stone1 = map.placeStone(point3, STONE_1, 7);

        /* Place road to connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Wait for the quarry to get constructed and occupied */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(quarry0);

        Worker stonemason0 = Utils.waitForNonMilitaryBuildingToGetPopulated(quarry0);

        assertTrue(stonemason0.isInsideBuilding());
        assertEquals(stonemason0.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason0);

        /* Block storage of stones */
        headquarter0.blockDeliveryOfMaterial(STONE);

        /* Verify that the quarry puts eight stones on the flag and then stops */
        Utils.waitForFlagToGetStackedCargo(map, quarry0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, quarry0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(quarry0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), STONE);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndQuarryIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place quarry */
        Point point2 = new Point(18, 6);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2);

        /* Place road to connect the storehouse with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarter with the quarry */
        Road road1 = map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the quarry and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, quarry0);

        /* Wait for the quarry and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, quarry0);

        Worker stonemason0 = quarry0.getWorker();

        assertTrue(stonemason0.isInsideBuilding());
        assertEquals(stonemason0.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason0);

        /* Verify that the worker goes to the storage when the quarry is torn down */
        headquarter0.blockDeliveryOfMaterial(STONEMASON);

        quarry0.tearDown();

        map.stepTime();

        assertFalse(stonemason0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, quarry0.getFlag().getPosition());

        assertEquals(stonemason0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, stonemason0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(stonemason0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndQuarryIsTornDown() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place storehouse */
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        /* Place quarry */
        Point point2 = new Point(18, 6);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2);

        /* Place road to connect the storehouse with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        /* Place road to connect the headquarters with the quarry */
        Road road1 = map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Add a lot of planks and stones to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the quarry and the storehouse to get constructed */
        Utils.waitForBuildingsToBeConstructed(storehouse, quarry0);

        /* Wait for the quarry and the storage to get occupied */
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, quarry0);

        Worker stonemason0 = quarry0.getWorker();

        assertTrue(stonemason0.isInsideBuilding());
        assertEquals(stonemason0.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason0);

        /* Verify that the worker goes to the storage off-road when the quarry is torn down */
        headquarter0.blockDeliveryOfMaterial(STONEMASON);

        quarry0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(stonemason0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, quarry0.getFlag().getPosition());

        assertEquals(stonemason0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(stonemason0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        /* Start new game with one player only */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, STONEMASON, 1);

        assertEquals(headquarter0.getAmount(STONEMASON), 1);

        headquarter0.pushOutAll(STONEMASON);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(Stonemason.class, player0);

            assertEquals(headquarter0.getAmount(STONEMASON), 0);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Verify that worker goes out and in continuously when sent out without being blocked */
        Utils.adjustInventoryTo(headquarter0, STONEMASON, 1);

        headquarter0.blockDeliveryOfMaterial(STONEMASON);
        headquarter0.pushOutAll(STONEMASON);

        Worker worker = Utils.waitForWorkerOutsideBuilding(Stonemason.class, player0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Place road to connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the quarry to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(quarry0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter
        */
        headquarter0.blockDeliveryOfMaterial(STONEMASON);

        Worker worker = quarry0.getWorker();

        quarry0.tearDown();

        assertEquals(worker.getPosition(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, quarry0.getFlag().getPosition());

        assertEquals(worker.getPosition(), quarry0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), quarry0.getPosition());
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Place road to connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        /* Wait for the quarry to get constructed */
        Utils.waitForBuildingToBeConstructed(quarry0);

        /* Wait for a stonemason to start walking to the quarry */
        Stonemason stonemason = Utils.waitForWorkerOutsideBuilding(Stonemason.class, player0);

        /* Wait for the stonemason to go past the headquarter's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getFlag().getPosition());

        map.stepTime();

        /* Verify that the stonemason goes away and dies when the house has been torn down and storage is not possible */
        assertEquals(stonemason.getTarget(), quarry0.getPosition());

        headquarter0.blockDeliveryOfMaterial(STONEMASON);

        quarry0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, quarry0.getFlag().getPosition());

        assertEquals(stonemason.getPosition(), quarry0.getFlag().getPosition());
        assertNotEquals(stonemason.getTarget(), headquarter0.getPosition());
        assertFalse(stonemason.isInsideBuilding());
        assertNull(quarry0.getWorker());
        assertNotNull(stonemason.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, stonemason.getTarget());

        Point point = stonemason.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(stonemason.isDead());
            assertEquals(stonemason.getPosition(), point);
            assertTrue(map.getWorkers().contains(stonemason));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(stonemason));
    }

    @Test
    public void testThereAreTwoTypesOfStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Verify that stones can be place with both types */
        Point point2 = new Point(11, 5);
        Point point3 = new Point(13, 5);
        Stone stone0 = map.placeStone(point2, STONE_1, 7);
        Stone stone1 = map.placeStone(point3, STONE_2, 3);

        assertEquals(stone0.getStoneType(), STONE_1);
        assertEquals(stone1.getStoneType(), STONE_2);
    }
}
