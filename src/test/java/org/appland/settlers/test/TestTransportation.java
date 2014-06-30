package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Barracks;
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

        map.placeRoad(f1, f2);

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

        map.placeRoad(new Flag(6, 7), new Flag(4, 6));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutEndBuilding() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        Woodcutter wc = new Woodcutter();

        map.placeBuilding(wc, new Point(3, 3));

        map.placeRoad(new Flag(8, 6), new Flag(3, 3));
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithoutAnyValidEndpoints() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        map.placeRoad(new Flag(1, 1), new Flag(3, 5));
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

        map.placeRoad(wc.getFlag(), middleFlag);

        map.placeFlag(endFlag);

        map.placeRoad(middleFlag, endFlag);
    }

    @Test(expected = InvalidEndPointException.class)
    public void testCreateRoadWithSameEndAndStart() throws InvalidEndPointException, Exception {
        GameMap map = new GameMap(10, 10);

        map.placeFlag(new Flag(3, 3));
        map.placeRoad(new Flag(3, 3), new Flag(3, 3));
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

        map.placeRoad(points[0], points[1]);

        assertTrue(map.routeExist(points[0], points[1]));
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

        map.placeRoad(points[0], points[1]);

        assertFalse(map.routeExist(points[0], points[2]));
    }

    @Test(expected = InvalidRouteException.class)
    public void testFindRouteWithSameStartAndEnd() throws InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        map.findWay(new Flag(1, 1), new Flag(1, 1));
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

        GameMap map = new GameMap(50, 50);

        Flag[] points = new Flag[]{
            new Flag(1, 1),
            new Flag(3, 1),
            new Flag(5, 1),
            new Flag(7, 1),
            new Flag(9, 1),
            new Flag(2, 6),
            new Flag(4, 6),
            new Flag(2, 8),
            new Flag(4, 8),
            new Flag(7, 3)};

        int i;
        for (i = 0; i < points.length; i++) {
            map.placeFlag(points[i]);
        }

        Woodcutter wc = new Woodcutter();

        map.placeBuilding(wc, new Point(15, 3));

        Flag target = wc.getFlag();

        map.placeRoad(points[0], points[1]);
        map.placeRoad(points[1], points[2]);
        map.placeRoad(points[2], points[3]);
        map.placeRoad(points[3], points[4]);
        map.placeRoad(points[2], points[9]);
        map.placeRoad(points[9], target);
        map.placeRoad(points[1], points[5]);
        map.placeRoad(points[5], points[6]);
        map.placeRoad(points[1], points[7]);
        map.placeRoad(points[7], points[8]);

        Courier worker = new Courier(map);

        assertNotNull(worker);

        worker.setPosition(points[0]);
        worker.setTargetFlag(target);

        assertTrue(worker.getPosition().equals(points[0]));

        Utils.fastForward(10, worker);

        assertTrue(worker.getPosition().equals(points[1]));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(points[2]));

        Utils.fastForward(10, worker);
        assertTrue(worker.getPosition().equals(points[9]));

        Utils.fastForward(10, worker);

        assertTrue(worker.getPosition().equals(target));
    }

    @Test(expected = InvalidRouteException.class)
    public void testWorkerUnreachableTarget() throws InvalidRouteException, Exception {
        GameMap map = new GameMap(10, 10);

        Flag target = new Flag(6, 2);
        Flag start = new Flag(2, 2);

        map.placeFlag(start);
        map.placeFlag(target);

        Courier worker = new Courier(map);

        worker.setPosition(start);
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

        map.placeRoad(start, target);

        Courier worker = new Courier(map);

        worker.setPosition(start);

        Utils.constructSmallHouse(qry);
        Utils.constructMediumHouse(stge);

        assertTrue(qry.getConstructionState() == DONE);

        /* Production starts, wait for it to finish */
        Utils.fastForward(100, qry, stge, worker);

        assertTrue(qry.isCargoReady());

        Cargo c = qry.retrieveCargo();
        assertTrue(c.getPosition().equals(qry.getFlag()));

        map.findWay(qry.getFlag(), stge.getFlag());

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
        Point middlePoint    = new Point(10, 10);
        Point endPoint       = new Point(10, 14);
        Flag middleFlag      = new Flag(middlePoint);
        Flag endFlag         = new Flag(endPoint);
        Road hqToMiddleRoad  = new Road(storage.getFlag(), middleFlag);
        Road middleToEndRoad = new Road(middleFlag, endFlag);
        Courier mdlToEndCr   = new Courier(map);
        Courier hqToMdlCr    = new Courier(map);
        
        
        map.placeBuilding(storage, hqPoint);
        map.placeFlag(middleFlag);
        map.placeFlag(endFlag);
        
        map.placeRoad(hqToMiddleRoad);
        map.placeRoad(middleToEndRoad);
        
        map.placeWorker(hqToMdlCr, storage.getFlag());
        map.placeWorker(mdlToEndCr, endFlag);
        
        map.assignCourierToRoad(hqToMdlCr, hqToMiddleRoad);
        map.assignCourierToRoad(mdlToEndCr, middleToEndRoad);
        
        Cargo c = new Cargo(WOOD);
        endFlag.putCargo(c);
        c.setTarget(storage, map);
        
        gameLogic.assignWorkToIdleCouriers(map);
        
        assertTrue(mdlToEndCr.getCargo().equals(c));
        assertTrue(mdlToEndCr.getTarget().equals(middleFlag));
        
        Utils.fastForward(10, map);
        
        assertTrue(mdlToEndCr.isArrived());
        assertTrue(mdlToEndCr.getCargo().equals(c));
        
        gameLogic.deliverForWorkersAtTarget(map);
        
        assertTrue(middleFlag.getStackedCargo().size() == 1);
        assertTrue(middleFlag.getStackedCargo().get(0).equals(c));
        assertNull(mdlToEndCr.getCargo());
        assertNull(hqToMdlCr.getCargo());
        assertTrue(middleFlag.hasCargoWaitingForRoad(hqToMiddleRoad));
        
        gameLogic.assignWorkToIdleCouriers(map);
        
        assertTrue(hqToMdlCr.getCargo().equals(c));

        Utils.fastForward(10, map);
        
        assertFalse(storage.isInStock(WOOD));
        
        gameLogic.deliverForWorkersAtTarget(map);
        
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
        Road hqToMiddleRoad  = new Road(storage.getFlag(), middleFlag);
        
        map.placeBuilding(storage, hqPoint);
        map.placeFlag(middleFlag);
        
        map.placeRoad(hqToMiddleRoad);
        
        assertTrue(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        assertTrue(map.getRoadsWithoutWorker().contains(hqToMiddleRoad));
        assertTrue(map.getClosestStorage(hqToMiddleRoad).equals(storage));
        
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        
        assertFalse(hqToMiddleRoad.needsCourier());
        assertNull(hqToMiddleRoad.getCourier());
        
        Utils.fastForward(10, map);
        
        gameLogic.assignTravelingWorkersThatHaveArrived(map);
        
        assertNotNull(hqToMiddleRoad.getCourier());
    }

    @Test
    public void testEmptyRoadNeedsCourier() {
        Flag f1 = new Flag(new Point(1, 1));
        Flag f2 = new Flag(new Point(2, 2));
        Road r  = new Road(f1, f2);
        
        assertTrue(r.needsCourier());
    }
    
    @Test(expected=Exception.class)
    public void testPromiseCourierTwice() throws Exception {
        Road r = new Road(null, null);
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

        r = new Road(hq.getFlag(), b.getFlag());

        map.placeRoad(r);
        map.placeWorker(w, hq.getFlag());
        map.assignCourierToRoad(w, r);

        /* Construct barracks */
        Utils.constructSmallHouse(b);

        /* Add a private to the hq */
        Military m = new Military(Rank.PRIVATE_RANK);
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
        m.setPosition(hq.getFlag());
        m.setTargetFlag(b.getFlag());
        assertEquals(m.getTarget(), b.getFlag());

        /* Verify that the military reaches the barracks */
        Utils.fastForward(10, m);
        assertTrue(m.getPosition().equals(b.getFlag()));
        assertTrue(m.isArrived());

        /* Make the military enter the barracks */
        assertTrue(b.getHostedMilitary() == 0);

        b.hostMilitary(m);
        assertTrue(b.getHostedMilitary() == 1);
    }
}
