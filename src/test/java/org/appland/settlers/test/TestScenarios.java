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
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Flag;
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
import org.appland.settlers.model.SawmillWorker;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.LARGE;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForwardUntilWorkersReachTarget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestScenarios {

    @Test
    public void productionAndTransportForWoodcutterAndSawmill() throws InvalidEndPointException, InvalidRouteException, InvalidStateForProduction, InvalidMaterialException, DeliveryNotPossibleException, Exception {

        /*   --   SETUP   --   */
        
        // TODO: RE-verify and add asserts!
        /* Create starting position */
        GameMap map = new GameMap(30, 30);
        Storage hq = new Headquarter();
        Point startPosition = new Point(6, 6);

        /* Player creates woodcutter, sawmill and quarry */
        Building wc = new Woodcutter();
        Sawmill sm = new Sawmill();
        Quarry qry = new Quarry();

        Point wcSpot = new Point(6, 12);
        Point smSpot = new Point(12, 6);
        Point qrySpot = new Point(20, 6);

        map.placeBuilding(hq, startPosition);
        map.placeBuilding(wc, wcSpot);
        map.placeBuilding(sm, smSpot);
        map.placeBuilding(qry, qrySpot);

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
        map.placeWorker(wcr, wc);

        wr1.assignToRoad(wcToHqRoad);
        wr2.assignToRoad(smToHqRoad);
        wr3.assignToRoad(qryToHqRoad);

        /* Move forward in time until the small buildings are done */
        Utils.constructHouse(wc, map);
        Utils.constructHouse(qry, map);
        Utils.constructHouse(sm, map);

        assertTrue(wc.ready());
        assertTrue(qry.ready());
        assertTrue(sm.ready());

        assertTrue(hq.getAmount(WOOD) == 4);
        assertTrue(hq.getAmount(PLANCK) == 15);
        assertTrue(hq.getAmount(STONE) == 10);

        Utils.occupyBuilding(wcr, wc, map);
        Utils.occupyBuilding(new SawmillWorker(map), sm, map);

        /* Let the couriers reach their targeted roads */
        Utils.fastForwardUntilWorkersReachTarget(map, wr1, wr2, wr3);        
        
        /*   --   START TEST   --   */
        
        /* Fast forward until the woodcutter has cut some wood */
        assertTrue(wc.getFlag().getStackedCargo().isEmpty());
        
        int i;
        for (i = 0; i < 700; i++) {
            if (!wc.getFlag().getStackedCargo().isEmpty()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(wc.getFlag().getStackedCargo().isEmpty());
        
        /* Retrieve cargo from woodcutter and put it on the flag */
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
        assertEquals(courierWcToHq.getTarget(), hq.getPosition());
        assertEquals(courierWcToHq.getCargo().getMaterial(), WOOD);
        
        int amountWood = hq.getAmount(WOOD);
        
        for (i = 0; i < 500; i++) {
            if (courierWcToHq.getPosition().equals(hq.getPosition())) {
                break;
            }
            
            amountWood = hq.getAmount(WOOD);
            
            map.stepTime();
        }
        
        assertTrue(wcToHqRoad.getCourier().isAt(hq.getPosition()));

        /* Cargo has arrived at the headquarter and stored */
        assertNull(wcToHqRoad.getCourier().getCargo());
        assertTrue(hq.getAmount(WOOD) == amountWood + 1);

        /* Find out that the sawmill needs the wood */
        Worker w = hq.getWorker();
        
        for (i = 0; i < 300; i++) {
            
            if (w.getTarget().equals(hq.getFlag().getPosition()) && w.getCargo().getMaterial().equals(WOOD)) {
                break;
            }

            map.stepTime();
        }
        
        assertEquals(w.getTarget(), hq.getFlag().getPosition());
        
        int amountInStack = hq.getFlag().getStackedCargo().size();
        
        Utils.fastForwardUntilWorkerReachesPoint(map, w, hq.getFlag().getPosition());
        
        Courier courierSmToHq = smToHqRoad.getCourier();
        
        assertTrue(hq.getFlag().getStackedCargo().get(amountInStack).getMaterial() == WOOD);

        Cargo cargo = hq.getFlag().getStackedCargo().get(amountInStack);

        assertTrue(cargo.getTarget().equals(sm));
        
        /* Wait for smToHqRoad's courier to pick up the cargo */
        for (i = 0; i < 400; i++) {
            if (cargo.equals(courierSmToHq.getCargo())) {
                break;
            }
            
            map.stepTime();
        }
        
        assertEquals(cargo, courierSmToHq.getCargo());
        
        /* The courier has picked up the WOOD */
        assertFalse(courierSmToHq.isArrived());
        assertNotNull(courierSmToHq.getCargo());
        assertEquals(courierSmToHq.getCargo(), cargo);
        assertTrue(courierSmToHq.isTraveling());
        assertTrue(courierSmToHq.getCargo().getMaterial() == WOOD);
        assertEquals(courierSmToHq.getTarget(), sm.getPosition());
        assertEquals(courierSmToHq.getPosition(), hq.getFlag().getPosition());
        assertTrue(courierSmToHq.getCargo().getTarget().equals(sm));
        
        /* Get the wood transported to the sawmill and deliver it*/
        Utils.fastForwardUntilWorkerReachesPoint(map, courierSmToHq, sm.getPosition());

        /* Cargo has arrived at the sawmill and the courier has delivered it */
        assertTrue(sm.getAmount(WOOD) > 0);
        int amountInQueue = sm.getAmount(WOOD);
        
        /* Produce plancks in sawmill. 
        
        Note! The sawmill worker is after the courier 
              in the worker list so it will get called to step time once before 
              this section is reached
        */
        for (i = 0; i < 500; i++) {
            if (sm.getWorker().getCargo() != null) {
                break;
            }
            
            assertNull(sm.getWorker().getCargo());
            map.stepTime();
        }

        assertNotNull(sm.getWorker().getCargo());
        assertTrue(sm.getAmount(WOOD) == amountInQueue - 1);

        /* Let the sawmill worker leave the cargo at the flag */
        assertEquals(sm.getWorker().getTarget(), sm.getFlag().getPosition());
        
        Utils.fastForwardUntilWorkerReachesPoint(map, sm.getWorker(), sm.getFlag().getPosition());
        
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
        assertEquals(courierSmToHq.getTarget(), hq.getPosition());
        assertFalse(courierSmToHq.isAt(hq.getFlag().getPosition()));
        
        fastForwardUntilWorkersReachTarget(map, courierSmToHq);
        
        assertNull(courierSmToHq.getCargo());
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
        fastForward(100, map);

        // TODO: assert that nothing happens
        
        /* Player creates woodcutter */
        Building wc = new Woodcutter();
        Point wcSpot = new Point(6, 12);

        map.placeBuilding(wc, wcSpot);

        fastForward(100, map);
        
        /* Player creates road between hq and wc */
        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());

        // TODO: assert that the road is unoccupied
        
        fastForward(100, map);
        
        // TODO: assert that the road is occupied
        
        /* The road is occupied so the delivery of plancks and stone to the wc can start  */
        // TODO: assert that the wc is under construction and has no material yet
        
        fastForward(100, map);

        fastForward(100, map);
        
        fastForward(100, map);
        
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

        fastForward(100, map);
        
        /*   --   Create road to woodcutter   --   */
        map.placeAutoSelectedRoad(hq.getFlag(), wc.getFlag());
        
        fastForward(100, map);        
    
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

        fastForward(100, map);        
    
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

        fastForward(100, map);        
        
        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(17, 13));
        
        assertTrue(roadConnections.contains(new Point(16, 12)));

        /*  - Choose 16, 12 -  */
        chosenPointsForRoad.add(new Point(16, 12));
        
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(16, 12));
        
        assertTrue(roadConnections.contains(new Point(14, 12)));

        /*  - Choose 14, 12 -  */
        chosenPointsForRoad.add(new Point(14, 12));
        
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(14, 12));
        
        assertTrue(roadConnections.contains(new Point(13, 11)));

        /*  - Choose 13, 11 -  */
        chosenPointsForRoad.add(new Point(13, 11));
        
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(13, 11));
        
        assertTrue(roadConnections.contains(new Point(12, 10)));
        
        /*  - Choose 12, 10 -  */
        chosenPointsForRoad.add(new Point(12, 10));
        
        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(12, 10));

        assertTrue(roadConnections.contains(new Point(13, 9)));
        
        /*  - Choose 13, 9 -  */
        chosenPointsForRoad.add(new Point(13, 9));

        fastForward(100, map);        

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(13, 9));
        
        assertTrue(roadConnections.contains(new Point(12, 8)));
        
        /*  - Choose 12, 8 -  */
        chosenPointsForRoad.add(new Point(12, 8));
        
        fastForward(100, map);        

        /*  - Connect to sawmill's flag -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(new Point(12, 8));
        
        assertTrue(roadConnections.contains(sm.getFlag().getPosition()));
        

        chosenPointsForRoad.add(0,startFlag.getPosition());
	chosenPointsForRoad.add(sm.getFlag().getPosition());

        map.placeRoad(chosenPointsForRoad);
        
        fastForward(100, map);        

        /* Sawmill and woodcutter built and connected to headquarter */
    }
}
