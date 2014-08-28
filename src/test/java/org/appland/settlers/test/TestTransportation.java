package org.appland.settlers.test;

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
import org.appland.settlers.model.Military.Rank;
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

            assertTrue(c.getMaterial() == m);
        }
    }

    @Test
    public void testCreateRoad() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(30, 30);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag f1 = map.placeFlag(new Point(6, 4));
        Flag f2 = map.placeFlag(new Point(11, 5));

        map.placeAutoSelectedRoad(f1, f2);

        List<Road> roads = map.getRoads();

        assertTrue(roads.size() == 2);

        Road r = map.getRoad(new Point(6, 4), new Point(11, 5));

        assertTrue(r.getStart().x == 6);
        assertTrue(r.getStart().y == 4);

        assertTrue(r.getEnd().x == 11);
        assertTrue(r.getEnd().y == 5);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutStartBuilding() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Storage s = new Storage();

        map.placeBuilding(s, new Point(4, 6));

        map.placeAutoSelectedRoad(new Flag(5, 7), new Flag(4, 6));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutEndBuilding() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Woodcutter wc = new Woodcutter();

        map.placeBuilding(wc, new Point(3, 3));

        map.placeAutoSelectedRoad(new Flag(8, 6), new Flag(3, 3));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeAutoSelectedRoad(new Flag(1, 1), new Flag(3, 5));
    }

    @Test
    public void testCreateTwoChainedRoads() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(40, 40);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag middleFlag = new Flag(7, 7);
        Flag endFlag = new Flag(10, 10);
        Woodcutter wc = new Woodcutter();
        Point wcPoint = new Point(3, 3);

        map.placeBuilding(wc, wcPoint);
        map.placeFlag(middleFlag);

        map.placeAutoSelectedRoad(wc.getFlag(), middleFlag);

        map.placeFlag(endFlag);

        map.placeAutoSelectedRoad(middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeFlag(new Flag(3, 3));
        map.placeAutoSelectedRoad(new Flag(3, 3), new Flag(3, 3));
    }

    @Test(expected = InvalidRouteException.class)
    public void testFindRouteWithSameStartAndEnd() throws InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        map.findWayWithExistingRoads(new Point(1, 1), new Point(1, 1));
    }

    @Test
    public void testWorkerWalk() throws InvalidEndPointException, InvalidRouteException, Exception {
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
        ForesterHut hut = new ForesterHut();
        Flag start = new Flag(new Point(2, 18));
        Flag f1 = new Flag(new Point(4, 18));
        Flag f2 = new Flag(new Point(6, 18));
        Flag f9 = new Flag(new Point(8, 14));

        GameMap map = new GameMap(20, 20);
        map.placeBuilding(new Headquarter(), new Point(5, 5));
        map.placeFlag(start);
        map.placeFlag(f1);
        map.placeFlag(f2);
        map.placeFlag(new Flag(new Point(8, 18)));
        map.placeFlag(new Flag(new Point(10, 18)));
        map.placeRoad(new Point(2, 18), new Point(3, 19), new Point(4, 18));
        map.placeRoad(new Point(4, 18), new Point(5, 19), new Point(6, 18));
        map.placeRoad(new Point(6, 18), new Point(7, 19), new Point(8, 18));
        map.placeRoad(new Point(8, 18), new Point(9, 19), new Point(10, 18));
        map.placeBuilding(hut, wcPoint);
        map.placeFlag(f9);
        map.placeRoad(new Point(6, 18), new Point(7, 17), new Point(8, 16), new Point(7, 15), f9.getPosition());
        map.placeRoad(f9.getPosition(), new Point(10, 14), new Point(11, 15));
        map.placeFlag(new Flag(new Point(8, 12)));
        map.placeRoad(new Point(4, 18), new Point(5, 17), new Point(6, 16), new Point(5, 15), new Point(6, 14), new Point(7, 13), new Point(8, 12));
        map.placeFlag(new Flag(new Point(8, 10)));
        map.placeRoad(new Point(4, 18), new Point(3, 17), new Point(2, 16), new Point(1, 15), new Point(2, 14), new Point(3, 13), new Point(4, 12), new Point(5, 11), new Point(6, 10), new Point(8, 10));
        map.placeFlag(new Flag(new Point(10, 10)));
        map.placeRoad(new Point(8, 10), new Point(9, 11), new Point(10, 10));
        map.placeFlag(new Flag(new Point(10, 12)));
        map.placeRoad(new Point(8, 12), new Point(9, 13), new Point(10, 12));

        Utils.constructSmallHouse(hut);

        Flag target = hut.getFlag();

        assertTrue(target.getPosition().equals(new Point(11, 15)));

        Forester forester = new Forester(map);

        assertNotNull(forester);

        map.placeWorker(forester, start);
        forester.setTargetBuilding(hut);


        /* Road to go: [(2, 18), (3, 19), (4, 18), (5, 19), (6, 18), (7, 17), (8, 16), (7, 15), (8, 14), (10, 14), (11, 15), (10, 16)] */
        /*             start              f1                f2                                   f9                          target */
        assertTrue(forester.getPosition().equals(start.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(3, 19));
        assertTrue(forester.getPosition().equals(new Point(3, 19)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f1.getPosition());
        assertTrue(forester.getPosition().equals(f1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(5, 19));
        assertTrue(forester.getPosition().equals(new Point(5, 19)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f2.getPosition());
        assertTrue(forester.getPosition().equals(f2.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(7, 17));
        assertTrue(forester.getPosition().equals(new Point(7, 17)));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(8, 16));
        assertTrue(forester.getPosition().equals(new Point(8, 16)));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(7, 15));
        assertTrue(forester.getPosition().equals(new Point(7, 15)));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, f9.getPosition());
        assertTrue(forester.getPosition().equals(f9.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(10, 14));
        assertTrue(forester.getPosition().equals(new Point(10, 14)));

        Utils.fastForwardUntilWorkerReachesPoint(map, forester, new Point(11, 15));
        assertTrue(forester.getPosition().equals(new Point(11, 15)));
        
        Utils.fastForwardUntilWorkerReachesPoint(map, forester, hut.getPosition());
        assertTrue(forester.isExactlyAtPoint());
        assertTrue(forester.getPosition().equals(hut.getPosition()));
    }

    @Test(expected = Exception.class)
    public void testWorkerUnreachableTarget() throws InvalidRouteException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Flag away = new Flag(6, 2);
        Flag start = new Flag(2, 2);
        Flag end = map.placeFlag(new Point(4, 2));
        
        Point middle = new Point(3, 3);

        map.placeFlag(start);
        map.placeFlag(away);

        Road targetRoad = map.placeRoad(start.getPosition(), middle, end.getPosition());
        Courier worker = new Courier(map);

        map.placeWorker(worker, away);

        worker.assignToRoad(targetRoad);
    }

    @Test
    public void testProduceThenDeliverToStorage() throws InvalidStateForProduction, InvalidRouteException, InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        GameMap map = new GameMap(20, 20);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Quarry qry = new Quarry();
        Storage stge = new Storage();

        map.placeBuilding(qry, new Point(2, 2));
        map.placeBuilding(stge, new Point(6, 2));

        Flag target = stge.getFlag();
        Flag start = qry.getFlag();

        map.placeAutoSelectedRoad(start, target);

        Courier worker = new Courier(map);
        Stonemason mason = new Stonemason(map);

        map.placeStone(qry.getFlag().getPosition().up().up());

        worker.setPosition(start.getPosition());

        Utils.constructSmallHouse(qry);
        Utils.constructMediumHouse(stge);

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
        assertTrue(c.getMaterial() == STONE);
    }

    @Test
    public void testDeliverWithHandover() throws Exception {
        GameMap map = new GameMap(40, 40);
        Storage storage = new Headquarter();
        Point hqPoint = new Point(6, 4);
        Point middlePoint = new Point(10, 10); // hq to middle 6 steps
        Point endPoint = new Point(10, 14); // end to middle 4 steps
        map.placeBuilding(storage, hqPoint);
        Flag middleFlag = new Flag(middlePoint);
        Flag endFlag = new Flag(endPoint);
        Courier mdlToEndCr = new Courier(map);
        Courier hqToMdlCr = new Courier(map);

        map.placeFlag(middleFlag);
        map.placeFlag(endFlag);

        Road hqToMiddleRoad = map.placeAutoSelectedRoad(storage.getFlag(), middleFlag);
        Road middleToEndRoad = map.placeRoad(middlePoint, middlePoint.upRight(), middlePoint.upRight().upLeft(), endPoint.downLeft(), endPoint);

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
        assertTrue(middleFlag.getStackedCargo().size() == 1);
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
        GameMap map = new GameMap(30, 30);
        Storage storage = new Headquarter();
        Point hqPoint = new Point(5, 5);
        Point middlePoint = new Point(11, 5);
        Flag middleFlag = new Flag(middlePoint);

        map.placeBuilding(storage, hqPoint);
        map.placeFlag(middleFlag);

        Road hqToMiddleRoad = map.placeAutoSelectedRoad(storage.getFlag(), middleFlag);

        assertTrue(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        assertTrue(map.getRoadsThatNeedCouriers().contains(hqToMiddleRoad));
        assertTrue(map.getClosestStorage(hqToMiddleRoad.getStart()).equals(storage));

        /* Step time to let the headquarter send new workers */
        map.stepTime();

        assertFalse(hqToMiddleRoad.needsCourier());

        /* Courier needs to walk to road before it's assigned */
        Courier c = null;
        for (Worker w : map.getAllWorkers()) {
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
        GameMap map = new GameMap(20, 20);
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(3, 1));
        Point middle = new Point(2, 2);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r = map.placeRoad(f1.getPosition(), middle, f2.getPosition());

        assertTrue(r.needsCourier());
    }


    @Test
    public void testMilitaryTransportation() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        GameMap map = new GameMap(20, 20);
        Headquarter hq = new Headquarter();
        Barracks b = new Barracks();
        Point bSpot = new Point(7, 7);
        Point hqSpot = new Point(2, 2);
        Road r;
        Courier w = new Courier(map);

        map.placeBuilding(hq, hqSpot);
        map.placeBuilding(b, bSpot);

        r = map.placeAutoSelectedRoad(hq.getFlag(), b.getFlag());
        map.placeWorker(w, hq.getFlag());
        w.assignToRoad(r);
        
        /* Let the courier get to its target road */
        Utils.fastForwardUntilWorkersReachTarget(map, w);

        /* Construct barracks */
        Utils.constructSmallHouse(b);

        /* Add a private to the hq */
        Military m = new Military(Rank.PRIVATE_RANK, map);
        hq.depositWorker(m);

        /* Check that the barracks needs a military */
        assertTrue(b.isMilitaryBuilding());
        int hostedMilitary = b.getHostedMilitary();
        int maxHostedMilitary = b.getMaxHostedMilitary();
        assertTrue(hostedMilitary == 0);
        assertTrue(maxHostedMilitary == 2);

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
        assertTrue(b.getHostedMilitary() == 1);
    }

    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsNotOnFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point start = new Point(5, 5);
        Point middle = new Point(6, 6);
        Point end = new Point(7, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(), end.upLeft());

        Flag flag0 = map.placeFlag(start);
        Road road0 = map.placeRoad(start, middle, end);

        Courier courier = new Courier(map);

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
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        Point point3 = new Point(9, 7);
        Point point4 = new Point(11, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(), point4.upLeft());

        Flag flag0 = map.placeFlag(point0);
        Flag flag1 = map.placeFlag(point2);
        Road road0 = map.placeRoad(point0, point1, point2);
        Road road1 = map.placeRoad(point2, point3, point4);

        Courier courier = new Courier(map);
        Courier secondCourier = new Courier(map);

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
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        Point point3 = new Point(9, 7);
        Point point4 = new Point(11, 7);
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        Building sm = map.placeBuilding(new Sawmill(), point4.upLeft());

        Flag flag0 = map.placeFlag(point0);
        Flag flag1 = map.placeFlag(point2);
        Road road0 = map.placeRoad(point0, point1, point2);
        Road road1 = map.placeRoad(point2, point3, point4);

        Courier courier = new Courier(map);
        Courier secondCourier = new Courier(map);

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
