/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.Collection;
import java.util.List;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidStateForProduction;
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Woodcutter;
import static org.appland.settlers.test.Utils.fastForward;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class TestScenarios {

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

}
