package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Military;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    public void testCreateRoad() throws InvalidEndPointException, Exception {
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
    public void testCreateRoadWithoutStartBuilding() throws InvalidEndPointException, Exception {
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
    public void testCreateRoadWithoutEndBuilding() throws InvalidEndPointException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Woodcutter wc = new Woodcutter(player0);

        map.placeBuilding(wc, new Point(3, 3));

        map.placeAutoSelectedRoad(player0, new Flag(new Point(8, 6)), new Flag(new Point(3, 3)));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws InvalidEndPointException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        map.placeAutoSelectedRoad(player0, new Flag(new Point(1, 1)), new Flag(new Point(3, 5)));
    }

    @Test
    public void testCreateTwoChainedRoads() throws InvalidEndPointException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point middleFlag = new Point(7, 7);
        Point endFlag = new Point(10, 10);
        Woodcutter wc = new Woodcutter(player0);
        Point wcPoint = new Point(3, 3);

        map.placeBuilding(wc, wcPoint);
        map.placeFlag(player0, middleFlag);

        map.placeAutoSelectedRoad(player0, wc.getFlag().getPosition(), middleFlag);

        map.placeFlag(player0, endFlag);

        map.placeAutoSelectedRoad(player0, middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws InvalidEndPointException, Exception {
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
    public void testFindRouteWithSameStartAndEnd() throws InvalidRouteException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        map.findWayWithExistingRoads(new Point(1, 1), new Point(1, 1));
    }

    @Test
    public void testWorkerWalk() throws InvalidEndPointException, InvalidRouteException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);
        
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
        Point wcPoint = new Point(10, 16);
        ForesterHut hut = new ForesterHut(player0);
        Point start = new Point(2, 18);
        Point f1 = new Point(6, 18);
        Point f2 = new Point(10, 18);
        Point f3 = new Point(14, 18);
        Point f4 = new Point(18, 18);
        Point f5 = new Point(8, 12);
        Point f6 = new Point(12, 12);
        Point f7 = new Point(8, 10);
        Point f8 = new Point(12, 10);
        Point f9 = new Point(14, 14);


        map.placeBuilding(new Headquarter(player0), new Point(5, 5));

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

        assertTrue(target.getPosition().equals(new Point(11, 15)));

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
    public void testWorkerUnreachableTarget() throws InvalidRouteException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Point away = new Point(6, 2);
        Point start = new Point(2, 2);
        Flag end = map.placeFlag(player0, new Point(4, 2));
        
        Point middle = new Point(3, 3);

        map.placeFlag(player0, start);
        Flag flag = map.placeFlag(player0, away);

        Road targetRoad = map.placeRoad(player0, start, middle, end.getPosition());
        Courier worker = new Courier(player0, map);

        map.placeWorker(worker, flag);

        worker.assignToRoad(targetRoad);
    }

    @Test
    public void testProduceThenDeliverToStorage() throws InvalidStateForProduction, InvalidRouteException, InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Quarry qry = new Quarry(player0);
        Storage stge = new Storage(player0);

        map.placeBuilding(qry, new Point(2, 2));
        map.placeBuilding(stge, new Point(6, 2));

        Flag target = stge.getFlag();
        Flag start = qry.getFlag();

        map.placeAutoSelectedRoad(player0, start, target);

        Courier worker = new Courier(player0, map);
        Stonemason mason = new Stonemason(player0, map);

        map.placeStone(qry.getFlag().getPosition().up().up());

        worker.setPosition(start.getPosition());

        Utils.constructHouse(qry, map);
        Utils.constructHouse(stge, map);

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

        Cargo c = qry.getFlag().getCargoWaitingForRoad(map.getRoad(stge.getFlag().getPosition(), qry.getFlag().getPosition()));
        assertTrue(c.getPosition().equals(qry.getFlag().getPosition()));
        assertEquals(c.getTarget(), stge);
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

        /* Let the courier walk to the cargo at end flag and picks up the cargo*/
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
        assertTrue(map.getClosestStorage(hqToMiddleRoad.getStart()).equals(storage));

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point1 = new Point(1, 1);
        Point point2 = new Point(5, 1);
        Point middle = new Point(3, 1);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        map.placeFlag(player0, point1);
        map.placeFlag(player0, point2);

        Road r = map.placeRoad(player0, point1, middle, point2);

        assertTrue(r.needsCourier());
    }


    @Test
    public void testMilitaryTransportation() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Headquarter hq = new Headquarter(player0);
        Barracks b = new Barracks(player0);
        Point bSpot = new Point(7, 7);
        Point hqSpot = new Point(2, 2);
        Road r;
        Courier w = new Courier(player0, map);

        map.placeBuilding(hq, hqSpot);
        map.placeBuilding(b, bSpot);

        r = map.placeAutoSelectedRoad(player0, hq.getFlag(), b.getFlag());
        map.placeWorker(w, hq.getFlag());
        w.assignToRoad(r);
        
        /* Construct barracks */
        Utils.constructHouse(b, map);

        /* Let the courier get to its target road */
        Utils.fastForwardUntilWorkersReachTarget(map, w);

        /* Add a private to the hq */
        Military m = new Military(player0, PRIVATE_RANK, map);
        hq.depositWorker(m);

        /* Check that the barracks needs a military */
        assertTrue(b.isMilitaryBuilding());
        int hostedMilitary = b.getHostedMilitary();
        int maxHostedMilitary = b.getMaxHostedMilitary();
        assertEquals(hostedMilitary, 0);
        assertEquals(maxHostedMilitary, 2);

        /* Get military from the headquarter
         * - retrieve should set location of the worker
         */
        m = hq.retrieveAnyMilitary();

        /* Tell military to go to the barracks */
        m.setMap(map);
        m.setPosition(hq.getFlag().getPosition());
        m.setTargetBuilding(b);
        assertEquals(m.getTargetBuilding(), b);
        assertEquals(m.getTarget(), b.getPosition());

        /* Verify that the military reaches the barracks */
        Utils.fastForward(60, m);

        assertTrue(m.isAt(b.getPosition()));
        assertTrue(m.isArrived());

        /* Make the military enter the barracks */
        assertEquals(b.getHostedMilitary(), 1);
    }

    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsNotOnFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point start = new Point(5, 5);
        Point middle = new Point(6, 6);
        Point end = new Point(7, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(player0), end.upLeft());

        Flag flag0 = map.placeFlag(player0, start);
        Road road0 = map.placeRoad(player0, start, middle, end);

        Courier courier = new Courier(player0, map);

        map.placeWorker(courier, sm.getFlag());
        courier.assignToRoad(road0);

        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        Cargo cargo = new Cargo(WOOD, map);
        cargo.setPosition(start);
        cargo.setTarget(sm);

        flag0.putCargo(cargo);

        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(middle));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());

        map.stepTime();

        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, start);

        assertTrue(courier.isAt(start));
        assertEquals(courier.getCargo(), cargo);
    }

    @Test
    public void testCargoTargetRemainsWhenItIsPutDownAtFlag() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        Point point3 = new Point(9, 7);
        Point point4 = new Point(11, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        Flag flag0 = map.placeFlag(player0, point0);
        Flag flag1 = map.placeFlag(player0, point2);
        Road road0 = map.placeRoad(player0, point0, point1, point2);
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

        assertTrue(road1.getWayPoints().contains(cargo.getNextStep()));
        assertEquals(cargo.getTarget(), sm);
        assertEquals(cargo.getPosition(), point2);
        assertEquals(flag1.getStackedCargo().get(0), cargo);
        assertFalse(flag1.getStackedCargo().isEmpty());
    }

    @Test
    public void testCargoDeliveryPromiseIsCleared() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        Point point3 = new Point(9, 7);
        Point point4 = new Point(11, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(player0), point4.upLeft());

        Flag flag0 = map.placeFlag(player0, point0);
        Flag flag1 = map.placeFlag(player0, point2);
        Road road0 = map.placeRoad(player0, point0, point1, point2);
        Road road1 = map.placeRoad(player0, point2, point3, point4);

        Courier courier = new Courier(player0, map);
        Courier secondCourier = new Courier(player0, map);

        map.placeWorker(courier, flag1);
        courier.assignToRoad(road0);

        map.placeWorker(secondCourier, flag1);
        secondCourier.assignToRoad(road1);

        /* Let the couriers reach their target road and become idle */
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
}
