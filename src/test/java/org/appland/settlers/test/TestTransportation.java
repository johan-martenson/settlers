package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Catapult;
import org.appland.settlers.model.CatapultWorker;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestTransportation {

    @Test
    public void testCreateCargo() {
        for (Material military : Material.values()) {
            Cargo cargo = new Cargo(military, null);

            assertNotNull(cargo);

            assertEquals(cargo.getMaterial(), military);
        }
    }

    @Test
    public void testCreateRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Flag flag0 = map.placeFlag(player0, new Point(6, 4));

        /* Place flag */
        Flag flag1 = map.placeFlag(player0, new Point(11, 5));

        /* Place road */
        map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Verify that the roads are created */
        List<Road> roads = map.getRoads();

        assertEquals(roads.size(), 2);

        Road road = map.getRoad(new Point(6, 4), new Point(11, 5));

        assertEquals(road.getStart().x, 6);
        assertEquals(road.getStart().y, 4);

        assertEquals(road.getEnd().x, 11);
        assertEquals(road.getEnd().y, 5);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutStartBuilding() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place storage */
        Storage storage0 = new Storage(player0);
        map.placeBuilding(storage0, new Point(4, 6));

        /* Place road */
        map.placeAutoSelectedRoad(player0, new Flag(new Point(5, 7)), new Flag(new Point(4, 6)));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutEndBuilding() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), new Point(4, 4));

        /* Verify that it' not possible to place a road without flags at both sides */
        map.placeAutoSelectedRoad(player0, new Flag(new Point(8, 6)), woodcutter.getFlag());
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place road */
        map.placeAutoSelectedRoad(player0, new Flag(new Point(1, 1)), new Flag(new Point(3, 5)));
    }

    @Test
    public void testCreateTwoChainedRoads() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(4, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place middle flag */
        Point middleFlag = new Point(7, 7);
        map.placeFlag(player0, middleFlag);

        /* Place road from woodcutter to the middle flag */
        map.placeAutoSelectedRoad(player0, woodcutter.getFlag().getPosition(), middleFlag);

        /* Place end flag */
        Point endFlag = new Point(10, 10);
        map.placeFlag(player0, endFlag);

        /* Place road from middle flag to end flag */
        map.placeAutoSelectedRoad(player0, middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        map.placeFlag(player0, new Point(3, 3));

        /* Place road */
        map.placeAutoSelectedRoad(player0, new Flag(new Point(3, 3)), new Flag(new Point(3, 3)));
    }

    @Test(expected = InvalidRouteException.class)
    public void testFindRouteWithSameStartAndEnd() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that there is no road with the same start and end points */
        map.findWayWithExistingRoads(new Point(1, 1), new Point(1, 1));
    }

    @Test
    public void testWorkerWalk() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /*
         * F--F1--F2--F3--F4
         *    |    |
         *    |    |---F9--Target building
         *    |
         *    |
         *    |---F5---F6
         *    |
         *    |---F7---F8
         */
        Point wcPoint = new Point(12, 16);
        Point start = new Point(4, 18);
        Point point1 = new Point(8, 18);
        Point point2 = new Point(12, 18);
        Point point3 = new Point(16, 18);
        Point point4 = new Point(20, 18);
        Point point5 = new Point(10, 12);
        Point point6 = new Point(14, 12);
        Point point7 = new Point(10, 10);
        Point point8 = new Point(14, 10);
        Point point9 = new Point(16, 14);

        /* Place headquarter */
        map.placeBuilding(new Headquarter(player0), new Point(7, 5));

        /* Place start flag */
        Flag startFlag = map.placeFlag(player0, start);

        /* Place flags */
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);
        map.placeFlag(player0, point3);
        map.placeFlag(player0, point4);

        /* Place roads */
        map.placeRoad(player0, start, start.right(), point1);
        map.placeRoad(player0, point1, point1.right(), point2);
        map.placeRoad(player0, point2, point2.right(), point3);
        map.placeRoad(player0, point3, point3.right(), point4);

        /* Place forester hut */
        ForesterHut foresterHut = new ForesterHut(player0);
        Building woodcutter = map.placeBuilding(foresterHut, wcPoint);

        /* Place flag */
        map.placeFlag(player0, point9);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point2, point9);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point9, woodcutter.getFlag().getPosition());

        /* Place flag */
        map.placeFlag(player0, point5);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point1, point5);

        /* Place flag */
        map.placeFlag(player0, point6);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point5, point6);

        /* Place flag */
        map.placeFlag(player0, point7);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point1, point7);

        /* Place flag */
        map.placeFlag(player0, point8);

        /* Place road */
        map.placeAutoSelectedRoad(player0, point7, point8);

        /* Finish construction of the forester hut */
        Utils.constructHouse(foresterHut, map);

        Flag target = foresterHut.getFlag();

        assertEquals(target.getPosition(), new Point(13, 15));

        /* Place a forester on the map and assign it to the forester hut */
        Forester forester = new Forester(player0, map);

        assertNotNull(forester);

        map.placeWorker(forester, startFlag);
        forester.setTargetBuilding(foresterHut);

