/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.STONEMASON;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestQuarry {


    @Test
    public void testQuarryOnlyNeedsTwoPlancksForConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing quarry */
        Point point22 = new Point(6, 22);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point22);

        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        quarry0.putCargo(cargo);
        quarry0.putCargo(cargo);

        /* Verify that this is enough to construct the quarry */
        for (int i = 0; i < 100; i++) {
            assertTrue(quarry0.underConstruction());

            map.stepTime();
        }

        assertTrue(quarry0.ready());
    }

    @Test
    public void testQuarryCannotBeConstructedWithOnePlanck() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing quarry */
        Point point22 = new Point(6, 22);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point22);

        /* Deliver two plancks */
        Cargo cargo = new Cargo(PLANCK, map);

        quarry0.putCargo(cargo);

        /* Verify that this is enough to construct the quarry */
        for (int i = 0; i < 500; i++) {
            assertTrue(quarry0.underConstruction());

            map.stepTime();
        }

        assertFalse(quarry0.ready());
    }

    @Test
    public void testFinishedQuarryNeedsWorker() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(8, 6);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        Utils.constructHouse(quarry, map);

        assertTrue(quarry.ready());
        assertTrue(quarry.needsWorker());
    }

    @Test
    public void testStonemasonIsAssignedToFinishedHouse() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(8, 6);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the woodcutter */
        Utils.constructHouse(quarry, map);

        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        Utils.fastForward(2, map);

        /* Verify that the right amount of workers are added to the map */
        assertEquals(map.getWorkers().size(), 3);

        /* Verify that the map contains a stonemason */
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Stonemason.class);
    }

    @Test
    public void testArrivedStonemasonRestsInHutAndThenLeaves() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);
        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        assertTrue(mason.isInsideBuilding());

        /* Run the game logic 99 times and make sure the forester stays in the hut */
        for (int i = 0; i < 99; i++) {
            assertTrue(mason.isInsideBuilding());
            map.stepTime();
        }

        assertTrue(mason.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());
    }

    @Test
    public void testStonemasonFindsSpotToGetStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        assertTrue(mason.isInsideBuilding());

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once and make sure the stone mason goes out of the hut */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();
        assertNotNull(point);

        /* Verify that the stonemason has chosen a correct spot */
        assertTrue(point.isAdjacent(point2));
        assertTrue(mason.isTraveling() || point.equals(mason.getPosition()));
        assertFalse(map.isBuildingAtPoint(point));
    }

    @Test
    public void testStonemasonReachesPointToGetStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);

        /* Construct the forester hut */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();

        assertFalse(mason.getTarget().equals(point2));
        assertTrue(mason.getTarget().isAdjacent(point2));
        assertTrue(mason.isTraveling());

        map.stepTime();

        /* Wait for the stonemason to arrive if it isn't already at the right spot */
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }

        assertTrue(mason.getPosition().isAdjacent(point2));
        assertTrue(mason.isArrived());
        assertTrue(mason.isAt(point));
        assertFalse(mason.isTraveling());
    }

    @Test
    public void testStonemasonGetsStone() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());

        Point point = mason.getTarget();

        assertTrue(point.isAdjacent(point2));
        assertTrue(mason.isTraveling());

        map.stepTime();

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }

        assertTrue(mason.isArrived());
        assertTrue(mason.getPosition().isAdjacent(point2));
        assertTrue(mason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(mason.isGettingStone());
            map.stepTime();
        }

        assertTrue(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));

        /* Verify that the stonemason is done getting stone at the correct time */
        map.stepTime();

        assertFalse(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));
        assertNotNull(mason.getCargo());
        assertEquals(mason.getCargo().getMaterial(), STONE);
    }

    @Test
    public void testStonemasonReturnsAndStoresStoneAsCargo() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point1 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point1);

        Point point2 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point2);

        map.placeAutoSelectedRoad(player0, hq.getFlag(), quarry.getFlag());

        /* Place stone */
        Point point3 = new Point(13, 5);
        Stone stone = map.placeStone(point3);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(mason.isInsideBuilding());

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());

        Point point = mason.getTarget();

        assertTrue(point.isAdjacent(point3));
        assertTrue(mason.isTraveling());

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }

        assertTrue(mason.isArrived());
        assertTrue(mason.getPosition().isAdjacent(point3));
        assertTrue(mason.isGettingStone());
        assertNull(mason.getCargo());

        /* Wait for the stonemason to get some stone */
        Utils.fastForward(49, map);

        assertTrue(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));
        assertNull(mason.getCargo());

        map.stepTime();

        /* Stonemason has the stone and goes back to the quarry */
        assertFalse(mason.isGettingStone());

        assertEquals(mason.getTarget(), quarry.getPosition());
        assertNotNull(mason.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, quarry.getPosition());

        assertTrue(mason.isInsideBuilding());
        assertNotNull(mason.getCargo());

        /* Stonemason leaves the hut and goes to the flag to drop the cargo */
        map.stepTime();

        assertFalse(mason.isInsideBuilding());
        assertEquals(mason.getTarget(), quarry.getFlag().getPosition());
        assertTrue(quarry.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, quarry.getFlag().getPosition());

        assertFalse(quarry.getFlag().getStackedCargo().isEmpty());
        assertNull(mason.getCargo());

        /* The stonemason goes back to the quarry */
        assertEquals(mason.getTarget(), quarry.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, mason);

        assertTrue(mason.isInsideBuilding());
    }

    @Test
    public void testQuarryWithoutStoneProducesNothing() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        assertTrue(mason.isInsideBuilding());
        assertNull(mason.getCargo());

        /* Verify that no stone is available from the quarry or its flag */
        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertTrue(quarry.getStackedCargo().isEmpty());
            assertNull(mason.getCargo());
        }
    }

    @Test
    public void testStonemasonStaysAtHomeWhenNoStonesAreAvailable() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Construct the quarry */
        constructHouse(quarry, map);

        /* Assign a stonemason to the quarry */
        Stonemason mason = new Stonemason(player0, map);

        Utils.occupyBuilding(mason, quarry, map);

        assertTrue(mason.isInsideBuilding());

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(mason.isInsideBuilding());

        /* Verify that the stone mason hasn't understood that there are no  
           resources available */
        assertFalse(quarry.outOfNaturalResources());

        /* Step once to verify that the stonemason stays inside */
        map.stepTime();

        assertTrue(mason.isInsideBuilding());

        /* Verify that the quarry is out of natural resources */
        assertTrue(quarry.outOfNaturalResources());
    }

    @Test
    public void testStonemasonIgnoresStoneTooFarAway() {
        // TODO
    }

    @Test
    public void testStoneDisappearsAfterAllHasBeenRetrieved() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 15, 15);

        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        Point point1 = new Point(5, 5);

        Stone stone0 = map.placeStone(point1);

        for (int i = 0; i < 9; i++) {
            stone0.removeOnePart();
            map.stepTime();
            assertTrue(map.isStoneAtPoint(point1));
        }

        stone0.removeOnePart();

        map.stepTime();

        assertFalse(map.isStoneAtPoint(point1));
    }

    @Test
    public void testQuarryWithoutConnectedStorageKeepsProducing() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place stone */
        Point point3 = new Point(12, 8);
        Stone stone = map.placeStone(point3);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Let the stone mason rest */
        Utils.fastForward(100, map);

        /* Wait for the stone mason to produce a new stone cargo */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.getTarget().isAdjacent(stone.getPosition()));

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

        assertTrue(stonemason.getTarget().isAdjacent(stone.getPosition()));

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
    public void testCargosProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place stone */
        Point point3 = new Point(12, 8);
        Stone stone = map.placeStone(point3);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Let the stone mason rest */
        Utils.fastForward(100, map);

        /* Wait for the stone mason to go to the stone */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.getTarget().isAdjacent(stone.getPosition()));

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
        Cargo cargo = quarry0.getFlag().getStackedCargo().get(0);

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), quarry0.getFlag().getPosition());

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to reach the idle point of the road */
        assertFalse(courier.getTarget().equals(headquarter0.getFlag().getPosition()));
        assertFalse(courier.getTarget().equals(quarry0.getFlag().getPosition()));
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

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
        for (Point p : stonemason.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedQuarryIsRemovedAfterSomeTime() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Connect the quarry with the headquarter */
        map.placeAutoSelectedRoad(player0, quarry0.getFlag(), headquarter0.getFlag());

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Destroy the quarry */
        quarry0.tearDown();

        assertTrue(quarry0.burningDown());

        /* Wait for the quarry to stop burning */
        Utils.fastForward(50, map);

        assertTrue(quarry0.destroyed());

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Remove the flag and verify that the driveway is removed */
        assertNotNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));

        map.removeFlag(quarry0.getFlag());

        assertNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(8, 8);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Tear down the building and verify that the driveway is removed */
        assertNotNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));

        quarry0.tearDown();

        assertNull(map.getRoad(quarry0.getPosition(), quarry0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInQuarryCanBeStopped() throws Exception {

        /* Create game map */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point5 = new Point(10, 8);
        Stone stone = map.placeStone(point5);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the quarry */
        Utils.constructHouse(quarry0, map);

        /* Assign a worker to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Building hq = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point5 = new Point(10, 8);
        Stone stone = map.placeStone(point5);

        /* Place quarry */
        Point point1 = new Point(8, 6);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Connect the quarry and the headquarter */
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        /* Finish the quarry */
        Utils.constructHouse(quarry0, map);

        /* Assign a worker to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place quarry */
        Point point1 = new Point(20, 14);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Connect the quarry with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Wait for stonemason to get assigned and leave the headquarter */
        List<Stonemason> workers = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0, map);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        /* Verify that the player is set correctly in the worker */
        Stonemason worker = workers.get(0);

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);
        Player player2 = new Player("Player 2", RED);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 2's headquarter */
        Building headquarter2 = new Headquarter(player2);
        Point point10 = new Point(70, 70);
        map.placeBuilding(headquarter2, point10);

        /* Place player 0's headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Building headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place fortress for player 0 */
        Point point2 = new Point(21, 5);
        Building fortress0 = new Fortress(player0);
        map.placeBuilding(fortress0, point2);

        /* Finish construction of the fortress */
        Utils.constructHouse(fortress0, map);

        /* Occupy the fortress */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0, map);

        /* Place quarry close to the new border */
        Point point4 = new Point(28, 18);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point4);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Stonemason worker = Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Verify that the worker goes back to its own storage when the fortress
           is torn down */
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    // Add test for stones in/out of range

    @Test
    public void teststoneMasonReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing quarry */
        Point point2 = new Point(14, 4);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stone mason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0, map);

        Stonemason stoneMason = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Stonemason) {
                stoneMason = (Stonemason) w;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing quarry */
        Point point2 = new Point(14, 4);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stone mason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0, map);

        Stonemason stoneMason = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Stonemason) {
                stoneMason = (Stonemason) w;
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing first flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Placing quarry */
        Point point2 = new Point(14, 4);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point2.upLeft());

        /* Connect headquarter and first flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the first flag with the second flag */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, quarry0.getFlag());

        /* Wait for the stoneMason to be on the second road on its way to the flag */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0, map);

        Stonemason stoneMason = null;

        for (Worker w : map.getWorkers()) {
            if (w instanceof Stonemason) {
                stoneMason = (Stonemason) w;
            }
        }

        assertNotNull(stoneMason);
        assertEquals(stoneMason.getTarget(), quarry0.getPosition());

        /* Wait for the stone mason to reach the first flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, flag0.getPosition());

        map.stepTime();

        /* See that the stone mason has started walking */
        assertFalse(stoneMason.isExactlyAtPoint());

        /* Tear down the quarry */
        quarry0.tearDown();

        /* Verify that the stone mason continues walking to the next flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, stoneMason, quarry0.getFlag().getPosition());

        assertEquals(stoneMason.getPosition(), quarry0.getFlag().getPosition());

        /* Verify that the stone mason goes back to storage */
        assertEquals(stoneMason.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testStonemasonGoesOffroadBackToClosestStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(17, 17);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the quarry */
        Worker stonemason = quarry0.getWorker();

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getPosition(), quarry0.getPosition());

        quarry0.tearDown();

        /* Verify that the worker leaves the building and goes back to the headquarter */
        assertFalse(stonemason.isInsideBuilding());
        assertEquals(stonemason.getTarget(), storage0.getPosition());

        int amount = storage0.getAmount(STONEMASON);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, storage0.getPosition());

        /* Verify that the stonemason is stored correctly in the headquarter */
        assertEquals(storage0.getAmount(STONEMASON), amount + 1);
    }

    @Test
    public void testStonemasonReturnsOffroadAndAvoidsBurningStorageWhenQuarryIsDestroyed() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(17, 17);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(17, 17);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

        /* Finish construction of the storage */
        Utils.constructHouse(storage0, map);

        /* Destroy the storage */
        storage0.tearDown();

        /* Wait for the storage to burn down */
        Utils.waitForBuildingToBurnDown(storage0, map);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Placing quarry */
        Point point26 = new Point(17, 17);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Occupy the quarry */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        /* Place a second storage closer to the quarry */
        Point point2 = new Point(13, 13);
        Storage storage0 = map.placeBuilding(new Storage(player0), point2);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point25 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        /* Place quarry */
        Point point26 = new Point(17, 17);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point26);

        /* Place road to connect the headquarter and the quarry */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Wait for a worker to start walking to the building */
        Worker worker = Utils.waitForWorkersOutsideBuilding(Stonemason.class, 1, player0, map).get(0);

        /* Wait for the worker to get to the building's flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, quarry0.getFlag().getPosition());

        /* Tear down the building */
        quarry0.tearDown();

        /* Verify that the worker goes to the building and then returns to the
           headquarter instead of entering
        */
        assertEquals(worker.getTarget(), quarry0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, quarry0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoStonemasonsGettingLastStone() throws Exception {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Building headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place stone */
        Point point1 = new Point(8, 8);
        Stone stone = map.placeStone(point1);

        /* Place two quarries with the same distance to the stone */
        Point point2 = new Point(5, 9);
        Point point3 = new Point(9, 9);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point2);
        Quarry quarry1 = map.placeBuilding(new Quarry(player0), point3);

        /* Finish construction of the quarries */
        Utils.constructHouse(quarry0, map);
        Utils.constructHouse(quarry1, map);

        /* Occupy the quarries */
        Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);
        Utils.occupyBuilding(new Stonemason(player0, map), quarry1, map);

        /* Remove almost all of the stone so there is only a single piece left */
        Utils.removePiecesFromStoneUntil(stone, 1);

        /* Wait for the stone masons to go out and try to get the same piece of stone */
        Utils.waitForWorkersOutsideBuilding(Stonemason.class, 2, player0, map);

        Worker stonemason0 = quarry0.getWorker();
        Worker stonemason1 = quarry1.getWorker();

        assertTrue(stonemason0.getTarget().isAdjacent(stone.getPosition()));
        assertTrue(stonemason0.getTarget().isAdjacent(stone.getPosition()));

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
}
