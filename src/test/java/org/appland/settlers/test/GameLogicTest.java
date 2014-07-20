package org.appland.settlers.test;

import org.appland.settlers.model.Barracks;
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
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Military.Rank;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;

import static org.appland.settlers.test.Utils.fastForward;
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
    public void testInitiateCollectionOfNewProduce() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidRouteException, Exception {
        GameMap map    = new GameMap(30, 30);
        Sawmill sm     = new Sawmill();
        Storage stg    = new Storage();
        Point stgPoint = new Point(5, 5);
        Point wcPoint  = new Point(10, 6);
        Road r;
        Courier w = new Courier(map);

        map.placeBuilding(sm, wcPoint);
        map.placeBuilding(stg, stgPoint);

        r = map.placeAutoSelectedRoad(stg.getFlag(), sm.getFlag());
        map.placeWorker(w, stg.getFlag());
        map.assignCourierToRoad(w, r);

        Utils.constructMediumHouse(sm);

        /* Deliver WOOD to the sawmill */
        Cargo woodCargo = new Cargo(WOOD);
        woodCargo.setPosition(sm.getFlag().getPosition());

        sm.deliver(woodCargo);
        
        /* Fast forward until the sawmill has produced a cargo with PLANCKS */
        fastForward(100, sm, w);
        assertTrue(sm.isCargoReady());
        assertNull(w.getCargo());

        /* Verify that the worker doesn't pick up the cargo by himself as time passes */
        fastForward(100, sm, w);
        assertTrue(sm.isCargoReady());
        assertNull(w.getCargo());
        assertTrue(sm.getFlag().getStackedCargo().isEmpty());

        /* Verify that initiateCollectionOfNewProduce gets the worker to pick up the cargo */
        gameLogic.initiateCollectionOfNewProduce(map);
        assertFalse(sm.isCargoReady());
        assertNull(w.getCargo());
        assertFalse(sm.getFlag().getStackedCargo().isEmpty());

        Cargo c = sm.getFlag().getStackedCargo().get(0);
        assertTrue(c.getTarget().equals(stg));
        assertTrue(c.getMaterial() == PLANCK);
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
        gameLogic.initiateNewDeliveriesForStorage(hq, map);
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        /* Place an unfinished sawmill on the map and verify that it needs deliveries */
        Sawmill sm = new Sawmill();

        Point smPoint = new Point(10, 10);

        map.placeBuilding(sm, smPoint);

        map.placeAutoSelectedRoad(hq.getFlag(), sm.getFlag());

        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sm.needsMaterial(PLANCK));
        assertTrue(sm.needsMaterial(STONE));
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        gameLogic.initiateNewDeliveriesForStorage(hq, map);
        assertFalse(hq.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testAssignWorkToIdleCouriers() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map   = new GameMap(30, 30);
        Sawmill sm    = new Sawmill();
        Point smPoint = new Point(5, 5);
        Flag f        = new Flag(10, 10);
        Courier w     = new Courier(map);
        Road r;
        
        map.placeFlag(f);
        map.placeBuilding(sm, smPoint);
        r = map.placeAutoSelectedRoad(f, sm.getFlag());
        map.placeWorker(w, f);
        map.assignCourierToRoad(w, r);

        Cargo c = new Cargo(PLANCK);

        c.setPosition(f.getPosition());
        c.setTarget(sm, map);
        f.putCargo(c);

        /* Verify that the worker is idle */
        assertNull(w.getCargo());
        assertNull(w.getTargetFlag());

        assertEquals(w.getPosition(), f.getPosition());
        assertTrue(w.isArrived());
        assertFalse(w.isTraveling());
        assertTrue(f.hasCargoWaitingForRoad(w.getAssignedRoad()));

        /* Verify that the worker picks up the cargo and has the sawmill as target */
        map.stepTime();
        assertNotNull(w.getCargo());

        Cargo tmp = w.getCargo();
        assertEquals(c, tmp);
        assertEquals(w.getTargetFlag(), sm.getFlag());
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

        map.placeFlag(src);
        map.placeBuilding(wc, wcPoint);

        r = map.placeAutoSelectedRoad(src, wc.getFlag());
        map.placeWorker(w, src);

        map.assignCourierToRoad(w, r);

        c = new Cargo(PLANCK);

        src.putCargo(c);
        c.setTarget(wc, map);

        w.pickUpCargoForRoad(src, r);

        /* Move worker to the sawmill */
        fastForward(40, w, wc);
        
        assertTrue(w.isAt(wc.getFlag().getPosition()));

        /* Verify the worker delivers the cargo when it has reached the target */
        fastForward(100, w, wc);
        assertNull(w.getCargo());
        assertTrue(w.isAt(wc.getFlag().getPosition()));
        assertTrue(wc.getMaterialInQueue(PLANCK) == 1);

        /* Verify that deliverForWorkersAtTarget gets the worker to deliver the cargo */
        map.stepTime();

        assertNull(w.getCargo());
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
        assertTrue(map.getAllWorkers().isEmpty());
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        assertTrue(map.getAllWorkers().isEmpty());

        /* Construct a road without any courier assigned */
        Road r  = map.placeAutoSelectedRoad(hq.getFlag(), bk.getFlag());
        Road r2 = map.placeAutoSelectedRoad(hq.getFlag(), fHut.getFlag());

        assertTrue(map.getAllWorkers().isEmpty());

        /* Prep the headquarter's inventory */
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));

        assertTrue(hq.getAmount(PRIVATE) == 3);

        hq.depositWorker(new Forester());

        assertTrue(hq.getAmount(FORESTER) == 3);
        assertTrue(map.getAllWorkers().isEmpty());
        
        /* Assign new workers to unoccupied places and verify that there is a 
         * worker designated for the road. There should be no worker for the 
         * barracks as it's not finished yet
         */
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        // TODO: Ensure that only one worker is dispatched even though there are two unassigned roads
        assertTrue(map.getAllWorkers().size() == 2);
        
        Worker w1 = map.getAllWorkers().get(0);
        Worker w2 = map.getAllWorkers().get(1);
        
        assertTrue(w1.isAt(hq.getFlag().getPosition()));
        assertTrue(w1.getAssignedRoad().equals(r));
        
        assertTrue(w2.isAt(hq.getFlag().getPosition()));
        assertTrue(w2.getAssignedRoad().equals(r2));
        
