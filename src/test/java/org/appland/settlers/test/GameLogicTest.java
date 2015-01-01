package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import org.appland.settlers.model.Barracks;
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
import static org.appland.settlers.model.Material.FORESTER;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
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

import org.junit.Test;

public class GameLogicTest {

    @Test
    public void testInitiateNewDeliveries() throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidEndPointException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,30, 30);

        Headquarter hq = new Headquarter(player0);
        Woodcutter wc = new Woodcutter(player0);

        Point hqPoint = new Point(5, 5);
        Point wcPoint = new Point(5, 11);

        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(wc, wcPoint);

        map.placeAutoSelectedRoad(player0, hq.getFlag(), wc.getFlag());

        Utils.constructHouse(wc, map);

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
        Sawmill sm = new Sawmill(player0);

        Point smPoint = new Point(10, 10);

        map.placeBuilding(sm, smPoint);

        map.placeAutoSelectedRoad(player0, hq.getFlag(), sm.getFlag());

        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sm.needsMaterial(PLANCK));
        assertTrue(sm.needsMaterial(STONE));
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        
        map.stepTime();

        assertNotNull(hq.getWorker().getCargo());
    }

    @Test
    public void testAssignWorkToIdleCouriers() throws InvalidEndPointException, InvalidRouteException, Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map   = new GameMap(players, 30, 30);
        Sawmill sm    = new Sawmill(player0);
        Point smPoint = new Point(5, 5);
        Point fp      = new Point(10, 10);
        Courier courier     = new Courier(map);
        Road r;
                
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Flag f = map.placeFlag(player0, fp);
        map.placeBuilding(sm, smPoint);
        r = map.placeAutoSelectedRoad(player0, f, sm.getFlag());
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        
        GameMap map   = new GameMap(players, 30, 30);
        Woodcutter wc = new Woodcutter(player0);
        Point srcPnt  = new Point(5, 5);
        Point wcPoint = new Point(11, 5);
        Courier w     = new Courier(map);
        Cargo c;
        Road r;
        
        Point hqPoint = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), hqPoint);
        
        Flag src = map.placeFlag(player0, srcPnt);
        map.placeBuilding(wc, wcPoint);

        r = map.placeAutoSelectedRoad(player0, src, wc.getFlag());
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
        assertEquals(wc.getAmount(PLANCK), 1);
    }

    @Test
    public void testAssignNewWorkerToUnoccupiedPlaces() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players,30, 30);

        Headquarter hq = new Headquarter(player0);
        Barracks bk = new Barracks(player0);
        ForesterHut fHut = new ForesterHut(player0);

        Point hqPoint = new Point(5, 5);
        Point wcPoint = new Point(6, 10);
        Point fhPoint = new Point(5, 17);

        map.placeBuilding(hq, hqPoint);
        Flag bkFlag = map.placeFlag(player0, wcPoint.downRight());
        Flag hutFlag = map.placeFlag(player0, fhPoint.downRight());

        /* Assign new workers to unocupied places. Since there are no places 
         * that require workers this should not do anything*/
        assertEquals(map.getWorkers().size(), 1);
        
        /* Step time to make the headquarter assign new workers */
        Utils.fastForward(3, map);
        
        assertEquals(map.getWorkers().size(), 1);

        /* Construct a road without any courier assigned */
        Road r  = map.placeAutoSelectedRoad(player0, hq.getFlag(), bkFlag);
        Road r2 = map.placeAutoSelectedRoad(player0, hq.getFlag(), hutFlag);

        assertEquals(map.getWorkers().size(), 1);

        /* Prep the headquarter's inventory */
        assertEquals(hq.getAmount(PRIVATE), 10);

        hq.depositWorker(new Forester(map));

        assertEquals(hq.getAmount(FORESTER), 3);
        assertEquals(map.getWorkers().size(), 1);
        
        /* Assign new workers to unoccupied places and verify that there is a 
         * worker designated for the road. There should be no worker for the 
         * barracks as it's not finished yet
         */
        Utils.fastForward(3, map);

        // TODO: Ensure that only one worker is dispatched even though there are two unassigned roads
        assertEquals(map.getWorkers().size(), 3);
        
        assertTrue(map.getWorkers().get(1) instanceof Courier);
        assertTrue(map.getWorkers().get(2) instanceof Courier);
        
        Courier w1 = (Courier)map.getWorkers().get(1);
        Courier w2 = (Courier)map.getWorkers().get(2);

        
        /* Fast forward to let the couriers reach their roads */
        Utils.fastForwardUntilWorkersReachTarget(map, w1, w2);
        
        assertTrue(w1.getAssignedRoad().equals(r));
        
        assertTrue(w2.getAssignedRoad().equals(r2));
        
        assertEquals(map.getRoad(hq.getFlag().getPosition(), bkFlag.getPosition()), r);
        assertNotNull(r.getCourier().getAssignedRoad());

        assertTrue(r.getCourier().equals(w1));
        assertTrue(r.getCourier().getAssignedRoad().equals(r));
        
        assertEquals(map.getWorkers().size(), 3);

        /* Construct the barracks */
        assertTrue(bk.underConstruction());
        assertTrue(bk.isMilitaryBuilding());
        assertEquals(hq.getAmount(Material.PRIVATE), 10);

        map.placeBuilding(bk, wcPoint);
        Utils.constructHouse(bk, map);

        assertTrue(bk.needsMilitaryManning());
        assertEquals(bk.getHostedMilitary(), 0);

        /* Step time to make the headquarter assign new workers */
        map.stepTime();

        assertEquals(map.getWorkers().size(), 4);
        assertTrue(map.getWorkers().get(3) instanceof Military);
        assertFalse(map.getWorkers().get(3).isArrived());
        assertTrue(map.getWorkers().get(3).isTraveling());

        assertEquals(hq.getAmount(Material.PRIVATE), 9);

        /* Let the military reach the barracks */
        Utils.fastForwardUntilWorkersReachTarget(map, map.getWorkers().get(3));

        assertEquals(map.getWorkers().size(), 5);
        assertTrue(map.getWorkers().get(3).isArrived());
        assertFalse(map.getWorkers().get(3).isTraveling());

        /* Make traveling workers that have arrived enter their building or road */

        Utils.fastForwardUntilWorkersReachTarget(map, map.getWorkers().get(4));
        
        assertFalse(bk.needsMilitaryManning());
        assertEquals(bk.getHostedMilitary(), 2);

        /* Assign new workers to unoccupied buildings again. There is building
         * or road that requires a worker so this should have no effect
         */
        assertEquals(map.getWorkers().size(), 5);

        map.stepTime();
        
        assertEquals(map.getWorkers().size(), 5);

        /* Finish construction of the forester hut which requires a 
         * forester worker to function
         */
        map.placeBuilding(fHut, fhPoint);
        Utils.constructHouse(fHut, map);

        assertTrue(fHut.needsWorker());

        /* Assign new workers to unoccupied buildings and roads. The forester
         * hut needs a forester so a forester should be dispatched from the hq
         */
        assertEquals(hq.getAmount(FORESTER), 3);
        assertTrue(fHut.needsWorker());

        /* Step time to make the headquarter assign new workers */
        map.stepTime();

        assertEquals(map.getWorkers().size(), 6);
        assertFalse(fHut.needsWorker());
        
        Worker forester = null;
        for (Worker w : map.getWorkers()) {
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
