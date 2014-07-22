/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.test;

import java.util.List;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Building.ConstructionState;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameLogic;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Material;
import static org.appland.settlers.model.Material.WOOD;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Size;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.MEDIUM;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.WoodcutterWorker;
import org.appland.settlers.model.Worker;
import static org.appland.settlers.test.Utils.constructSmallHouse;
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
import org.junit.Test;

/**
 *
 * @author johan
 */
public class TestWoodcutter {
    
    @Test
    public void testUnfinishedWoodcutterNeedsNoWoodcutter() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        assertTrue(wc.getConstructionState() == ConstructionState.UNDER_CONSTRUCTION);

        assertFalse(wc.needsWorker());
        assertFalse(wc.needsWorker(Material.WOODCUTTER_WORKER));
    }

    @Test
    public void testFinishedWoodcutterNeedsWoodcutterWorker() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Flag flag0 = map.placeFlag(point2);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        Utils.constructSmallHouse(wc);
        
        assertTrue(wc.needsWorker());
        assertTrue(wc.needsWorker(Material.WOODCUTTER_WORKER));
    }
    
    @Test
    public void testWoodcutterIsAssignedToFinishedHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();
        
        /* Finish the woodcutter */
        Utils.constructSmallHouse(wc);
        
        /* Run game logic twice, once to place courier and once to place woodcutter worker */
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        gameLogic.gameLoop(map);
        Utils.fastForward(10, map);
        
        List<Worker> workers = map.getAllWorkers();
        assertTrue(map.getAllWorkers().size() == 2);
        assertTrue(workers.get(0) instanceof WoodcutterWorker || workers.get(1) instanceof WoodcutterWorker);
        assertTrue(workers.get(0) instanceof Courier || workers.get(1) instanceof Courier);
    }
    
    @Test
    public void testOnlyOneWoodcutterIsAssignedToHouse() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point0 = new Point(5, 5);
        Building building0 = map.placeBuilding(new Headquarter(), point0);
        Point point1 = new Point(8, 6);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(point2, point3, point4);

        GameLogic gameLogic = new GameLogic();

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
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
    public void testArrivedWoodcutterRestsInHutAndThenLeaves() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);

        /* Grow tree */
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();
        
        for (i = 0; i < 9; i++) {
            assertTrue(wcWorker.isInsideBuilding());
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
    
    @Test
    public void testWoodcutterFindsSpotToCutDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(12, 4);

        Tree tree = map.placeTree(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Grow tree */
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }
        
        assertTrue(tree.getSize() == LARGE);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();
        assertNotNull(point);
        
        assertEquals(point, point2);
        assertTrue(map.isTreeAtPoint(point));
        assertTrue(wcWorker.isTraveling());
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
    }

    @Test
    public void testWoodcutterReachesPointToCutDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(12, 4);
        
        Tree tree = map.placeTree(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Grow tree */
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertTrue(tree.getSize() == LARGE);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(wcWorker.getTarget(), point2);
        assertTrue(wcWorker.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertEquals(wcWorker.getPosition(), point);
        assertFalse(wcWorker.isTraveling());
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));
    }

    @Test
    public void testWoodcutterCutsDownTree() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Grow tree */
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertTrue(tree.getSize() == LARGE);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        for (i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(wcWorker.isCuttingTree());
        assertTrue(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
    }

    @Test
    public void testWoodcutterReturnsAndStoresWoodAsCargo() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Grow tree */
        int i;
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertTrue(tree.getSize() == LARGE);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());    

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);
        
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();
        
        assertTrue(wcWorker.isCuttingTree());
        
        for (i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            gameLogic.gameLoop(map);
            map.stepTime();
        }

        assertTrue(wcWorker.isCuttingTree());
        assertTrue(map.isTreeAtPoint(point));

        map.stepTime();
        
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        
        assertEquals(wcWorker.getTarget(), wc.getFlag().getPosition());
        assertFalse(wc.isCargoReady());
        
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertFalse(wc.isCargoReady());
        assertFalse(wcWorker.isInsideBuilding());
        
        map.stepTime();
        
        assertTrue(wc.isCargoReady());
        assertTrue(wcWorker.isInsideBuilding());
        assertNull(wcWorker.getCargo());
    }
    
    @Test
    public void testWoodcutterHutWithoutTreesProducesNothing() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the forester hut */
        
        constructSmallHouse(wc);
        assertFalse(wc.isCargoReady());
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        assertTrue(wcWorker.isInsideBuilding());
        assertFalse(wc.isCargoReady());

        int i;
        for (i = 0; i < 100; i++) {
            map.stepTime();
            assertFalse(wc.isCargoReady());
        }
    }

    @Test
    public void testWoodcutterStaysInHouseWhenNoTreeIsAvailable() {
        // TODO
    }

    @Test
    public void testWoodcutterDoesNotCutSmallOrMediumTrees() throws Exception {
        GameMap map = new GameMap(20, 20);
        Point point1 = new Point(10, 4);
        Building wc = map.placeBuilding(new Woodcutter(), point1);

        /* Construct the forester hut */
        constructSmallHouse(wc);
        
        /* Manually place forester */
        WoodcutterWorker wcWorker = new WoodcutterWorker(map);
        map.placeWorker(wcWorker, wc.getFlag());
        wc.assignWorker(wcWorker);
        wcWorker.enterBuilding(wc);
        
        /* Run the game logic 10 times and make sure the forester stays in the hut */
        GameLogic gameLogic = new GameLogic();

        int i;
        for (i = 0; i < 9; i++) {
            gameLogic.gameLoop(map);
            Utils.fastForward(10, map);
        }
        
        Utils.fastForward(9, map);
        
        assertTrue(wcWorker.isInsideBuilding());
        
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2);
        
        assertTrue(tree.getSize() == Size.SMALL);
        
        /* Step once and make sure the forester stays in the hut */
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow tree to medium*/
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == MEDIUM) {
                break;
            }
        }

        assertTrue(tree.getSize() == MEDIUM);

        /* Step once and make sure the forester stays in the hut */
        
        map.stepTime();
        
        assertTrue(wcWorker.isInsideBuilding());
        
        /* Grow the tree to large */
        for (i = 0; i < 500; i++) {
            map.stepTime();
            
            if (tree.getSize() == LARGE) {
                break;
            }
        }

        assertTrue(tree.getSize() == LARGE);
        
        /* Step time and make sure the forester leaves the hut */
        
        map.stepTime();
        
        assertFalse(wcWorker.isInsideBuilding());
    }
}