//        assertFalse(w1.isTraveling());
        assertTrue(map.getRoadsThatNeedCouriers().isEmpty());
        assertEquals(map.getRoad(hq.getFlag().getPosition(), bk.getFlag().getPosition()), r);
        assertNotNull(r.getCourier().getAssignedRoad());

        assertTrue(r.getCourier().equals(w1));
        assertTrue(r.getCourier().getAssignedRoad().equals(r));

        /* Finish the construction of the barracks and assign new workers to 
         * unoccupied places and verify that there is a military assigned
         * to occupy the barracks
         */
        Utils.constructSmallHouse(bk);

        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(bk.isMilitaryBuilding());
        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 0);
        assertTrue(hq.getAmount(Material.PRIVATE) == 3);

        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(map.getAllWorkers().get(2) instanceof Military);
        assertFalse(map.getAllWorkers().get(2).isArrived());
        assertTrue(map.getAllWorkers().get(2).isTraveling());

        assertTrue(map.getTravelingWorkers().size() == 1);

        assertTrue(hq.getAmount(Material.PRIVATE) == 2);

        /* Let the military reach the barracks */
        Utils.fastForward(100, map);

        assertTrue(map.getTravelingWorkers().isEmpty());
        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(map.getAllWorkers().get(2).isArrived());
        assertFalse(map.getAllWorkers().get(2).isTraveling());

        /* Make traveling workers that have arrived enter their building or road */
        //gameLogic.assignTravelingWorkersThatHaveArrived(map);

        assertTrue(map.getTravelingWorkers().isEmpty());
        assertTrue(bk.needMilitaryManning());
        assertTrue(bk.getHostedMilitary() == 1);

        /* Assign new workers again to see that a second military is dispatched
         * for the barracks
         */
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getAllWorkers().get(3) instanceof Military);
        assertFalse(map.getAllWorkers().get(3).isArrived());
        assertTrue(map.getAllWorkers().get(3).isTraveling());
        assertTrue(map.getTravelingWorkers().size() == 1);

        assertFalse(bk.needMilitaryManning());

        assertTrue(hq.getAmount(Material.PRIVATE) == 1);

        /* Let the military reach the barracks */
        Utils.fastForward(100, map);

        assertTrue(map.getTravelingWorkers().isEmpty());
        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getAllWorkers().get(3).isArrived());
        assertFalse(map.getAllWorkers().get(3).isTraveling());
        assertTrue(bk.getHostedMilitary() == 2);
        assertTrue(map.getTravelingWorkers().isEmpty());

        /* Assign new workers to unoccupied buildings again. There is building
         * or road that requires a worker so this should have no effect
         */
        assertTrue(map.getAllWorkers().size() == 4);
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getTravelingWorkers().isEmpty());

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

        assertTrue(map.getAllWorkers().size() == 5);
        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getTravelingWorkers().get(0) instanceof Forester);
        assertTrue(map.getTravelingWorkers().get(0).isTraveling());
        assertFalse(fHut.needsWorker());
        Worker forester = map.getTravelingWorkers().get(0);

        /* Let the forester reach the forester hut */
        Utils.fastForward(120, map);
        
        assertNotNull(fHut.getWorker());
        assertTrue(fHut.getWorker() instanceof Forester);
        assertTrue(map.getTravelingWorkers().isEmpty());
    }
    
    @Test
    public void testInitiateNewDeliveriesForAllStoragesWithNoRoad() throws Exception {
        GameMap map    = new GameMap(30, 30);
        Point hqPoint  = new Point(5, 5);
        Headquarter hq = new Headquarter();
        Woodcutter wc  = new Woodcutter();
        Point wcPoint  = new Point(5, 11);
        
        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(wc, wcPoint);
        
        assertTrue(map.getBuildingsWithinReach(hq.getFlag()).size() == 1);
        assertTrue(map.getBuildingsWithinReach(hq.getFlag()).contains(hq));
        
        gameLogic.initiateNewDeliveriesForAllStorages(map);
    }
}
