/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
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
import static org.appland.settlers.model.Material.PLANCK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.LARGE;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import static org.appland.settlers.test.Utils.fastForward;
import static org.junit.Assert.assertEquals;
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
    public void productionAndTransportForWoodcutterAndSawmill() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        /*   --   SETUP   --   */
        
        // TODO: RE-verify and add asserts!
        /* Create starting position */
        GameMap map = new GameMap(30, 30);
        Storage hq = new Storage();
        Point startPosition = new Point(6, 6);

        /* Game loop */
        gameLogic.gameLoop(map);

        /* Player creates woodcutter, sawmill and quarry */
        Building wc = new Woodcutter();
        Sawmill sm = new Sawmill();
        Quarry qry = new Quarry();

        Point wcSpot = new Point(6, 12);
        Point smSpot = new Point(12, 6);
        Point qrySpot = new Point(20, 6);

        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);
        map.placeBuilding(hq, startPosition);

        /* Create roads */
        Road wcToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());
        Road smToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), sm.getFlag());
        Road qryToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), qry.getFlag());

        /* Assign workers to the roads */
        Courier wr1 = new Courier(map);
        Courier wr2 = new Courier(map);
        Courier wr3 = new Courier(map);

        map.placeWorker(wr1, wc.getFlag());
        map.placeWorker(wr2, sm.getFlag());
        map.placeWorker(wr3, qry.getFlag());

        map.assignCourierToRoad(wr1, wcToHqRoad);
        map.assignCourierToRoad(wr2, smToHqRoad);
        map.assignCourierToRoad(wr3, qryToHqRoad);

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

        assertTrue(hq.getAmount(WOOD) == 0);
        assertTrue(hq.getAmount(PLANCK) == 0);
        assertTrue(hq.getAmount(STONE) == 0);
        
        
        
        /*   --   START TEST   --   */
        
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

        /* Cargo has arrived at the headquarter so store it */
        gameLogic.deliverForWorkersAtTarget(map);
        
        assertNull(map.getWorkerForRoad(wcToHqRoad).getCargo());
        assertTrue(hq.getAmount(WOOD) == 1);

        /* Find out that the sawmill needs the wood */
        gameLogic.initiateNewDeliveriesForAllStorages(map);

        assertTrue(hq.getFlag().getStackedCargo().get(0).getMaterial() == WOOD);
        assertTrue(hq.getFlag().getStackedCargo().get(0).getTarget().equals(sm));
        assertNull(map.getWorkerForRoad(smToHqRoad).getCargo());
        assertTrue(hq.getFlag().getStackedCargo().get(0).getPlannedRoads().get(0).equals(smToHqRoad));
        assertTrue(hq.getFlag().hasCargoWaitingForRoad(smToHqRoad));
        assertTrue(hq.getAmount(WOOD) == 0);

        /* Get the wood transported to the sawmill */
        gameLogic.assignWorkToIdleCouriers(map);

        assertNotNull(map.getWorkerForRoad(smToHqRoad).getCargo());
        assertTrue(map.getWorkerForRoad(smToHqRoad).getCargo().getTarget().equals(sm));
        assertTrue(map.getWorkerForRoad(smToHqRoad).getTarget().equals(sm.getFlag()));
        assertTrue(map.getWorkerForRoad(smToHqRoad).getCargo().getMaterial() == WOOD);

        fastForward(100, map);

        /* Cargo has arrived at the sawmill so deliver it */
        assertTrue(map.getWorkerForRoad(smToHqRoad).getPosition().equals(sm.getFlag()));

        gameLogic.deliverForWorkersAtTarget(map);

        assertTrue(sm.getMaterialInQueue(WOOD) == 1);

        /* Produce plancks in sawmill */
        assertFalse(sm.isCargoReady());

        fastForward(100, map);

        assertTrue(sm.isCargoReady());
        assertTrue(sm.getMaterialInQueue(WOOD) == 0);

        gameLogic.initiateCollectionOfNewProduce(map);

        /* Transport plancks and new wood to nearest storage*/
        gameLogic.assignWorkToIdleCouriers(map);

        assertNotNull(map.getWorkerForRoad(smToHqRoad).getCargo());
        assertNotNull(map.getWorkerForRoad(smToHqRoad).getCargo().getMaterial() == PLANCK);
        
        assertNotNull(map.getWorkerForRoad(wcToHqRoad).getCargo());
        assertNotNull(map.getWorkerForRoad(wcToHqRoad).getCargo().getMaterial() == WOOD);
        
        fastForward(10, map);

        /* Cargo has arrived at the headquarter so store it */
        assertTrue(map.getWorkerForRoad(smToHqRoad).getPosition().equals(hq.getFlag()));
        
        gameLogic.deliverForWorkersAtTarget(map);
        
        assertTrue(hq.getAmount(PLANCK) == 1);
        assertTrue(hq.getAmount(WOOD) == 1);
    }

    @Test
    public void buildWoodcutterSawmillQuarrySequenciallyFromScratch() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        /*   --   SETUP   --   */
        
        
        /* Create Initial Game Setup */
        GameMap map = new GameMap(30, 30);
        Headquarter hq = new Headquarter();

        Point startPosition = new Point(6, 6);

        map.placeBuilding(hq, startPosition);


        /*   --   START TEST   --   */

        gameLogic.gameLoop(map);
        fastForward(100, map);

        // TODO: assert that nothing happens
        
        /* Player creates woodcutter */
        Building wc = new Woodcutter();
        Point wcSpot = new Point(6, 12);

        map.placeBuilding(wc, wcSpot);

        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        /* Player creates road between hq and wc */
        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());

        // TODO: assert that the road is unoccupied
        
        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        // TODO: assert that the road is occupied
        
        /* The road is occupied so the delivery of plancks and stone to the wc can start  */
        // TODO: assert that the wc is under construction and has no material yet
        
        gameLogic.gameLoop(map);
        fastForward(100, map);

        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        /* The woodcutter has all material so construction can finish */
        fastForward(150, map);
        
        // TODO: assert that the woodcutter is finished
        
        // TODO: construct remaining houses, wait ~10 turns for production, construct barracks
    
    }

    @Test
    public void testGameStartFromScratchWithUserInput() throws Exception {
        /*   --   SETUP   --   */
        
        
        /* Create Initial Game Setup */
        GameMap map = new GameMap(30, 30);
        Headquarter hq = new Headquarter();

        Point startPosition = new Point(15, 15);

        map.placeBuilding(hq, startPosition);

        /*   --   User    --   */

        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        /*   --   Create woodcutter   --  */
        
        /*  - List all house spots -  */
        Map<Point, Size> possibleHouseSpots = map.getAvailableHousePoints();
        
        assertTrue(possibleHouseSpots.containsKey(new Point (22, 20)));
        assertTrue(possibleHouseSpots.get(new Point(22, 20)) == LARGE);
        
        /*  - Pick 22, 20 -  */        
        Woodcutter wc      = new Woodcutter();
        Point      wcPoint = new Point(22, 20);
        
        map.placeBuilding(wc, wcPoint);

        gameLogic.gameLoop(map);
        fastForward(100, map);
        
        /*   --   Create road to woodcutter   --   */
        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        
    
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*   --   Create sawmill   --   */
        
        /*  - List all house spots -  */
        possibleHouseSpots = map.getAvailableHousePoints();
        
        assertTrue(possibleHouseSpots.containsKey(new Point (10, 10)));
        assertTrue(possibleHouseSpots.get(new Point(10, 10)) == LARGE);        
        
        /*  - Pick 10, 10 -  */
        Sawmill sm      = new Sawmill();
        Point   smPoint = new Point(10, 10);
        
        map.placeBuilding(sm, smPoint);

        gameLogic.gameLoop(map);
        fastForward(100, map);        
    
        gameLogic.gameLoop(map);
        fastForward(100, map);        
        
        /*  - Build road carefully to samwill -  */
        Flag startFlag = hq.getFlag();
        
        List<Point> chosenPointsForRoad = new ArrayList<>();
        
        /*  - List possible adjacent connections for the road -  */
        List<Point> roadConnections = map.getPossibleAdjacentRoadConnections(startFlag.getPosition());
    
        assertEquals(startFlag.getPosition(), new Point(16, 14));
        
        assertTrue(roadConnections.contains(new Point(17, 13)));
    
        /*  - Choose 17, 13 -  */
        chosenPointsForRoad.add(new Point(17, 13));

        gameLogic.gameLoop(map);
        fastForward(100, map);        
        
        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(17, 13));
        
        assertTrue(roadConnections.contains(new Point(16, 12)));

        /*  - Choose 16, 12 -  */
        chosenPointsForRoad.add(new Point(16, 12));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(16, 12));
        
        assertTrue(roadConnections.contains(new Point(14, 12)));

        /*  - Choose 14, 12 -  */
        chosenPointsForRoad.add(new Point(14, 12));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(14, 12));
        
        assertTrue(roadConnections.contains(new Point(13, 11)));

        /*  - Choose 13, 11 -  */
        chosenPointsForRoad.add(new Point(13, 11));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(13, 11));
        
        assertTrue(roadConnections.contains(new Point(13, 9)));
        
        /*  - Choose 13, 9 -  */
        chosenPointsForRoad.add(new Point(13, 9));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(13, 9));
        
        assertTrue(roadConnections.contains(new Point(12, 8)));
        
        /*  - Choose 12, 8 -  */
        chosenPointsForRoad.add(new Point(12, 8));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - Connect to sawmill's flag -  */
        roadConnections = map.getPossibleAdjacentRoadConnections(new Point(12, 8));
        
        assertTrue(roadConnections.contains(sm.getFlag().getPosition()));
        
        map.placeRoad(startFlag, chosenPointsForRoad, sm.getFlag());
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /* Sawmill and woodcutter built and connected to headquarter */
    }

}
