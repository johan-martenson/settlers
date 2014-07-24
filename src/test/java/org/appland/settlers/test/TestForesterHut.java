/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.DeliveryNotPossibleException;
import org.appland.settlers.model.Forester;
import org.appland.settlers.model.ForesterHut;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidMaterialException;
import org.appland.settlers.model.InvalidStateForProduction;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructSmallHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestForesterHut {

    @Test
    public void testConstructForrester() throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction, Exception {
        ForesterHut f = new ForesterHut();

        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);

        assertFalse(f.needsWorker());

        // TODO: New test to verify that a worker can't be assigned to an unfinished forrester */
        Utils.constructSmallHouse(f);

        /* Verify that the forrester is unoccupied when it's newly constructed */
        assertTrue(f.needsWorker());

        /* Verify that the ForesterHut requires a worker */
        assertTrue(f.needsWorker());

        Forester forester = new Forester();

        /* Assign worker */
        f.assignWorker(forester);

        assertFalse(f.needsWorker());
        assertTrue(f.getWorker().equals(forester));
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerToUnfinishedForester() throws Exception {
        GameMap map = new GameMap(10, 10);
        ForesterHut f = new ForesterHut();

        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);

        f.promiseWorker(new Forester());
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerToUnfinishedForester() throws Exception {
        GameMap map = new GameMap(10, 10);
        ForesterHut f = new ForesterHut();

        assertTrue(f.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);

        f.assignWorker(new Forester());
    }

    @Test(expected = Exception.class)
    public void testAssignWorkerTwice() throws Exception {
        GameMap map = new GameMap(10, 10);
        ForesterHut f = new ForesterHut();

        Utils.constructSmallHouse(f);

        f.assignWorker(new Forester());

        f.assignWorker(new Forester());
    }

    @Test(expected = Exception.class)
    public void testPromiseWorkerTwice() throws Exception {
        GameMap map = new GameMap(10, 10);
        ForesterHut f = new ForesterHut();

        Utils.constructSmallHouse(f);

        f.promiseWorker(new Forester());

        f.promiseWorker(new Forester());
    }

    @Test
    public void testForesterHutIsNotMilitary() throws Exception {
        GameMap map = new GameMap(10, 10);
        ForesterHut f = new ForesterHut();

        Utils.constructSmallHouse(f);

        assertFalse(f.isMilitaryBuilding());
        assertTrue(f.getHostedMilitary() == 0);
        assertTrue(f.getMaxHostedMilitary() == 0);
    }

    @Test
    public void testForesterHutUnderConstructionNotNeedsWorker() {
        ForesterHut f = new ForesterHut();

        assertFalse(f.needsWorker());
    }

    @Test
    public void testForesterIsAssignedToForesterHut() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(7, 3);
        Point point4 = new Point(8, 2);
        Point point5 = new Point(10, 2);
        Point point6 = new Point(11, 3);
        Road road0 = map.placeRoad(point2, point3, point4, point5, point6);

        GameLogic gameLogic = new GameLogic();

        /* Finish the forester hut */
        Utils.constructSmallHouse(foresterHut);
        
        /* Run game logic twice, once to place courier and once to place forester */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);

        List<Worker> workers = map.getAllWorkers();
        assertTrue(workers.size() == 2);
        assertTrue(workers.get(0) instanceof Forester || workers.get(1) instanceof Forester);
    }

    @Test
    public void testOnlyOneForesterIsAssignedToForesterHut() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(7, 3);
        Point point4 = new Point(8, 2);
        Point point5 = new Point(10, 2);
        Point point6 = new Point(11, 3);
        Road road0 = map.placeRoad(point2, point3, point4, point5, point6);

        GameLogic gameLogic = new GameLogic();

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Run game logic twice, once to place courier and once to place forester */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);

        assertTrue(map.getAllWorkers().size() == 2);

        /* Keep running the gameloop and make sure no more workers are allocated */
        int i;
        for (i = 0; i < 20; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }

        assertTrue(map.getAllWorkers().size() == 2);
    }

    @Test
    public void testArrivedForesterRestsInHutAndThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(forester.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();        
        
        assertFalse(forester.isInsideBuilding());
    }

    @Test
    public void testForesterFindsSpotToPlantNewTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();
        assertNotNull(point);
        
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
        assertFalse(map.isTreeAtPoint(point));
        assertTrue(forester.isTraveling());
    }

    @Test
    public void testForesterReachesPointToPlantTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertEquals(forester.getPosition(), point);
        assertFalse(forester.isTraveling());
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
    }

    @Test
    public void testForesterPlantsTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));

        map.stepTime();
        
        assertTrue(forester.isPlanting());
        
        for (i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
    }

    @Test
    public void testForesterReturnsHomeAfterPlantingTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(forester.isInsideBuilding());    

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);
        
        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));

        map.stepTime();
        
        assertTrue(forester.isPlanting());
        
        for (i = 0; i < 19; i++) {
            assertTrue(forester.isPlanting());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(forester.isPlanting());
        assertFalse(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));

        assertEquals(forester.getTarget(), foresterHut.getFlag().getPosition());
        assertTrue(forester.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertFalse(forester.isInsideBuilding());
        
        map.stepTime();
        
        assertTrue(forester.isInsideBuilding());
    }
    
    @Test
    public void testForesterHutProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        
        constructSmallHouse(foresterHut);
        assertFalse(foresterHut.isCargoReady());
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        assertTrue(forester.isInsideBuilding());
        assertFalse(foresterHut.isCargoReady());

        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertFalse(foresterHut.isCargoReady());
        }
    }

    @Test
    public void testForesterStaysInsideWhenThereAreNoSpotsAvailable() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point p : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }
            
            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }
            
            map.placeTree(p);
        }
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(forester.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();        
        
        assertTrue(forester.isInsideBuilding());        
    }

    @Test
    public void testForesterDoesNotPlantTreeOnStone() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(), point1);

        /* Construct the forester hut */
        constructSmallHouse(foresterHut);

        /* Put trees around the forester hut */
        for (Point p : map.getPointsWithinRadius(foresterHut.getPosition(), 4)) {
            if (p.equals(point1)) {
                continue;
            }
            
            if (map.isBuildingAtPoint(p) || map.isFlagAtPoint(p) || map.isRoadAtPoint(p) || map.isStoneAtPoint(p)) {
                continue;
            }
            
            map.placeStone(p);
        }
        
        /* Manually place forester */
        Forester forester = new Forester(map);
        map.placeWorker(forester, foresterHut.getFlag());
        foresterHut.assignWorker(forester);
        forester.enterBuilding(foresterHut);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(forester.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(forester.isInsideBuilding());
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();        
        
        assertTrue(forester.isInsideBuilding());
    }
}