/*             [(2, 18), (4, 18), (6, 18), (8, 18), (10, 18), (11, 17), (12, 16), (13, 15), (14, 14), (12, 14), (11, 15), (10, 16)] */
        assertEquals(forester.getPosition(), start);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, point1);
        assertEquals(forester.getPosition(), point1);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, point1.right());
        assertEquals(forester.getPosition(), point1.right());

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, point2);
        assertEquals(forester.getPosition(), point2);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, point9);
        assertEquals(forester.getPosition(), point9);

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, foresterHut.getPosition());
        assertTrue(forester.isExactlyAtPoint());
        assertEquals(forester.getPosition(), foresterHut.getPosition());
    }

    @Test(expected = Exception.class)
    public void testWorkerUnreachableTarget() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(6, 2);
        Flag end = map.placeFlag(player0, new Point(4, 2));

        /* Place second flag */
        Point point2 = new Point(2, 2);
        map.placeFlag(player0, point2);
        Flag flag = map.placeFlag(player0, point1);

        /* Place road */
        Point point3 = new Point(3, 3);
        Road targetRoad = map.placeRoad(player0, point2, point3, end.getPosition());

        /* Place a courier */
        Courier worker = new Courier(player0, map);

        map.placeWorker(worker, flag);

        worker.assignToRoad(targetRoad);
    }

    @Test
    public void testProduceThenDeliverToStorage() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), new Point(4, 4));

        /* Place storage */
        Storage storage = map.placeBuilding(new Storage(player0), new Point(8, 4));

        Flag target = storage.getFlag();
        Flag start = quarry0.getFlag();

        /* Connect the storage and the quarry */
        map.placeAutoSelectedRoad(player0, start, target);

        Courier worker = new Courier(player0, map);
        Stonemason mason = new Stonemason(player0, map);

        /* Place a stone */
        map.placeStone(quarry0.getFlag().getPosition().up().up());

        worker.setPosition(start.getPosition());

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Finish construction of the storage */
        Utils.constructHouse(storage, map);

        assertTrue(quarry0.ready());

        /* Occupy the quarry */
        Utils.occupyBuilding(mason, quarry0, map);

        /* Production starts, wait for it to finish */
        Utils.fastForward(100, map);

        assertFalse(mason.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, mason.getTarget());

        assertTrue(mason.isGettingStone());

        Utils.fastForward(50, map);

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, quarry0.getPosition());

        assertTrue(mason.isInsideBuilding());

        map.stepTime();

        assertEquals(mason.getTarget(), quarry0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, quarry0.getFlag().getPosition());

        assertFalse(quarry0.getFlag().getStackedCargo().isEmpty());

        Cargo cargo = quarry0.getFlag().getCargoWaitingForRoad(map.getRoad(storage.getFlag().getPosition(), quarry0.getFlag().getPosition()));
        assertEquals(cargo.getPosition(), quarry0.getFlag().getPosition());
        assertEquals(cargo.getTarget(), storage);
        assertEquals(cargo.getMaterial(), STONE);
    }

    @Test
    public void testDeliverWithHandover() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(6, 4);
        Storage storage = new Headquarter(player0);
        map.placeBuilding(storage, point0);

        /* Place flag */
        Point point1 = new Point(10, 10); // headquarter to middle 6 steps
        Flag middleFlag = map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(10, 14); // end to middle 4 steps
        Flag endFlag = map.placeFlag(player0, point2);

        /* Place road */
        Road hqToMiddleRoad = map.placeAutoSelectedRoad(player0, storage.getFlag().getPosition(), point1);

        /* Place road */
        Road middleToEndRoad = map.placeRoad(player0, point1, point1.upRight(), point1.upRight().upLeft(), point2.downLeft(), point2);

        /* Assign couriers to the roads */
        Courier mdlToEndCr = new Courier(player0, map);
        Courier hqToMdlCr  = new Courier(player0, map);
        map.placeWorker(hqToMdlCr, middleFlag);
        map.placeWorker(mdlToEndCr, endFlag);

        hqToMdlCr.assignToRoad(hqToMiddleRoad);
        mdlToEndCr.assignToRoad(middleToEndRoad);

        /* Let couriers walk to the middle of their roads and become idle */
        for (int i = 0; i < 500; i++) {
            if (hqToMdlCr.isIdle() && mdlToEndCr.isIdle()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(mdlToEndCr.isArrived());
        assertFalse(mdlToEndCr.isTraveling());
        assertNull(mdlToEndCr.getCargo());

        Cargo cargo = new Cargo(WOOD, map);
        endFlag.putCargo(cargo);
        cargo.setTarget(storage);

        /* The courier detects the cargo on next tick */
        map.stepTime();

        assertEquals(mdlToEndCr.getTarget(), endFlag.getPosition());

        /* Let the courier walk to the cargo at end flag and picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, mdlToEndCr, endFlag.getPosition());

        assertEquals(mdlToEndCr.getCargo(), cargo);
        assertEquals(mdlToEndCr.getTarget(), middleFlag.getPosition());

        /* Let the courier walk to  */
        Utils.fastForwardUntilWorkerReachesPoint(map, mdlToEndCr, point1);

        assertEquals(mdlToEndCr.getPosition(), point1);

        /* Courier at middle point */
        assertFalse(mdlToEndCr.isIdle());
        assertNull(mdlToEndCr.getCargo());
        assertTrue(middleFlag.getStackedCargo().contains(cargo));
        assertEquals(middleFlag.getStackedCargo().size(), 1);
        assertEquals(middleFlag.getStackedCargo().get(0), cargo);
        assertNull(hqToMdlCr.getCargo());
        assertTrue(middleFlag.hasCargoWaitingForRoad(hqToMiddleRoad));

        /* Next courier detects the cargo and walks there */
        map.stepTime();

        assertEquals(hqToMdlCr.getTarget(), point1);

        Utils.fastForwardUntilWorkerReachesPoint(map, hqToMdlCr, point1);

        /* Courier has picked up cargo */
        assertEquals(hqToMdlCr.getCargo(), cargo);

        Utils.fastForwardUntilWorkerReachesPoint(map, hqToMdlCr, point0);

        assertTrue(hqToMdlCr.isAt(storage.getPosition()));
        assertNull(hqToMdlCr.getCargo());
        assertTrue(storage.isInStock(WOOD));
    }

    @Test
    public void testCourierIsAssignedToNewRoad() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Storage storage = new Headquarter(player0);
        map.placeBuilding(storage, point0);

        /* Place flag */
        Point point1 = new Point(11, 5);
        map.placeFlag(player0, point1);

        /* Place road */
        Road hqToMiddleRoad = map.placeAutoSelectedRoad(player0, storage.getFlag().getPosition(), point1);

        assertTrue(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        assertEquals(GameUtils.getClosestStorage(hqToMiddleRoad.getStart(), player0), storage);

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        assertFalse(hqToMiddleRoad.needsCourier());

        /* Courier needs to walk to road before it's assigned */
        Courier courier = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Courier) {
                courier = (Courier)worker;
            }
        }

        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        assertFalse(hqToMiddleRoad.needsCourier());
        assertNotNull(hqToMiddleRoad.getCourier());
    }

    @Test
    public void testEmptyRoadNeedsCourier() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        /* Place flag */
        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point2);

        /* Place road */
        Point point3 = new Point(5, 3);
        Road road = map.placeRoad(player0, point1, point3, point2);

        assertTrue(road.needsCourier());
    }

    @Test
    public void testMilitaryTransportation() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(7, 7);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place barracks */
        Point point1 = new Point(12, 12);
        Barracks barracks0 = map.placeBuilding(new Barracks(player0), point1);

        /* Place road */
        Road road = map.placeAutoSelectedRoad(player0, headquarter.getFlag(), barracks0.getFlag());

        /* Occupy the road */
        Courier courier = Utils.occupyRoad(road, map);

        /* Construct barracks */
        Utils.constructHouse(barracks0, map);

        /* Add a private to the headquarter */
        Military military = new Military(player0, PRIVATE_RANK, map);
        headquarter.depositWorker(military);

        /* Check that the barracks needs a military */
        assertTrue(barracks0.isMilitaryBuilding());

        int hostedMilitary = barracks0.getNumberOfHostedMilitary();
        int maxHostedMilitary = barracks0.getMaxHostedMilitary();

        assertEquals(hostedMilitary, 0);
        assertEquals(maxHostedMilitary, 2);

        /* Get military from the headquarter
         * - retrieve should set location of the worker
         */
        military = headquarter.retrieveAnyMilitary();

        /* Tell military to go to the barracks */
        map.placeWorker(military, barracks0.getFlag());
        military.setTargetBuilding(barracks0);

        assertEquals(military.getTargetBuilding(), barracks0);
        assertEquals(military.getTarget(), barracks0.getPosition());

        /* Verify that the military reaches the barracks */
        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertEquals(military.getPosition(), barracks0.getPosition());
        assertTrue(military.isArrived());

        /* Verify that the military entered the barracks */
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsNotOnFlag() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill at the other end of the road */
        Point point1 = new Point(7, 7);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1.upLeft());

        /* Place start flag */
        Point point2 = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place middle flag */
        Point point3 = new Point(6, 6);
        Road road0 = map.placeRoad(player0, point2, point3, point1);

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, sawmill.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to rest at the middle of the road */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        /* Place cargo to be delivered to the sawmill at the start flag */
        Cargo cargo = new Cargo(WOOD, map);
        cargo.setPosition(point2);
        cargo.setTarget(sawmill);

        flag0.putCargo(cargo);

        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point3));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        /* Step time to get the courier to notice the cargo */
        map.stepTime();

        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTarget(), flag0.getPosition());

        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point2);

        assertTrue(courier.isAt(point2));
        assertEquals(courier.getCargo(), cargo);
    }

    @Test
    public void testCargoTargetRemainsWhenItIsPutDownAtFlag() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create the game map */
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        Point point3 = new Point(9, 7);
        Point point4 = new Point(11, 7);

        /* Place the headquarter */
        Point point5 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point5);

        /* Place a sawmill */
        Building sawmill = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        /* Place flags */
        Flag flag0 = map.placeFlag(player0, point0);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road from the first flag to the second flag */
        Road road0 = map.placeRoad(player0, point0, point1, point2);

        /* Place road from the second flag to the sawmill's flag */
        Road road1 = map.placeRoad(player0, point2, point3, point4);

        Courier courier = new Courier(player0, map);
        Courier secondCourier = new Courier(player0, map);

        map.placeWorker(courier, flag0);
        courier.assignToRoad(road0);

        map.placeWorker(secondCourier, flag1);
        secondCourier.assignToRoad(road1);

        /* Let the couriers reach their roads and get assigned */
        Utils.fastForwardUntilWorkersReachTarget(map, courier, secondCourier);

        Cargo cargo = new Cargo(WOOD, map);
        cargo.setPosition(point0);
        cargo.setTarget(sawmill);

        flag0.putCargo(cargo);

        assertEquals(cargo.getTarget(), sawmill);
        assertEquals(cargo.getPosition(), point0);
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point1));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        /* Make courier detect cargo */
        map.stepTime();

        assertNull(courier.getCargo());
        assertEquals(courier.getTarget(), point0);
        assertTrue(cargo.isDeliveryPromised());

        /* Let the courier reach the cargo and pick it up */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point0);

        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTarget(), flag1.getPosition());
        assertTrue(flag1.getStackedCargo().isEmpty());
        assertEquals(courier.getTarget(), point2);
        assertFalse(cargo.isDeliveryPromised());

        /* Fast forward until the courier reaches the other flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point2);

        assertTrue(courier.isAt(point2));
        assertNull(courier.getCargo());

        assertEquals(cargo.getNextFlagOrBuilding(), sawmill.getFlag().getPosition());
        assertEquals(cargo.getTarget(), sawmill);
        assertEquals(cargo.getPosition(), point2);
        assertEquals(flag1.getStackedCargo().get(0), cargo);
        assertFalse(flag1.getStackedCargo().isEmpty());
    }

    @Test
    public void testCargoDeliveryPromiseIsCleared() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(11, 7);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1.upLeft());

        /* Place flags */
        Point point2 = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, point2);
        Point point3 = new Point(7, 7);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place roads */
        Point point4 = new Point(6, 6);
        Point point5 = new Point(9, 7);
        Road road0 = map.placeRoad(player0, point2, point4, point3);
        Road road1 = map.placeRoad(player0, point3, point5, point1);

        /* Populate the roads */
        Courier courier = new Courier(player0, map);
        Courier secondCourier = new Courier(player0, map);

        map.placeWorker(courier, flag1);
        courier.assignToRoad(road0);

        map.placeWorker(secondCourier, flag1);
        secondCourier.assignToRoad(road1);

        /* Let the couriers reach their target road and become idle */
        Utils.fastForwardUntilWorkersReachTarget(map, courier, secondCourier);

        /* Place a cargo on the first flag */
        Cargo cargo = new Cargo(WOOD, map);
        cargo.setPosition(point2);
        cargo.setTarget(sawmill);

        flag0.putCargo(cargo);

        assertEquals(cargo.getTarget(), sawmill);
        assertEquals(cargo.getPosition(), point2);
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point4));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        /* Make the first courier detect the cargo */
        map.stepTime();

        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTarget(), flag0.getPosition());
        assertFalse(flag0.getStackedCargo().isEmpty());

        /* Let the courier reach and pick up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point2);

        assertEquals(courier.getCargo(), cargo);
        assertTrue(flag0.getStackedCargo().isEmpty());
        assertFalse(cargo.isDeliveryPromised());
    }

    @Test
    public void testCargoIsReturnedToStorageIfItCannotBeDelivered() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(23, 15);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1.upLeft());

        /* Place flags */
        Point point2 = new Point(19, 5);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill.getFlag());

        /* Populate the roads */
        Courier courier = new Courier(player0, map);
        Courier courier2 = new Courier(player0, map);

        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        map.placeWorker(courier2, flag0);
        courier2.assignToRoad(road1);

        /* Empty the headquarter's store of planks and stones */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Let the couriers reach their target road and become idle */
        Utils.fastForwardUntilWorkersReachTarget(map, courier, courier2);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANK, map);
        headquarter0.getFlag().putCargo(cargo);

        /* Target the cargo to the sawmill */
        cargo.setTarget(sawmill);
        sawmill.promiseDelivery(PLANK);

        /* Promise planks to the sawmill until it doesn't need any new deliveries of planks */
        sawmill.promiseDelivery(PLANK);

        assertFalse(sawmill.needsMaterial(PLANK));

        /* Wait for the first courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        assertEquals(courier.getTarget(), flag0.getPosition());
        assertFalse(flag0.getStackedCargo().contains(cargo));

        /* Remove the second road */
        map.removeRoad(road1);
        assertFalse(map.areFlagsOrBuildingsConnectedViaRoads(flag0, sawmill));

        /* Verify that the cargo is placed at the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the cargo is picked up and about to be returned to the headquarter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        assertEquals(cargo.getTarget(), headquarter0);

        /* Verify that the cargo isn't promised to the sawmill any longer */
        assertTrue(sawmill.needsMaterial(PLANK));
    }

    @Test
    public void testCargoIsReturnedToStorageIfItCannotBeDeliveredWhenTargetBuildingIsBurning() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place catapult */
        Point point1 = new Point(23, 15);
        Building catapult = map.placeBuilding(new Catapult(player0), point1.upLeft());

        /* Finish construction of the catapult */
        Utils.constructHouse(catapult, map);

        /* Occupy the catapult */
        Utils.occupyBuilding(new CatapultWorker(player0, map), catapult, map);

        /* Place road to connect the catapult with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), catapult.getFlag());

        /* Populate the road */
        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        /* Empty the headquarter's store of planks and stones */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Let the courier reach its target road and become idle */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(STONE, map);
        headquarter0.getFlag().putCargo(cargo);

        /* Target the cargo to the catapult */
        cargo.setTarget(catapult);
        catapult.promiseDelivery(STONE);

        /* Wait for the courier to pick up the stone */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, STONE);

        /* Let the courier start walking */
        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());

        /* Tear down the catapult */
        catapult.tearDown();

        assertTrue(catapult.burningDown());
        assertFalse(catapult.ready());
        assertEquals(courier.getCargo().getTarget(), catapult);
        assertFalse(courier.getCargo().getTarget().ready());

        /* Verify that the courier walks to the catapult's flag and then returns with the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, catapult.getFlag().getPosition());

        assertEquals(courier.getTarget(), headquarter0.getPosition());
        assertNotNull(courier.getCargo());

        /* Wait for the first courier to go back to the headquarter */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        /* Verify that the courier has put the stone back on the flag */
        assertNull(courier.getCargo());
    }

    @Test
    public void testCargoIsDeliveredViaNewRouteIfRoadIsRemoved() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(23, 15);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1.upLeft());

        /* Place flags */
        Point point2 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point2);
        Point point3 = new Point(4, 4);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag1);
        Road road3 = map.placeAutoSelectedRoad(player0, flag1, sawmill.getFlag());

        /* Populate the roads */
        Courier courier = new Courier(player0, map);
        Courier courier2 = new Courier(player0, map);
        Courier courier3 = new Courier(player0, map);
        Courier courier4 = new Courier(player0, map);

        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        map.placeWorker(courier2, flag0);
        courier2.assignToRoad(road1);

        map.placeWorker(courier3, headquarter0.getFlag());
        courier3.assignToRoad(road2);

        map.placeWorker(courier4, flag1);
        courier4.assignToRoad(road3);

        /* Remove all planks, wood and stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0, map);
        Utils.adjustInventoryTo(headquarter0, WOOD, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Give the couriers time to get to the middle of their roads */
        Utils.fastForward(100, map);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANK, map);
        headquarter0.getFlag().putCargo(cargo);

        /* Target the cargo to the sawmill */
        cargo.setTarget(sawmill);
        sawmill.promiseDelivery(PLANK);

        /* Verify that the cargo is planned to go via first flag */
        assertEquals(cargo.getNextFlagOrBuilding(), flag0.getPosition());

        /* Wait for the first courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Remove the second road */
        map.removeRoad(road1);

        /* Wait for the courier to place the cargo at the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the first courier picks up the cargo again */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Wait for the courier to leave the cargo at the headquarter's flag */
        assertEquals(courier.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        assertTrue(headquarter0.getFlag().getStackedCargo().contains(cargo));
        assertNull(courier.getCargo());

        /* Verify that the third courier picks up the cargo */
        assertEquals(courier3.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier3, cargo);

        /* Verify that the third courier delivers the cargo to the flag */
        assertEquals(courier3.getTarget(), flag1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier3, flag1.getPosition());

        assertNull(courier3.getCargo());

        /* Wait for the fourth courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier4, cargo);

        /* Verify that the fourth courier delivers the cargo to the sawmill */
        assertEquals(courier4.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier4, sawmill.getPosition());

        assertNull(courier4.getCargo());
    }

    @Test
    public void testCargoTakesBetterRouteIfNewRoadIsAdded() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point1 = new Point(20, 18);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point1);

        /* Place flags */
        Point point2 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point2);
        Point point3 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point3);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, sawmill.getFlag());

        /* Populate the roads */
        Courier courier = new Courier(player0, map);
        Courier courier2 = new Courier(player0, map);
        Courier courier3 = new Courier(player0, map);

        map.placeWorker(courier, flag0);
        courier.assignToRoad(road0);

        map.placeWorker(courier2, flag1);
        courier2.assignToRoad(road1);

        map.placeWorker(courier3, flag1);
        courier3.assignToRoad(road2);

        /* Remove all planks and stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANK, map);
        headquarter0.getFlag().putCargo(cargo);
        cargo.setTarget(sawmill);
        sawmill.promiseDelivery(PLANK);

        /* Wait for the first courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Verify that the cargo will go via the second flag */
        assertEquals(cargo.getNextFlagOrBuilding(), flag0.getPosition());

        /* Add a shortcut from the second flag to the sawmill */
        Road road3 = map.placeAutoSelectedRoad(player0, flag0, sawmill.getFlag());

        /* Populate the shortcut */
        Courier courier4 = new Courier(player0, map);

        map.placeWorker(courier4, flag0);
        courier4.assignToRoad(road3);

        /* Wait for the first courier to reach the second flag */
        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the cargo takes the new, shorter road instead of the old road */
        assertEquals(cargo.getNextFlagOrBuilding(), sawmill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier4, cargo);

        /* Verify that the third courier delivers the cargo to the sawmill */
        assertEquals(courier4.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier4, sawmill.getPosition());

        assertNull(courier4.getCargo());
    }

    @Test
    public void testFindDirectWayBetweenTwoFlagsWithNoStepsInBetween() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Verify that there is a road between the two flags */
        List<Point> path = map.findWayWithExistingRoadsInFlagsAndBuildings(headquarter0.getFlag(), flag0);
        assertNotNull(path);
        assertEquals(path.size(), 2);
        assertEquals(path.get(0), headquarter0.getFlag().getPosition());
        assertEquals(path.get(1), flag0.getPosition());
    }

    @Test
    public void testFindNoWayBetweenTwoFlagsWithNoStepsInBetween() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Verify that there is a road between the two flags */
        List<Point> path = map.findWayWithExistingRoadsInFlagsAndBuildings(headquarter0.getFlag(), flag0);
        assertNull(path);
    }

    @Test
    public void testFindShortestWayBetweenTwoFlagsWithNoStepsInBetween() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place first flag */
        Point point1 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place roads */
        Point point3 = new Point(22, 14);
        Point point4 = new Point(20, 14);
        Point point5 = new Point(19, 13);
        Point point6 = new Point(17, 13);
        Point point7 = new Point(16, 14);
        Road road0 = map.placeRoad(player0, point2, point3, point4, point5, point6, point7, headquarter0.getFlag().getPosition());
        Road road1 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road2 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Verify that the short road is chosen */
        List<Point> path = map.findWayWithExistingRoadsInFlagsAndBuildings(headquarter0.getFlag(), flag1);

        assertNotNull(path);
        assertEquals(path.size(), 3);
        assertEquals(path.get(0), headquarter0.getFlag().getPosition());
        assertEquals(path.get(1), flag0.getPosition());
        assertEquals(path.get(2), flag1.getPosition());
    }

    @Test
    public void testUtilBuildingsAreConnected() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(19, 15);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place second woodcutter */
        Point point2 = new Point(23, 15);
        Woodcutter woodcutter1 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the woodcutters */
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), woodcutter1.getFlag());

        /* Verify that the roads are connected according to the util function */
        assertTrue(map.areFlagsOrBuildingsConnectedViaRoads(woodcutter0, woodcutter1));
    }

    @Test
    public void testUtilBuildingsAreNotConnected() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(19, 15);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place second woodcutter */
        Point point2 = new Point(23, 15);
        Woodcutter woodcutter1 = map.placeBuilding(new Woodcutter(player0), point2);

        /* Verify that the woodcutters are not connected according to the util function */
        assertFalse(map.areFlagsOrBuildingsConnectedViaRoads(woodcutter0, woodcutter1));
    }

    @Test
    public void testUtilFlagsAreNotConnected() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Verify that the flags are not connected according to the util function */
        assertFalse(map.areFlagsOrBuildingsConnectedViaRoads(flag0, flag1));
    }

    @Test
    public void testUtilFlagsAreConnected() throws Exception {

        /* Create players */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 50, 50);

        /* Place headquarter */
        Point point0 = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place second flag */
        Point point2 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place road to connect the flags */
        map.placeAutoSelectedRoad(player0, flag0, flag1);

        /* Verify that the flags are connected according to the util function */
        assertTrue(map.areFlagsOrBuildingsConnectedViaRoads(flag0, flag1));
    }
}
