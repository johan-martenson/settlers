package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameLogic;
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
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
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
            Cargo c = new Cargo(m);

            assertNotNull(c);

            assertTrue(c.getMaterial() == m);
        }
    }

    @Test
    public void testCreateRoad() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(30, 30);

        Storage hq = new Storage();
        Woodcutter wc = new Woodcutter();

        map.placeBuilding(hq, new Point(5, 5));
        map.placeBuilding(wc, new Point(10, 6));

        Flag f1 = hq.getFlag();
        Flag f2 = wc.getFlag();

        map.placeAutoSelectedRoad(f1, f2);

        List<Road> roads = map.getRoads();

        assertTrue(1 == roads.size());

        Road r = roads.get(0);

        assertTrue(r.start.getPosition().x == 6);
        assertTrue(r.start.getPosition().y == 4);

        assertTrue(r.end.getPosition().x == 11);
        assertTrue(r.end.getPosition().y == 5);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutStartBuilding() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        Storage s = new Storage();

        map.placeBuilding(s, new Point(4, 6));

        map.placeAutoSelectedRoad(new Flag(5, 7), new Flag(4, 6));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutEndBuilding() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        Woodcutter wc = new Woodcutter();

        map.placeBuilding(wc, new Point(3, 3));

        map.placeAutoSelectedRoad(new Flag(8, 6), new Flag(3, 3));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        map.placeAutoSelectedRoad(new Flag(1, 1), new Flag(3, 5));
    }

    @Test
    public void testCreateTwoChainedRoads() throws InvalidEndPointException, Exception {
        GameMap    map        = new GameMap(40, 40);
        Flag       middleFlag = new Flag(7, 7);
        Flag       endFlag    = new Flag(10, 10);
        Woodcutter wc         = new Woodcutter();
        Point      wcPoint    = new Point(3, 3);

        map.placeBuilding(wc, wcPoint);
        map.placeFlag(middleFlag);

        map.placeAutoSelectedRoad(wc.getFlag(), middleFlag);

        map.placeFlag(endFlag);

        map.placeAutoSelectedRoad(middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        map.placeFlag(new Flag(3, 3));
        map.placeAutoSelectedRoad(new Flag(3, 3), new Flag(3, 3));
    }

    @Test
    public void testDoesRouteExist() throws InvalidEndPointException, InvalidRouteException, Exception {

        GameMap map = new GameMap(10, 10);

        Flag[] points = new Flag[]{
            new Flag(1, 1),
            new Flag(3, 3)
        };

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(points[i]);
        }

        map.placeAutoSelectedRoad(points[0], points[1]);

        assertTrue(map.routeExist(points[0].getPosition(), points[1].getPosition()));
    }

    @Test
    public void testDoesRouteExistNo() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag[] points = new Flag[]{
            new Flag(1, 1),
            new Flag(3, 3),
            new Flag(5, 5)
        };

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(points[i]);
        }

        map.placeAutoSelectedRoad(points[0], points[1]);

        assertFalse(map.routeExist(points[0].getPosition(), points[2].getPosition()));
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
        Woodcutter wc = new Woodcutter();
        Flag start    = new Flag(new Point(2, 18));
        Flag f1       = new Flag(new Point(4, 18));
        Flag f2       = new Flag(new Point(6, 18));
        Flag f9       = new Flag(new Point(8, 14));
        
        GameMap map = new GameMap(20, 20);
        map.placeBuilding(new Headquarter(), new Point(5, 5));
        map.placeFlag(start);
        map.placeFlag(f1);
        map.placeFlag(f2);
        map.placeFlag(new Flag(new Point(8, 18)));
        map.placeFlag(new Flag(new Point(10, 18)));
        map.placeRoad(new Point(2, 18), new Point(4, 18));
        map.placeRoad(new Point(4, 18), new Point(6, 18));
        map.placeRoad(new Point(6, 18), new Point(8, 18));
        map.placeRoad(new Point(8, 18), new Point(10, 18));
        map.placeBuilding(wc, wcPoint);
        map.placeFlag(f9);
        map.placeRoad(new Point(6, 18), new Point(7, 17), new Point(8, 16), new Point(7, 15), f9.getPosition());
        map.placeRoad(f9.getPosition(), new Point(10, 14), new Point(11, 15));
        map.placeFlag(new Flag(new Point(8, 12)));
        map.placeRoad(new Point(4, 18), new Point(5, 17), new Point(6, 16), new Point(5, 15), new Point(6, 14), new Point(7, 13), new Point(8, 12));
        map.placeFlag(new Flag(new Point(8, 10)));
        map.placeRoad(new Point(4, 18), new Point(3, 17), new Point(2, 16), new Point(1, 15), new Point(2, 14), new Point(3, 13), new Point(4, 12), new Point(5, 11), new Point(6, 10), new Point(8, 10));
        map.placeFlag(new Flag(new Point(10, 10)));
        map.placeRoad(new Point(8, 10), new Point(10, 10));
        map.placeFlag(new Flag(new Point(10, 12)));
        map.placeRoad(new Point(8, 12), new Point(10, 12));

        Flag target = wc.getFlag();

        assertTrue(target.getPosition().equals(new Point(11, 15)));
        
        Courier worker = new Courier(map);

        assertNotNull(worker);

        worker.setPosition(start.getPosition());
        worker.setTargetFlag(target);
        
        /* Road to go: (2, 18), (4, 18), (6, 18), (7, 17), (8, 16), (7, 15), (8, 14), (10, 14), (11, 15) */
        /*             start    f1       f2                                  f9                 target */

        assertTrue(worker.getPosition().equals(start.getPosition()));

        Utils.fastForward(10, worker);

        assertTrue(worker.getPosition().equals(f1.getPosition()));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(f2.getPosition()));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(new Point(7, 17)));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(new Point(8, 16)));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(new Point(7, 15)));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(f9.getPosition()));
        
        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(new Point(10, 14)));
        
        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(target.getPosition()));
    }

    @Test(expected = InvalidRouteException.class)
    public void testWorkerUnreachableTarget() throws InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag target = new Flag(6, 2);
        Flag start = new Flag(2, 2);

        map.placeFlag(start);
        map.placeFlag(target);

        Courier worker = new Courier(map);

        worker.setPosition(start.getPosition());
        worker.setTargetFlag(target);
    }

    @Test
    public void testProduceThenDeliverToStorage() throws InvalidStateForProduction, InvalidRouteException, InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, Exception {
        GameMap map = new GameMap(10, 10);

        Quarry qry = new Quarry();
        Storage stge = new Storage();

        map.placeBuilding(qry, new Point(2, 2));
        map.placeBuilding(stge, new Point(6, 2));

        Flag target = stge.getFlag();
        Flag start = qry.getFlag();

        map.placeAutoSelectedRoad(start, target);

        Courier worker = new Courier(map);

        worker.setPosition(start.getPosition());

        Utils.constructSmallHouse(qry);
        Utils.constructMediumHouse(stge);

        assertTrue(qry.getConstructionState() == DONE);

        /* Production starts, wait for it to finish */
        Utils.fastForward(100, qry, stge, worker);

        assertTrue(qry.isCargoReady());

        Cargo c = qry.retrieveCargo();
        assertTrue(c.getPosition().equals(qry.getFlag().getPosition()));

        map.findWayWithExistingRoads(qry.getFlag().getPosition(), stge.getFlag().getPosition());

        c.setTarget(stge, map);

        qry.getFlag().putCargo(c);

        List<Cargo> cargos = qry.getFlag().getStackedCargo();

        assertTrue(cargos.size() == 1);

        c = cargos.get(0);

        assertTrue(c.getMaterial() == STONE);

		// TODO: Make sure the cargo has a target which is to go to the closest storage building
        c.setTarget(stge, map);
    }

    @Test
    public void testDeliverWithHandover() throws Exception {
        GameLogic gameLogic  = new GameLogic();
        GameMap map          = new GameMap(40, 40);
        Storage storage      = new Storage();
        Point hqPoint        = new Point(6, 4);
        Point middlePoint    = new Point(10, 10); // hq to middle 6 steps
        Point endPoint       = new Point(10, 14); // end to middle 4 steps
        Flag middleFlag      = new Flag(middlePoint);
        Flag endFlag         = new Flag(endPoint);
        Courier mdlToEndCr   = new Courier(map);
        Courier hqToMdlCr    = new Courier(map);
        
        
        map.placeBuilding(storage, hqPoint);
        map.placeFlag(middleFlag);
        map.placeFlag(endFlag);
        
        Road hqToMiddleRoad = map.placeAutoSelectedRoad(storage.getFlag(), middleFlag);
        Road middleToEndRoad = map.placeRoad(middlePoint, middlePoint.upRight(), middlePoint.upRight().upLeft(), endPoint.downLeft(), endPoint);
        
        map.placeWorker(hqToMdlCr, middleFlag);
        map.placeWorker(mdlToEndCr, endFlag);
        
        map.assignCourierToRoad(hqToMdlCr, hqToMiddleRoad);
        map.assignCourierToRoad(mdlToEndCr, middleToEndRoad);
        
        Cargo c = new Cargo(WOOD);
        endFlag.putCargo(c);
        c.setTarget(storage, map);
        
        assertTrue(mdlToEndCr.isArrived());
        assertFalse(mdlToEndCr.isTraveling());
        assertTrue(mdlToEndCr.isAt(endFlag.getPosition()));
        assertNull(mdlToEndCr.getCargo());
        
        /* Check that the courier picks up the cargo on next tick */
        map.stepTime();
        
        assertTrue(mdlToEndCr.getCargo().equals(c));
        assertTrue(mdlToEndCr.getTargetFlag().equals(middleFlag));
        
        Utils.fastForwardUntilWorkersReachTarget(map, mdlToEndCr);

        assertTrue(mdlToEndCr.getPosition().equals(middlePoint));

        /* Courier at middle point */        
        assertTrue(mdlToEndCr.isArrived());
        assertNull(mdlToEndCr.getCargo());
        assertTrue(middleFlag.getStackedCargo().contains(c));
        assertTrue(middleFlag.getStackedCargo().size() == 1);
        assertTrue(middleFlag.getStackedCargo().get(0).equals(c));
        assertNull(hqToMdlCr.getCargo());
        assertTrue(middleFlag.hasCargoWaitingForRoad(hqToMiddleRoad));
        
        /* Next courier picks up cargo */
        map.stepTime();
        
        assertTrue(hqToMdlCr.getCargo().equals(c));

        Utils.fastForward(80, map);
        
        assertTrue(hqToMdlCr.isAt(storage.getFlag().getPosition()));        
        assertNull(hqToMdlCr.getCargo());
        assertTrue(storage.isInStock(WOOD));
        
    }
    
    @Test
    public void testCourierIsAssignedToNewRoad() throws Exception {
        GameLogic gameLogic  = new GameLogic();
        GameMap map          = new GameMap(30, 30);
        Storage storage      = new Storage();
        Point hqPoint        = new Point(5, 5);
        Point middlePoint    = new Point(11, 5);
        Flag middleFlag      = new Flag(middlePoint);
        
        map.placeBuilding(storage, hqPoint);
        map.placeFlag(middleFlag);
        
        Road hqToMiddleRoad = map.placeAutoSelectedRoad(storage.getFlag(), middleFlag);
        
        assertTrue(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        assertTrue(map.getRoadsThatNeedCouriers().contains(hqToMiddleRoad));
        assertTrue(map.getClosestStorage(hqToMiddleRoad).equals(storage));
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertFalse(hqToMiddleRoad.needsCourier());
        assertNotNull(hqToMiddleRoad.getCourier());
    }

    @Test
    public void testEmptyRoadNeedsCourier() throws Exception {
        GameMap map = new GameMap(10, 10);
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(3, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);
        
        Road r  = map.placeRoad(f1.getPosition(), f2.getPosition());
        
        assertTrue(r.needsCourier());
    }
    
    @Test(expected=Exception.class)
    public void testPromiseCourierTwice() throws Exception {
        GameMap map = new GameMap(10, 10);
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(3, 1));
        
        map.placeFlag(f1);
        map.placeFlag(f2);

        Road r  = map.placeRoad(f1.getPosition(), f2.getPosition());

        r.promiseCourier();
        r.promiseCourier();
    }
    
    @Test
    public void testMilitaryTransportation() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        GameMap map = new GameMap(10, 10);
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
        map.assignCourierToRoad(w, r);

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
        m.setTargetFlag(b.getFlag());
        assertEquals(m.getTargetFlag(), b.getFlag());
        
        /* Verify that the military reaches the barracks */
        Utils.fastForward(60, m);
        
        assertTrue(m.isAt(b.getFlag().getPosition()));
        assertTrue(m.isArrived());

        /* Make the military enter the barracks */
        assertTrue(b.getHostedMilitary() == 0);

        b.hostMilitary(m);
        assertTrue(b.getHostedMilitary() == 1);
    }

    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsOnFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        
        Building sm = map.placeBuilding(new Sawmill(), point2.upLeft());
        
        Flag flag0 = map.placeFlag(point0);
        Road road0 = map.placeRoad(point0, point1, point2);
        
        Courier courier = new Courier(map);
        
        map.placeWorker(courier, flag0);
        map.assignCourierToRoad(courier, road0);
        
        Cargo cargo = new Cargo(WOOD);
        cargo.setPosition(point0);
        cargo.setTarget(sm, map);
        
        flag0.putCargo(cargo);
        
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point0));
        assertNull(courier.getCargo());
        
        map.stepTime();
        
        assertEquals(courier.getCargo(), cargo);
    }
    
    @Test
    public void testCourierPicksUpCargoWhenItAppearsAndWorkerIsNotOnFlag() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Point point1 = new Point(6, 6);
        Point point2 = new Point(7, 7);
        
        Building sm = map.placeBuilding(new Sawmill(), point2.upLeft());
        
        Flag flag0 = map.placeFlag(point0);
        Road road0 = map.placeRoad(point0, point1, point2);
        
        Courier courier = new Courier(map);
        
        map.placeWorker(courier, sm.getFlag());
        map.assignCourierToRoad(courier, road0);
        
        Cargo cargo = new Cargo(WOOD);
        cargo.setPosition(point0);
        cargo.setTarget(sm, map);
        
        flag0.putCargo(cargo);
        
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point2));
        assertNull(courier.getCargo());
        assertFalse(cargo.isDeliveryPromised());
        
        map.stepTime();
        
        assertTrue(cargo.isDeliveryPromised());
        assertEquals(courier.getTargetFlag(), flag0);
        
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        assertTrue(courier.isAt(point0));
        
        map.stepTime();
        
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
        
        Building sm = map.placeBuilding(new Sawmill(), point4.upLeft());
        
        Flag flag0 = map.placeFlag(point0);
        Flag flag1 = map.placeFlag(point2);
        Road road0 = map.placeRoad(point0, point1, point2);
        Road road1 = map.placeRoad(point2, point3, point4);
        
        Courier courier = new Courier(map);
        Courier secondCourier = new Courier(map);
        
        map.placeWorker(courier, flag0);
        map.assignCourierToRoad(courier, road0);        
        
        map.placeWorker(secondCourier, flag1);
        map.assignCourierToRoad(secondCourier, road1);
        
        Cargo cargo = new Cargo(WOOD);
        cargo.setPosition(point0);
        cargo.setTarget(sm, map);
        
        flag0.putCargo(cargo);

        assertEquals(cargo.getTarget(), sm);
        assertEquals(cargo.getPosition(), point0);
        assertFalse(courier.isTraveling());
        assertTrue(courier.isAt(point0));
        assertNull(courier.getCargo());
        
        map.stepTime();

        assertEquals(courier.getCargo(), cargo);
        assertEquals(courier.getTargetFlag(), flag1);
        assertTrue(flag1.getStackedCargo().isEmpty());
        
        int i;
        for (i = 0; i < 1000; i++) {
            if (!courier.isArrived()) {
                courier.stepTime();
            }
        }

        assertTrue(courier.isAt(point2));
        assertNull(courier.getCargo());

        assertEquals(cargo.getPlannedRoads().get(0), road1);
        assertEquals(cargo.getTarget(), sm);
        assertEquals(cargo.getPosition(), point2);
        assertEquals(flag1.getStackedCargo().get(0), cargo);
        assertFalse(flag1.getStackedCargo().isEmpty());
    }
}
