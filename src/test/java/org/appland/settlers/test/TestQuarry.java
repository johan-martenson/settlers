/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructSmallHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestQuarry {
    
    @Test
    public void testFinishedQuarryNeedsWorker() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(8, 6);
        Building quarry = map.placeBuilding(new Quarry(), point1);

        Utils.constructSmallHouse(quarry);

        assertTrue(quarry.getConstructionState() == DONE);
        assertTrue(quarry.needsWorker());
        assertTrue(quarry.needsWorker(Material.STONEMASON));
    }
    
    @Test
    public void testStonemasonIsAssignedToFinishedHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        /* Finish the woodcutter */
        Utils.constructSmallHouse(quarry);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        List<Worker> workers = map.getAllWorkers();
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(workers.get(0) instanceof Stonemason || workers.get(1) instanceof Stonemason);
        assertTrue(workers.get(0) instanceof Courier || workers.get(1) instanceof Courier);
    }

    @Test
    public void testArrivedStonemasonRestsInHutAndThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2);

        /* Construct the forester hut */
        constructSmallHouse(quarry);
        
        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(mason.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(mason.isInsideBuilding());
    }

    @Test
    public void testStonemasonFindsSpotToGetStone() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(12, 4);
        Stone stone = map.placeStone(point2);
        
        /* Construct the quarry */
        constructSmallHouse(quarry);

        
        /* Manually place stonemason */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();
        assertNotNull(point);
        
        assertTrue(point.isAdjacent(point2));
        assertTrue(mason.isTraveling());
        assertFalse(map.isBuildingAtPoint(point));
    }

    @Test
    public void testStonemasonReachesPointToGetStone() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(quarry);
        
        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();

        assertFalse(mason.getTarget().equals(point2));
        assertTrue(mason.getTarget().isAdjacent(point2));
        assertTrue(mason.isTraveling());

        map.stepTime();
        
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }
        
        assertTrue(mason.getPosition().isAdjacent(point2));
        assertTrue(mason.isArrived());
        assertTrue(mason.isAt(point));
        assertFalse(mason.isTraveling());
    }

    @Test
    public void testStonemasonGetsStone() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(quarry);

        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();

        assertTrue(point.isAdjacent(point2));
        assertTrue(mason.isTraveling());
        
        map.stepTime();
        
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }
        
        assertTrue(mason.isArrived());
        assertTrue(mason.getPosition().isAdjacent(point2));

        map.stepTime();
        
        assertTrue(mason.isGettingStone());
        
        for (i = 0; i < 49; i++) {
            assertTrue(mason.isGettingStone());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));

        map.stepTime();
        
        assertFalse(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));
    }

    @Test
    public void testStonemasonReturnsAndStoresStoneAsCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(13, 5);
        Stone stone = map.placeStone(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(quarry);

        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(mason.isInsideBuilding());    

        Point point = mason.getTarget();

        assertTrue(point.isAdjacent(point2));
        assertTrue(mason.isTraveling());
        
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }
        
        assertTrue(mason.isArrived());
        assertTrue(mason.getPosition().isAdjacent(point2));

        map.stepTime();
        
        assertTrue(mason.isGettingStone());
        
        for (i = 0; i < 49; i++) {
            assertTrue(mason.isGettingStone());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(mason.isGettingStone());
        assertFalse(map.isStoneAtPoint(point));

        map.stepTime();
        
        assertFalse(mason.isGettingStone());
        
        assertEquals(mason.getTarget(), quarry.getFlag().getPosition());
        assertFalse(quarry.isCargoReady());
        
        if (!mason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, mason);
        }

        assertFalse(quarry.isCargoReady());
        assertFalse(mason.isInsideBuilding());
        
        map.stepTime();
        
        assertTrue(quarry.isCargoReady());
        assertTrue(mason.isInsideBuilding());
        assertNull(mason.getCargo());

    }

    @Test
    public void testQuarryWithoutStoneProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);

        /* Construct the forester hut */
        
        constructSmallHouse(quarry);
        assertFalse(quarry.isCargoReady());
        
        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        assertTrue(mason.isInsideBuilding());
        assertFalse(quarry.isCargoReady());

        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertFalse(quarry.isCargoReady());
        }
        
    }

    @Test
    public void testStonemasonStaysAtHomeWhenNoStonesAreAvailable() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building quarry = map.placeBuilding(new Quarry(), point1);
        Point point2 = new Point(12, 4);

        /* Construct the forester hut */
        constructSmallHouse(quarry);
        
        /* Manually place forester */
        Stonemason mason = new Stonemason(map);
        map.placeWorker(mason, quarry.getFlag());
        quarry.assignWorker(mason);
        mason.enterBuilding(quarry);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        int i;
        for (i = 0; i < 9; i++) {
            assertTrue(mason.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(mason.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertTrue(mason.isInsideBuilding());
    }

    @Test
    public void testStonemasonIgnoresStoneTooFarAway() {
        // TODO
    }
}