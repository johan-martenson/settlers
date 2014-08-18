package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Military.Rank;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GameLogicTest {
    private GameLogic gameLogic;

    @Before
    public void setup() {
        gameLogic = new GameLogic();
    }

    @Test
    public void testInitiateNewDeliveries() throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidEndPointException, Exception {
        GameMap map = new GameMap(30, 30);

        Headquarter hq = new Headquarter();
        Woodcutter wc = new Woodcutter();

        Point hqPoint = new Point(5, 5);
        Point wcPoint = new Point(5, 11);

        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(wc, wcPoint);

        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());

        Utils.constructSmallHouse(wc);

        /* Since the woodcutter is finished it does not need any deliveries
         * Verify that no new deliveries are initiated
         */
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        assertNull(hq.getWorker().getCargo());
        
        map.stepTime();
        
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        assertNull(hq.getWorker().getCargo());

        /* Fast forward so the worker in the hq is rested */
        Utils.fastForward(20, map);
        
        /* Place an unfinished sawmill on the map and verify that it needs deliveries */
        Sawmill sm = new Sawmill();

        Point smPoint = new Point(10, 10);

        map.placeBuilding(sm, smPoint);

        map.placeAutoSelectedRoad(hq.getFlag(), sm.getFlag());

        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sm.needsMaterial(PLANCK));
        assertTrue(sm.needsMaterial(STONE));
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        
        map.stepTime();

        assertNotNull(hq.getWorker().getCargo());
    }

    @Test
    public void testAssignWorkToIdleCouriers() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map   = new GameMap(30, 30);
        Sawmill sm    = new Sawmill();
        Point smPoint = new Point(5, 5);
        Flag f        = new Flag(10, 10);
        Courier courier     = new Courier(map);
        Road r;
                
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeFlag(f);
        map.placeBuilding(sm, smPoint);
        r = map.placeAutoSelectedRoad(f, sm.getFlag());
        map.placeWorker(courier, f);
        courier.assignToRoad(r);

        /* Fast forward so the courier can reach its road and be assigned */
        Utils.fastForwardUntilWorkersReachTarget(map, courier);
        
        Cargo c = new Cargo(PLANCK, map);

        c.setPosition(f.getPosition());
        c.setTarget(sm);
        f.putCargo(c);

        /* Verify that the worker is idle */
        assertTrue(courier.isIdle());
        assertNull(courier.getCargo());

        assertTrue(courier.isArrived());
        assertFalse(courier.isTraveling());
        assertTrue(f.hasCargoWaitingForRoad(courier.getAssignedRoad()));

        /* Verify that the worker picks up the cargo and has the sawmill as target */
        map.stepTime();

        assertEquals(courier.getTarget(), f.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, f.getPosition());
        
        /* Verify that the courier has picked up the cargo */
        assertNotNull(courier.getCargo());
        assertEquals(c, courier.getCargo());
        assertEquals(courier.getTarget(), sm.getPosition());
    }

    @Test
    public void testDeliverForWorkersAtTarget() throws InvalidEndPointException, InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        GameMap map   = new GameMap(30, 30);
        Woodcutter wc = new Woodcutter();
        Flag src      = new Flag(5, 5);
        Point wcPoint = new Point(11, 5);
        Courier w     = new Courier(map);
        Cargo c;
        Road r;
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(), hqPoint);
        
        map.placeFlag(src);
        map.placeBuilding(wc, wcPoint);

        r = map.placeAutoSelectedRoad(src, wc.getFlag());
        map.placeWorker(w, src);
        w.assignToRoad(r);

        /* Fast forward to let the courier reach its road and get assigned */
        Utils.fastForwardUntilWorkersReachTarget(map, w);
        
        c = new Cargo(PLANCK, map);

        src.putCargo(c);
        c.setTarget(wc);

        /* Let the courier detect and pick up the cargo */
        map.stepTime();
        
        assertEquals(w.getTarget(), src.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, w, src.getPosition());
        
        assertEquals(w.getCargo(), c);
        
        /* Move worker to the sawmill */
        assertEquals(w.getTarget(), wc.getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, w, wc.getPosition());
        
        assertTrue(w.isAt(wc.getPosition()));

        /* Verify the worker delivers the cargo when it has reached the target */
        assertNull(w.getCargo());
        assertTrue(w.isAt(wc.getPosition()));
        assertTrue(wc.getMaterialInQueue(PLANCK) == 1);
    }

    @Test
    public void testAssignNewWorkerToUnoccupiedPlaces() throws Exception {
        GameMap map = new GameMap(30, 30);

        Headquarter hq = new Headquarter();
        Barracks bk = new Barracks();
        ForesterHut fHut = new ForesterHut();

        Point hqPoint = new Point(5, 5);
        Point wcPoint = new Point(6, 10);
        Point fhPoint = new Point(5, 17);

        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(bk, wcPoint);
        map.placeBuilding(fHut, fhPoint);

        /* Assign new workers to unocupied places. Since there are no places 
         * that require workers this should not do anything*/
        assertTrue(map.getAllWorkers().size() == 1);
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        assertTrue(map.getAllWorkers().size() == 1);

        /* Construct a road without any courier assigned */
        Road r  = map.placeAutoSelectedRoad(hq.getFlag(), bk.getFlag());
        Road r2 = map.placeAutoSelectedRoad(hq.getFlag(), fHut.getFlag());

        assertTrue(map.getAllWorkers().size() == 1);

        /* Prep the headquarter's inventory */
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));

        assertTrue(hq.getAmount(PRIVATE) == 3);

        hq.depositWorker(new Forester());

        assertTrue(hq.getAmount(FORESTER) == 3);
        assertTrue(map.getAllWorkers().size() == 1);
        
        /* Assign new workers to unoccupied places and verify that there is a 
         * worker designated for the road. There should be no worker for the 
         * barracks as it's not finished yet
         */
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        // TODO: Ensure that only one worker is dispatched even though there are two unassigned roads
        assertTrue(map.getAllWorkers().size() == 3);
        
        assertTrue(map.getAllWorkers().get(1) instanceof Courier);
        assertTrue(map.getAllWorkers().get(2) instanceof Courier);
        
        Courier w1 = (Courier)map.getAllWorkers().get(1);
        Courier w2 = (Courier)map.getAllWorkers().get(2);

        /* Construct the barracks */
        
        Utils.constructSmallHouse(bk);
        
        assertTrue(bk.getConstructionState() == DONE);
        
        /* Fast forward to let the couriers reach their roads */
        Utils.fastForwardUntilWorkersReachTarget(map, w1, w2);
        
        assertTrue(w1.getAssignedRoad().equals(r));
        
        assertTrue(w2.getAssignedRoad().equals(r2));
        
        assertTrue(map.getRoadsThatNeedCouriers().isEmpty());
        assertEquals(map.getRoad(hq.getFlag().getPosition(), bk.getFlag().getPosition()), r);
        assertNotNull(r.getCourier().getAssignedRoad());

        assertTrue(r.getCourier().equals(w1));
        assertTrue(r.getCourier().getAssignedRoad().equals(r));
        
        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(bk.isMilitaryBuilding());
        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 0);
        assertTrue(hq.getAmount(Material.PRIVATE) == 3);

        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getAllWorkers().get(3) instanceof Military);
        assertFalse(map.getAllWorkers().get(3).isArrived());
        assertTrue(map.getAllWorkers().get(3).isTraveling());

        assertTrue(hq.getAmount(Material.PRIVATE) == 2);

        /* Let the military reach the barracks */
        Utils.fastForward(100, map);

        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getAllWorkers().get(3).isArrived());
        assertFalse(map.getAllWorkers().get(3).isTraveling());

        /* Make traveling workers that have arrived enter their building or road */

        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 1);

        /* Assign new workers again to see that a second military is dispatched
         * for the barracks
         */
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 5);
        assertTrue(map.getAllWorkers().get(4) instanceof Military);
        assertFalse(map.getAllWorkers().get(4).isArrived());
        assertTrue(map.getAllWorkers().get(4).isTraveling());

        assertFalse(bk.needMilitaryManning());

        assertTrue(hq.getAmount(Material.PRIVATE) == 1);

        /* Let the military reach the barracks */
        Utils.fastForward(100, map);

        assertTrue(map.getAllWorkers().size() == 5);
        assertTrue(map.getAllWorkers().get(4).isArrived());
        assertFalse(map.getAllWorkers().get(4).isTraveling());
        assertTrue(bk.getHostedMilitary() == 2);

        /* Assign new workers to unoccupied buildings again. There is building
         * or road that requires a worker so this should have no effect
         */
        assertTrue(map.getAllWorkers().size() == 5);
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        assertTrue(map.getAllWorkers().size() == 5);

        /* Finish construction of the forester hut which requires a 
         * forester worker to function
         */
        Utils.constructSmallHouse(fHut);

        assertTrue(fHut.needsWorker());

        /* Assign new workers to unoccupied buildings and roads. The forester
         * hut needs a forester so a forester should be dispatched from the hq
         */
        assertTrue(hq.getAmount(FORESTER) == 3);
        assertTrue(fHut.needsWorker(FORESTER));

        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 6);
        assertFalse(fHut.needsWorker());
        
        Worker forester = null;
        for (Worker w : map.getAllWorkers()) {
            if (w instanceof Forester) {
                forester = w;
            }
        }

        /* Let the forester reach the forester hut */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertNotNull(fHut.getWorker());
        assertTrue(fHut.getWorker() instanceof Forester);
    }
}
