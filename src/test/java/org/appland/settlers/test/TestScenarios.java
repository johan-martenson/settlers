/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.SawmillWorker;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Material.WOOD;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.test.Utils.fastForward;
import static org.appland.settlers.test.Utils.fastForwardUntilWorkersReachTarget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestScenarios {

    @Test
    public void productionAndTransportForWoodcutterAndSawmill() throws Exception {

        /*   --   SETUP   --   */

        // TODO: RE-verify and add asserts!
        /* Create starting position */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Player creates woodcutter, sawmill and quarry */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        Point point3 = new Point(20, 6);
        Quarry quarry0 = map.placeBuilding(new Quarry(player0), point3);

        Point point2 = new Point(12, 6);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point2);

        /* Place tree */
        map.placeTree(point1.downRight().right());

        /* Create roads */
        Road wcToHqRoad = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());
        Road smToHqRoad = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());
        Road qryToHqRoad = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Assign workers to the roads */
        Courier courier0 = new Courier(player0, map);
        Courier courier1 = new Courier(player0, map);
        Courier courier2 = new Courier(player0, map);

        map.placeWorker(courier0, woodcutter0.getFlag());
        map.placeWorker(courier1, sawmill0.getFlag());
        map.placeWorker(courier2, quarry0.getFlag());

        courier0.assignToRoad(wcToHqRoad);
        courier1.assignToRoad(smToHqRoad);
        courier2.assignToRoad(qryToHqRoad);

        /* Move forward in time until the small buildings are done */
        Utils.constructHouse(woodcutter0);
        Utils.constructHouse(quarry0);
        Utils.constructHouse(sawmill0);

        assertTrue(woodcutter0.isReady());
        assertTrue(quarry0.isReady());
        assertTrue(sawmill0.isReady());

        Utils.adjustInventoryTo(headquarter0, WOOD, 4);
        Utils.adjustInventoryTo(headquarter0, PLANK, 15);
        Utils.adjustInventoryTo(headquarter0, STONE, 10);

        assertEquals(headquarter0.getAmount(WOOD), 4);
        assertEquals(headquarter0.getAmount(PLANK), 15);
        assertEquals(headquarter0.getAmount(STONE), 10);

        /* Assign worker to the woodcutter */
        assertNotNull(woodcutter0.getWorker());

        /* Assign worker to the sawmill */
        Utils.occupyBuilding(new SawmillWorker(player0, map), sawmill0);

        /* Let the couriers reach their targeted roads */
        Utils.waitForCouriersToBeIdle(map, courier0, courier1, courier2);

        /*   --   START TEST   --   */

        /* Fast forward until the woodcutter has cut some wood */
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        for (int i = 0; i < 700; i++) {
            if (!woodcutter0.getFlag().getStackedCargo().isEmpty()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());

        /* Retrieve cargo from woodcutter and put it on the flag */
        Courier courierWcToHq = wcToHqRoad.getCourier();

        assertNull(courierWcToHq.getCargo());
        assertTrue(courierWcToHq.isArrived());
        assertFalse(courierWcToHq.isTraveling());
        assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 1);

        /* Transport cargo one hop */
        map.stepTime();

        assertEquals(courierWcToHq.getTarget(), woodcutter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courierWcToHq, woodcutter0.getFlag().getPosition());

        assertNotNull(courierWcToHq.getCargo());
        assertEquals(courierWcToHq.getTarget(), headquarter0.getPosition());
        assertEquals(courierWcToHq.getCargo().getMaterial(), WOOD);

        int amountWood = headquarter0.getAmount(WOOD);

        for (int i = 0; i < 500; i++) {
            if (courierWcToHq.getPosition().equals(headquarter0.getPosition())) {
                break;
            }

            amountWood = headquarter0.getAmount(WOOD);

            map.stepTime();
        }

        assertTrue(wcToHqRoad.getCourier().isAt(headquarter0.getPosition()));

        /* Cargo has arrived at the headquarter and stored */
        assertNull(wcToHqRoad.getCourier().getCargo());
        assertEquals(headquarter0.getAmount(WOOD), amountWood + 1);

        /* Find out that the sawmill needs the wood */
        Worker worker = headquarter0.getWorker();

        for (int i = 0; i < 300; i++) {

            if (worker.getTarget().equals(headquarter0.getFlag().getPosition()) && worker.getCargo().getMaterial().equals(WOOD)) {
                break;
            }

            map.stepTime();
        }

        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        int amountInStack = headquarter0.getFlag().getStackedCargo().size();

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        Courier courierSmToHq = smToHqRoad.getCourier();

        assertEquals(headquarter0.getFlag().getStackedCargo().get(amountInStack).getMaterial(), WOOD);

        Cargo cargo = headquarter0.getFlag().getStackedCargo().get(amountInStack);

        assertEquals(cargo.getTarget(), sawmill0);

        /* Wait for smToHqRoad's courier to pick up the cargo */
        for (int i = 0; i < 400; i++) {
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
        assertEquals(courierSmToHq.getCargo().getMaterial(), WOOD);
        assertEquals(courierSmToHq.getTarget(), sawmill0.getPosition());
        assertEquals(courierSmToHq.getPosition(), headquarter0.getFlag().getPosition());
        assertEquals(courierSmToHq.getCargo().getTarget(), sawmill0);

        /* Get the wood transported to the sawmill and deliver it */
        Utils.fastForwardUntilWorkerReachesPoint(map, courierSmToHq, sawmill0.getPosition());

        /* Cargo has arrived at the sawmill and the courier has delivered it */
        assertTrue(sawmill0.getAmount(WOOD) > 0);
        int amountInQueue = sawmill0.getAmount(WOOD);

        /* Produce planks in sawmill.

        /* Make sure the sawmill worker is done with the previous plank */
        for (int i = 0; i < 500; i++) {
            if (sawmill0.getWorker().getCargo() == null) {
                break;
            }

            map.stepTime();
        }

        assertNull(sawmill0.getWorker().getCargo());

        /*
        Note! The sawmill worker is after the courier
              in the worker list so it will get called to step time once before
              this section is reached
        */
        for (int i = 0; i < 500; i++) {
            if (sawmill0.getWorker().getCargo() != null) {
                break;
            }

            assertNull(sawmill0.getWorker().getCargo());
            map.stepTime();
        }

        assertNotNull(sawmill0.getWorker().getCargo());
        assertEquals(sawmill0.getAmount(WOOD), amountInQueue - 1);

        Cargo woodCargo = sawmill0.getWorker().getCargo();

        /* Let the sawmill worker leave the cargo at the flag */
        assertEquals(sawmill0.getWorker().getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmill0.getWorker(), sawmill0.getFlag().getPosition());

        /* Wait for the courier to pick up the wood cargo */
        for (int i = 0; i < 200; i++) {
            if (woodCargo.equals(courierSmToHq.getPromisedDelivery())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(courierSmToHq.getPromisedDelivery(), woodCargo);
        assertNull(courierSmToHq.getCargo());

        /* Transport planks and new wood to nearest storage */
        assertEquals(courierSmToHq.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courierSmToHq, sawmill0.getFlag().getPosition());

        assertNotNull(courierSmToHq.getCargo());
        assertEquals(courierSmToHq.getCargo().getMaterial(), PLANK);
        assertEquals(courierSmToHq.getCargo().getTarget(), headquarter0);
        assertEquals(courierSmToHq.getTarget(), headquarter0.getPosition());
        assertFalse(courierSmToHq.isAt(headquarter0.getFlag().getPosition()));

        fastForwardUntilWorkersReachTarget(map, courierSmToHq);

        assertNull(courierSmToHq.getCargo());
    }

    @Test
    public void buildWoodcutterSawmillQuarrySequentiallyFromScratch() throws Exception {

        /* Create initial game setup */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(6, 6);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /*   --   START TEST   --   */
        fastForward(100, map);

        // TODO: assert that nothing happens

        /* Player creates woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        fastForward(100, map);

        /* Player creates road between headquarter and woodcutter */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter.getFlag());

        // TODO: assert that the road is unoccupied

        fastForward(100, map);

        // TODO: assert that the road is occupied

        /* The road is occupied so the delivery of planks and stone to the woodcutter can start  */
        // TODO: assert that the woodcutter is under construction and has no material yet

        fastForward(400, map);

        /* The woodcutter has all material so construction can finish */
        fastForward(150, map);

        // TODO: assert that the woodcutter is finished

        // TODO: construct remaining houses, wait ~10 turns for production, construct barracks

    }

    @Test
    public void testGameStartFromScratchWithUserInput() throws Exception {

        /* Create initial game setup */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 30, 30);

        /* Place headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /*   --   User    --   */

        fastForward(100, map);

        /*   --   Create woodcutter   --  */

        /*  - List all house spots -  */
        Map<Point, Size> possibleHouseSpots = map.getAvailableHousePoints(player0);

        Point point1 = new Point(22, 20);
        assertTrue(possibleHouseSpots.containsKey(point1));
        assertEquals(possibleHouseSpots.get(point1), LARGE);

        /*  - Pick 22, 20 -  */
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        fastForward(100, map);

        /*   --   Create road to woodcutter   --   */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), woodcutter.getFlag());

        fastForward(200, map);

        /*   --   Create sawmill   --   */

        /*  - List all house spots -  */
        possibleHouseSpots = map.getAvailableHousePoints(player0);

        Point point2 = new Point(10, 10);

        assertTrue(possibleHouseSpots.containsKey(point2));
        assertEquals(possibleHouseSpots.get(point2), LARGE);

        /*  - Pick 10, 10 -  */
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point2);

        fastForward(200, map);

        /*  - Build road carefully to sawmill -  */
        Flag startFlag = headquarter.getFlag();

        List<Point> chosenPointsForRoad = new ArrayList<>();

        /*  - List possible adjacent connections for the road -  */
        List<Point> roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, startFlag.getPosition());

        Point point3 = new Point(17, 13);

        assertEquals(startFlag.getPosition(), point3.upLeft());
        assertTrue(roadConnections.contains(point3));

        /*  - Choose 17, 13 -  */
        chosenPointsForRoad.add(point3);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point3);

        Point point4 = new Point(16, 12);

        assertTrue(roadConnections.contains(point4));

        /*  - Choose 16, 12 -  */
        chosenPointsForRoad.add(point4);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point4);

        Point point5 = new Point(14, 12);

        assertTrue(roadConnections.contains(point5));

        /*  - Choose 14, 12 -  */
        chosenPointsForRoad.add(point5);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, new Point(14, 12));

        Point point6 = new Point(13, 11);

        assertTrue(roadConnections.contains(point6));

        /*  - Choose 13, 11 -  */
        chosenPointsForRoad.add(point6);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point6);

        Point point7 = new Point(12, 10);

        assertTrue(roadConnections.contains(point7));

        /*  - Choose 12, 10 -  */
        chosenPointsForRoad.add(point7);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point7);

        Point point8 = new Point(13, 9);

        assertTrue(roadConnections.contains(point8));

        /*  - Choose 13, 9 -  */
        chosenPointsForRoad.add(point8);

        fastForward(100, map);

        /*  - List possible adjacent connections for the road -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point8);

        Point point9 = new Point(12, 8);

        assertTrue(roadConnections.contains(point9));

        /*  - Choose 12, 8 -  */
        chosenPointsForRoad.add(point9);

        fastForward(100, map);

        /*  - Connect to sawmill's flag -  */
        roadConnections = map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, point9);

        assertTrue(roadConnections.contains(sawmill.getFlag().getPosition()));

        chosenPointsForRoad.add(0,startFlag.getPosition());
	    chosenPointsForRoad.add(sawmill.getFlag().getPosition());

        map.placeRoad(player0, chosenPointsForRoad);

        fastForward(100, map);

        /* Sawmill and woodcutter built and connected to headquarter */
    }
}
