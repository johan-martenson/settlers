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

import static org.appland.settlers.model.Material.PLANCK;
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
        for (Material m : Material.values()) {
            Cargo c = new Cargo(m, null);

            assertNotNull(c);

            assertEquals(c.getMaterial(), m);
        }
    }

    @Test
    public void testCreateRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Flag f1 = map.placeFlag(player0, new Point(6, 4));
        Flag f2 = map.placeFlag(player0, new Point(11, 5));

        map.placeAutoSelectedRoad(player0, f1, f2);

        List<Road> roads = map.getRoads();

        assertEquals(roads.size(), 2);

        Road r = map.getRoad(new Point(6, 4), new Point(11, 5));

        assertEquals(r.getStart().x, 6);
        assertEquals(r.getStart().y, 4);

        assertEquals(r.getEnd().x, 11);
        assertEquals(r.getEnd().y, 5);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutStartBuilding() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        Storage s = new Storage(player0);

        map.placeBuilding(s, new Point(4, 6));

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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place woodcutter */
        Woodcutter wc = map.placeBuilding(new Woodcutter(player0), new Point(4, 4));

        /* Verify that it' not possible to place a road without flags at both sides */
        map.placeAutoSelectedRoad(player0, new Flag(new Point(8, 6)), wc.getFlag());
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place woodcutter */
        Point wcPoint = new Point(4, 4);
        Woodcutter wc = map.placeBuilding(new Woodcutter(player0), wcPoint);

        /* Place middle flag */
        Point middleFlag = new Point(7, 7);
        map.placeFlag(player0, middleFlag);

        /* Place road from woodcutter to the middle flag */
        map.placeAutoSelectedRoad(player0, wc.getFlag().getPosition(), middleFlag);

        /* Place end flag */
        Point endFlag = new Point(10, 10);
        map.placeFlag(player0, endFlag);

        /* Place road from middle flag to end flag */
        map.placeAutoSelectedRoad(player0, middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        map.placeFlag(player0, new Point(3, 3));
        map.placeAutoSelectedRoad(player0, new Flag(new Point(3, 3)), new Flag(new Point(3, 3)));
    }

    @Test(expected = InvalidRouteException.class)
    public void testFindRouteWithSameStartAndEnd() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        map.findWayWithExistingRoads(new Point(1, 1), new Point(1, 1));
    }

    @Test
    public void testWorkerWalk() throws Exception {
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
        ForesterHut hut = new ForesterHut(player0);
        Point start = new Point(4, 18);
        Point f1 = new Point(8, 18);
        Point f2 = new Point(12, 18);
        Point f3 = new Point(16, 18);
        Point f4 = new Point(20, 18);
        Point f5 = new Point(10, 12);
        Point f6 = new Point(14, 12);
        Point f7 = new Point(10, 10);
        Point f8 = new Point(14, 10);
        Point f9 = new Point(16, 14);


        map.placeBuilding(new Headquarter(player0), new Point(7, 5));

        Flag startFlag = map.placeFlag(player0, start);

        map.placeFlag(player0, f1);
        map.placeFlag(player0, f2);
        map.placeFlag(player0, f3);
        map.placeFlag(player0, f4);

        map.placeRoad(player0, start, start.right(), f1);
        map.placeRoad(player0, f1, f1.right(), f2);
        map.placeRoad(player0, f2, f2.right(), f3);
        map.placeRoad(player0, f3, f3.right(), f4);

        Building wc = map.placeBuilding(hut, wcPoint);

        map.placeFlag(player0, f9);

        map.placeAutoSelectedRoad(player0, f2, f9);

        map.placeAutoSelectedRoad(player0, f9, wc.getFlag().getPosition());

        map.placeFlag(player0, f5);
        map.placeAutoSelectedRoad(player0, f1, f5);

        map.placeFlag(player0, f6);
        map.placeAutoSelectedRoad(player0, f5, f6);

        map.placeFlag(player0, f7);
        map.placeAutoSelectedRoad(player0, f1, f7);

        map.placeFlag(player0, f8);
        map.placeAutoSelectedRoad(player0, f7, f8);

        Utils.constructHouse(hut, map);

        Flag target = hut.getFlag();

        assertTrue(target.getPosition().equals(new Point(13, 15)));

        Forester forester = new Forester(player0, map);

        assertNotNull(forester);

        map.placeWorker(forester, startFlag);
        forester.setTargetBuilding(hut);

/*             [(2, 18), (4, 18), (6, 18), (8, 18), (10, 18), (11, 17), (12, 16), (13, 15), (14, 14), (12, 14), (11, 15), (10, 16)]*/
        assertTrue(forester.getPosition().equals(start));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f1);
        assertTrue(forester.getPosition().equals(f1));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f1.right());
        assertTrue(forester.getPosition().equals(f1.right()));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f2);
        assertTrue(forester.getPosition().equals(f2));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f9);
        assertTrue(forester.getPosition().equals(f9));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, hut.getPosition());
        assertTrue(forester.isExactlyAtPoint());
        assertTrue(forester.getPosition().equals(hut.getPosition()));
    }

    @Test(expected = Exception.class)
    public void testWorkerUnreachableTarget() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place first flag */
        Point away = new Point(6, 2);
        Flag end = map.placeFlag(player0, new Point(4, 2));

        /* Place second flag */
        Point start = new Point(2, 2);
        map.placeFlag(player0, start);
        Flag flag = map.placeFlag(player0, away);

        /* Place road */
        Point middle = new Point(3, 3);
        Road targetRoad = map.placeRoad(player0, start, middle, end.getPosition());

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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place quarry */
        Quarry qry = map.placeBuilding(new Quarry(player0), new Point(4, 4));

        /* Place storage */
        Storage storage = map.placeBuilding(new Storage(player0), new Point(8, 4));

        Flag target = storage.getFlag();
        Flag start = qry.getFlag();

        /* Connect the storage and the quarry */
        map.placeAutoSelectedRoad(player0, start, target);

        Courier worker = new Courier(player0, map);
        Stonemason mason = new Stonemason(player0, map);

        map.placeStone(qry.getFlag().getPosition().up().up());

        worker.setPosition(start.getPosition());

        Utils.constructHouse(qry, map);
        Utils.constructHouse(storage, map);

        assertTrue(qry.ready());

        Utils.occupyBuilding(mason, qry, map);

        /* Production starts, wait for it to finish */
        Utils.fastForward(100, map);

        assertFalse(mason.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, mason.getTarget());

        assertTrue(mason.isGettingStone());

        Utils.fastForward(50, map);

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, qry.getPosition());

        assertTrue(mason.isInsideBuilding());

        map.stepTime();

        assertEquals(mason.getTarget(), qry.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, mason, qry.getFlag().getPosition());

        assertFalse(qry.getFlag().getStackedCargo().isEmpty());

        Cargo c = qry.getFlag().getCargoWaitingForRoad(map.getRoad(storage.getFlag().getPosition(), qry.getFlag().getPosition()));
        assertTrue(c.getPosition().equals(qry.getFlag().getPosition()));
        assertEquals(c.getTarget(), storage);
        assertEquals(c.getMaterial(), STONE);
    }

    @Test
    public void testDeliverWithHandover() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);
        Storage storage   = new Headquarter(player0);
        Point hqPoint     = new Point(6, 4);
        Point middlePoint = new Point(10, 10); // hq to middle 6 steps
        Point endPoint    = new Point(10, 14); // end to middle 4 steps
        map.placeBuilding(storage, hqPoint);
        Courier mdlToEndCr = new Courier(player0, map);
        Courier hqToMdlCr  = new Courier(player0, map);

        Flag middleFlag = map.placeFlag(player0, middlePoint);
        Flag endFlag = map.placeFlag(player0, endPoint);

        Road hqToMiddleRoad = map.placeAutoSelectedRoad(player0, storage.getFlag().getPosition(), middlePoint);
        Road middleToEndRoad = map.placeRoad(player0, middlePoint, middlePoint.upRight(), middlePoint.upRight().upLeft(), endPoint.downLeft(), endPoint);

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

        Cargo c = new Cargo(WOOD, map);
        endFlag.putCargo(c);
        c.setTarget(storage);

        /* The courier detects the cargo on next tick */
        map.stepTime();

        assertEquals(mdlToEndCr.getTarget(), endFlag.getPosition());

        /* Let the courier walk to the cargo at end flag and picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, mdlToEndCr, endFlag.getPosition());

        assertEquals(mdlToEndCr.getCargo(), c);
        assertEquals(mdlToEndCr.getTarget(), middleFlag.getPosition());

        /* Let the courier walk to  */
        Utils.fastForwardUntilWorkerReachesPoint(map, mdlToEndCr, middlePoint);

        assertTrue(mdlToEndCr.getPosition().equals(middlePoint));

        /* Courier at middle point */
        assertFalse(mdlToEndCr.isIdle());
        assertNull(mdlToEndCr.getCargo());
        assertTrue(middleFlag.getStackedCargo().contains(c));
        assertEquals(middleFlag.getStackedCargo().size(), 1);
        assertTrue(middleFlag.getStackedCargo().get(0).equals(c));
        assertNull(hqToMdlCr.getCargo());
        assertTrue(middleFlag.hasCargoWaitingForRoad(hqToMiddleRoad));

        /* Next courier detects the cargo and walks there */
        map.stepTime();

        assertEquals(hqToMdlCr.getTarget(), middlePoint);

        Utils.fastForwardUntilWorkerReachesPoint(map, hqToMdlCr, middlePoint);

        /* Courier has picked up cargo */
        assertEquals(hqToMdlCr.getCargo(), c);

        Utils.fastForwardUntilWorkerReachesPoint(map, hqToMdlCr, hqPoint);

        assertTrue(hqToMdlCr.isAt(storage.getPosition()));
        assertNull(hqToMdlCr.getCargo());
        assertTrue(storage.isInStock(WOOD));
    }

    @Test
    public void testCourierIsAssignedToNewRoad() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);
        Storage storage = new Headquarter(player0);
        Point hqPoint = new Point(5, 5);
        Point middlePoint = new Point(11, 5);

        map.placeBuilding(storage, hqPoint);
        map.placeFlag(player0, middlePoint);

        Road hqToMiddleRoad = map.placeAutoSelectedRoad(player0, storage.getFlag().getPosition(), middlePoint);

        assertTrue(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        assertTrue(GameUtils.getClosestStorage(hqToMiddleRoad.getStart(), player0).equals(storage));

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        assertFalse(hqToMiddleRoad.needsCourier());

        /* Courier needs to walk to road before it's assigned */
        Courier c = null;
        for (Worker w : map.getWorkers()) {
            if (w instanceof Courier) {
                c = (Courier)w;
            }
        }

        Utils.fastForwardUntilWorkersReachTarget(map, c);

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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flags */
        Point point1 = new Point(3, 3);
        map.placeFlag(player0, point1);

        Point point2 = new Point(7, 3);
        map.placeFlag(player0, point2);

        /* Place road */
        Point middle = new Point(5, 3);
        Road r = map.placeRoad(player0, point1, middle, point2);

        assertTrue(r.needsCourier());
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
        Point hqSpot = new Point(7, 7);
        Headquarter hq = map.placeBuilding(new Headquarter(player0), hqSpot);

        /* Place barracks */
        Point bSpot = new Point(12, 12);
        Barracks b = map.placeBuilding(new Barracks(player0), bSpot);

        /* Place road */
        Road r = map.placeAutoSelectedRoad(player0, hq.getFlag(), b.getFlag());

        /* Occupy the road */
        Courier w = Utils.occupyRoad(r, map);

        /* Construct barracks */
        Utils.constructHouse(b, map);

        /* Add a private to the hq */
        Military m = new Military(player0, PRIVATE_RANK, map);
        hq.depositWorker(m);

        /* Check that the barracks needs a military */
        assertTrue(b.isMilitaryBuilding());

        int hostedMilitary = b.getNumberOfHostedMilitary();
        int maxHostedMilitary = b.getMaxHostedMilitary();

        assertEquals(hostedMilitary, 0);
        assertEquals(maxHostedMilitary, 2);

        /* Get military from the headquarter
         * - retrieve should set location of the worker
         */
        m = hq.retrieveAnyMilitary();

        /* Tell military to go to the barracks */
        map.placeWorker(m, b.getFlag());
        m.setTargetBuilding(b);

        assertEquals(m.getTargetBuilding(), b);
        assertEquals(m.getTarget(), b.getPosition());

        /* Verify that the military reaches the barracks */
        Utils.fastForwardUntilWorkerReachesPoint(map, m, b.getPosition());

        assertEquals(m.getPosition(), b.getPosition());
        assertTrue(m.isArrived());

        /* Verify that the military entered the barracks */
        assertEquals(b.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsNotOnFlag() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill at the other end of the road */
        Point end = new Point(7, 7);
        Building sm = map.placeBuilding(new Sawmill(player0), end.upLeft());

        /* Place start flag */
        Point start = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, start);

        /* Place middle flag */
        Point middle = new Point(6, 6);
        Road road0 = map.placeRoad(player0, start, middle, end);

        /* Assign a courier to the road */
        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, sm.getFlag());
        courier.assignToRoad(road0);

        /* Wait for the courier to rest at the middle of the road */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);

        /* Place cargo to be delivered to the sawmill at the start flag */
        Cargo cargo = new Cargo(WOOD, map);
        cargo.setPosition(start);
        cargo.setTarget(sm);

        flag0.putCargo(cargo);

        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(middle));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        /* Step time to get the courier to notice the cargo */
        map.stepTime();

        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTarget(), flag0.getPosition());

        /* Fast forward until the courier picks up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, start);

        assertTrue(courier.isAt(start));
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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a sawmill */
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

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
        cargo.setTarget(sm);

        flag0.putCargo(cargo);

        assertEquals(cargo.getTarget(), sm);
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

        assertEquals(cargo.getNextFlagOrBuilding(), sm.getFlag().getPosition());
        assertEquals(cargo.getTarget(), sm);
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
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point4 = new Point(11, 7);
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        /* Place flags */
        Point point0 = new Point(5, 5);
        Flag flag0 = map.placeFlag(player0, point0);
        Point point2 = new Point(7, 7);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place roads */
        Point point1 = new Point(6, 6);
        Point point3 = new Point(9, 7);
        Road road0 = map.placeRoad(player0, point0, point1, point2);
        Road road1 = map.placeRoad(player0, point2, point3, point4);

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
        cargo.setPosition(point0);
        cargo.setTarget(sm);

        flag0.putCargo(cargo);

        assertEquals(cargo.getTarget(), sm);
        assertEquals(cargo.getPosition(), point0);
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point1));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        /* Make the first courier detect the cargo */
        map.stepTime();

        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTarget(), flag0.getPosition());
        assertFalse(flag0.getStackedCargo().isEmpty());

        /* Let the courier reach and pick up the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, point0);

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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point4 = new Point(23, 15);
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        /* Place flags */
        Point point0 = new Point(19, 5);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sm.getFlag());

        /* Populate the roads */
        Courier courier = new Courier(player0, map);
        Courier courier2 = new Courier(player0, map);

        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        map.placeWorker(courier2, flag0);
        courier2.assignToRoad(road1);

        /* Empty the headquarter's store of plancks and stones */
        Utils.adjustInventoryTo(headquarter0, PLANCK, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Let the couriers reach their target road and become idle */
        Utils.fastForwardUntilWorkersReachTarget(map, courier, courier2);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANCK, map);
        headquarter0.getFlag().putCargo(cargo);

        /* Target the cargo to the sawmill */
        cargo.setTarget(sm);
        sm.promiseDelivery(PLANCK);

        /* Promise plancks to the sawmill until it doesn't need any new deliveries of plancks */
        sm.promiseDelivery(PLANCK);

        assertFalse(sm.needsMaterial(PLANCK));

        /* Wait for the first courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        assertEquals(courier.getTarget(), flag0.getPosition());
        assertFalse(flag0.getStackedCargo().contains(cargo));

        /* Remove the second road */
        map.removeRoad(road1);
        assertFalse(map.areFlagsOrBuildingsConnectedViaRoads(flag0, sm));

        /* Verify that the cargo is placed at the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the cargo is picked up and about to be returned to the headquarter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        assertEquals(cargo.getTarget(), headquarter0);

        /* Verify that the cargo isn't promised to the sawmill any longer */
        assertTrue(sm.needsMaterial(PLANCK));
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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place catapult */
        Point point4 = new Point(23, 15);
        Building catapult = map.placeBuilding(new Catapult(player0), point4.upLeft());

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

        /* Empty the headquarter's store of plancks and stones */
        Utils.adjustInventoryTo(headquarter0, PLANCK, 0, map);
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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point4 = new Point(23, 15);
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        /* Place flags */
        Point point0 = new Point(19, 5);
        Flag flag0 = map.placeFlag(player0, point0);
        Point point1 = new Point(4, 4);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, sm.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag1);
        Road road3 = map.placeAutoSelectedRoad(player0, flag1, sm.getFlag());

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

        /* Remove all plancks, wood and stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANCK, 0, map);
        Utils.adjustInventoryTo(headquarter0, WOOD, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Give the couriers time to get to the middle of their roads */
        Utils.fastForward(100, map);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANCK, map);
        headquarter0.getFlag().putCargo(cargo);

        /* Target the cargo to the sawmill */
        cargo.setTarget(sm);
        sm.promiseDelivery(PLANCK);

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
        assertEquals(courier4.getTarget(), sm.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier4, sm.getPosition());

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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place sawmill */
        Point point4 = new Point(20, 18);
        Building sm = map.placeBuilding(new Sawmill(player0), point4);

        /* Place flags */
        Point point0 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point0);
        Point point2 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point2);

        /* Place roads */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);
        Road road2 = map.placeAutoSelectedRoad(player0, flag1, sm.getFlag());

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

        /* Remove all plancks and stones in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANCK, 0, map);
        Utils.adjustInventoryTo(headquarter0, STONE, 0, map);

        /* Place a cargo on the headquarter's flag */
        Cargo cargo = new Cargo(PLANCK, map);
        headquarter0.getFlag().putCargo(cargo);
        cargo.setTarget(sm);
        sm.promiseDelivery(PLANCK);

        /* Wait for the first courier to pick up the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Verify that the cargo will go via the second flag */
        assertEquals(cargo.getNextFlagOrBuilding(), flag0.getPosition());

        /* Add a shortcut from the second flag to the sawmill */
        Road road3 = map.placeAutoSelectedRoad(player0, flag0, sm.getFlag());

        /* Populate the shortcut */
        Courier courier4 = new Courier(player0, map);

        map.placeWorker(courier4, flag0);
        courier4.assignToRoad(road3);

        /* Wait for the first courier to reach the second flag */
        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        /* Verify that the cargo takes the new, shorter road instead of the old road */
        assertEquals(cargo.getNextFlagOrBuilding(), sm.getFlag().getPosition());

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier4, cargo);

        /* Verify that the third courier delivers the cargo to the sawmill */
        assertEquals(courier4.getTarget(), sm.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier4, sm.getPosition());

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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flag */
        Point point0 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point0);

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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place flag */
        Point point0 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point0);

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
        Point hqPoint = new Point(14, 16);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place first flag */
        Point point0 = new Point(19, 15);
        Flag flag0 = map.placeFlag(player0, point0);

        /* Place second flag */
        Point point1 = new Point(23, 15);
        Flag flag1 = map.placeFlag(player0, point1);

        /* Place roads */
        Point point2 = new Point(22, 14);
        Point point3 = new Point(20, 14);
        Point point4 = new Point(19, 13);
        Point point5 = new Point(17, 13);
        Point point6 = new Point(16, 14);
        Road road0 = map.placeRoad(player0, point1, point2, point3, point4, point5, point6, headquarter0.getFlag().getPosition());
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

}
