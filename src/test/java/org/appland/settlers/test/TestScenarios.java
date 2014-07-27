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
import org.appland.settlers.model.WoodcutterWorker;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForwardUntilWorkersReachTarget;
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

        map.placeTree(wcSpot.downRight().right());
        
        /* Create roads */
        Road wcToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());
        Road smToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), sm.getFlag());
        Road qryToHqRoad = map.placeAutoSelectedRoad(hq.getFlag(), qry.getFlag());

        /* Assign workers to the roads */
        Courier wr1 = new Courier(map);
        Courier wr2 = new Courier(map);
        Courier wr3 = new Courier(map);
        WoodcutterWorker wcr = new WoodcutterWorker(map);

        map.placeWorker(wr1, wc.getFlag());
        map.placeWorker(wr2, sm.getFlag());
        map.placeWorker(wr3, qry.getFlag());
        map.placeWorker(wcr, wc.getFlag());

        wr1.setTargetRoad(wcToHqRoad);
        wr2.setTargetRoad(smToHqRoad);
        wr3.setTargetRoad(qryToHqRoad);

        /* Let the couriers reach their targeted roads */
        Utils.fastForwardUntilWorkersReachTarget(map, wr1, wr2, wr3);
        
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
        
        wc.assignWorker(wcr);
        wcr.enterBuilding(wc);
        
        assertEquals(wc.getWorker(), wcr);
        
        /*   --   START TEST   --   */
        
        /* Fast forward until the woodcutter has cut some wood */
        int i;
        for (i = 0; i < 700; i++) {
            if (wc.isCargoReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(wc.isCargoReady());
        
        /* Retrieve cargo from woodcutter and put it on the flag */
        gameLogic.initiateCollectionOfNewProduce(map);

        Courier courierWcToHq = wcToHqRoad.getCourier();

        assertNull(courierWcToHq.getCargo());
        assertTrue(courierWcToHq.isArrived());
        assertFalse(courierWcToHq.isTraveling());
        
        assertTrue(wc.getFlag().getStackedCargo().size() == 1);
        assertTrue(wc.getFlag().hasCargoWaitingForRoad(wcToHqRoad));
        
        /* Transport cargo one hop */
        map.stepTime();

        assertEquals(courierWcToHq.getTarget(), wc.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courierWcToHq, wc.getFlag().getPosition());
        
        assertNotNull(courierWcToHq.getCargo());
        assertEquals(courierWcToHq.getTarget(), hq.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courierWcToHq, hq.getFlag().getPosition());

        assertTrue(wcToHqRoad.getCourier().isAt(hq.getFlag().getPosition()));

        /* Cargo has arrived at the headquarter and stored */

        assertNull(wcToHqRoad.getCourier().getCargo());
        assertTrue(hq.getAmount(WOOD) == 1);

        /* Find out that the sawmill needs the wood */
        gameLogic.initiateNewDeliveriesForAllStorages(map);

        Courier courierSmToHq = smToHqRoad.getCourier();
        
        assertTrue(hq.getFlag().getStackedCargo().get(0).getMaterial() == WOOD);

        Cargo cargo = hq.getFlag().getStackedCargo().get(0);

        assertTrue(cargo.getTarget().equals(sm));
        assertNull(courierSmToHq.getCargo());
        assertTrue(cargo.getPlannedRoads().get(0).equals(smToHqRoad));
        assertTrue(hq.getFlag().hasCargoWaitingForRoad(smToHqRoad));
        assertTrue(hq.getAmount(WOOD) == 0);
        assertFalse(courierSmToHq.isTraveling());
        assertEquals(courierSmToHq.getAssignedRoad(), smToHqRoad);
        assertFalse(cargo.isDeliveryPromised());
        
        /* Get the wood transported to the sawmill */
        map.stepTime();
        
        /* The courier starts walking to the HQ */

        assertTrue(cargo.isDeliveryPromised());
        assertTrue(courierSmToHq.isTraveling());
        assertFalse(courierSmToHq.isArrived());
        assertEquals(courierSmToHq.getTarget(), hq.getFlag().getPosition());
        
        /* The courier reaches the HQ */
        Utils.fastForwardUntilWorkerReachesPoint(map, courierSmToHq, hq.getFlag().getPosition());
        
        /* The courier has picked up the WOOD */
        
        assertFalse(courierSmToHq.isArrived());
        assertNotNull(courierSmToHq.getCargo());
        assertEquals(courierSmToHq.getCargo(), cargo);
        assertTrue(courierSmToHq.isTraveling());
        assertTrue(courierSmToHq.getCargo().getMaterial() == WOOD);
        assertEquals(courierSmToHq.getTargetFlag(), sm.getFlag());
        assertEquals(courierSmToHq.getPosition(), hq.getFlag().getPosition());
        assertTrue(courierSmToHq.getCargo().getTarget().equals(sm));
        assertTrue(sm.getMaterialInQueue(WOOD) == 0);
        
        /* Get the wood transported to the sawmill and deliver it*/
        Utils.fastForwardUntilWorkersReachTarget(map, courierSmToHq);

        /* Cargo has arrived at the sawmill and the courier has delivered it */
        assertTrue(sm.getMaterialInQueue(WOOD) == 1);
        assertTrue(courierSmToHq.isIdle());

        /* Produce plancks in sawmill */
        assertFalse(sm.isCargoReady());

        fastForward(100, map);

        assertTrue(sm.isCargoReady());
        assertTrue(sm.getMaterialInQueue(WOOD) == 0);

        gameLogic.initiateCollectionOfNewProduce(map);

        assertNull(courierSmToHq.getCargo());
        assertFalse(courierSmToHq.isTraveling());
        assertFalse(courierWcToHq.isAt(wc.getFlag().getPosition()));
        
        /* Transport plancks and new wood to nearest storage*/
        map.stepTime();

        assertEquals(courierSmToHq.getTarget(), sm.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, courierSmToHq, sm.getFlag().getPosition());
        
        assertNotNull(courierSmToHq.getCargo());
        assertTrue(courierSmToHq.getCargo().getMaterial() == PLANCK);
        assertTrue(courierSmToHq.getCargo().getTarget().equals(hq));
        assertTrue(courierSmToHq.getTargetFlag().equals(hq.getFlag()));
        assertTrue(hq.getAmount(PLANCK) == 0);
        assertFalse(courierSmToHq.isAt(hq.getFlag().getPosition()));
        
        fastForwardUntilWorkersReachTarget(map, courierSmToHq);
        
        assertNull(courierSmToHq.getCargo());
        assertTrue(hq.getAmount(PLANCK) == 1);
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
        List<Point> roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(startFlag.getPosition());
    
        assertEquals(startFlag.getPosition(), new Point(16, 14));
        
        assertTrue(roadConnections.contains(new Point(17, 13)));
    
        /*  - Choose 17, 13 -  */
        chosenPointsForRoad.add(new Point(17, 13));

        gameLogic.gameLoop(map);
        fastForward(100, map);        
        
        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(17, 13));
        
        assertTrue(roadConnections.contains(new Point(16, 12)));

        /*  - Choose 16, 12 -  */
        chosenPointsForRoad.add(new Point(16, 12));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(16, 12));
        
        assertTrue(roadConnections.contains(new Point(14, 12)));

        /*  - Choose 14, 12 -  */
        chosenPointsForRoad.add(new Point(14, 12));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(14, 12));
        
        assertTrue(roadConnections.contains(new Point(13, 11)));

        /*  - Choose 13, 11 -  */
        chosenPointsForRoad.add(new Point(13, 11));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(13, 11));
        
        assertTrue(roadConnections.contains(new Point(12, 10)));
        
        /*  - Choose 12, 10 -  */
        chosenPointsForRoad.add(new Point(12, 10));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(12, 10));

        assertTrue(roadConnections.contains(new Point(13, 9)));
        
        /*  - Choose 13, 9 -  */
        chosenPointsForRoad.add(new Point(13, 9));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(13, 9));
        
        assertTrue(roadConnections.contains(new Point(12, 8)));
        
        /*  - Choose 12, 8 -  */
        chosenPointsForRoad.add(new Point(12, 8));
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /*  - Connect to sawmill's flag -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(12, 8));
        
        assertTrue(roadConnections.contains(sm.getFlag().getPosition()));
        

        chosenPointsForRoad.add(0,startFlag.getPosition());
	chosenPointsForRoad.add(sm.getFlag().getPosition());

        map.placeRoad(chosenPointsForRoad);
        
        gameLogic.gameLoop(map);
        fastForward(100, map);        

        /* Sawmill and woodcutter built and connected to headquarter */
    }
}
