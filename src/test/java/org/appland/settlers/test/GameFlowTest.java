package org.appland.settlers.test;

import java.util.Collection;
import java.util.List;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
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
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.fastForward;

import static org.appland.settlers.test.Utils.fastForward;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GameFlowTest {
    private GameLogic gameLogic;

    @Before
    public void setup() {
        gameLogic = new GameLogic();
    }
    
    @Test
    public void gameFlowTest() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        // TODO: RE-verify and add asserts!
        /* Create starting position */
        GameMap map = new GameMap();
        Headquarter hq = new Headquarter();
        Point startPosition = new Point(6, 6);

        /* Game loop */
        gameLogic.gameLoop(map);

        /* Player creates woodcutter, sawmill and quarry */
        Building wc = new Woodcutter();
        Sawmill sm = new Sawmill();
        Quarry qry = new Quarry();

        Point wcSpot = new Point(6, 8);
        Point smSpot = new Point(8, 6);
        Point qrySpot = new Point(4, 6);

        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);
        map.placeBuilding(hq, startPosition);

        /* Create roads */
        Road wcToHqRoad = new Road(hq.getFlag(), wc.getFlag());
        Road smToHqRoad = new Road(hq.getFlag(), sm.getFlag());
        Road qryToHqRoad = new Road(hq.getFlag(), qry.getFlag());

        map.placeRoad(wcToHqRoad);
        map.placeRoad(smToHqRoad);
        map.placeRoad(qryToHqRoad);

        /* Assign workers to the roads */
        Courier wr1 = new Courier(map);
        Courier wr2 = new Courier(map);
        Courier wr3 = new Courier(map);

        map.placeWorker(wr1, wc.getFlag());
        map.placeWorker(wr2, sm.getFlag());
        map.placeWorker(wr3, qry.getFlag());

        map.assignWorkerToRoad(wr1, wcToHqRoad);
        map.assignWorkerToRoad(wr2, smToHqRoad);
        map.assignWorkerToRoad(wr3, qryToHqRoad);

	// TODO: add that workers need to populate the buildings before they start producing
        // TODO: add that workers need to move to roads to populate them
        /* Move forward in time until the small buildings are done */
        // TODO: Change to deliver required material for construction
        Utils.constructSmallHouse(wc);
        Utils.constructSmallHouse(qry);
        Utils.constructMediumHouse(sm);

        assertTrue(wc.getConstructionState() == DONE);
        assertTrue(qry.getConstructionState() == DONE);
        assertTrue(sm.getConstructionState() == DONE);

        /* Fast forward until the woodcutter has cut some wood */
        fastForward(100, map);

        /* Retrieve cargo from woodcutter and put it on the flag */
        gameLogic.initiateCollectionOfNewProduce(map);

        /* Transport cargo one hop */
        gameLogic.assignWorkToIdleCouriers(map);

        assertNotNull(map.getWorkerForRoad(wcToHqRoad).getCargo());
        assertTrue(map.getWorkerForRoad(wcToHqRoad).getTarget().equals(hq.getFlag()));

        fastForward(100, map);

        assertTrue(map.getWorkerForRoad(wcToHqRoad).getPosition().equals(hq.getFlag()));

        /* Cargo has arrived at its target so store it */
        gameLogic.deliverForWorkersAtTarget(map);

        /* Find out who needs the wood */
        gameLogic.initiateNewDeliveriesForAllStorages(map);

        assertTrue(hq.getFlag().getStackedCargo().get(0).getMaterial() == WOOD);
        assertTrue(hq.getFlag().getStackedCargo().get(0).getTarget().equals(sm));
        assertNull(map.getWorkerForRoad(smToHqRoad).getCargo());
        assertTrue(hq.getFlag().getStackedCargo().get(0).getPlannedRoads().get(0).equals(smToHqRoad));
        assertTrue(hq.getFlag().hasCargoWaitingForRoad(smToHqRoad));

        /* Get the wood transported to the sawmill */
        gameLogic.assignWorkToIdleCouriers(map);

        assertNotNull(map.getWorkerForRoad(smToHqRoad).getCargo());
        assertTrue(map.getWorkerForRoad(smToHqRoad).getCargo().getTarget().equals(sm));
        assertTrue(map.getWorkerForRoad(smToHqRoad).getTarget().equals(sm.getFlag()));
        assertTrue(map.getWorkerForRoad(smToHqRoad).getCargo().getMaterial() == WOOD);

        fastForward(100, map);

        /* Cargo has arrived at its target so store it */
        assertTrue(map.getWorkerForRoad(smToHqRoad).getPosition().equals(sm.getFlag()));

        gameLogic.deliverForWorkersAtTarget(map);

        assertTrue(sm.getMaterialInQueue(WOOD) == 1);

        /* Produce plancks in sawmill */
        assertFalse(sm.isCargoReady());

        fastForward(100, map);

        assertTrue(sm.isCargoReady());
        assertTrue(sm.getMaterialInQueue(WOOD) == 0);

        gameLogic.initiateCollectionOfNewProduce(map);

        /* Transport plancks to nearest storage */
        gameLogic.assignWorkToIdleCouriers(map);

        fastForward(10, map);

    }

    @Test
    public void gameFlowWithProperGameLoopTest() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        /* Create Initial Game Setup */
        GameMap map = new GameMap();
        Headquarter hq = new Headquarter();

        Point startPosition = new Point(6, 6);

        /* Game loop */
        gameLogic.gameLoop(map);

        /* Player creates woodcutter, sawmill and quarry */
        Building wc = new Woodcutter();
        Sawmill sm = new Sawmill();
        Quarry qry = new Quarry();

        Point wcSpot = new Point(6, 8);
        Point smSpot = new Point(8, 6);
        Point qrySpot = new Point(4, 6);

        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);
        map.placeBuilding(hq, startPosition);

        /* Create roads */
        map.placeRoad(hq.getFlag(), wc.getFlag());
        map.placeRoad(hq.getFlag(), sm.getFlag());
        map.placeRoad(hq.getFlag(), qry.getFlag());

        /* Assign workers to the roads */
        Courier wr1 = new Courier(map);
        Courier wr2 = new Courier(map);
        Courier wr3 = new Courier(map);

        Road r = map.getRoad(hq.getFlag(), wc.getFlag());
        map.placeWorker(wr1, wc.getFlag());
        map.assignWorkerToRoad(wr1, r);

        r = map.getRoad(hq.getFlag(), sm.getFlag());
        map.placeWorker(wr2, sm.getFlag());
        map.assignWorkerToRoad(wr2, r);

        r = map.getRoad(hq.getFlag(), qry.getFlag());
        map.placeWorker(wr3, qry.getFlag());
        map.assignWorkerToRoad(wr3, r);

	// TODO: add that workers need to populate the buildings before they start producing
        // TODO: add that workers need to move to roads to populate them
        /* Buildings, roads, workers done - test that construction happens */
        assertTrue(wc.getConstructionState() == UNDER_CONSTRUCTION);
        assertTrue(qry.getConstructionState() == UNDER_CONSTRUCTION);
        assertTrue(sm.getConstructionState() == UNDER_CONSTRUCTION);

        /* Add more plancks and stone to the HQ inventory */
        Utils.fillUpInventory(hq, PLANCK, 10);
        Utils.fillUpInventory(hq, STONE, 10);

        /* -- Assert that all workers are idle */
        Collection<Courier> workers = map.getCourierAssignedToRoads();

        for (Courier w : workers) {
            assertNull(w.getCargo());
            assertNull(w.getTarget());
        }

        /* Gameloop */
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        gameLogic.gameLoop(map);

        List<Cargo> hqOutCargos = hq.getFlag().getStackedCargo();
        assertTrue(hqOutCargos.size() == 1);
        Cargo c = hqOutCargos.get(0);
        assertTrue(c.getMaterial() == PLANCK || c.getMaterial() == STONE);
        assertFalse(c.getTarget().equals(hq));

        /* Gameloop */
        fastForward(10, map);
        gameLogic.gameLoop(map);

        /* -- Assert that delivery is started for one cargo */
        workers = map.getCourierAssignedToRoads();

        int busyWorkers = 0;
        for (Courier w : workers) {
            if (w.getCargo() != null) {
                busyWorkers++;
            }
        }

        assertTrue(busyWorkers == 1);

        /* Gameloop */
        fastForward(10, map);
        gameLogic.gameLoop(map);

        /* Ensure first cargo is delivered to house */
        /* Ensure next cargo is started */
    }

    @Test
    public void testInitiateCollectionOfNewProduce() throws InvalidEndPointException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidRouteException, Exception {
        GameMap map = new GameMap();
        Woodcutter wc = new Woodcutter();
        Storage stg = new Storage();
        Point stgPoint = new Point(1, 1);
        Point wcPoint = new Point(2, 2);
        Road r;
        Courier w = new Courier(map);

        map.placeBuilding(wc, wcPoint);
        map.placeBuilding(stg, stgPoint);

        r = new Road(stg.getFlag(), wc.getFlag());

        map.placeRoad(r);
        map.placeWorker(w, stg.getFlag());
        map.assignWorkerToRoad(w, r);

        Utils.constructSmallHouse(wc);

        /* Fast forward until the woodcutter has produced a cargo with WOOD */
        fastForward(100, wc, w);
        assertTrue(wc.isCargoReady());
        assertNull(w.getCargo());

        /* Verify that the worker doesn't pick up the cargo by himself as time passes */
        fastForward(100, wc, w);
        assertTrue(wc.isCargoReady());
        assertNull(w.getCargo());
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());

        /* Verify that initiateCollectionOfNewProduce gets the worker to pick up the cargo */
        gameLogic.initiateCollectionOfNewProduce(map);
        assertFalse(wc.isCargoReady());
        assertNull(w.getCargo());
        assertFalse(wc.getFlag().getStackedCargo().isEmpty());

        Cargo c = wc.getFlag().getStackedCargo().get(0);
        assertTrue(c.getTarget().equals(stg));
        assertTrue(c.getMaterial() == WOOD);
    }

    @Test
    public void testInitiateNewDeliveries() throws InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, InvalidEndPointException, Exception {
        GameMap map = new GameMap();

        Headquarter hq = new Headquarter();
        Woodcutter wc = new Woodcutter();

        Point hqPoint = new Point(1, 1);
        Point wcPoint = new Point(2, 2);

        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(wc, wcPoint);

        map.placeRoad(hq.getFlag(), wc.getFlag());

        Utils.constructSmallHouse(wc);

        /* Since the woodcutter is finished it does not need any deliveries
         * Verify that no new deliveries are initiated
         */
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());
        gameLogic.initiateNewDeliveriesForStorage(hq, map);
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        /* Place an unfinished sawmill on the map and verify that it needs deliveries */
        Sawmill sm = new Sawmill();

        Point smPoint = new Point(1, 2);

        map.placeBuilding(sm, smPoint);

        map.placeRoad(hq.getFlag(), sm.getFlag());

        /* Verify that a new delivery is initiated for the sawmill */
        assertTrue(sm.needsMaterial(PLANCK));
        assertTrue(sm.needsMaterial(STONE));
        assertTrue(hq.getFlag().getStackedCargo().isEmpty());

        gameLogic.initiateNewDeliveriesForStorage(hq, map);
        assertFalse(hq.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testAssignWorkToIdleCouriers() throws InvalidEndPointException, InvalidRouteException, Exception {
        GameMap map = new GameMap();
        Sawmill sm = new Sawmill();
        Point smPoint = new Point(1, 1);
        Flag f = new Flag(2, 2);
        Road r = new Road(f, sm.getFlag());
        Courier w = new Courier(map);

        map.placeFlag(f);
        map.placeBuilding(sm, smPoint);
        map.placeRoad(r);
        map.placeWorker(w, f);
        map.assignWorkerToRoad(w, r);

        Cargo c = new Cargo(PLANCK);

        c.setPosition(f);
        c.setTarget(sm, map);
        f.putCargo(c);

        /* Verify that the worker is idle */
        assertNull(w.getCargo());
        assertNull(w.getTarget());

        Utils.fastForward(100, w);
        assertNull(w.getCargo());
        assertNull(w.getTarget());

        /* Verify that the worker picks up the cargo and has the sawmill as target */
        gameLogic.assignWorkToIdleCouriers(map);
        assertNotNull(w.getCargo());

        Cargo tmp = w.getCargo();
        assertEquals(c, tmp);
        assertEquals(w.getTarget(), sm.getFlag());
    }

    @Test
    public void testDeliverForWorkersAtTarget() throws InvalidEndPointException, InvalidRouteException, InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        GameMap map = new GameMap();
        Woodcutter wc = new Woodcutter();
        Flag src = new Flag(1, 1);
        Point wcPoint = new Point(2, 2);
        Courier w = new Courier(map);
        Cargo c;

        map.placeFlag(src);
        map.placeBuilding(wc, wcPoint);

        Road r = new Road(src, wc.getFlag());

        map.placeRoad(r);
        map.placeWorker(w, src);

        map.assignWorkerToRoad(w, r);

        c = new Cargo(PLANCK);

        src.putCargo(c);
        c.setTarget(wc, map);

        w.pickUpCargoForRoad(src, r);

        /* Move worker to the sawmill */
        fastForward(10, w, wc);
        assertTrue(w.getPosition().equals(wc.getFlag()));

        /* Verify that fast forwarding does not get the worker to deliver the cargo */
        fastForward(100, w, wc);
        assertEquals(w.getCargo(), c);
        assertTrue(w.getPosition().equals(wc.getFlag()));
        assertTrue(wc.getMaterialInQueue(PLANCK) == 0);

        /* Verify that deliverForWorkersAtTarget gets the worker to deliver the cargo */
        gameLogic.deliverForWorkersAtTarget(map);

        assertNull(w.getCargo());
        assertTrue(wc.getMaterialInQueue(PLANCK) == 1);
    }

    @Test
    public void testAssignNewWorkerToUnoccupiedPlaces() throws Exception {
        GameMap map = new GameMap();

        Headquarter hq = new Headquarter();
        Barracks bk = new Barracks();
        ForesterHut fHut = new ForesterHut();

        Point hqPoint = new Point(1, 1);
        Point wcPoint = new Point(2, 2);
        Point fhPoint = new Point(1, 2);

        map.placeBuilding(hq, hqPoint);
        map.placeBuilding(bk, wcPoint);
        map.placeBuilding(fHut, fhPoint);

        /* Assign new workers to unocupied places. Since there are no places 
         * that require workers this should not do anything*/
        assertTrue(map.getAllWorkers().isEmpty());
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);
        assertTrue(map.getAllWorkers().isEmpty());

        /* Construct a road without any courier assigned */
        Road r = new Road(hq.getFlag(), bk.getFlag());
        Road r2 = new Road(bk.getFlag(), fHut.getFlag());

        map.placeRoad(r);
        map.placeRoad(r2);

        assertTrue(map.getAllWorkers().isEmpty());

        /* Prep the headquarter's inventory */
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));
        hq.depositWorker(new Military(Rank.PRIVATE_RANK));

        assertTrue(hq.getAmount(PRIVATE) == 3);

        hq.depositWorker(new Forester());

        assertTrue(hq.getAmount(FORESTER) == 1);

        /* Assign new workers to unoccupied places and verify that there is a 
         * worker designated for the road. There should be no worker for the 
         * barracks as it's not finished yet
         */
        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        // TODO: Ensure that only one worker is dispatched even though there are two unassigned roads
        assertTrue(map.getAllWorkers().size() == 2);
        Worker w1 = map.getAllWorkers().get(0);

        assertTrue(w1.getPosition().equals(hq.getFlag()));
        assertTrue(w1.getTargetRoad().equals(r));

        /* Fast forward to let the couriers reach their roads */
        fastForward(100, map);

        assertTrue(map.getTravelingWorkers().size() == 2);
        assertTrue(map.getTravelingWorkers().get(0).equals(w1));
        assertTrue(w1.isArrived());
        assertTrue(w1.isTraveling());

        /* Assign arrived workers to tasks */
        assertTrue(map.getTravelingWorkers().contains(w1));
        assertTrue(w1.getTargetRoad().equals(r));

        gameLogic.assignTravelingWorkersThatHaveArrived(map);

        assertFalse(w1.isTraveling());
        assertTrue(map.getRoadsWithoutWorker().isEmpty());
        assertEquals(map.getRoad(hq.getFlag(), bk.getFlag()), r);
        assertNotNull(r.getCourier().getRoad());

        assertTrue(r.getCourier().equals(w1));
        assertTrue(r.getCourier().getRoad().equals(r));

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

        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getAllWorkers().size() == 3);
        assertTrue(map.getAllWorkers().get(2).isArrived());
        assertTrue(map.getAllWorkers().get(2).isTraveling());

        /* Make traveling workers that have arrived enter their building or road */
        gameLogic.assignTravelingWorkersThatHaveArrived(map);

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

        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getAllWorkers().size() == 4);
        assertTrue(map.getAllWorkers().get(3).isArrived());
        assertTrue(map.getAllWorkers().get(3).isTraveling());
        assertTrue(bk.getHostedMilitary() == 1);

        /* Make traveling workers that have arrived at their assigned
         * buildings or roads enter
         */
        gameLogic.assignTravelingWorkersThatHaveArrived(map);

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
        assertTrue(hq.getAmount(FORESTER) == 1);
        assertTrue(fHut.needsWorker(FORESTER));

        gameLogic.assignNewWorkerToUnoccupiedPlaces(map);

        assertTrue(map.getAllWorkers().size() == 5);
        assertTrue(map.getTravelingWorkers().size() == 1);
        assertTrue(map.getTravelingWorkers().get(0) instanceof Forester);
        assertTrue(map.getTravelingWorkers().get(0).isTraveling());
        assertFalse(fHut.needsWorker());

        /* Let the forester reach the forester hut */
        Utils.fastForward(100, map);

        assertTrue(map.getTravelingWorkers().get(0).isArrived());
        assertNull(fHut.getWorker());

        /* Make traveling workers that have arrived at their assigned 
         * buildings or roads enter
         */
        gameLogic.assignTravelingWorkersThatHaveArrived(map);

        assertNotNull(fHut.getWorker());
        assertTrue(fHut.getWorker() instanceof Forester);
        assertTrue(map.getTravelingWorkers().isEmpty());
    }
}
